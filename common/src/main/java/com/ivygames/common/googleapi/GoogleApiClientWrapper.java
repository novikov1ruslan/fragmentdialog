package com.ivygames.common.googleapi;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.InvitationBuffer;
import com.google.android.gms.games.multiplayer.Invitations;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMultiplayer;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.games.snapshot.Snapshots;
import com.google.android.gms.plus.Plus;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.ivygames.common.BuildConfig;
import com.ivygames.common.achievements.AchievementsResultCallback;
import com.ivygames.common.invitations.GameInvitation;
import com.ivygames.common.invitations.InvitationLoadListener;
import com.ivygames.common.multiplayer.RoomListener;

import org.commons.logger.Ln;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;


public class GoogleApiClientWrapper implements ApiClient {
    private static final boolean LOG_ENABLED = false;

    @NonNull
    private final GoogleApiClient mGoogleApiClient;
    @Nullable
    private ApiConnectionListener mConnectedListener;
    private final boolean mDryRun = BuildConfig.DEBUG;
    private Activity mActivity;

    // Are we currently resolving a connection failure?
    private boolean mResolvingConnectionFailure;
    private int mSignInRequestCode;
    private int mServiceResolveRequestCode;
    private String mErrorMessage;

    public GoogleApiClientWrapper(@NonNull Context context, int signInRequestCode,
                                  @NonNull String errorMessage, int serviceResolveRequestCode) {
        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(context, new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                mResolvingConnectionFailure = false;

                if (mConnectedListener != null) {
                    mConnectedListener.onConnected();
                } else {
                    Ln.w("connection listener is null");
                }
            }

            @Override
            public void onConnectionSuspended(int cause) {
                Ln.d("connection suspended - trying to reconnect: " + GpgsUtils.connectionCauseToString(cause));
                // GoogleApiClient will automatically attempt to restore the connection.
                // Applications should disable UI components that require the service,
                // and wait for a call to onConnected(Bundle) to re-enable them.
            }
        }, new OnConnectionFailedListenerImpl());
        builder.addApi(Games.API).addScope(Games.SCOPE_GAMES);
        builder.addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN);
        builder.addApi(Drive.API).addScope(Drive.SCOPE_APPFOLDER);

        mGoogleApiClient = builder.build();

        mSignInRequestCode = signInRequestCode;
        mServiceResolveRequestCode = serviceResolveRequestCode;
        mErrorMessage = errorMessage;
    }

    @Override
    public void connect() {
        mGoogleApiClient.connect();
    }

    @Override
    public boolean isConnected() {
        return mGoogleApiClient.isConnected();
    }

    @Override
    public void disconnect() {
        mGoogleApiClient.disconnect();
    }

    @Override
    public String getDisplayName() {
        return Games.Players.getCurrentPlayer(mGoogleApiClient).getDisplayName();
    }

    @Override
    public void registerInvitationListener(@NonNull OnInvitationReceivedListener listener) {
        if (LOG_ENABLED) {
            Ln.v("registering invitation listener: " + listener);
        }
        Games.Invitations.registerInvitationListener(mGoogleApiClient, listener);
    }

    @Override
    public void loadInvitations(@NonNull InvitationLoadListener listener) {
        if (LOG_ENABLED) {
            Ln.v("loading invitations...");
        }
        Games.Invitations.loadInvitations(mGoogleApiClient).
                setResultCallback(new LoadInvitationsResultImpl(listener));
    }

    @Override
    public void unlockAchievement(@NonNull String achievementId) {
        if (mDryRun) {
            Ln.v("dry run - not executing");
            return;
        }

        Games.Achievements.unlock(mGoogleApiClient, achievementId);
    }

    @Override
    public void revealAchievement(@NonNull String achievementId) {
        if (mDryRun) {
            Ln.v("dry run - not executing");
            return;
        }

        Games.Achievements.reveal(mGoogleApiClient, achievementId);
    }

    @Override
    public void loadAchievements(@NonNull AchievementsResultCallback resultCallback) {
        Games.Achievements.load(mGoogleApiClient, true).setResultCallback(resultCallback);
    }

    @Override
    public void increment(@NonNull String achievementId, int steps) {
        if (mDryRun) {
            Ln.v("dry run - not executing");
            return;
        }

        Games.Achievements.increment(mGoogleApiClient, achievementId, steps);
    }

    @Override
    public void openAsynchronously(@NonNull String snapshotName,
                                   @NonNull ResultCallback<? super Snapshots.OpenSnapshotResult> callback) {
        final boolean createIfMissing = true;
        Games.Snapshots.open(mGoogleApiClient, snapshotName, createIfMissing).setResultCallback(callback);
    }

    @Override
    public PendingResult<Snapshots.OpenSnapshotResult> resolveConflict(@NonNull String conflictId, @NonNull Snapshot snapshot) {
        return Games.Snapshots.resolveConflict(mGoogleApiClient, conflictId, snapshot);
    }

    @Override
    public PendingResult<Snapshots.CommitSnapshotResult> commitAndClose(@NonNull Snapshot snapshot, @NonNull SnapshotMetadataChange change) {
        return Games.Snapshots.commitAndClose(mGoogleApiClient, snapshot, change);
    }

    @Override
    public void leaveRoom(@NonNull RoomUpdateListener updateListener, @NonNull String roomId) {
        Games.RealTimeMultiplayer.leave(mGoogleApiClient, updateListener, roomId);
    }

    @Override
    public void joinRoom(@NonNull Invitation invitation,
                         @NonNull RoomListener roomListener,
                         @NonNull RealTimeMessageReceivedListener rtListener) {
        String invId = invitation.getInvitationId();
        if (LOG_ENABLED) {
            Ln.v("accepting invitation: " + invId);
        }
        RoomConfig.Builder builder = getRoomConfigBuilder(roomListener, rtListener);
        builder.setInvitationIdToAccept(invId);

        Games.RealTimeMultiplayer.join(mGoogleApiClient, builder.build());
    }

    @Override
    public void createRoom(@NonNull ArrayList<String> invitees,
                           int minAutoMatchPlayers,
                           int maxAutoMatchPlayers,
                           @NonNull RoomListener roomListener,
                           @NonNull RealTimeMessageReceivedListener rtListener) {
        RoomConfig.Builder builder = getBuilder(minAutoMatchPlayers, maxAutoMatchPlayers, roomListener, rtListener);
        builder.addPlayersToInvite(invitees);

        Games.RealTimeMultiplayer.create(mGoogleApiClient, builder.build());
    }

    @Override
    public void createRoom(int minAutoMatchPlayers,
                           int maxAutoMatchPlayers,
                           @NonNull RoomListener roomListener,
                           @NonNull RealTimeMessageReceivedListener rtListener) {
        RoomConfig.Builder builder = getBuilder(minAutoMatchPlayers, maxAutoMatchPlayers, roomListener, rtListener);

        Games.RealTimeMultiplayer.create(mGoogleApiClient, builder.build());
    }

    private RoomConfig.Builder getBuilder(int minAutoMatchPlayers,
                                          int maxAutoMatchPlayers,
                                          @NonNull RoomListener roomListener,
                                          @NonNull RealTimeMessageReceivedListener rtListener) {
        RoomConfig.Builder builder = getRoomConfigBuilder(roomListener, rtListener);

        Bundle autoMatchCriteria = createAutomatchCriteria(minAutoMatchPlayers, maxAutoMatchPlayers);
        if (autoMatchCriteria != null) {
            Ln.v("automatch criteria: " + autoMatchCriteria);
            builder.setAutoMatchCriteria(autoMatchCriteria);
        }
        return builder;
    }

    private static Bundle createAutomatchCriteria(int minAutoMatchPlayers, int maxAutoMatchPlayers) {
        Bundle autoMatchCriteria = null;
        if (minAutoMatchPlayers > 0 || maxAutoMatchPlayers > 0) {
            // TODO: call this method anyway - do not return null
            autoMatchCriteria = RoomConfig.createAutoMatchCriteria(minAutoMatchPlayers, maxAutoMatchPlayers, 0);
        }
        return autoMatchCriteria;
    }

    private RoomConfig.Builder getRoomConfigBuilder(@NonNull RoomListener roomListener,
                                                    @NonNull RealTimeMessageReceivedListener rtListener) {
        RoomConfig.Builder builder = RoomConfig.builder(roomListener);
        builder.setMessageReceivedListener(rtListener);
        builder.setRoomStatusUpdateListener(roomListener);
        return builder;
    }

    @Override
    public int sendReliableMessage(@NonNull RealTimeMultiplayer.ReliableMessageSentCallback callback,
                                   @NonNull byte[] messageData,
                                   @NonNull String roomId,
                                   @NonNull String recipientId) {
        return Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, callback, messageData, roomId, recipientId);
    }

    @Override
    public void selectOpponents(int requestCode, int minOpponents, int maxOpponents) {
        Intent intent = Games.RealTimeMultiplayer.getSelectOpponentsIntent(mGoogleApiClient, minOpponents, maxOpponents, false);
        mActivity.startActivityForResult(intent, requestCode);
    }

    public void dismissInvitation(@NonNull String invitationId) {
        Games.RealTimeMultiplayer.dismissInvitation(mGoogleApiClient, invitationId);
    }

    @Override
    public void showWaitingRoom(int requestCode, @NonNull Room room, int minPlayers) {
        Intent intent = Games.RealTimeMultiplayer.getWaitingRoomIntent(mGoogleApiClient, room, minPlayers);
        mActivity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void showInvitationInbox(int requestCode) {
        Intent intent = Games.Invitations.getInvitationInboxIntent(mGoogleApiClient);
        mActivity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void showAchievements(int requestCode) {
        Intent intent = Games.Achievements.getAchievementsIntent(mGoogleApiClient);
        mActivity.startActivityForResult(intent, requestCode);
    }


    @Override
    public void showLeaderboards(@NonNull String boardName, int requestCode) {
        mActivity.startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient, boardName), requestCode);
    }

    @Override
    public void submitScore(@NonNull String boardName, int totalScores) {
        if (mDryRun) {
            Ln.v("dry run - not executing");
            return;
        }
        Games.Leaderboards.submitScore(mGoogleApiClient, boardName, totalScores);
    }

    @Override
    public Player getCurrentPlayer() {
        return Games.Players.getCurrentPlayer(mGoogleApiClient);
    }

    @Override
    public void setConnectionListener(@NonNull ApiConnectionListener callback) {
        mConnectedListener = callback;
    }

    @Override
    public void setActivity(@NonNull Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode) {
        if (requestCode == mSignInRequestCode) {
            if (resultCode != Activity.RESULT_OK) {
                Ln.w("connection issue could not be resolved");
                mResolvingConnectionFailure = false;
            }
        }
    }

    private static class LoadInvitationsResultImpl implements ResultCallback<Invitations.LoadInvitationsResult> {
        @NonNull
        private final InvitationLoadListener mLoadListener;

        LoadInvitationsResultImpl(@NonNull InvitationLoadListener listener) {
            mLoadListener = listener;
        }

        @Override
        public void onResult(@NonNull Invitations.LoadInvitationsResult list) {
            Collection<GameInvitation> invitationsCopy = new HashSet<>();
            if (list.getInvitations().getCount() > 0) {
                InvitationBuffer invitations = list.getInvitations();
                if (LOG_ENABLED) {
                    Ln.v("loaded " + invitations.getCount() + " invitations");
                }
                for (int i = 0; i < invitations.getCount(); i++) {
                    Invitation invitation = invitations.get(i);
                    invitationsCopy.add(
                            new GameInvitation(invitation.getInviter().getDisplayName(),
                                    invitation.getInvitationId()));
                }
                list.getInvitations().release();
            }
            mLoadListener.onLoaded(invitationsCopy);
        }
    }

    private class OnConnectionFailedListenerImpl implements GoogleApiClient.OnConnectionFailedListener {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult result) {
            Ln.v("connection failed - result: " + result);

            switch (result.getErrorCode()) {
                case ConnectionResult.SERVICE_MISSING:
                case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                case ConnectionResult.SERVICE_DISABLED:
                    Ln.w("connection failed: " + result.getErrorCode());
                    Dialog errorDialog = GoogleApiAvailability.getInstance().getErrorDialog(mActivity,
                            result.getErrorCode(), mServiceResolveRequestCode);
                    errorDialog.show();
                    return;
            }

            if (mResolvingConnectionFailure) {
                Ln.w("ignoring connection failure; already resolving.");
                return;
            }

            Ln.v("resolving connection failure");
            mResolvingConnectionFailure = BaseGameUtils.resolveConnectionFailure(mActivity, mGoogleApiClient,
                    result, mSignInRequestCode, mErrorMessage);
            Ln.v("has resolution = " + mResolvingConnectionFailure);
        }
    }

    @Override
    public String toString() {
        return GoogleApiClientWrapper.class.getSimpleName() + "#" + (hashCode() % 1000);
    }
}
