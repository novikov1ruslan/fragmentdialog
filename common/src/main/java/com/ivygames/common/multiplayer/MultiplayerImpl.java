package com.ivygames.common.multiplayer;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.ivygames.common.DebugUtils;
import com.ivygames.common.googleapi.ApiClient;
import com.ivygames.common.invitations.InvitationListener;

import org.commons.logger.Ln;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import static org.apache.commons.lang3.Validate.notNull;

public class MultiplayerImpl implements RealTimeMultiplayer {
    private static final int MIN_PLAYERS = 2;

    private static final int MIN_OPPONENTS = 1;
    private static final int MAX_OPPONENTS = 1;

    @NonNull
    private final ApiClient mApiClient;
    @NonNull
    private final InvitationManager mInvitationManager;
    private GameCreationListener mGameCreationListener;

    private int mSelectPlayersRc;
    private int mInvitationInboxRc;
    private final int mWaitingRoomRc;

    @NonNull
    private final Collection<ConnectionLostListener> mConnectionLostListeners = new ArrayList<>();
    private MultiplayerRoom mRoom;

    private RoomConnectionErrorListener mRoomConnectionErrorListener;
    private RealTimeMessageReceivedListener mRtmListener;

    public MultiplayerImpl(@NonNull ApiClient apiClient, int requestCode) {
        mApiClient = apiClient;
        mWaitingRoomRc = requestCode;
        mInvitationManager = new InvitationManager(apiClient);
    }

    @Override
    public void setRtmListener(@NonNull RealTimeMessageReceivedListener listener) {
        mRtmListener = listener;
    }

    @Override
    public void setRoomConnectionErrorListener(@NonNull RoomConnectionErrorListener listener) {
        mRoomConnectionErrorListener = listener;
    }

    @Override
    public void setGameCreationListener(@NonNull GameCreationListener listener) {
        mGameCreationListener = listener;
    }

    @Override
    public void invitePlayers(int requestCode, @NonNull MultiplayerRoom room) {
        Ln.d("inviting players, request=" + requestCode);

        mSelectPlayersRc = requestCode;
        mRoom = room;
        setRoomListeners(room);
        mApiClient.selectOpponents(requestCode, MIN_OPPONENTS, MAX_OPPONENTS);
    }

    @Override
    public void showInvitations(int requestCode, @NonNull MultiplayerRoom room) {
        Ln.v("showing invitations, request=" + requestCode);

        mInvitationInboxRc = requestCode;
        mRoom = room;
        setRoomListeners(room);
        mApiClient.showInvitationInbox(requestCode);
    }

    @Override
    public void quickGame(@NonNull MultiplayerRoom room) {
        Ln.v("quick game");

        mRoom = room;
        setRoomListeners(room);
        mApiClient.createRoom(MIN_OPPONENTS, MAX_OPPONENTS, room, mRtmListener);
    }

    @Override
    public void handleResult(int requestCode, int resultCode, @NonNull Intent data) {
        Ln.v("request=" + requestCode + ", result=" + resultCode);
        if (requestCode == mSelectPlayersRc) {
            // Handle the result of the "Select players UI" we launched when the user
            // clicked the
            // "Invite friends" button. We react by creating a room with those players.
            if (resultCode == Activity.RESULT_OK) {
                ArrayList<String> invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);
                Ln.d("opponent selected: " + invitees + ", creating room...");
                int minAutoMatchPlayers = data.getIntExtra(com.google.android.gms.games.multiplayer.Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
                int maxAutoMatchPlayers = data.getIntExtra(com.google.android.gms.games.multiplayer.Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);

                mApiClient.createRoom(invitees, minAutoMatchPlayers, maxAutoMatchPlayers,
                        mRoom, mRtmListener);
            } else {
                Ln.d("select players cancelled");
                endSession();
                mGameCreationListener.gameAborted();
            }
        } else if (requestCode == mInvitationInboxRc) {
            // Handle the result of the invitation inbox UI, where the player can pick an invitation to accept.
            // We react by accepting the selected invitation, if any.
            if (resultCode == Activity.RESULT_OK) {
                Invitation invitation = (Invitation) notNull(data.getExtras().getParcelable(Multiplayer.EXTRA_INVITATION));
                mApiClient.joinRoom(invitation, mRoom, mRtmListener);
            } else {
                Ln.d("invitation cancelled");
                endSession();
                mGameCreationListener.gameAborted();
            }
        } else if (requestCode == mWaitingRoomRc) {
            if (resultCode == Activity.RESULT_OK) {
                if (mRoom == null) {
                    Ln.d("game began, but then session ended");
                    mGameCreationListener.gameAborted();
                } else {
                    Ln.d("starting game");
                    mGameCreationListener.gameStarted();
                }
            } else if (resultCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
                Ln.d("user explicitly chose to leave the room");
                // if the activity result is RESULT_LEFT_ROOM,
                // it's the caller's responsibility to actually leave the room
//                leaveCurrentRoom();
                endSession();
                mGameCreationListener.gameAborted();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Dialog was cancelled (user pressed back key, for instance).
                // In our game, this means leaving the room too. In more elaborate games,this could mean
                // something else (like minimizing the waiting room UI but continue in the handshake process).
                Ln.d("user closed the waiting room - leaving");
//                leaveCurrentRoom();
                endSession();
                mGameCreationListener.gameAborted();
            }
        }
    }

    @Override
    public void addInvitationListener(@NonNull InvitationListener listener) {
        mInvitationManager.addInvitationListener(listener);
    }

    @Override
    public void loadInvitations() {
        mInvitationManager.loadInvitations();
    }

    @Override
    public void removeInvitationListener(@NonNull InvitationListener listener) {
        mInvitationManager.removeInvitationReceiver(listener);
    }

    @NonNull
    public Set<String> getInvitationIds() {
        return mInvitationManager.getInvitationIds();
    }

    @Override
    public void registerConnectionLostListener(@NonNull ConnectionLostListener listener) {
        mConnectionLostListeners.add(listener);
        Ln.v("registered: " + listener + ", size=" + mConnectionLostListeners.size());
    }

    @Override
    public boolean unregisterConnectionLostListener(@NonNull ConnectionLostListener listener) {
        boolean removed = mConnectionLostListeners.remove(listener);
        Ln.v("unregistered: " + listener + ", size=" + mConnectionLostListeners.size());
        return removed;
    }

    private void setRoomListeners(@NonNull MultiplayerRoom room) {
        room.setConnectionErrorListener(new RoomConnectionErrorListener() {
            @Override
            public void onError(int statusCode) {
                mRoomConnectionErrorListener.onError(statusCode);
                endSession();
            }
        });

        room.setCreationListener(new RoomCreationListener() {

            @Override
            public void onRoomCreated(@NonNull Room room) {
                Ln.d("waiting for opponent...");
                mApiClient.showWaitingRoom(mWaitingRoomRc, room, MIN_PLAYERS);
            }
        });

        room.setConnectionListener(new RoomConnectionListener() {
            @Override
            public void onConnectionLost(@NonNull MultiplayerEvent event) {
                endSession();
                for (ConnectionLostListener listener : mConnectionLostListeners) {
                    listener.onConnectionLost(event);
                }
            }
        });
        Ln.v("room listener set to: " + room);
    }

    private void endSession() {
        if (mRoom == null) {
            Ln.v("session already ended");
            return;
        }

//        if (mInvitees != null) {
//            mInvitees.get(0);
//        }
        mRoom.leave();
        mRoom = null;
        mSelectPlayersRc = 0;
        mInvitationInboxRc = 0;
        Ln.d("multiplayer session ended");
    }

    @Override
    public String toString() {
        return DebugUtils.getSimpleName(this);
    }
}
