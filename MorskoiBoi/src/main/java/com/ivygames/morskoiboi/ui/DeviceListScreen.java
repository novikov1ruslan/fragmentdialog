package com.ivygames.morskoiboi.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.view.ViewGroup;

import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.PlayerOpponent;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.analytics.ExceptionEvent;
import com.ivygames.morskoiboi.bluetooth.BluetoothGame;
import com.ivygames.morskoiboi.bluetooth.BluetoothOpponent;
import com.ivygames.morskoiboi.bluetooth.BluetoothUtils;
import com.ivygames.morskoiboi.bluetooth.ConnectionListener;
import com.ivygames.morskoiboi.bluetooth.MessageSender;
import com.ivygames.morskoiboi.model.Model;
import com.ivygames.morskoiboi.ui.BattleshipActivity.BackPressListener;
import com.ivygames.morskoiboi.ui.view.DeviceListLayout;
import com.ivygames.morskoiboi.ui.view.DeviceListLayout.DeviceListActions;
import com.ruslan.fragmentdialog.FragmentAlertDialog;

import org.acra.ACRA;
import org.commons.logger.Ln;

import java.io.IOException;
import java.util.Set;

public class DeviceListScreen extends BattleshipScreen implements DeviceListActions, ConnectionListener, BackPressListener {
    private static final String TAG = "bluetooth";

    private static final int REQUEST_ENSURE = 3;

    private static final String DIALOG = FragmentAlertDialog.TAG;

    private DeviceListLayout mLayout;
    private BluetoothAdapter mBtAdapter;
    private BluetoothGame mGame;

    private boolean mStopAcceptingOnStop;

    @Override
    public View getView() {
        return mLayout;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        mGame = new BluetoothGame();
        Model.instance.game = mGame;
    }

    @Override
    public View onCreateView(ViewGroup container) {
        mLayout = (DeviceListLayout) getLayoutInflater().inflate(R.layout.device_list, container, false);
        mLayout.setListener(this);
        Set<BluetoothDevice> bondedDevices = mBtAdapter.getBondedDevices();
        Ln.d(TAG + ": retrieved bonded devices: " + bondedDevices);
        mLayout.setBondedDevices(bondedDevices);

        Ln.d(this + " screen created");
        return mLayout;
    }

    @Override
    public void onStart() {
        super.onStart();
        Ln.d(TAG + ": register BT broadcast receiver");
        getActivity().registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        getActivity().registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        getActivity().registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED));

        Ln.d(TAG + ": start listening for incoming connections");
        if (mGame.isAccepting()) {
            Ln.e("should have been accepting");
            ACRA.getErrorReporter().handleException(new RuntimeException("should have been accepting"));
        } else {
            mStopAcceptingOnStop = true;
            mGame.startAccepting(this);
        }

        updateEnsureDiscoverable(mBtAdapter.getScanMode());
    }

    @Override
    public void onStop() {
        super.onStop();
        mBtAdapter.cancelDiscovery();
        mLayout.cancelDiscovery();

        if (mStopAcceptingOnStop) {
            mGame.stopAccepting();
        }

        getActivity().unregisterReceiver(mReceiver);

        Ln.d(TAG + ": activity stopped, receivers are unregistered");
    }

    /**
     * The BroadcastReceiver that listens for discovered devices and changes the title when discovery is finished
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Ln.v(TAG + ": received broadcast: " + action);

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Ln.d(TAG + ": device is found, but already bound");
                } else {
                    Ln.d(TAG + ": new device is found: " + device);
                    mLayout.addBondedDevice(device);
                }
                // mLayout.setBondedDevices(mBtAdapter.getBondedDevices());
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Set<BluetoothDevice> bondedDevices = mBtAdapter.getBondedDevices();
                Ln.d(TAG + ": discovery finished, bonded devices: " + bondedDevices);
                mLayout.setBondedDevices(bondedDevices);
                mLayout.cancelDiscovery();
            } else if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {
                int scanMode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, 0);
                Ln.d(TAG + ": scan mode changed to " + scanMode);
                updateEnsureDiscoverable(scanMode);
            }
        }
    };

    private void updateEnsureDiscoverable(int scanMode) {
        if (scanMode == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Ln.d(TAG + ": scan mode CONNECTABLE_DISCOVERABLE - hiding 'show me' button");
            mLayout.hideEnsureDiscoverable();
        } else {
            Ln.d(TAG + ": scan mode " + scanMode + " - showing 'show me' button");
            mLayout.showEnsureDiscoverable();
        }
    }

    @Override
    public void ensureDiscoverable() {
        if (isDiscoverable()) {
            Ln.w(TAG + ": already discoverable");
        } else {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivityForResult(discoverableIntent, REQUEST_ENSURE);
        }
    }

    private boolean isDiscoverable() {
        return mBtAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE;
    }

    @Override
    public void doDiscover() {
        if (mBtAdapter.isDiscovering()) {
            Ln.w("already discovering");
            return;
        }

        Ln.d("starting discovery...");
        mBtAdapter.startDiscovery();
        mLayout.startDiscovery();
    }

    @Override
    public void selectDevice(String info) {
        Ln.d(TAG + ": selected device [" + info + "]");
        if (mGame.isConnecting()) {
            Ln.w("already connecting to device");
            return;
        }

        // Cancel discovery because it's costly and we're about to connect.
        // Always cancel discovery because it will slow down a connection
        mBtAdapter.cancelDiscovery();

        String address = BluetoothUtils.extractMacAddress(info);
        BluetoothDevice device = mBtAdapter.getRemoteDevice(address);

        mGame.cancelAcceptAndCloseConnection();

        mLayout.connectingTo(device.getName());
        mGame.connectToDevice(device, this);
    }

    @Override
    public void onConnectFailed() {
        Ln.d(TAG + ": connection attempt failed - start listening");
        mLayout.connectionFailed();
        mStopAcceptingOnStop = true;
        mGame.startAccepting(this);
    }

    @Override
    public void onAcceptFailed(IOException exception) {
        Ln.w(TAG + ": accept failed - exiting to select game");
        mGaTracker.send(new ExceptionEvent("bluetooth_accept_failed", exception).build());
        DialogUtils.newOkDialog(R.string.bluetooth_not_available, new BackToSelectGameCommand(mParent)).show(mFm, DIALOG);
    }

    @Override
    public void onConnected(MessageSender sender) {
        Ln.d(TAG + ": connected - creating opponent and showing board setup");
        BluetoothOpponent opponent = new BluetoothOpponent(sender);
        sender.setMessageListener(opponent);
        Model.instance.setOpponents(new PlayerOpponent(GameSettings.get().getPlayerName()), opponent);

        mStopAcceptingOnStop = mGame.isConnecting();
        mParent.setScreen(new BoardSetupScreen());
    }

    @Override
    public void onBackPressed() {
        mGame.stopConnecting();
        mParent.setScreen(new MainScreen());
    }

    @Override
    public String toString() {
        return "DEVICE_LIST" + debugSuffix();
    }
}
