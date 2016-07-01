package com.ivygames.morskoiboi;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.achievement.Achievements;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMultiplayer;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.games.snapshot.Snapshots;
import com.ivygames.morskoiboi.invitations.InvitationLoadListener;

public class DummyApiClient implements ApiClient {
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
    public void unregisterConnectionCallbacks(@NonNull GoogleApiClient.ConnectionCallbacks callbacks) {

    }

    @Override
    public void unregisterConnectionFailedListener(@NonNull GoogleApiClient.OnConnectionFailedListener listener) {

    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public boolean resolveConnectionFailure(@NonNull Activity activity, @NonNull ConnectionResult connectionResult, int rcSignIn, @NonNull String string) {
        return false;
    }

    @Override
    public void unregisterInvitationListener() {

    }

    @Override
    public void registerInvitationListener(@NonNull OnInvitationReceivedListener listener) {

    }

    @Override
    public void loadInvitations(InvitationLoadListener listener) {
    }

    @Override
    public Intent getAchievementsIntent() {
        return null;
    }

    @Override
    public Intent getLeaderboardIntent(@NonNull String boardName) {
        return null;
    }

    @Override
    public void unlock(@NonNull String achievementId) {

    }

    @Override
    public void reveal(@NonNull String achievementId) {

    }

    @Override
    public PendingResult<Achievements.LoadAchievementsResult> load(boolean b) {
        return null;
    }

    @Override
    public void increment(@NonNull String achievementId, int steps) {

    }

    @Override
    public PendingResult<Snapshots.OpenSnapshotResult> open(@NonNull String snapshotName, boolean createIfMissing) {
        return null;
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
    public void leave(@NonNull RoomUpdateListener updateListener, @NonNull String roomId) {

    }

    @Override
    public void join(@NonNull RoomConfig roomConfig) {

    }

    @Override
    public void create(@NonNull RoomConfig build) {

    }

    @Override
    public int sendReliableMessage(@NonNull RealTimeMultiplayer.ReliableMessageSentCallback callback, @NonNull byte[] messageData, @NonNull String roomId, @NonNull String recipientId) {
        return 0;
    }

    @Override
    public Intent getInvitationInboxIntent() {
        return null;
    }

    @Override
    public Intent getSelectOpponentsIntent(int minOpponents, int maxOpponents, boolean b) {
        return null;
    }

    @Override
    public Intent getWaitingRoomIntent(@NonNull Room room, int minPlayers) {
        return null;
    }

    @Override
    public void submitScore(@NonNull String boardName, int totalScores) {

    }

    @Override
    public Player getCurrentPlayer() {
        return null;
    }

    @Override
    public void setConnectionCallbacks(@NonNull GoogleApiClient.ConnectionCallbacks callback) {

    }

    @Override
    public void setOnConnectionFailedListener(@NonNull GoogleApiClient.OnConnectionFailedListener listener) {

    }
}
