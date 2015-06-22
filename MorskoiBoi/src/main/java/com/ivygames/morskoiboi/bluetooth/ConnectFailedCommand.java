package com.ivygames.morskoiboi.bluetooth;

import org.apache.commons.lang3.Validate;

final class ConnectFailedCommand implements Runnable {

	private final ConnectionListener mConnectionListener;

	ConnectFailedCommand(ConnectionListener listener) {
		Validate.notNull(listener, "listener cannot be null");
		mConnectionListener = listener;
	}

	@Override
	public void run() {
		mConnectionListener.onConnectFailed();
	}
}
