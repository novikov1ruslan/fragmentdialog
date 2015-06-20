package com.ivygames.morskoiboi.bluetooth;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.lang3.Validate;
import org.commons.logger.Ln;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import com.ivygames.morskoiboi.bluetooth.BluetoothGame.MessageListener;

/**
 * This thread runs during a connection with a remote device. It handles all incoming and outgoing transmissions.
 */
class MessageReceiver implements MessageSender {
	private InputStream mInStream;
	private OutputStream mOutStream;

	private final BluetoothSocket mSocket;
	private final Handler mHandler;
	private volatile MessageListener mMessageListener;

	MessageReceiver(BluetoothSocket socket, Handler handler) {
		Validate.notNull(socket, "socket cannot be null");
		mSocket = socket;

		Validate.notNull(handler, "handler cannot be null");
		mHandler = handler;
	}

	void connect() throws IOException {
		// get the BluetoothSocket input and output streams
		mInStream = mSocket.getInputStream();
		mOutStream = mSocket.getOutputStream();
		// The input stream will be returned even if the socket is not
		// yet connected, but operations on that
		// stream will throw IOException until the associated socket is
		// connected.
	}

	void startReceiving() throws IOException {
		waitForMessageReceiver();
		while (!Thread.currentThread().isInterrupted()) {
			String message = readMessage();
			mHandler.post(new MessageReceivedCommand(mMessageListener, message));
		}

		close(mSocket);
	}

	private void waitForMessageReceiver() {
		while (mMessageListener == null && !Thread.currentThread().isInterrupted()) {
			Ln.v("busy wait"); // TODO: replace by lazy wait
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	private String readMessage() throws IOException {
		int c = -1;
		StringBuilder builder = new StringBuilder();
		while (true) {
			c = mInStream.read();
			if (c == -1) {
				throw new IOException("-1 is read");
			}
			if (c == '|') {
				break;
			}
			builder.append((char) c);
		}

		return builder.toString();
	}

	/**
	 * Write to the connected OutStream.
	 *
	 * @throws IOException
	 */
	@Override
	public void write(String message) {
		byte[] buffer = message.getBytes();
		Ln.v("writing " + buffer.length + " bytes");
		try {
			mOutStream.write(buffer);
		} catch (IOException ioe) {
			Ln.w(ioe);
		}
	}

	private void close(Closeable closable) {
		if (closable != null) {
			try {
				closable.close();
			} catch (IOException e) {
			}
		}
	}

	@Override
	public void setMessageListener(MessageListener listener) {
		mMessageListener = listener;
	}
}
