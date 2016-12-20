package com.ivygames.morskoiboi.bluetooth.peer;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.bluetooth.BluetoothAdapterWrapper;

import org.commons.logger.Ln;

import java.util.Set;
import java.util.UUID;

public class BluetoothPeer {

    @NonNull
    private final BluetoothAdapterWrapper mBtAdapter;
    @NonNull
    private final UUID mUuid;
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectionListener mConnectionListener;

    public BluetoothPeer(@NonNull BluetoothAdapterWrapper mBtAdapter, @NonNull UUID uuid) {
        this.mBtAdapter = mBtAdapter;
        mUuid = uuid;
    }

    public void setConnectionListener(@NonNull ConnectionListener listener) {
        mConnectionListener = listener;
    }

    /**
     * Start AcceptThread to begin a session in listening (server) mode. Called by the Activity onResume()
     */
    public synchronized void startAccepting() {
        Ln.d("starting listening to new connections");

        // Start the thread to listen on a BluetoothServerSocket
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread(new ConnectionListener() {
                @Override
                public void onConnected(@NonNull BluetoothConnection connection) {
                    mConnectionListener.onConnected(connection);
                }

                @Override
                public void onConnectFailed() {
                    mConnectionListener.onConnectFailed();
                }
            }, mBtAdapter, mUuid);
            mAcceptThread.start();
        } else {
            Ln.e("already accepting");
        }
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

    public Set<BluetoothDevice> getBondedDevices() {
        return mBtAdapter.getBondedDevices();
    }

    public BluetoothDevice getRemoteDevice(@NonNull String address) {
        return mBtAdapter.getRemoteDevice(address);
    }

    public void cancelDiscovery() {
        mBtAdapter.cancelDiscovery();
    }

    public void startDiscovery() {
        mBtAdapter.startDiscovery();
    }

    public boolean isDiscoverable() {
        return mBtAdapter.isDiscoverable();
    }

    public boolean isConnecting() {
        return mConnectThread != null;
    }

    /**
     * Cancel any thread attempting to make a connection
     */
    public synchronized void stopConnecting() {
        if (!isConnecting()) {
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
    public void connectToDevice(BluetoothDevice device) {
        stopConnecting();
        Ln.d("connecting to: " + device);
        mConnectThread = new ConnectThread(device, mConnectionListener, mUuid);
        mConnectThread.start();
    }
}
