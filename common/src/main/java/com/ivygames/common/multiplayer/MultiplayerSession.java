package com.ivygames.common.multiplayer;

import android.support.annotation.NonNull;

import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;

public class MultiplayerSession {
    @NonNull
    private final RoomConnectionErrorListener mRoomConnectionErrorListener;

    public QueuedRtmSender rtmSender;
    public RealTimeMessageReceivedListener rtmListener;

    public MultiplayerSession(@NonNull RoomConnectionErrorListener listener) {
        mRoomConnectionErrorListener = listener;
    }

    public void setRtmSender(QueuedRtmSender rtmSender) {
        this.rtmSender = rtmSender;
    }

    public void setRtmListener(RealTimeMessageReceivedListener rtmListener) {
        this.rtmListener = rtmListener;
    }

    public void onRoomConnectionError(int statusCode) {
        mRoomConnectionErrorListener.onError(statusCode);
    }

}
