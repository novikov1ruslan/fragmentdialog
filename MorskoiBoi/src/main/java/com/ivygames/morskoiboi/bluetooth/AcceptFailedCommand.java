package com.ivygames.morskoiboi.bluetooth;

import org.apache.commons.lang3.Validate;

import java.io.IOException;

final class AcceptFailedCommand implements Runnable {

	private final ConnectionListener mConnectionListener;
	private final IOException mException;

	AcceptFailedCommand(ConnectionListener listener, IOException exception) {
		mException = Validate.notNull(exception);
		mConnectionListener = Validate.notNull(listener);
	}

	@Override
	public void run() {
		mConnectionListener.onAcceptFailed(mException);
	}
}
