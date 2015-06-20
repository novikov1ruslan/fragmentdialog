package com.ivygames.morskoiboi.bluetooth;

import org.apache.commons.lang3.Validate;

final class ConnectFailedCommand implements Runnable {

	private final ConnectionListener mConnnectionListener;

	ConnectFailedCommand(ConnectionListener listener) {
		Validate.notNull(listener, "listener cannot be null");
		mConnnectionListener = listener;
	}

	@Override
	public void run() {
		mConnnectionListener.onConnectFailed();
	}
}
