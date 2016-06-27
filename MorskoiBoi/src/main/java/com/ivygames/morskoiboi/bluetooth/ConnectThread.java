package com.ivygames.morskoiboi.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.model.GameEvent;

import org.commons.logger.Ln;

import java.io.IOException;

import de.greenrobot.event.EventBus;

/**
 * This thread runs while attempting to make an outgoing connection with a device. It runs straight through; the connection either succeeds or fails.
 */
public final class ConnectThread extends Thread {
    private volatile BluetoothSocket mSocket;
    private volatile boolean mCancelled;

    @NonNull
    private final BluetoothDevice mDevice;
    @NonNull
    private final ConnectionListener mConnectionListener;
    @NonNull
    private final Handler mHandler = new Handler(Looper.myLooper());

    public ConnectThread(@NonNull BluetoothDevice device,
                         @NonNull ConnectionListener connectionListener) {
        super("bt_connect");
        mDevice = device;
        mConnectionListener = connectionListener;
    }

    @Override
    public void run() {
        Ln.v("connecting to " + mDevice);
        try {
            mSocket = obtainSocket(mDevice);
        } catch (final IOException ioe) {
            if (mCancelled) {
                Ln.v("cancelled while connecting");
            } else {
                Ln.d(ioe, "failed to obtain socket");
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mConnectionListener.onConnectFailed();
                    }
                });
            }
            BluetoothUtils.close(mSocket);
            return;
        }

        Ln.d("socket connected - starting transmission");
        try {
            final BluetoothConnectionImpl connection = new BluetoothConnectionImpl(mSocket, mHandler);
            connection.connect();

            // we post connected event after connection object is created
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mConnectionListener.onConnected(connection);
                }
            });
            connection.startReceiving();
        } catch (IOException ioe) {
            if (mCancelled) {
                Ln.d("cancelled while connected");
            } else {
                Ln.d("connection lost: " + ioe.getMessage());
                EventBus.getDefault().postSticky(GameEvent.CONNECTION_LOST);
            }
        } finally {
            BluetoothUtils.close(mSocket);
        }
    }

    private BluetoothSocket obtainSocket(BluetoothDevice device) throws IOException {
        // get a BluetoothSocket for a connection with the given BluetoothDevice
        BluetoothSocket socket = device.createRfcommSocketToServiceRecord(BluetoothGame.MY_UUID);

        Ln.v("socket created - connecting...");
        // This is a blocking call and will only return on a
        // successful connection or an exception
        socket.connect();
        return socket;
    }

    public void cancel() {
        Ln.v("cancelling...");
        mCancelled = true;
        interrupt();
        BluetoothUtils.close(mSocket);
    }

}