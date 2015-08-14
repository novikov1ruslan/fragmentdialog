package com.ivygames.morskoiboi.rt;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.ivygames.morskoiboi.AbstractOnlineOpponent;
import com.ivygames.morskoiboi.RtmSender;
import com.ivygames.morskoiboi.model.Opponent;

import org.apache.commons.lang3.Validate;
import org.commons.logger.Ln;

public class InternetOpponent extends AbstractOnlineOpponent implements RealTimeMessageReceivedListener {
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private final RtmSender mRtmSender;

    public InternetOpponent(RtmSender rtmSender) {
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

}
