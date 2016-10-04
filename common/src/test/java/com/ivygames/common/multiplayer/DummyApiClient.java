package com.ivygames.common.multiplayer;

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
import com.ivygames.common.googleapi.ApiClient;
import com.ivygames.common.googleapi.ApiConnectionListener;
import com.ivygames.common.invitations.InvitationLoadListener;

import java.util.ArrayList;

class DummyApiClient implements ApiClient {

    @Override
    public void connect() {

    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public void disconnect() {

    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public void registerInvitationListener(@NonNull OnInvitationReceivedListener listener) {

    }

    @Override
    public void loadInvitations(@NonNull InvitationLoadListener listener) {
    }

    @Override
    public void unlockAchievement(@NonNull String achievementId) {

    }

    @Override
    public void revealAchievement(@NonNull String achievementId) {

    }

    @Override
    public void loadAchievements(@NonNull AchievementsResultCallback resultCallback) {
    }

    @Override
    public void increment(@NonNull String achievementId, int steps) {

    }

    @Override
    public void openAsynchronously(@NonNull String snapshotName, @NonNull ResultCallback<? super Snapshots.OpenSnapshotResult> callback) {

    }

    @Override
    public PendingResult<Snapshots.OpenSnapshotResult> resolveConflict(@NonNull String conflictId, @NonNull Snapshot snapshot) {
        return null;
    }

    @Override
    public PendingResult<Snapshots.CommitSnapshotResult> commitAndClose(@NonNull Snapshot snapshot, @NonNull SnapshotMetadataChange change) {
        return null;
    }

    @Override
    public void leaveRoom(@NonNull RoomUpdateListener updateListener, @NonNull String roomId) {

    }

    @Override
    public void joinRoom(@NonNull Invitation invitation, @NonNull RoomListener roomListener, @NonNull RealTimeMessageReceivedListener rtListener) {

    }

    @Override
    public void createRoom(@NonNull ArrayList<String> invitees, int minAutoMatchPlayers, int maxAutoMatchPlayers, @NonNull RoomListener roomListener, @NonNull RealTimeMessageReceivedListener rtListener) {

    }

    @Override
    public void createRoom(int minAutoMatchPlayers, int maxAutoMatchPlayers, @NonNull RoomListener roomListener, @NonNull RealTimeMessageReceivedListener rtListener) {

    }

    @Override
    public int sendReliableMessage(@NonNull RealTimeMultiplayer.ReliableMessageSentCallback callback, @NonNull byte[] messageData, @NonNull String roomId, @NonNull String recipientId) {
        return 0;
    }

    @Override
    public void submitScore(@NonNull String boardName, int totalScores) {

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

    }

    @Override
    public void showInvitationInbox(int requestCode) {

    }

    @Override
    public void showAchievements(int requestCode) {

    }

    @Override
    public void showWaitingRoom(int requestCode, @NonNull Room room, int minPlayers) {

    }

    @Override
    public void showLeaderboards(@NonNull String boardName, int requestCode) {

    }
}
