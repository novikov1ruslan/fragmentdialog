package com.ivygames.morskoiboi.bluetooth;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.ivygames.common.multiplayer.MultiplayerEvent;

import org.commons.logger.Ln;

import java.io.IOException;

import de.greenrobot.event.EventBus;

/**
 * This thread runs while listening for incoming connections. It behaves like a server-side client. It runs until a connection is accepted (or until cancelled).
 */
public final class AcceptThread extends Thread {
    // Name for the SDP record when creating server socket
    private static final String NAME = "BtGameManager";

    private volatile BluetoothServerSocket mServerSocket;
    private volatile BluetoothSocket mSocket;
    private volatile boolean mCancelled;

    @NonNull
    private final ConnectionListener mConnectionListener;

    @NonNull
    private final Handler mHandler = new Handler(Looper.myLooper());

    @NonNull
    private final BluetoothAdapterWrapper mAdapter;

    public AcceptThread(@NonNull ConnectionListener listener,
                        @NonNull BluetoothAdapterWrapper adapter) {
        super("bt_accept");
        mConnectionListener = listener;
        mAdapter = adapter;
    }

    @Override
    public void run() {
        Ln.v("obtaining transmission socket...");
        try {
            mSocket = obtainBluetoothSocket();
        } catch (final IOException ioe) {
            if (mCancelled) {
                Ln.v("cancelled while accepting");
            } else {
                // timeout?
                Ln.w("failed to obtain socket");
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mConnectionListener.onConnectFailed();
                    }
                });
            }
            return;
        }

        Ln.v("connection accepted - starting transmission");
        try {
            final BluetoothConnectionImpl mConnection = new BluetoothConnectionImpl(mSocket, mHandler);
            mConnection.connect();

            // we post connected event after connection object is created
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mConnectionListener.onConnected(mConnection);
                }
            });
            mConnection.startReceiving();
        } catch (IOException ioe) {
            if (mCancelled) {
                Ln.v("cancelled while connected");
            } else {
                Ln.w(ioe);
                EventBus.getDefault().postSticky(MultiplayerEvent.CONNECTION_LOST);
            }
        } finally {
            BluetoothUtils.close(mSocket);
        }
    }

    private BluetoothSocket obtainBluetoothSocket() throws IOException {
        try {
            mServerSocket = mAdapter.listenUsingRfcommWithServiceRecord(NAME, BluetoothGame.MY_UUID);
            Ln.v("server socket created, accepting connection...");
            // This is a blocking call and will only return on a successful connection or an exception
            return mServerSocket.accept();
        } finally {
            BluetoothUtils.close(mServerSocket);
        }
    }

    public void cancelAccept() {
        Ln.v("canceling accept...");
        mCancelled = true;
        BluetoothUtils.close(mServerSocket);
        if (mSocket != null) {
            Ln.v("closing accepted connection...");
            interrupt();
            BluetoothUtils.close(mSocket);
        }
    }

}
