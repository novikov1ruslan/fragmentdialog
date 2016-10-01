package com.ivygames.common.multiplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.ivygames.common.googleapi.ApiClient;
import com.ivygames.common.invitations.InvitationListener;

import org.commons.logger.Ln;

import java.util.ArrayList;
import java.util.Set;

public class MultiplayerManager {
    private static final int MIN_PLAYERS = 2;

    private static final int MIN_OPPONENTS = 1;
    private static final int MAX_OPPONENTS = 1;

    private Activity mActivity;
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

    public void setActivity(@NonNull Activity activity) {
        mActivity = activity;
    }

    public void setListener(@NonNull MultiplayerListener listener) {
        mListener = listener;
    }

    public void setRealTimeMessageReceivedListener(@NonNull RealTimeMessageReceivedListener rtListener) {
        mRtListener = rtListener;
        Ln.v("rt listener set to " + rtListener);
    }

    public void showWaitingRoom(@NonNull Room room, int requestCode) {
        mWaitingRoomRc = requestCode;
        Intent intent = mApiClient.getWaitingRoomIntent(room, MIN_PLAYERS);
        mActivity.startActivityForResult(intent, requestCode);
    }

    public void invitePlayers(int requestCode, @NonNull RoomListener roomListener) {
        mSelectPlayersRc = requestCode;
        mRoomListener = roomListener;
        Intent intent = mApiClient.getSelectOpponentsIntent(MIN_OPPONENTS, MAX_OPPONENTS, false);
        mActivity.startActivityForResult(intent, requestCode);
    }

    public void showInvitations(int requestCode, @NonNull RoomListener roomListener) {
        mInvitationInboxRc = requestCode;
        mRoomListener = roomListener;
        Intent intent = mApiClient.getInvitationInboxIntent();
        mActivity.startActivityForResult(intent, requestCode);
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
                create(invitees, minAutoMatchPlayers, maxAutoMatchPlayers, mRoomListener);
            } else {
                Ln.d("select players UI cancelled - hiding waiting screen; reason=" + resultCode);
                mListener.opponentInvitationCanceled();
            }
        } else if (requestCode == mInvitationInboxRc) {
            // Handle the result of the invitation inbox UI, where the player can pick an invitation to accept.
            // We react by accepting the selected invitation, if any.
            if (resultCode == Activity.RESULT_OK) {
                Invitation invitation = data.getExtras().getParcelable(Multiplayer.EXTRA_INVITATION);
                accept(invitation, mRoomListener);
                mInvitationManager.loadInvitations();
            } else {
                Ln.d("invitation cancelled - hiding waiting screen; reason=" + resultCode);
                mListener.invitationCanceled();
                mInvitationManager.loadInvitations();
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

    public void quickGame(@NonNull RoomListener roomListener) {
        // quick-start a game with 1 randomly selected opponent
        RoomConfig.Builder builder = getRoomConfigBuilder(roomListener);
        builder.setAutoMatchCriteria(createAutomatchCriteria(MIN_OPPONENTS, MAX_OPPONENTS));
        mApiClient.create(builder.build());
    }

    private void create(ArrayList<String> invitees, int minAutoMatchPlayers, int maxAutoMatchPlayers,
                        @NonNull RoomListener roomListener) {
        RoomConfig.Builder builder = getRoomConfigBuilder(roomListener);
        builder.addPlayersToInvite(invitees);

        Bundle autoMatchCriteria = createAutomatchCriteria(minAutoMatchPlayers, maxAutoMatchPlayers);
        if (autoMatchCriteria != null) {
            Ln.d("automatch criteria: " + autoMatchCriteria);
            builder.setAutoMatchCriteria(autoMatchCriteria);
        }

        mApiClient.create(builder.build());
    }

    private static Bundle createAutomatchCriteria(int minAutoMatchPlayers, int maxAutoMatchPlayers) {
        Bundle autoMatchCriteria = null;
        if (minAutoMatchPlayers > 0 || maxAutoMatchPlayers > 0) {
            // TODO: call this method anyway - do not return null
            autoMatchCriteria = RoomConfig.createAutoMatchCriteria(minAutoMatchPlayers, maxAutoMatchPlayers, 0);
        }
        return autoMatchCriteria;
    }

    private void accept(@NonNull Invitation invitation, @NonNull RoomListener roomListener) {
        String invId = invitation.getInvitationId();
        Ln.d("accepting invitation: " + invId);
        RoomConfig.Builder builder = getRoomConfigBuilder(roomListener);
        builder.setInvitationIdToAccept(invId);
        mApiClient.join(builder.build());
    }

    private RoomConfig.Builder getRoomConfigBuilder(@NonNull RoomListener roomListener) {
        RoomConfig.Builder builder = RoomConfig.builder(roomListener);
        builder.setMessageReceivedListener(mRtListener);
        builder.setRoomStatusUpdateListener(roomListener);
        return builder;
    }

    public void addInvitationListener(@NonNull InvitationListener listener) {
        mInvitationManager.addInvitationListener(listener);
    }

    public void loadInvitations() {
        mInvitationManager.loadInvitations();
    }

    public void removeInvitationReceiver(@NonNull InvitationListener listener) {
        mInvitationManager.removeInvitationReceiver(listener);
    }

    @NonNull
    public Set<String> getInvitationIds() {
        return mInvitationManager.getInvitationIds();
    }

    @Override
    public String toString() {
        return MultiplayerManager.class.getSimpleName() + "#" + (hashCode() % 1000);
    }
}
