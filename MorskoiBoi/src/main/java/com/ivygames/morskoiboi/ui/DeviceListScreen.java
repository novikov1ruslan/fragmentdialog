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
import com.ivygames.morskoiboi.bluetooth.BluetoothConnection;
import com.ivygames.morskoiboi.bluetooth.BluetoothGame;
import com.ivygames.morskoiboi.bluetooth.BluetoothOpponent;
import com.ivygames.morskoiboi.bluetooth.BluetoothUtils;
import com.ivygames.morskoiboi.bluetooth.ConnectThread;
import com.ivygames.morskoiboi.bluetooth.ConnectionListener;
import com.ivygames.morskoiboi.model.Model;
import com.ivygames.morskoiboi.ui.BattleshipActivity.BackPressListener;
import com.ivygames.morskoiboi.ui.view.DeviceListLayout;
import com.ivygames.morskoiboi.ui.view.DeviceListLayout.DeviceListActions;

import org.commons.logger.Ln;

import java.io.IOException;
import java.util.Set;

public class DeviceListScreen extends BattleshipScreen implements DeviceListActions, ConnectionListener, BackPressListener {
    private static final String TAG = "bluetooth";

    private DeviceListLayout mLayout;
    private final BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();

    private ConnectThread mConnectThread;

    @Override
    public View getView() {
        return mLayout;
    }

    @Override
    public View onCreateView(ViewGroup container) {
        mLayout = (DeviceListLayout) inflate(R.layout.device_list, container);
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
    }

    @Override
    public void onStop() {
        super.onStop();
        mBtAdapter.cancelDiscovery();
        mLayout.cancelDiscovery();

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
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BattleshipActivity.RC_ENSURE_DISCOVERABLE) {
            Ln.v("discoverable result=" + resultCode);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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
        if (isConnecting()) {
            Ln.w("already connecting to device");
            return;
        }

        // Cancel discovery because it's costly and we're about to connect.
        // Always cancel discovery because it will slow down a connection
        mBtAdapter.cancelDiscovery();

        BluetoothDevice device = mBtAdapter.getRemoteDevice(BluetoothUtils.extractMacAddress(info));
        mLayout.connectingTo(device.getName());
        connectToDevice(device);
    }

    public boolean isConnecting() {
        return mConnectThread != null;
    }

    @Override
    public void onConnectFailed(IOException exception) {
        Ln.d(TAG + ": connection attempt failed - start listening");
        mLayout.connectionFailed();
    }

    @Override
    public void onConnected(BluetoothConnection connection) {
        Ln.d(TAG + ": connected - creating opponent and showing board setup");
        BluetoothOpponent opponent = new BluetoothOpponent(connection);
        connection.setMessageListener(opponent);
        Model.instance.setOpponents(new PlayerOpponent(GameSettings.get().getPlayerName()), opponent);
        Model.instance.game = new BluetoothGame(connection);

        mParent.setScreen(new BoardSetupScreen());
    }

    @Override
    public void onBackPressed() {
        stopConnecting();
        mParent.setScreen(new MainScreen());
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
    private synchronized void connectToDevice(BluetoothDevice device) {
        stopConnecting();
        Ln.d("connecting to: " + device);
        mConnectThread = new ConnectThread(device, this);
        mConnectThread.start();
    }

    @Override
    public String toString() {
        return "DEVICE_LIST" + debugSuffix();
    }
}
