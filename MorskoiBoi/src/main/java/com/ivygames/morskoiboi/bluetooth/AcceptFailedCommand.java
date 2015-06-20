package com.ivygames.morskoiboi.bluetooth;

import java.io.IOException;

import org.apache.commons.lang3.Validate;

final class AcceptFailedCommand implements Runnable {

	private final ConnectionListener mConnnectionListener;
	private final IOException mException;

	AcceptFailedCommand(ConnectionListener listener, IOException exception) {
		Validate.notNull(exception);
		mException = exception;

		Validate.notNull(listener);
		mConnnectionListener = listener;
	}

	@Override
	public void run() {
		mConnnectionListener.onAcceptFailed(mException);
	}
}
