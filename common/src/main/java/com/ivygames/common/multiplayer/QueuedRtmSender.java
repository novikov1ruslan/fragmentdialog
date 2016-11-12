package com.ivygames.common.multiplayer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMultiplayer;
import com.ivygames.common.DebugUtils;
import com.ivygames.common.googleapi.ApiClient;

import org.commons.logger.Ln;

import java.util.LinkedList;
import java.util.Queue;

import static com.ivygames.common.analytics.ExceptionHandler.reportException;

public class QueuedRtmSender implements RtmSender {
    @NonNull
    private final Queue<String> mMessages = new LinkedList<>();

    private boolean mIsSending;

    private int mLastSentToken;

    @NonNull
    private final ApiClient mApiClient;
    @Nullable
    private String mRoomId;
    @Nullable
    private String mRecipientId;

    private boolean mStopped;

    @NonNull
    private final RealTimeMultiplayer.ReliableMessageSentCallback mSendCallback =
            new RealTimeMultiplayer.ReliableMessageSentCallback() {
                @Override
                public void onRealTimeMessageSent(int statusCode, int tokenId, String recipientId) {
                    Ln.v(tokenId + " message sent, status=" + getStatusString(statusCode));
                    mIsSending = false;
                    if (tokenId != mLastSentToken) {
                        reportException("last sent token is wrong");
                    }
                    assert mRoomId != null;
                    assert mRecipientId != null;

                    if (statusCode == GamesStatusCodes.STATUS_OK) {
                        mMessages.remove();
                        sendNextMessage(mRoomId, mRecipientId);
                    } else {
                        if (statusCode == GamesStatusCodes.STATUS_REAL_TIME_MESSAGE_SEND_FAILED) {
                            Ln.d("sending failed, retry");
                            sendNextMessage(mRoomId, mRecipientId);
                        } else {
                            Ln.e("could not send message: " + getStatusString(statusCode));
                        }
                    }
                }
            };

    private static String getStatusString(int statusCode) {
        if (statusCode == GamesStatusCodes.STATUS_MULTIPLAYER_ERROR_INVALID_OPERATION) {
            return "STATUS_MULTIPLAYER_ERROR_INVALID_OPERATION";
        }
        return GamesStatusCodes.getStatusString(statusCode);
    }

    public QueuedRtmSender(@NonNull ApiClient apiClient) {
        mApiClient = apiClient;
    }

    /**
     * After the sender is stopped it will not send any message
     */
    public void stop() {
        mStopped = true;
        Ln.d("sender stopped");
    }

    public void setRoom(@NonNull String roomId, @NonNull String recipientId) {
        mRoomId = roomId;
        mRecipientId = recipientId;
        Ln.v("set room=" + mRoomId + ", recipient=" + recipientId);
    }

    @Override
    public void sendRtm(@NonNull String message) {
        mMessages.offer(message);
        if (mIsSending) {
            Ln.d("already sending... skipping [" + message + "], q= " + mMessages.size());
        } else if (mRoomId != null && mRecipientId != null) {
            sendMessage(mMessages.peek(), mRoomId, mRecipientId);
        } else {
            Ln.d("queued: [" + message + "], q=" + mMessages.size());
        }
    }

    public void sendNextMessage() {
        if (mRoomId == null || mRecipientId == null) {
            throw  new IllegalStateException("room-" + mRoomId + "-recipient-" + mRecipientId);
        }
        sendNextMessage(mRoomId, mRecipientId);
    }

    private void sendNextMessage(@NonNull String roomId, @NonNull String recipientId) {
        if (mStopped) {
            Ln.w("wrong usage of stopped sender");
            return;
        }
        if (!mMessages.isEmpty()) {
            sendMessage(mMessages.peek(), roomId, recipientId);
        }
    }

    private void sendMessage(@NonNull String message, @NonNull String roomId, String recipientId) {
        mIsSending = true;
        byte[] messageData = message.getBytes();
        /*
         * The API uses weak references to listeners, so it often happens that these listeners will get garbage-collected before they are called.
         * Please try again using a non-anonymous listener, that is, a listener that you hold a reference to. (Bruno Oliveira)
         */
        if (mStopped) {
            Ln.d("sender stopped - not sending");
        } else {
            mLastSentToken = mApiClient.sendReliableMessage(mSendCallback, messageData, roomId, recipientId);
            Ln.v(mLastSentToken + " sending: [" + message + "], to " + recipientId + ", " + messageData.length + " bytes, messages left = " + mMessages.size());
        }
    }

    @Override
    public String toString() {
        return DebugUtils.getSimpleName(this);
    }

}
