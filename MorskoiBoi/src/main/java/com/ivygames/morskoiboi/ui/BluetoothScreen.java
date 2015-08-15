package com.ivygames.morskoiboi.ui;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.view.ViewGroup;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.analytics.UiEvent;
import com.ivygames.morskoiboi.bluetooth.AcceptThread;
import com.ivygames.morskoiboi.bluetooth.BluetoothConnection;
import com.ivygames.morskoiboi.bluetooth.BluetoothGame;
import com.ivygames.morskoiboi.bluetooth.BluetoothUtils;
import com.ivygames.morskoiboi.bluetooth.ConnectionListener;
import com.ivygames.morskoiboi.model.Model;
import com.ivygames.morskoiboi.ui.view.BluetoothLayout;
import com.ivygames.morskoiboi.ui.view.SingleTextDialog;

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

    @Override
    public void onCreate() {
        super.onCreate();
        getActivity().registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED));
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
            if (resultCode == Activity.RESULT_OK) {

            } else if (resultCode == Activity.RESULT_CANCELED) {
                UiEvent.send(mGaTracker, "reject_discover");
                Ln.d("user rejected discover-ability - canceling game creation");
                cancelGameCreation();
            } else {
                Ln.e("wrong result for insure: " + resultCode);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mReceiver);
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
        mParent.setScreen(new DeviceListScreen());
    }

    @Override
    public void onBackPressed() {
        if (isDialogShown()) {
            cancelGameCreation();
        } else {
            mParent.setScreen(new MainScreen());
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

    private boolean isDialogShown() {
        return mDialog != null;
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
        Model.instance.game = new BluetoothGame(connection);
    }

    @Override
    public void onConnectFailed(IOException exception) {
        if (isDialogShown()) {
            mDialog.setText(R.string.connection_failed);
        }
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
}
