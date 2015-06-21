package com.ivygames.morskoiboi.bluetooth;

import org.apache.commons.lang3.Validate;

import java.io.IOException;

final class AcceptFailedCommand implements Runnable {

	private final ConnectionListener mConnnectionListener;
	private final IOException mException;

	AcceptFailedCommand(ConnectionListener listener, IOException exception) {
		mException = Validate.notNull(exception);
		mConnnectionListener = Validate.notNull(listener);
	}

	@Override
	public void run() {
		mConnnectionListener.onAcceptFailed(mException);
	}
}
