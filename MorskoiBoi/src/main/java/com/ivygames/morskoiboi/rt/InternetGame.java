package com.ivygames.morskoiboi.rt;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMultiplayer.ReliableMessageSentCallback;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.ivygames.morskoiboi.GoogleApiClientWrapper;
import com.ivygames.morskoiboi.RtmSender;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.GameEvent;

import org.acra.ACRA;
import org.apache.commons.lang3.Validate;
import org.commons.logger.Ln;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import de.greenrobot.event.EventBus;

/**
 * Opponent leaves: onDisconnectedFromRoom->onPeerLeft Network disconnected: onP2PDisconnected->onDisconnectedFromRoom->onPeersDisconnected
 */
public class InternetGame extends Game implements RoomStatusUpdateListener, RoomUpdateListener, ReliableMessageSentCallback, RtmSender {
    public static final int WIN_PROGRESS_POINTS = 10000;

    public static final int MIN_OPPONENTS = 1;
    public static final int MAX_OPPONENTS = 1;

    private static final int TURN_TIMEOUT = 40 * 1000;

    public interface InternetGameListener {

        void onWaitingForOpponent(Room room);

        void onError(int statusCode);
    }

    @NonNull
    private final GoogleApiClientWrapper mApiClient;
    private Room mRoom;
    private String mRecipientId;
    private InternetGameListener mGameListener;
    private RealTimeMessageReceivedListener mRtListener;

    private boolean mConnectionLostSent;
    @NonNull
    private final Queue<String> mMessages = new LinkedList<String>();

    private int mLastSentToken;

    private boolean mIsSending;

    private boolean mIsRoomConnected;

    // private final RoomLeaveListener mRoomLeaveListener = new RoomLeaveListener();

    public InternetGame(@NonNull GoogleApiClientWrapper apiClient, @NonNull InternetGameListener listener) {
        super();
        mApiClient = apiClient;

        Validate.notNull(listener);
        mGameListener = listener;
        EventBus.getDefault().removeAllStickyEvents();
        Ln.v("new internet game created");
    }

    public void setRealTimeMessageReceivedListener(RealTimeMessageReceivedListener rtListener) {
        Validate.notNull(rtListener);
        mRtListener = rtListener;
        Ln.v("rt listener set to " + rtListener);
    }

    @Override
    public void finish() {
        if (hasFinished()) {
            Ln.w(getType() + " already finished");
            return;
        }

        super.finish();
        EventBus.getDefault().removeAllStickyEvents();
        Ln.d("finishing internet game - leaving the room");
        leaveRoom();
        mIsRoomConnected = false;
        mGameListener = null;
    }

    @Override
    public Type getType() {
        return Type.INTERNET;
    }

    /**
     * Called when we've successfully left the room (this happens a result of voluntarily leaving via a call to leaveRoom(). If we get disconnected, we get
     * onDisconnectedFromRoom()).
     */
    @Override
    public void onLeftRoom(int statusCode, String roomId) {
        mIsRoomConnected = false;
        if (GamesStatusCodes.STATUS_OK == statusCode) {
            Ln.d("player successfully left");
        } else {
            Ln.w("error while leaving the room: " + statusCode);
        }
    }

    /**
     * Called when the client attempts to create a real-time room
     */
    @Override
    public void onRoomCreated(int statusCode, Room room) {
        if (hasFinished()) {
            Ln.w("game already finished 3");
            return;
        }

        if (statusCode == GamesStatusCodes.STATUS_OK) {
            Ln.d("player created RT multiplayer room, waiting for it to become ready...");
            setRoom(room);
            mGameListener.onWaitingForOpponent(room);
        } else {
            Ln.w("room creation error");
            mGameListener.onError(statusCode);
        }
    }

    /**
     * Called when the client attempts to join a real-time room.
     */
    @Override
    public void onJoinedRoom(int statusCode, Room room) {
        if (hasFinished()) {
            Ln.w("game already finished 1");
            return;
        }

        if (statusCode == GamesStatusCodes.STATUS_OK) {
            Ln.d("player joined the multiplayer room, waiting for it to become ready...");
            setRoom(room);
            mGameListener.onWaitingForOpponent(room);
        } else {
            Ln.w("failed to join room, status: " + statusCode);
            mGameListener.onError(statusCode);
        }
    }

    /**
     * Called when all the participants in a real-time room are fully connected.
     */
    @Override
    public void onRoomConnected(int statusCode, Room room) { // 6
        if (hasFinished()) {
            Ln.w("game already finished 2");
            return;
        }

        if (statusCode == GamesStatusCodes.STATUS_OK) {
            Ln.d("room fully connected");
            setRoom(room);
            mIsRoomConnected = true;
            if (!mMessages.isEmpty()) {
                sendMessage(mMessages.poll());
            }
        } else {
            Ln.w("room connection error: " + statusCode);
            mGameListener.onError(statusCode);
        }
    }

    // -----------------------

    /*
     * We treat most of the room update callbacks in the same way: we update our list of participants and update the display. In a real game we would also have
     * to check if that change requires some action like removing the corresponding player avatar from the screen, etc.
     */
    @Override
    public void onP2PConnected(String participantId) { // 3, 1 on disconnect by peer
        Ln.d("peer-peer connection established with: " + participantId);
        setRecipientId(participantId);
    }

    private void setRecipientId(String participantId) {
        if (participantId == null) {
            Ln.w("recipient is null");
        } else {
            mRecipientId = participantId;
            Ln.v("recipient set to " + mRecipientId);
        }
    }

    @Override
    public void onP2PDisconnected(String participantId) {
        // TODO: "Called when client gets disconnected from a peer participant."
        Ln.d(participantId);
    }

    @Override
    public void onPeerDeclined(Room room, List<String> participantIds) {
        setRoom(room);
        Ln.d("peers declined invitation: " + participantIds);
    }

    @Override
    public void onPeerInvitedToRoom(Room room, List<String> participantIds) {
        setRoom(room);
        Ln.d("peers invited to the room: " + participantIds);
    }

    @Override
    public void onPeerJoined(Room room, List<String> participantIds) { // 1
        setRoom(room);
        // if (participantIds.size() > 0) {
        // setRecipientId(participantIds.get(0));
        // }
        Ln.d("participants joined the room: " + participantIds);
    }

    @Override
    public void onPeerLeft(Room room, List<String> participantIds) { // by peer
        setRoom(room);
        Ln.d("participants left: " + participantIds);
        if (mConnectionLostSent) {
            Ln.w(GameEvent.CONNECTION_LOST + " already has been sent");
        } else {
            EventBus.getDefault().postSticky(GameEvent.OPPONENT_LEFT);
            connectionLost();
        }
    }

    @Override
    public void onPeersConnected(Room room, List<String> participantIds) { // 5
        setRoom(room);
        if (participantIds.size() > 0) {
            setRecipientId(participantIds.get(0));
        }
        Ln.d("participants connected to the room: " + participantIds);
    }

    @Override
    public void onPeersDisconnected(Room room, List<String> participantIds) {
        setRoom(room);
        Ln.d("participants that disconnected from room: " + participantIds);
        if (!mConnectionLostSent) {
            Ln.w("sending " + GameEvent.CONNECTION_LOST + " message because " + participantIds + " disconnected");
            EventBus.getDefault().postSticky(GameEvent.CONNECTION_LOST);
            connectionLost();
        }
    }

    @Override
    public void onRoomAutoMatching(Room room) {
        setRoom(room);
        Ln.d("automatching started");
    }

    @Override
    public void onRoomConnecting(Room room) { // 2
        setRoom(room);
        Ln.d("room is connecting...");
    }

    /*
     * Called when we are connected to the room. We're not ready to play yet! (maybe not everybody is connected yet).
     */
    @Override
    public void onConnectedToRoom(Room room) { // 4
        setRoom(room);
        Ln.d("player is connected to the room");
    }

    @Override
    public void onDisconnectedFromRoom(Room room) { // by peer 1
        setRoom(room);
        Ln.d("player is disconnected from the connected set in the room");
    }

    private void setRoom(Room room) {
        if (room == null) {
            Ln.w("room could not be loaded successfully");
        } else {
            mRoom = room;
            Ln.v("room set to " + mRoom.getRoomId());
        }
    }

    private void leaveRoom() {
        if (mRoom == null) {
            Ln.w("should not request leaving room without actually setting it");
            return;
        }

        Ln.d("leaving room: " + mRoom.getRoomId());
        mApiClient.leave(this, mRoom.getRoomId());
        mRoom = null;
    }

    public void accept(Invitation invitation) {
        String invId = invitation.getInvitationId();
        Ln.d("accepting invitation: " + invId);
        RoomConfig.Builder builder = getRoomConfigBuilder();
        builder.setInvitationIdToAccept(invId);
        mApiClient.join(builder.build());
    }

    public void create(ArrayList<String> invitees, int minAutoMatchPlayers, int maxAutoMatchPlayers) {
        RoomConfig.Builder builder = getRoomConfigBuilder();
        builder.addPlayersToInvite(invitees);

        Bundle autoMatchCriteria = createAutomatchCriteria(minAutoMatchPlayers, maxAutoMatchPlayers);
        if (autoMatchCriteria != null) {
            Ln.d("automatch criteria: " + autoMatchCriteria);
            builder.setAutoMatchCriteria(autoMatchCriteria);
        }

        mApiClient.create(builder.build());
    }

    private Bundle createAutomatchCriteria(int minAutoMatchPlayers, int maxAutoMatchPlayers) {
        Bundle autoMatchCriteria = null;
        if (minAutoMatchPlayers > 0 || maxAutoMatchPlayers > 0) {
            // TODO: call this method anyway - do not return null
            autoMatchCriteria = RoomConfig.createAutoMatchCriteria(minAutoMatchPlayers, maxAutoMatchPlayers, 0);
        }
        return autoMatchCriteria;
    }

    public void quickGame() {
        // quick-start a game with 1 randomly selected opponent
        RoomConfig.Builder builder = getRoomConfigBuilder();
        builder.setAutoMatchCriteria(createAutomatchCriteria(MIN_OPPONENTS, MAX_OPPONENTS));
        mApiClient.create(builder.build());
    }

    private RoomConfig.Builder getRoomConfigBuilder() {
        RoomConfig.Builder builder = RoomConfig.builder(this);
        builder.setMessageReceivedListener(mRtListener);
        builder.setRoomStatusUpdateListener(this);
        return builder;
    }

    @Override
    public void onRealTimeMessageSent(int statusCode, int tokenId, String recipientParticipantId) {
        Ln.v(tokenId + " message sent, status=" + InternetGame.getStatusName(statusCode));
        mIsSending = false;
        if (statusCode == GamesStatusCodes.STATUS_OK) {
            if (tokenId != mLastSentToken) {
                Ln.e("last sent token is wrong");
                ACRA.getErrorReporter().handleException(new RuntimeException("last sent token is wrong"));
            }

            if (!mMessages.isEmpty()) {
                sendMessage(mMessages.poll());
            }
        } else {
            if (!mConnectionLostSent && !hasFinished()) {
                Ln.w("sending " + GameEvent.CONNECTION_LOST + " message because could not send message");
                EventBus.getDefault().postSticky(GameEvent.CONNECTION_LOST);
                connectionLost();
            }
        }
    }

    private void connectionLost() {
        mConnectionLostSent = true;
        Ln.i("connection lost");
    }

    @Override
    public int getTurnTimeout() {
        return TURN_TIMEOUT;
    }

    @Override
    public void sendRtm(String message) {
        mMessages.offer(message);
        if (mIsRoomConnected && !mIsSending) {
            sendMessage(mMessages.poll());
        } else {
            Ln.v("skipping message, queue size = " + mMessages.size());
        }
    }

    private void sendMessage(String message) {
        mIsSending = true;
        byte[] messageData = message.getBytes();
        mLastSentToken = mApiClient.sendReliableMessage(this, messageData, mRoom.getRoomId(), mRecipientId);
        Ln.v(mLastSentToken + " sent: [" + message + "], to " + mRecipientId + ", " + messageData.length + " bytes, messages left = " + mMessages.size());
    }

    private static String getStatusName(int statusCode) {
        switch (statusCode) {
            case GamesStatusCodes.STATUS_PARTICIPANT_NOT_CONNECTED:
                return "STATUS_PARTICIPANT_NOT_CONNECTED";

            default:
                return "" + statusCode;
        }
    }
}
