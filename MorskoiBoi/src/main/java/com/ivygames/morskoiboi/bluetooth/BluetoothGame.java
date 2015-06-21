// FIXME: support android 4.2
package com.ivygames.morskoiboi.bluetooth;

import android.bluetooth.BluetoothDevice;

import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.GameEvent;

import org.commons.logger.Ln;

import java.io.IOException;
import java.util.UUID;

import de.greenrobot.event.EventBus;

public class BluetoothGame extends Game implements ConnectionListener {
	// private static final String TAG = "bluetooth";
	public static final int WIN_PROGRESS_POINTS = 5000;

	private static final int TURN_TIMEOUT = 40 * 1000;

	// Unique UUID for this application
	static final UUID MY_UUID = UUID.fromString("9ecd276e-c044-43ea-969e-2ed67fc9f633");

	// Member fields
	private volatile ConnectionListener mConnectionListener;
	private AcceptThread mAcceptThread;
	private ConnectThread mConnectThread;

	private boolean mConnected;

	public BluetoothGame() {
		super();
		EventBus.getDefault().removeAllStickyEvents();
		EventBus.getDefault().register(this);
		Ln.v("new bluetooth game created");
	}

	@Override
	public void finish() {
		if (hasFinished()) {
			Ln.w(getType() + " already finished");
			return;
		}

		super.finish();
		EventBus.getDefault().removeAllStickyEvents();
		EventBus.getDefault().unregister(this);
		cancelAcceptAndCloseConnection();
		stopConnecting();
		Ln.v("game finished");
	}

	@Override
	public Type getType() {
		return Type.BLUETOOTH;
	}

	/**
	 * Start AcceptThread to begin a session in listening (server) mode. Called by the Activity onResume()
	 */
	public synchronized void startAccepting(ConnectionListener listener) {
		Ln.d("starting listening to new connections");
		mConnectionListener = listener;

		stopConnecting();

		// Start the thread to listen on a BluetoothServerSocket
		if (mAcceptThread == null) {
			mAcceptThread = new AcceptThread(this);
			mAcceptThread.start();
		} else {
			Ln.w("already accepting");
		}
	}

	/**
	 * Cancel any thread attempting to make a connection
	 */
	public synchronized void stopConnecting() {
		if (mConnectThread == null) {
			return;
		}

		Ln.d("canceling current connection attempt...");
		mConnectThread.cancel();
		join(mConnectThread);
		mConnectThread = null;
		Ln.d("connection cancelled");
	}

	public boolean isConnecting() {
		return mConnectThread != null;
	}

	private void join(Thread t) {
		try {
			t.join();
		} catch (InterruptedException ie) {
			Ln.d("interrupted while joining");
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Start the ConnectThread to initiate a connection to a remote device.
	 *
	 * @param device
	 *            The BluetoothDevice to connect
	 */
	public synchronized void connectToDevice(BluetoothDevice device, ConnectionListener listener) {
		stopConnecting();
		Ln.d("connecting to: " + device);
		mConnectionListener = listener;

		mConnectThread = new ConnectThread(device, this);
		mConnectThread.start();
	}

	/**
	 * Cancel the accept thread because we only want to connect to one device
	 */
	public synchronized void stopAccepting() {
		if (mAcceptThread == null) {
			Ln.w("not accepting - cannot cancel");
			return;
		}

		mAcceptThread.cancelAccept();
	}

	public synchronized void cancelAcceptAndCloseConnection() {
		if (mAcceptThread == null) {
			Ln.w("not accepting - cannot close");
			return;
		}

		mAcceptThread.cancelAcceptAndCloseConnection();
		join(mAcceptThread);
		mAcceptThread = null;
	}

	@Override
	public void onConnected(MessageSender sender) {
		mConnected = true;
		mConnectionListener.onConnected(sender);
	}

	@Override
	public void onConnectFailed() {
		join(mConnectThread);
		mConnectThread = null;
		mConnectionListener.onConnectFailed();
	}

	@Override
	public void onAcceptFailed(IOException exception) {
		join(mAcceptThread);
		mAcceptThread = null;
		mConnectionListener.onAcceptFailed(exception);
	}

	public void onEventMainThread(GameEvent event) {
		if (event == GameEvent.CONNECTION_LOST) {
			mConnected = false;
		}
	}

	@Override
	public int getTurnTimeout() {
		return TURN_TIMEOUT;
	}

	public boolean isConnected() {
		return mConnected;
	}

	public boolean isAccepting() {
		return mAcceptThread != null;
	}

	interface MessageListener {
		void onMessageReceived(String message);
	}

}
