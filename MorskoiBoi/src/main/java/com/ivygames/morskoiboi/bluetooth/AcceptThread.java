package com.ivygames.morskoiboi.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;

import com.ivygames.morskoiboi.model.GameEvent;

import org.apache.commons.lang3.Validate;
import org.commons.logger.Ln;

import java.io.Closeable;
import java.io.IOException;

import de.greenrobot.event.EventBus;

/**
 * This thread runs while listening for incoming connections. It behaves like a server-side client. It runs until a connection is accepted (or until cancelled).
 */
final class AcceptThread extends Thread {
	// Name for the SDP record when creating server socket
	private static final String NAME = "BtGameManager";

	private volatile BluetoothServerSocket mServerSocket;
	private volatile BluetoothSocket mSocket;
	private volatile boolean mCancelled;

	private final ConnectionListener mConnectionListener;
	private final Handler mHandler = new Handler(Looper.myLooper());

	AcceptThread(ConnectionListener connnectionListener) {
		super("bt_accept");
		Validate.notNull(connnectionListener, "connnectionListener cannot be null");
		mConnectionListener = connnectionListener;
	}

	@Override
	public void run() {
		Ln.v("obtaining transmission socket...");
		mSocket = obtainBluetoothSocket(BluetoothAdapter.getDefaultAdapter());
		if (mSocket == null) {
			if (mCancelled) {
				Ln.v("cancelled while accepting");
			} else {
				Ln.w("failed to obtain socket");
			}
			return;
		}

		Ln.v("connection accepted - starting transmission");
		MessageReceiver mConnection = new MessageReceiver(mSocket, mHandler);

		try {
			mConnection.connect();

			// we post connected event after connection object is created
			mHandler.post(new ConnectedCommand(mConnectionListener, mConnection));
			mConnection.startReceiving();
		} catch (IOException ioe) {
			if (mCancelled) {
				Ln.v("cancelled while connected");
			} else {
				Ln.w(ioe);
				EventBus.getDefault().postSticky(GameEvent.CONNECTION_LOST);
			}
		} finally {
			BluetoothUtils.close(mSocket);
		}
	}

	private BluetoothSocket obtainBluetoothSocket(BluetoothAdapter bluetoothAdapter) {
		BluetoothSocket socket = null;
		try {
			mServerSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, BluetoothGame.MY_UUID);
			Ln.v("server socket created, accepting connection...");
			// This is a blocking call and will only return on a successful connection or an exception
			socket = mServerSocket.accept();
		} catch (IOException ioe) {
			if (!mCancelled) {
				// timeout?
				Ln.w(ioe);
				mHandler.post(new AcceptFailedCommand(mConnectionListener, ioe));
			}
		} finally {
			BluetoothUtils.close(mServerSocket);
		}

		return socket;
	}

	public void cancelAccept() {
		Ln.v("canceling accept...");
		mCancelled = true;
		BluetoothUtils.close(mServerSocket);
	}

	public void cancelAcceptAndCloseConnection() {
		cancelAccept();
		if (mSocket != null) {
			Ln.v("closing accepted connection...");
			interrupt();
			BluetoothUtils.close(mSocket);
		}
	}

}
