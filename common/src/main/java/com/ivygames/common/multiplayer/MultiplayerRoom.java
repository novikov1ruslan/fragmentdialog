package com.ivygames.common.multiplayer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.realtime.Room;

import org.commons.logger.Ln;

import java.util.List;

/**
 * Opponent leaves: onDisconnectedFromRoom->onPeerLeft Network disconnected: onP2PDisconnected->onDisconnectedFromRoom->onPeersDisconnected
 */
public class MultiplayerRoom implements RoomListener {
    @NonNull
    private final RoomConnectionErrorListener mListener;

    private Room mRoom;
    @Nullable
    private String mRecipientId;
    private RoomCreationListener mRoomCreationListener;
    private RoomConnectionListener mRoomConnectionListener;

    public MultiplayerRoom(@NonNull RoomConnectionErrorListener listener) {
        mListener = listener;
    }

    /**
     * Called when we've successfully left the room
     * (this happens a result of voluntarily leaving via a call to leaveRoom().
     * If we get disconnected, we get onDisconnectedFromRoom()).
     */
    @Override
    public void onLeftRoom(int statusCode, String roomId) {
        if (GamesStatusCodes.STATUS_OK == statusCode) {
            Ln.v("player successfully left");
        } else {
            Ln.w("error while leaving the room: " + statusCode);
        }
    }

    /**
     * Called when the client attempts to create a real-time room
     */
    @Override
    public void onRoomCreated(int statusCode, Room room) {
        if (statusCode == GamesStatusCodes.STATUS_OK) {
            Ln.d("player created RT multiplayer room, waiting for it to become ready...");
            setRoom(room);
            mRoomCreationListener.onRoomCreated(room);
        } else {
            Ln.w("room creation error: " + statusCode);
            mListener.onError(statusCode);
        }
    }

    /**
     * Called when the client attempts to join a real-time room.
     */
    @Override
    public void onJoinedRoom(int statusCode, Room room) {
        if (statusCode == GamesStatusCodes.STATUS_OK) {
            Ln.d("player joined the multiplayer room, waiting for it to become ready...");
            setRoom(room);
            mRoomCreationListener.onRoomCreated(room);
        } else {
            Ln.w("failed to join room, status: " + statusCode);
            mListener.onError(statusCode);
        }
    }

    /**
     * Called when all the participants in a real-time room are fully connected.
     */
    @Override
    public void onRoomConnected(int statusCode, Room room) { // 6
        if (statusCode == GamesStatusCodes.STATUS_OK) {
            Ln.v("room connected");
            setRoom(room);
        } else {
            Ln.w("room connection error: " + statusCode);
            mListener.onError(statusCode);
        }
    }

    private void notifyConnected(@NonNull String roomId, @NonNull String recipientId) {
        Ln.d("room is fully connected");
        mRoomConnectionListener.onRoomConnected(roomId, recipientId);
    }

    // -----------------------

    /*
     * We treat most of the room update callbacks in the same way: we update our list of participants and update the display.
     * In a real game we would also have to check if that change requires some action
     * like removing the corresponding player avatar from the screen, etc.
     */
    @Override
    public void onP2PConnected(String participantId) { // 3, 1 on disconnect by peer
        Ln.v("peer-peer connection established with: " + participantId);
        setRecipientId(participantId);
        mRoomConnectionListener.onP2PConnected(participantId);
    }

    private void setRecipientId(@Nullable String recipientId) {
        if (recipientId == null) {
            Ln.w("recipient is null");
        } else {
            if (mRecipientId == null) {
                mRecipientId = recipientId;
                Ln.d("recipient set to " + mRecipientId);
                notifyConnected(mRoom.getRoomId(), mRecipientId);
            }
        }
    }

    @Override
    public void onP2PDisconnected(String participantId) {
        // TODO: "Called when client gets disconnected from a peer participant."
        Ln.v("disconnected from a peer: " + participantId);
    }

    @Override
    public void onPeerDeclined(Room room, List<String> participantIds) {
        Ln.v("peers declined invitation: " + participantIds);
//        setRoom(room);
    }

    @Override
    public void onPeerInvitedToRoom(Room room, List<String> participantIds) {
        Ln.v("peers invited to the room: " + participantIds);
//        setRoom(room);
    }

    @Override
    public void onPeerJoined(Room room, List<String> participantIds) { // 1
        Ln.v("participants joined the room: " + participantIds);
//        setRoom(room);
    }

    @Override
    public void onPeerLeft(Room room, List<String> participantIds) { // by peer
        Ln.d("participants left: " + participantIds);
//        setRoom(room);
        mRoomConnectionListener.onConnectionLost(MultiplayerEvent.OPPONENT_LEFT);
    }

    @Override
    public void onPeersConnected(Room room, List<String> participantIds) { // 5
        Ln.v("participants connected to the room: " + participantIds);
//        setRoom(room);
        if (participantIds.size() > 0) {
            setRecipientId(participantIds.get(0));
        }
    }

    @Override
    public void onPeersDisconnected(Room room, List<String> participantIds) {
        Ln.d("participants disconnected from the room: " + participantIds);
//        setRoom(room);
        mRoomConnectionListener.onConnectionLost(MultiplayerEvent.CONNECTION_LOST);
    }

    @Override
    public void onRoomAutoMatching(Room room) {
        Ln.v("automatching started");
        setRoom(room);
    }

    @Override
    public void onRoomConnecting(Room room) { // 2
        Ln.v("room is connecting...");
//        setRoom(room);
    }

    /*
     * Called when we are connected to the room.
     * We're not ready to play yet! (maybe not everybody is connected yet).
     */
    @Override
    public void onConnectedToRoom(Room room) { // 4
        Ln.v("player is connected to the room");
        setRoom(room);
    }

    @Override
    public void onDisconnectedFromRoom(Room room) { // by peer 1
        Ln.v("player is disconnected from the connected set in the room");
//        setRoom(room);
    }

    private void setRoom(@Nullable Room room) {
        if (room == null) {
            Ln.w("room could not be loaded successfully");
        } else if (mRoom == null) {
            mRoom = room;
            Ln.d("room set to: " + mRoom.getRoomId());
            if (mRecipientId != null) {
                notifyConnected(mRoom.getRoomId(), mRecipientId);
            }
        }
    }

    public void setRoomCreationListener(@NonNull RoomCreationListener listener) {
        mRoomCreationListener = listener;
        Ln.v("room creation listener set: " + listener);
    }

    public void setRoomConnectionListener(@NonNull RoomConnectionListener listener) {
        mRoomConnectionListener = listener;
        Ln.v("room connection listener set: " + listener);
    }

    public void leave() {
        mRoom = null;
        mRecipientId = null;
    }

    @Override
    public String toString() {
        return MultiplayerRoom.class.getSimpleName() + "#" + (hashCode() % 1000);
    }
}
