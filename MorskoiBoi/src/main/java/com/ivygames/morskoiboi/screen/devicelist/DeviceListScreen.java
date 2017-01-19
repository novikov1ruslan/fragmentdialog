package com.ivygames.morskoiboi.screen.devicelist;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.ivygames.battleship.Rules;
import com.ivygames.battleship.player.PlayerFactory;
import com.ivygames.battleship.player.PlayerOpponent;
import com.ivygames.bluetooth.peer.BluetoothConnection;
import com.ivygames.bluetooth.peer.BluetoothPeer;
import com.ivygames.bluetooth.peer.BluetoothUtils;
import com.ivygames.bluetooth.peer.ConnectionCreationListener;
import com.ivygames.common.ui.BackPressListener;
import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.Dependencies;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.Session;
import com.ivygames.morskoiboi.bluetooth.BluetoothGame;
import com.ivygames.morskoiboi.bluetooth.BluetoothOpponent;
import com.ivygames.morskoiboi.dialogs.SingleTextDialog;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.ScreenCreator;

import org.commons.logger.Ln;

public class DeviceListScreen extends BattleshipScreen implements DeviceListActions, BackPressListener {
    private static final String TAG = "bluetooth";

    private DeviceListLayout mLayout;

    private ViewGroup mContainer;
    private SingleTextDialog mDialog;

    @NonNull
    private final GameSettings mSettings = Dependencies.getSettings();
    @NonNull
    private final Rules mRules = Dependencies.getRules();
    @NonNull
    private final PlayerFactory mPlayerFactory = Dependencies.getPlayerFactory();
    @NonNull
    private final BluetoothPeer mBluetooth = Dependencies.getBluetooth();

    @NonNull
    private final ConnectionCreationListener mConnectionListener = new ConnectionCreationListener() {

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
            PlayerOpponent player = mPlayerFactory.createPlayer(mSettings.getPlayerName(), mRules.getAllShipsSizes().length);
            player.setChatListener(parent());
            Session session = new Session(player, opponent);
            Session.bindOpponents(player, opponent);
            setScreen(ScreenCreator.newBoardSetupScreen(new BluetoothGame(connection), session));
        }
    };

    public DeviceListScreen(@NonNull BattleshipActivity parent) {
        super(parent);
        mBluetooth.setConnectionListener(mConnectionListener);
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
        mLayout.setBondedDevices(mBluetooth.getBondedDevices());

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
                mLayout.setBondedDevices(mBluetooth.getBondedDevices());
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
        if (mBluetooth.isConnecting()) {
            Ln.w("already connecting to device");
            return;
        }

        // Cancel discovery because it's costly and we're about to connect.
        // Always cancel discovery because it will slow down a connection
        cancelDiscovery();

        BluetoothDevice device = mBluetooth.getRemoteDevice(BluetoothUtils.extractMacAddress(info));
        showDialog(getString(R.string.connecting_to, device.getName()));
        mBluetooth.connectToDevice(device);
    }

    private void cancelDiscovery() {
        mBluetooth.cancelDiscovery();
        mLayout.discoveryFinished();
    }

    @Override
    public void scan() {
        startDiscovery();
    }

    private void startDiscovery() {
        mLayout.discoveryStarted();
        mBluetooth.startDiscovery();
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
        mBluetooth.stopConnecting();
    }

    @Override
    public int getMusic() {
        return R.raw.intro_music;
    }
}
