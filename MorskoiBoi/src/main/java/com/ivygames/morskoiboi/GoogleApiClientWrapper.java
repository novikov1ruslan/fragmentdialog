package com.ivygames.morskoiboi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.achievement.Achievements;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.InvitationBuffer;
import com.google.android.gms.games.multiplayer.Invitations;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMultiplayer;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.games.snapshot.Snapshots;
import com.google.android.gms.plus.Plus;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.ivygames.morskoiboi.invitations.GameInvitation;
import com.ivygames.morskoiboi.invitations.InvitationLoadListener;

import org.commons.logger.Ln;

import java.util.Collection;
import java.util.HashSet;


public class GoogleApiClientWrapper implements ApiClient {

    @NonNull
    private final GoogleApiClient mGoogleApiClient;
    private GoogleApiClient.ConnectionCallbacks mConnectedListener;
    private GoogleApiClient.OnConnectionFailedListener mConnectionFailedListener;
    private InvitationLoadListener mLoadListener;
    private ResultCallback<? super Invitations.LoadInvitationsResult> mResultCallback = new LoadInvitationsResultImpl();
    private boolean mDryRun;

    GoogleApiClientWrapper(@NonNull Context context) {
        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(context, new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                mConnectedListener.onConnected(bundle);
            }

            @Override
            public void onConnectionSuspended(int i) {
                mConnectedListener.onConnectionSuspended(i);
            }
        }, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                mConnectionFailedListener.onConnectionFailed(connectionResult);
            }
        });
        builder.addApi(Games.API).addScope(Games.SCOPE_GAMES);
        builder.addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN);
        builder.addApi(Drive.API).addScope(Drive.SCOPE_APPFOLDER);

        mGoogleApiClient = builder.build();
    }

    public void setDryRun(boolean dryRun) {
        mDryRun = dryRun;
    }

    public void connect() {
        mGoogleApiClient.connect();
    }

    public boolean isConnected() {
        return mGoogleApiClient.isConnected();
    }

    public void disconnect() {
        mGoogleApiClient.disconnect();
    }

    public void unregisterConnectionCallbacks(@NonNull GoogleApiClient.ConnectionCallbacks callbacks) {
        mGoogleApiClient.unregisterConnectionCallbacks(callbacks);
    }

    public void unregisterConnectionFailedListener(@NonNull GoogleApiClient.OnConnectionFailedListener listener) {
        mGoogleApiClient.unregisterConnectionFailedListener(listener);
    }

    public String getDisplayName() {
        return Games.Players.getCurrentPlayer(mGoogleApiClient).getDisplayName();
    }

    public boolean resolveConnectionFailure(@NonNull Activity activity,
                                            @NonNull ConnectionResult connectionResult,
                                            int rcSignIn,
                                            @NonNull String string) {
        return BaseGameUtils.resolveConnectionFailure(activity, mGoogleApiClient, connectionResult, rcSignIn, string);
    }

    public void unregisterInvitationListener() {
        Games.Invitations.unregisterInvitationListener(mGoogleApiClient);
        Ln.v("invitation listener unregistered");
    }

    public void registerInvitationListener(@NonNull OnInvitationReceivedListener listener) {
        Ln.v("registering invitation listener: " + listener);
        Games.Invitations.registerInvitationListener(mGoogleApiClient, listener);
    }

    @Override
    public void loadInvitations(InvitationLoadListener listener) {
        mLoadListener = listener;
        Ln.v("loading invitations...");
        Games.Invitations.loadInvitations(mGoogleApiClient).setResultCallback(mResultCallback);
    }

    public Intent getAchievementsIntent() {
        return Games.Achievements.getAchievementsIntent(mGoogleApiClient);
    }

    public Intent getLeaderboardIntent(@NonNull String boardName) {
        return Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient, boardName);
    }

    public void unlock(@NonNull String achievementId) {
        if (mDryRun) {
            Ln.v("dry run - not executing");
            return;
        }
        Games.Achievements.unlock(mGoogleApiClient, achievementId);
    }

    public void reveal(@NonNull String achievementId) {
        if (mDryRun) {
            Ln.v("dry run - not executing");
            return;
        }
        Games.Achievements.reveal(mGoogleApiClient, achievementId);
    }

    public PendingResult<Achievements.LoadAchievementsResult> load(boolean b) {
        return Games.Achievements.load(mGoogleApiClient, b);
    }

    public void increment(@NonNull String achievementId, int steps) {
        if (mDryRun) {
            Ln.v("dry run - not executing");
            return;
        }
        Games.Achievements.increment(mGoogleApiClient, achievementId, steps);
    }

    public PendingResult<Snapshots.OpenSnapshotResult> open(@NonNull String snapshotName, boolean createIfMissing) {
        return Games.Snapshots.open(mGoogleApiClient, snapshotName, createIfMissing);
    }

    public void openAsynchronously(@NonNull String snapshotName, @NonNull ResultCallback<? super Snapshots.OpenSnapshotResult> callback) {
        final boolean CREATE_IF_MISSING = false;
        open(snapshotName, CREATE_IF_MISSING).setResultCallback(callback);
    }

    public PendingResult<Snapshots.OpenSnapshotResult> resolveConflict(@NonNull String conflictId, @NonNull Snapshot snapshot) {
        return Games.Snapshots.resolveConflict(mGoogleApiClient, conflictId, snapshot);
    }

    public PendingResult<Snapshots.CommitSnapshotResult> commitAndClose(@NonNull Snapshot snapshot, @NonNull SnapshotMetadataChange change) {
        return Games.Snapshots.commitAndClose(mGoogleApiClient, snapshot, change);
    }

    public void leave(@NonNull RoomUpdateListener updateListener, @NonNull String roomId) {
        Games.RealTimeMultiplayer.leave(mGoogleApiClient, updateListener, roomId);
    }

    public void join(@NonNull RoomConfig roomConfig) {
        Games.RealTimeMultiplayer.join(mGoogleApiClient, roomConfig);
    }

    public void create(@NonNull RoomConfig build) {
        Games.RealTimeMultiplayer.create(mGoogleApiClient, build);
    }

    public int sendReliableMessage(@NonNull RealTimeMultiplayer.ReliableMessageSentCallback callback,
                                   @NonNull byte[] messageData,
                                   @NonNull String roomId,
                                   @NonNull String recipientId) {
        return Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, callback, messageData, roomId, recipientId);
    }

    public Intent getInvitationInboxIntent() {
        return Games.Invitations.getInvitationInboxIntent(mGoogleApiClient);
    }

    public Intent getSelectOpponentsIntent(int minOpponents, int maxOpponents, boolean b) {
        return Games.RealTimeMultiplayer.getSelectOpponentsIntent(mGoogleApiClient, minOpponents, maxOpponents, b);
    }

    public Intent getWaitingRoomIntent(@NonNull Room room, int minPlayers) {
        return Games.RealTimeMultiplayer.getWaitingRoomIntent(mGoogleApiClient, room, minPlayers);
    }

    public void submitScore(@NonNull String boardName, int totalScores) {
        if (mDryRun) {
            Ln.v("dry run - not executing");
            return;
        }
        Games.Leaderboards.submitScore(mGoogleApiClient, boardName, totalScores);
    }

    public Player getCurrentPlayer() {
        return Games.Players.getCurrentPlayer(mGoogleApiClient);
    }

    public void setConnectionCallbacks(@NonNull GoogleApiClient.ConnectionCallbacks callback) {
        mConnectedListener = callback;
    }

    public void setOnConnectionFailedListener(@NonNull GoogleApiClient.OnConnectionFailedListener listener) {
        mConnectionFailedListener = listener;
    }

    private class LoadInvitationsResultImpl implements ResultCallback<Invitations.LoadInvitationsResult> {

        @Override
        public void onResult(@NonNull Invitations.LoadInvitationsResult list) {
            Collection<GameInvitation> invitationsCopy = new HashSet<>();
            if (list.getInvitations().getCount() > 0) {
                InvitationBuffer invitations = list.getInvitations();
                Ln.v("loaded " + invitations.getCount() + " invitations");
                for (int i = 0; i < invitations.getCount(); i++) {
                    Invitation invitation = invitations.get(i);
                    invitationsCopy.add(
                            new GameInvitation(invitation.getInviter().getDisplayName(),
                                    invitation.getInvitationId()));
                }
                list.getInvitations().release();
            } else {
                Ln.v("no invitations");
            }
            mLoadListener.onResult(invitationsCopy);
        }
    }

}
