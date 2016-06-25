package com.ivygames.morskoiboi.rt;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.ivygames.morskoiboi.multiplayer.AbstractOnlineOpponent;
import com.ivygames.morskoiboi.multiplayer.RtmSender;
import com.ivygames.morskoiboi.model.Opponent;

import org.apache.commons.lang3.Validate;
import org.commons.logger.Ln;

public class InternetOpponent extends AbstractOnlineOpponent implements RealTimeMessageReceivedListener {

    @NonNull
    private final RtmSender mRtmSender;

    public InternetOpponent(@NonNull RtmSender rtmSender, @NonNull String defaultName) {
        super(defaultName);
        mRtmSender = Validate.notNull(rtmSender);
        Ln.v("new internet opponent created");
    }

    @Override
    public void onRealTimeMessageReceived(RealTimeMessage rtm) {
        byte[] buf = rtm.getMessageData();
        String sender = rtm.getSenderParticipantId();
        String message = new String(buf);
        Ln.v("received [" + message + "] from [" + sender + "], " + buf.length + "bytes");
        onRealTimeMessageReceived(message);
    }

    @Override
    public void sendRtm(@NonNull String message) {
        mRtmSender.sendRtm(message);
    }

    @Override
    public void setOpponent(@NonNull Opponent opponent) {
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

}
