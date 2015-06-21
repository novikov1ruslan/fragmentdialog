package com.ivygames.morskoiboi.rt;

import android.text.TextUtils;

import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.ivygames.morskoiboi.AbstractOnlineOpponent;
import com.ivygames.morskoiboi.RtmSender;
import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.model.PokeResult;

import org.apache.commons.lang3.Validate;
import org.commons.logger.Ln;

public class InternetOpponent extends AbstractOnlineOpponent implements RealTimeMessageReceivedListener {

	// private String mRomId;
	// private String mRecipientParticipantId;
	// private ReliableMessageSentCallback mReliableMessageSentCallback;
	private final RtmSender mRtmSender;

	public InternetOpponent(RtmSender rtmSender) {
		Validate.notNull(rtmSender);
		mRtmSender = rtmSender;
		Ln.v("new internet opponent created");
	}

	// public void setRoom(String roomId) {
	// Ln.v("room=" + roomId);
	// mRomId = roomId;
	// if (mRecipientParticipantId != null && mOpponent != null) {
	// sendOpponentName();
	// }
	// }

	// public void setReliableMessageSentCallback(ReliableMessageSentCallback callback) {
	// mReliableMessageSentCallback = callback;
	// Ln.v("Reliable Message Sent Callback set to " + callback);
	// }

	// public void setParticipantId(String recipientParticipantId) {
	// Ln.v("recipient=" + recipientParticipantId);
	// mRecipientParticipantId = recipientParticipantId;
	// if (mRomId != null && mOpponent != null) {
	// sendOpponentName();
	// }
	// }

	@Override
	public void onRealTimeMessageReceived(RealTimeMessage rtm) {
		byte[] buf = rtm.getMessageData();
		String sender = rtm.getSenderParticipantId();
		String message = new String(buf);
		Ln.v("received [" + message + "] from [" + sender + "], " + buf.length + "bytes");
		onRealTimeMessageReceived(message);
	}

	@Override
	public void onShotResult(PokeResult pokeResult) {
		sendRtm("R" + pokeResult.toJson().toString());
	}

	@Override
	public void sendRtm(String message) {
		mRtmSender.sendRtm(message);
	}

	@Override
	public void setOpponent(Opponent opponent) {
		mOpponent = opponent;
		sendOpponentName();
	}

	private void sendOpponentName() {
		String name = mOpponent.getName();
		if (TextUtils.isEmpty(name)) {
			name = "Player"; // TODO: think about better solution
		}
		sendRtm(NAME + name);
	}

	// @Override
	// public void onRealTimeMessageSent(int statusCode, int tokenId, String recipientParticipantId) {
	// Ln.v("status=" + statusCode + ", token=" + tokenId + ", recipient=" + recipientParticipantId);
	// if (mReliableMessageSentCallback != null) {
	// mReliableMessageSentCallback.onRealTimeMessageSent(statusCode, tokenId, recipientParticipantId);
	// }
	// }

}
