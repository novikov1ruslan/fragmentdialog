package com.ivygames.morskoiboi.screen.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.PlayerOpponent;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.RulesFactory;
import com.ivygames.morskoiboi.ai.PlacementAlgorithm;
import com.ivygames.morskoiboi.ai.PlacementFactory;
import com.ivygames.morskoiboi.analytics.UiEvent;
import com.ivygames.morskoiboi.bluetooth.AcceptThread;
import com.ivygames.morskoiboi.bluetooth.BluetoothConnection;
import com.ivygames.morskoiboi.bluetooth.BluetoothGame;
import com.ivygames.morskoiboi.bluetooth.BluetoothOpponent;
import com.ivygames.morskoiboi.bluetooth.BluetoothUtils;
import com.ivygames.morskoiboi.bluetooth.ConnectionListener;
import com.ivygames.morskoiboi.model.Model;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.boardsetup.BoardSetupScreen;
import com.ivygames.morskoiboi.screen.devicelist.DeviceListScreen;
import com.ivygames.morskoiboi.screen.main.MainScreen;
import com.ivygames.morskoiboi.screen.view.SingleTextDialog;

import org.commons.logger.Ln;

import java.io.IOException;


public class BluetoothScreen extends BattleshipScreen implements BluetoothLayout.BluetoothActions, BattleshipActivity.BackPressListener, ConnectionListener {
    private static final String TAG = "bluetooth";

    private static final int DISCOVERABLE_DURATION = 300;

    private BluetoothLayout mLayout;
    private ViewGroup mContainer;
    private SingleTextDialog mDialog;
    private final BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();
    private AcceptThread mAcceptThread;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Ln.v(TAG + ": received broadcast: " + action);

            if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {
                int scanMode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, 0);
                Ln.d(TAG + ": scan mode changed to " + scanMode);

                if (!isDiscoverable()) {
                    cancelGameCreation();
                }
            }
        }
    };

    public BluetoothScreen(BattleshipActivity parent) {
        super(parent);
        parent.registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED));
    }

    @Override
    public View onCreateView(ViewGroup container) {
        mContainer = container;
        mLayout = (BluetoothLayout) inflate(R.layout.bluetooth_common, container);
        mLayout.setListener(this);

        Ln.d(this + " screen created");
        return mLayout;
    }

    @Override
    public View getView() {
        return mLayout;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BattleshipActivity.RC_ENSURE_DISCOVERABLE) {
            if (resultCode == Activity.RESULT_CANCELED) {
                UiEvent.send("reject_discover");
                Ln.d("user rejected discover-ability - canceling game creation");
                cancelGameCreation();
            } else {
                Ln.d("discover-ability insured for: " + resultCode);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getParent().unregisterReceiver(mReceiver);

        if (isDialogShown()) {
            hideDialog();
        }
    }

    @Override
    public void createGame() {
        showDialog();
        ensureDiscoverable();
        startAccepting();
    }

    /**
     * Start AcceptThread to begin a session in listening (server) mode. Called by the Activity onResume()
     */
    private synchronized void startAccepting() {
        Ln.d("starting listening to new connections");

        // Start the thread to listen on a BluetoothServerSocket
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread(this);
            mAcceptThread.start();
        } else {
            Ln.e("already accepting");
        }
    }

    @Override
    public void joinGame() {
        setScreen(new DeviceListScreen(getParent()));
    }

    @Override
    public void onBackPressed() {
        if (isDialogShown()) {
            cancelGameCreation();
        } else {
            setScreen(new MainScreen(getParent(), getParent().getApiClient()));
        }
    }

    private void cancelGameCreation() {
        hideDialog();
        cancelAcceptAndCloseConnection();
    }

    public synchronized void cancelAcceptAndCloseConnection() {
        if (mAcceptThread == null) {
            Ln.e("not accepting - cannot close");
            return;
        }

        mAcceptThread.cancelAccept();
        BluetoothUtils.join(mAcceptThread);
        mAcceptThread = null;
    }

    private void ensureDiscoverable() {
        if (isDiscoverable()) {
            Ln.w(TAG + ": already discoverable");
        } else {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABLE_DURATION);
            Ln.v("ensuring discover-ability for " + DISCOVERABLE_DURATION);
            startActivityForResult(discoverableIntent, BattleshipActivity.RC_ENSURE_DISCOVERABLE);
        }
    }

    private boolean isDiscoverable() {
        return mBtAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE;
    }

    @Override
    public void onConnected(BluetoothConnection connection) {
        Ln.d(TAG + ": connected - creating opponent and showing board setup");
        String defaultName = getString(R.string.player);
        BluetoothOpponent opponent = new BluetoothOpponent(connection, defaultName);
        connection.setMessageReceiver(opponent);
        String playerName = GameSettings.get().getPlayerName();
        if (TextUtils.isEmpty(playerName)) {
            playerName = getString(R.string.player);
            Ln.i("player name is empty - replaced by " + playerName);
        }
        PlacementAlgorithm placement = PlacementFactory.getAlgorithm();
        Rules rules = RulesFactory.getRules();
        Model.instance.setOpponents(new PlayerOpponent(playerName, placement, rules), opponent);
        Model.instance.game = new BluetoothGame(connection);

        setScreen(new BoardSetupScreen(getParent()));
    }

    @Override
    public void onConnectFailed(IOException exception) {
        if (isDialogShown()) {
            mDialog.setText(R.string.connection_failed);
        }
    }

    private boolean isDialogShown() {
        return mDialog != null;
    }

    private void showDialog() {
        mLayout.disable();
        mDialog = (SingleTextDialog) inflate(R.layout.wait_dialog, mContainer);
        mDialog.setText(R.string.waiting_connection);
        mContainer.addView(mDialog);
    }

    private void hideDialog() {
        mContainer.removeView(mDialog);
        mDialog = null;
        mLayout.enable();
    }

    @Override
    public int getMusic() {
        return R.raw.intro_music;
    }

    @Override
    public String toString() {
        return "BLUETOOTH" + debugSuffix();
    }
}
