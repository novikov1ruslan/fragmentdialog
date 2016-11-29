package com.ivygames.morskoiboi.screen.devicelist;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.ivygames.battleship.PlayerFactory;
import com.ivygames.battleship.player.PlayerOpponent;
import com.ivygames.common.DebugUtils;
import com.ivygames.common.ui.BackPressListener;
import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.Dependencies;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.R;
import com.ivygames.battleship.Rules;
import com.ivygames.morskoiboi.Session;
import com.ivygames.morskoiboi.bluetooth.BluetoothAdapterWrapper;
import com.ivygames.morskoiboi.bluetooth.BluetoothConnection;
import com.ivygames.morskoiboi.bluetooth.BluetoothGame;
import com.ivygames.morskoiboi.bluetooth.BluetoothOpponent;
import com.ivygames.morskoiboi.bluetooth.BluetoothUtils;
import com.ivygames.morskoiboi.bluetooth.ConnectThread;
import com.ivygames.morskoiboi.bluetooth.ConnectionListener;
import com.ivygames.morskoiboi.dialogs.SingleTextDialog;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.ScreenCreator;

import org.commons.logger.Ln;

public class DeviceListScreen extends BattleshipScreen implements DeviceListActions, ConnectionListener, BackPressListener {
    private static final String TAG = "bluetooth";

    private DeviceListLayout mLayout;

    @NonNull
    private final BluetoothAdapterWrapper mBtAdapter;

    private ConnectThread mConnectThread;

    private ViewGroup mContainer;
    private SingleTextDialog mDialog;

    @NonNull
    private final GameSettings mSettings = Dependencies.getSettings();
    @NonNull
    private final Rules mRules = Dependencies.getRules();
    @NonNull
    private final PlayerFactory mPlayerFactory = Dependencies.getPlayerFactory();

    public DeviceListScreen(@NonNull BattleshipActivity parent, @NonNull BluetoothAdapterWrapper adapter) {
        super(parent);
        mBtAdapter = adapter;
    }

    @NonNull
    @Override
    public View getView() {
        return mLayout;
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull ViewGroup container) {
        mContainer = container;
        mLayout = (DeviceListLayout) inflate(R.layout.device_list, container);
        mLayout.setListener(this);
        mLayout.setBondedDevices(mBtAdapter.getBondedDevices());

        Ln.d(this + " screen created");
        return mLayout;
    }

    @Override
    public void onStart() {
        super.onStart();
        Ln.d(TAG + ": register BT broadcast receiver, starting discovery...");
        mParent.registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        mParent.registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        startDiscovery();
    }

    @Override
    public void onStop() {
        super.onStop();
        cancelDiscovery();
        mParent.unregisterReceiver(mReceiver);
        Ln.d(TAG + ": receivers are unregistered, discovery canceled");
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Ln.v(TAG + ": received broadcast: " + action);
//            mLayout.setBondedDevices(mBtAdapter.getBondedDevices());

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Ln.d(TAG + ": device is found, but already bound");
                } else {
                    Ln.d(TAG + ": new device is found: " + device);
                    mLayout.addBondedDevice(device);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                mLayout.setBondedDevices(mBtAdapter.getBondedDevices());
                cancelDiscovery();
            }
//            else if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {
//                int scanMode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, 0);
//                Ln.d(TAG + ": scan mode changed to " + scanMode);
//            }
        }
    };

    @Override
    public void selectDevice(String info) {
        Ln.d(TAG + ": selected device [" + info + "]");
        if (isConnecting()) {
            Ln.w("already connecting to device");
            return;
        }

        // Cancel discovery because it's costly and we're about to connect.
        // Always cancel discovery because it will slow down a connection
        cancelDiscovery();

        BluetoothDevice device = mBtAdapter.getRemoteDevice(BluetoothUtils.extractMacAddress(info));
        showDialog(getString(R.string.connecting_to, device.getName()));
        connectToDevice(device);
    }

    private void cancelDiscovery() {
        mBtAdapter.cancelDiscovery();
        mLayout.discoveryFinished();
    }

    @Override
    public void scan() {
        startDiscovery();
    }

    private void startDiscovery() {
        mLayout.discoveryStarted();
        mBtAdapter.startDiscovery();
    }

    private boolean isConnecting() {
        return mConnectThread != null;
    }

    @Override
    public void onConnectFailed() {
        Ln.d(TAG + ": connection attempt failed");
        if (isDialogShown()) {
            mDialog.setText(R.string.connection_failed);
        }
    }

    @Override
    public void onConnected(@NonNull BluetoothConnection connection) {
        Ln.d(TAG + ": connected - creating opponent and showing board setup");
        String defaultName = getString(R.string.player);
        BluetoothOpponent opponent = new BluetoothOpponent(connection, defaultName);
        connection.setMessageReceiver(opponent);
        String playerName = mSettings.getPlayerName();
        if (TextUtils.isEmpty(playerName)) {
            playerName = getString(R.string.player);
            Ln.i("player name is empty - replaced by " + playerName);
        }
        PlayerOpponent player = mPlayerFactory.createPlayer(playerName, mRules.getAllShipsSizes().length);
        player.setChatListener(parent());
        Session session = new Session(player, opponent);
        Session.bindOpponents(player, opponent);
        setScreen(ScreenCreator.newBoardSetupScreen(new BluetoothGame(connection), session));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isDialogShown()) {
            hideDialog();
        }
    }

    @Override
    public void onBackPressed() {
        if (isDialogShown()) {
            cancelGameCreation();
        } else {
            setScreen(ScreenCreator.newMainScreen());
        }
    }

    /**
     * Cancel any thread attempting to make a connection
     */
    private synchronized void stopConnecting() {
        if (mConnectThread == null) {
            return;
        }

        Ln.d("canceling current connection attempt...");
        mConnectThread.cancel();
        BluetoothUtils.join(mConnectThread);
        mConnectThread = null;
        Ln.d("connection cancelled");
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     *
     * @param device The BluetoothDevice to connect
     */
    private void connectToDevice(BluetoothDevice device) {
        stopConnecting();
        Ln.d("connecting to: " + device);
        mConnectThread = new ConnectThread(device, this);
        mConnectThread.start();
    }

    private boolean isDialogShown() {
        return mDialog != null;
    }

    private void showDialog(String text) {
        mLayout.disable();
        mDialog = (SingleTextDialog) inflate(R.layout.wait_dialog, mContainer);
        mDialog.setText(text);
        mContainer.addView(mDialog);
    }

    private void hideDialog() {
        mContainer.removeView(mDialog);
        mDialog = null;
        mLayout.enable();
    }

    private void cancelGameCreation() {
        hideDialog();
        stopConnecting();
    }

    @Override
    public int getMusic() {
        return R.raw.intro_music;
    }

    @Override
    public String toString() {
        return DebugUtils.getSimpleName(this);
    }
}
