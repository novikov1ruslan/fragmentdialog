package com.ivygames.morskoiboi.bluetooth;

import com.ivygames.morskoiboi.AbstractOnlineOpponent;
import com.ivygames.morskoiboi.bluetooth.BluetoothGame.MessageListener;
import com.ivygames.morskoiboi.model.Opponent;

import org.apache.commons.lang3.Validate;
import org.commons.logger.Ln;

public class BluetoothOpponent extends AbstractOnlineOpponent implements MessageListener {

	private final MessageSender mSender;

	public BluetoothOpponent(MessageSender sender) {
		super();
		Validate.notNull(sender);
		mSender = sender;
		Ln.v("new bluetooth opponent created");
	}

	@Override
	public void sendRtm(String message) {
		Ln.v("sending: [" + message + "]");
		message = message + '|';
		mSender.write(message);
	}

	@Override
	public void setOpponent(Opponent opponent) {
		mOpponent = opponent;
		String message = NAME + mOpponent.getName();
		sendRtm(message);
	}

	@Override
	public void onMessageReceived(String message) {
		onRealTimeMessageReceived(message);
		Ln.v("received [" + message + "] from [" + getName() + "]");
	}

}
