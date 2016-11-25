package com.ivygames.morskoiboi.rt;

import android.support.annotation.NonNull;

import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.ivygames.common.multiplayer.RtmSender;
import com.ivygames.morskoiboi.multiplayer.AbstractOnlineOpponent;

import org.commons.logger.Ln;

public class InternetOpponent extends AbstractOnlineOpponent implements RealTimeMessageReceivedListener {

    @NonNull
    private final RtmSender mRtmSender;

    public InternetOpponent(@NonNull RtmSender rtmSender, @NonNull String name) {
        super(name);
        mRtmSender = rtmSender;
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
    public void send(@NonNull String message) {
        mRtmSender.sendRtm(message);
    }

}
