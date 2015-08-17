package com.ivygames.morskoiboi.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import org.apache.commons.lang3.Validate;
import org.commons.logger.Ln;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This thread runs during a connection with a remote device. It handles all incoming and outgoing transmissions.
 */
class BluetoothConnectionImpl implements BluetoothConnection {
    private InputStream mInStream;
    private OutputStream mOutStream;

    private final BluetoothSocket mSocket;
    private final Handler mHandler;
    private volatile MessageReceiver mMessageListener;

    BluetoothConnectionImpl(BluetoothSocket socket, Handler handler) {
        mSocket = Validate.notNull(socket);
        mHandler = Validate.notNull(handler);
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
            final String message = readMessage();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mMessageListener.onMessageReceived(message);
                }
            });
        }

        BluetoothUtils.close(mSocket);
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
        StringBuilder builder = new StringBuilder();
        while (true) {
            int c = mInStream.read();
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

    @Override
    public void setMessageListener(MessageReceiver listener) {
        mMessageListener = listener;
    }

    @Override
    public void disconnect() {
        BluetoothUtils.close(mSocket);
    }
}
