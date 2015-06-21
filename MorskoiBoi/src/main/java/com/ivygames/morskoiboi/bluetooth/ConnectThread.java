package com.ivygames.morskoiboi.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;

import com.ivygames.morskoiboi.model.GameEvent;

import org.apache.commons.lang3.Validate;
import org.commons.logger.Ln;

import java.io.IOException;

import de.greenrobot.event.EventBus;

/**
 * This thread runs while attempting to make an outgoing connection with a device. It runs straight through; the connection either succeeds or fails.
 */
final class ConnectThread extends Thread {
	private volatile BluetoothSocket mSocket;
	private volatile boolean mCancelled;

	private final BluetoothDevice mDevice;
	private final ConnectionListener mConnectionListener;
	private final Handler mHandler = new Handler(Looper.myLooper());

	ConnectThread(BluetoothDevice device, ConnectionListener connectionListener) {
		super("bt_connect");
		Validate.notNull(device, "deveice cannot be null");
		mDevice = device;

		Validate.notNull(connectionListener, "connectionListener cannot be null");
		mConnectionListener = connectionListener;
	}

	@Override
	public void run() {
		Ln.v("connecting to " + mDevice);
		mSocket = obtainSocket(mDevice);
		if (mSocket == null) {
			if (mCancelled) {
				Ln.v("cancelled while connecting");
			} else {
				Ln.d("failed to obtain socket");
				mHandler.post(new ConnectFailedCommand(mConnectionListener));
			}
			return;
		}

		Ln.d("socket connected - starting transmission");
		MessageReceiver mConnection = new MessageReceiver(mSocket, mHandler);

		try {
			mConnection.connect();

			// we post connected event after connection object is created
			mHandler.post(new ConnectedCommand(mConnectionListener, mConnection));
			mConnection.startReceiving();
		} catch (IOException ioe) {
			if (mCancelled) {
				Ln.d("cancelled whle connected");
			} else {
				Ln.d("connection lost: " + ioe.getMessage());
				EventBus.getDefault().postSticky(GameEvent.CONNECTION_LOST);
			}
		} finally {
			BluetoothUtils.close(mSocket);
		}
	}

	private BluetoothSocket obtainSocket(BluetoothDevice device) {
		BluetoothSocket socket = null;
		try {
			// get a BluetoothSocket for a connection with the given BluetoothDevice
			socket = device.createRfcommSocketToServiceRecord(BluetoothGame.MY_UUID);

			Ln.v("socket created - connecting...");
			// This is a blocking call and will only return on a
			// successful connection or an exception
			socket.connect();
			return socket;
		} catch (IOException ioe) {
			if (!mCancelled) {
				Ln.w(ioe);
			}
			BluetoothUtils.close(socket);
			return null;
		}
	}

	void cancel() {
		Ln.v("cancelling...");
		mCancelled = true;
		BluetoothUtils.close(mSocket);
	}

}