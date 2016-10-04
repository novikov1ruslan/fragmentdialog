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
import com.ivygames.common.googleapi.ApiClient;
import com.ivygames.common.invitations.InvitationListener;

import org.commons.logger.Ln;

import java.util.ArrayList;
import java.util.Set;

public class MultiplayerManager {
    private static final int MIN_PLAYERS = 2;

    private static final int MIN_OPPONENTS = 1;
    private static final int MAX_OPPONENTS = 1;

    @NonNull
    private final ApiClient mApiClient;
    @NonNull
    private final InvitationManager mInvitationManager;
    private MultiplayerListener mListener;
    private int mSelectPlayersRc;
    private RoomListener mRoomListener;
    private int mInvitationInboxRc;
    private int mWaitingRoomRc;

    private RealTimeMessageReceivedListener mRtListener;

    public MultiplayerManager(@NonNull ApiClient apiClient) {
        mApiClient = apiClient;
        mInvitationManager = new InvitationManager(apiClient);
    }

    public void setListener(@NonNull MultiplayerListener listener) {
        mListener = listener;
    }

    public void showWaitingRoom(int requestCode, @NonNull Room room) {
        mWaitingRoomRc = requestCode;
        mApiClient.showWaitingRoom(requestCode, room, MIN_PLAYERS);
    }

    public void invitePlayers(int requestCode, @NonNull RoomListener roomListener,
                              @NonNull RealTimeMessageReceivedListener rtListener) {
        mSelectPlayersRc = requestCode;
        mRoomListener = roomListener;
        mRtListener = rtListener;
        mApiClient.selectOpponents(requestCode, MIN_OPPONENTS, MAX_OPPONENTS);
    }

    public void showInvitations(int requestCode, @NonNull RoomListener roomListener,
                                @NonNull RealTimeMessageReceivedListener rtListener) {
        mInvitationInboxRc = requestCode;
        mRoomListener = roomListener;
        mRtListener = rtListener;
        mApiClient.showInvitationInbox(requestCode);
    }

    public void quickGame(@NonNull RoomListener roomListener,
                          @NonNull RealTimeMessageReceivedListener rtListener) {
        // quick-start a game with 1 randomly selected opponent
        mRtListener = rtListener;
        mApiClient.createRoom(MIN_OPPONENTS, MAX_OPPONENTS, roomListener, rtListener);
    }

    public void handleResult(int requestCode, int resultCode, @NonNull Intent data) {
        Ln.v("result=" + resultCode);
        if (requestCode == mSelectPlayersRc) {
            // Handle the result of the "Select players UI" we launched when the user
            // clicked the
            // "Invite friends" button. We react by creating a room with those players.
            if (resultCode == Activity.RESULT_OK) {
                ArrayList<String> invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);
                Ln.d("opponent selected: " + invitees + ", creating room...");
                int minAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
                int maxAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);

                mApiClient.createRoom(invitees, minAutoMatchPlayers, maxAutoMatchPlayers, mRoomListener, mRtListener);
            } else {
                Ln.d("select players UI cancelled - hiding waiting screen; reason=" + resultCode);
                mListener.invitationCanceled();
            }
        } else if (requestCode == mInvitationInboxRc) {
            // Handle the result of the invitation inbox UI, where the player can pick an invitation to accept.
            // We react by accepting the selected invitation, if any.
            if (resultCode == Activity.RESULT_OK) {
                Invitation invitation = data.getExtras().getParcelable(Multiplayer.EXTRA_INVITATION);
                mApiClient.joinRoom(invitation, mRoomListener, mRtListener);
            } else {
                Ln.d("invitation cancelled - hiding waiting screen; reason=" + resultCode);
                mListener.invitationCanceled();
            }
        } else if (requestCode == mWaitingRoomRc) {
            if (resultCode == Activity.RESULT_OK) {
                Ln.d("starting game");
                mListener.gameStarted();
            } else if (resultCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
                Ln.d("user explicitly chose to leave the room");
                // if the activity result is RESULT_LEFT_ROOM, i
                // t's the caller's responsibility to actually leave the room
                mListener.playerLeft();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Dialog was cancelled (user pressed back key, for instance).
                // In our game, this means leaving the room too. In more elaborate games,this could mean
                // something else (like minimizing the waiting room UI but continue in the handshake process).
                Ln.d("user closed the waiting room - leaving");
                mListener.playerLeft();
            }
        }
    }

    public void addInvitationListener(@NonNull InvitationListener listener) {
        mInvitationManager.addInvitationListener(listener);
    }

    public void loadInvitations() {
        mInvitationManager.loadInvitations();
    }

    public void removeInvitationListener(@NonNull InvitationListener listener) {
        mInvitationManager.removeInvitationReceiver(listener);
    }

    @NonNull
    Set<String> getInvitationIds() {
        return mInvitationManager.getInvitationIds();
    }

    @Override
    public String toString() {
        return MultiplayerManager.class.getSimpleName() + "#" + (hashCode() % 1000);
    }
}
