package com.ivygames.morskoiboi;

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
import com.ivygames.common.googleapi.ApiConnectionListener;
import com.ivygames.common.invitations.InvitationLoadListener;
import com.ivygames.common.multiplayer.RoomListener;

import java.util.ArrayList;

class ThrowingApiClient extends DummyApiClient {

    private boolean mConnected;
    private boolean mAchievementsShown;
    private boolean mLeaderboardsShown;
    private boolean mSubmitScoreBeCalled;

    @Override
    public void connect() {
        mConnected = true;
    }

    @Override
    public boolean isConnected() {
        return mConnected;
    }

    @Override
    public void disconnect() {
        mConnected = false;
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public void registerInvitationListener(@NonNull OnInvitationReceivedListener listener) {
        throwIfNotConnected();
    }

    private void throwIfNotConnected() {
        if (!mConnected) {
            throw new RuntimeException("not connected");
        }
    }

    @Override
    public void loadInvitations(@NonNull InvitationLoadListener listener) {
        throwIfNotConnected();
    }

    @Override
    public void unlockAchievement(@NonNull String achievementId) {
        throwIfNotConnected();
    }

    @Override
    public void revealAchievement(@NonNull String achievementId) {
        throwIfNotConnected();
    }

    @Override
    public void loadAchievements(@NonNull AchievementsResultCallback resultCallback) {
        throwIfNotConnected();
    }

    @Override
    public void increment(@NonNull String achievementId, int steps) {
        throwIfNotConnected();
    }

    @Override
    public void openAsynchronously(@NonNull String snapshotName, @NonNull ResultCallback<? super Snapshots.OpenSnapshotResult> callback) {
        throwIfNotConnected();
    }

    @Override
    public PendingResult<Snapshots.OpenSnapshotResult> resolveConflict(@NonNull String conflictId, @NonNull Snapshot snapshot) {
        throwIfNotConnected();
        return null;
    }

    @Override
    public PendingResult<Snapshots.CommitSnapshotResult> commitAndClose(@NonNull Snapshot snapshot, @NonNull SnapshotMetadataChange change) {
        return null;
    }

    @Override
    public void leaveRoom(@NonNull RoomUpdateListener updateListener, @NonNull String roomId) {
        throwIfNotConnected();
    }

    @Override
    public void joinRoom(@NonNull Invitation invitation, @NonNull RoomListener roomListener, @NonNull RealTimeMessageReceivedListener rtListener) {
        throwIfNotConnected();
    }

    @Override
    public void createRoom(@NonNull ArrayList<String> invitees, @NonNull RoomListener roomListener, @NonNull RealTimeMessageReceivedListener rtListener) {
        throwIfNotConnected();
    }

    @Override
    public void createRoom(int minAutoMatchPlayers, int maxAutoMatchPlayers, @NonNull RoomListener roomListener, @NonNull RealTimeMessageReceivedListener rtListener) {
        throwIfNotConnected();
    }

    @Override
    public int sendReliableMessage(@NonNull RealTimeMultiplayer.ReliableMessageSentCallback callback, @NonNull byte[] messageData, @NonNull String roomId, @NonNull String recipientId) {
        throwIfNotConnected();
        return 0;
    }

    @Override
    public void submitScore(@NonNull String boardName, int totalScores) {
        throwIfNotConnected();
        mSubmitScoreBeCalled = true;
    }

    @Override
    public Player getCurrentPlayer() {
        return null;
    }

    @Override
    public void setConnectionListener(@NonNull ApiConnectionListener callback) {
    }

    @Override
    public void setActivity(@NonNull Activity activity) {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode) {

    }

    @Override
    public void selectOpponents(int requestCode, int minOpponents, int maxOpponents) {
        throwIfNotConnected();
    }

    @Override
    public void showInvitationInbox(int requestCode) {
        throwIfNotConnected();
    }

    @Override
    public void showAchievements(int requestCode) {
        throwIfNotConnected();
        mAchievementsShown = true;
    }

    @Override
    public void showWaitingRoom(int requestCode, @NonNull Room room, int minPlayers) {
        throwIfNotConnected();
    }

    @Override
    public void showLeaderboards(@NonNull String boardName, int requestCode) {
        throwIfNotConnected();
        mLeaderboardsShown = true;
    }

    public boolean achievementsShown() {
        return mAchievementsShown;
    }

    public boolean leaderboardsShown() {
        return mLeaderboardsShown;
    }

    public boolean submitScoreBeCalled() {
        return mSubmitScoreBeCalled;
    }
}
