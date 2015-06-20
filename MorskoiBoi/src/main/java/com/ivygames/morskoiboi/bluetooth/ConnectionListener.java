package com.ivygames.morskoiboi.bluetooth;

import java.io.IOException;

public interface ConnectionListener {

	void onConnected(MessageSender sender);

	/**
	 * connection attempt failed
	 */
	void onConnectFailed();

	/**
	 * accept attempt failed
	 *
	 * @param exception
	 */
	void onAcceptFailed(IOException exception);
}