package com.ivygames.common.googleapi;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMultiplayer;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.games.snapshot.Snapshots;
import com.ivygames.common.achievements.AchievementsResultCallback;


public interface ApiClient {

    void connect();

    boolean isConnected();

    void disconnect();

    void unregisterConnectionCallbacks(@NonNull GoogleApiClient.ConnectionCallbacks callbacks);

    void unregisterConnectionFailedListener(@NonNull GoogleApiClient.OnConnectionFailedListener listener);

    String getDisplayName();

    boolean resolveConnectionFailure(@NonNull Activity activity,
                                     @NonNull ConnectionResult connectionResult,
                                     int rcSignIn,
                                     @NonNull String string);

    void unregisterInvitationListener();

    void registerInvitationListener(@NonNull OnInvitationReceivedListener listener);

    void loadInvitations(@NonNull InvitationLoadListener callback);

    Intent getAchievementsIntent();

    Intent getLeaderboardIntent(@NonNull String boardName);

    void unlockAchievement(@NonNull String achievementId);

    void revealAchievement(@NonNull String achievementId);

    void loadAchievements(@NonNull AchievementsResultCallback resultCallback);

    void increment(@NonNull String achievementId, int steps);

    void openAsynchronously(@NonNull String snapshotName,
                            @NonNull ResultCallback<? super Snapshots.OpenSnapshotResult> callback);

    PendingResult<Snapshots.OpenSnapshotResult> resolveConflict(@NonNull String conflictId, @NonNull Snapshot snapshot);

    /**
     * Calling this method with a snapshot that has already been committed or that was not opened will throw an exception
     * @throws Exception
     */
    PendingResult<Snapshots.CommitSnapshotResult> commitAndClose(@NonNull Snapshot snapshot,
                                                                 @NonNull SnapshotMetadataChange change) throws Exception;

    void leave(@NonNull RoomUpdateListener updateListener, @NonNull String roomId);

    void join(@NonNull RoomConfig roomConfig);

    void create(@NonNull RoomConfig build);

    int sendReliableMessage(@NonNull RealTimeMultiplayer.ReliableMessageSentCallback callback,
                            @NonNull byte[] messageData,
                            @NonNull String roomId,
                            @NonNull String recipientId);

    Intent getInvitationInboxIntent();

    Intent getSelectOpponentsIntent(int minOpponents, int maxOpponents, boolean b);

    Intent getWaitingRoomIntent(@NonNull Room room, int minPlayers);

    void submitScore(@NonNull String boardName, int totalScores);

    Player getCurrentPlayer();

    void setConnectionCallbacks(@NonNull GoogleApiClient.ConnectionCallbacks callback);

    void setOnConnectionFailedListener(@NonNull GoogleApiClient.OnConnectionFailedListener listener);
}
