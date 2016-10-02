package com.ivygames.common.googleapi;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMultiplayer;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.games.snapshot.Snapshots;
import com.ivygames.common.achievements.AchievementsResultCallback;
import com.ivygames.common.invitations.InvitationLoadListener;
import com.ivygames.common.multiplayer.RoomListener;

import java.util.ArrayList;


public interface ApiClient {

    void connect();

    boolean isConnected();

    void disconnect();

    String getDisplayName();

    void registerInvitationListener(@NonNull OnInvitationReceivedListener listener);

    void loadInvitations(@NonNull InvitationLoadListener callback);

    void unlockAchievement(@NonNull String achievementId);

    void revealAchievement(@NonNull String achievementId);

    void loadAchievements(@NonNull AchievementsResultCallback resultCallback);

    void increment(@NonNull String achievementId, int steps);

    void openAsynchronously(@NonNull String snapshotName,
                            @NonNull ResultCallback<? super Snapshots.OpenSnapshotResult> callback);

    PendingResult<Snapshots.OpenSnapshotResult> resolveConflict(@NonNull String conflictId, @NonNull Snapshot snapshot);

    /**
     * Calling this method with a snapshot that has already been committed or that was not opened will throw an exception
     *
     * @throws Exception
     */
    PendingResult<Snapshots.CommitSnapshotResult> commitAndClose(@NonNull Snapshot snapshot,
                                                                 @NonNull SnapshotMetadataChange change) throws Exception;

    void leaveRoom(@NonNull RoomUpdateListener updateListener, @NonNull String roomId);

    void joinRoom(@NonNull Invitation invitation,
                  @NonNull RoomListener roomListener,
                  @NonNull RealTimeMessageReceivedListener rtListener);

    void createRoom(@NonNull ArrayList<String> invitees,
                    int minAutoMatchPlayers,
                    int maxAutoMatchPlayers,
                    @NonNull RoomListener roomListener,
                    @NonNull RealTimeMessageReceivedListener rtListener);

    void createRoom(int minAutoMatchPlayers,
                    int maxAutoMatchPlayers,
                    @NonNull RoomListener roomListener,
                    @NonNull RealTimeMessageReceivedListener rtListener);


    int sendReliableMessage(@NonNull RealTimeMultiplayer.ReliableMessageSentCallback callback,
                            @NonNull byte[] messageData,
                            @NonNull String roomId,
                            @NonNull String recipientId);

    void submitScore(@NonNull String boardName, int totalScores);

    Player getCurrentPlayer();

    void setConnectionListener(@NonNull ApiConnectionListener callback);

    void setActivity(@NonNull Activity activity);

    void onActivityResult(int requestCode, int resultCode);

    void selectOpponents(int requestCode, int minOpponents, int maxOpponents);

    void showInvitationInbox(int requestCode);

    void showAchievements(int requestCode);

    void showWaitingRoom(int requestCode, @NonNull Room room, int minPlayers);

    void showLeaderboards(@NonNull String boardName, int requestCode);

}