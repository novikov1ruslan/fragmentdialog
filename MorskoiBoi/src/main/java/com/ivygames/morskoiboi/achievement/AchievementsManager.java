package com.ivygames.morskoiboi.achievement;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.appstate.AppStateManager;
import com.google.android.gms.appstate.AppStateManager.StateResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.achievement.Achievements.LoadAchievementsResult;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadata;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.games.snapshot.Snapshots;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.analytics.AnalyticsEvent;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.ui.BattleshipActivity;

import org.apache.commons.lang3.Validate;
import org.commons.logger.Ln;

import java.io.IOException;
import java.util.Collection;

public final class AchievementsManager {

    // achievements
    static final String NAVAL_MERIT = "CgkI8s_j3LsfEAIQAQ";
    static final String ORDER_OF_HONOUR = "CgkI8s_j3LsfEAIQGg";
    static final String BRAVERY_AND_COURAGE = "CgkI8s_j3LsfEAIQAg";
    static final String EXTRA_BRAVERY_AND_COURAGE = "CgkI8s_j3LsfEAIQGw";
    static final String FLYING_DUTCHMAN = "CgkI8s_j3LsfEAIQFw";
    static final String STEALTH = "CgkI8s_j3LsfEAIQAw";
    static final String LIFE_SAVING = "CgkI8s_j3LsfEAIQHA";
    /**
     * Win the battle with your aircraft carrier still afloat.
     */
    static final String AIRCRAFTSMAN = "CgkI8s_j3LsfEAIQBA";
    /**
     * Win the battle with both of your Cruisers afloat.
     */
    static final String CRUISER_COMMANDER = "CgkI8s_j3LsfEAIQDw";
    static final String DESTROYER = "CgkI8s_j3LsfEAIQBQ";
    static final String MILITARY_ACHIEVEMENTS = "CgkI8s_j3LsfEAIQEA";

    public static final int NORMAL_DIFFICULTY_PROGRESS_FACTOR = 1;

    private final GoogleApiClient mApiClient;
    private final Tracker mGaTracker;
    private final GameSettings mSettings = GameSettings.get();

    private final AchievementsResultCallback achievementsLoadCallback;
    private final AppStateResultCallback appStateCallback;

    public AchievementsManager(GoogleApiClient apiClient, Tracker tracker) {
        Validate.notNull(tracker);
        mGaTracker = tracker;

        Validate.notNull(apiClient);
        mApiClient = apiClient;
        achievementsLoadCallback = new AchievementsResultCallback(apiClient);
        appStateCallback = new AppStateResultCallback(apiClient, tracker);
    }

    public void loadAchievements() {
        PendingResult<LoadAchievementsResult> loadResult = Games.Achievements.load(mApiClient, true);
        loadResult.setResultCallback(achievementsLoadCallback);
        PendingResult<StateResult> stateResult = AppStateManager.load(mApiClient, AchievementsUtils.STATE_KEY);
        stateResult.setResultCallback(appStateCallback);
    }

    /**
     * @return true if change has been made
     */
    private boolean unlockIfNotUnlocked(String achievementId) {
        boolean alreadyUnlocked = mSettings.isAchievementUnlocked(achievementId);
        if (alreadyUnlocked) {
            Ln.d(AchievementsManager.name(achievementId) + " already unlocked - no need to unlock");
            return false;
        } else {
            unlock(achievementId);
            return true;
        }
    }

    public void processAchievements(Game game, Collection<Ship> ships) {
        Ln.v("game: " + game + "; ships: " + ships);

        processCombo(game.getCombo());
        processShellsLeft(game.getShells());
        processTimeSpent(game.getTimeSpent());
        processShipsLeft(ships);

        if (game.calcTotalScores(ships) >= 15000) {
            if (mSettings.isAchievementUnlocked(MILITARY_ACHIEVEMENTS)) {
                Ln.v(AchievementsManager.name(MILITARY_ACHIEVEMENTS) + " is already unlocked - do not increment");
            } else {
                increment(MILITARY_ACHIEVEMENTS, 1);
            }
        }
    }

    private void processCombo(int combo) {
        Ln.v("combo=" + combo);
        if (combo >= 1) {
            if (unlockIfNotUnlocked(NAVAL_MERIT)) {
                reveal(ORDER_OF_HONOUR);
            } else if (combo >= 2) {
                unlockIfNotUnlocked(ORDER_OF_HONOUR);
            }
        }
    }

    private void processShellsLeft(int shells) {
        Ln.v("shells=" + shells);
        if (shells >= 50) {
            if (unlockIfNotUnlocked(BRAVERY_AND_COURAGE)) {
                reveal(EXTRA_BRAVERY_AND_COURAGE);
            } else if (shells >= 60) {
                unlockIfNotUnlocked(EXTRA_BRAVERY_AND_COURAGE);
            }
        }
    }

    private void processTimeSpent(long time) {
        Ln.v("time=" + time);
        if (time <= 60000) {
            if (unlockIfNotUnlocked(FLYING_DUTCHMAN)) {
                // TODO: reveal next
            } else if (time < 50000) {
                // TODO: unlock next
            }
        }
    }

    private void processShipsLeft(Collection<Ship> ships) {
        Ln.v("ships=" + ships);

        int shipsLeft = AchievementsManager.countAliveShips(ships);
        if (shipsLeft >= 3) {
            if (unlockIfNotUnlocked(STEALTH)) {
                reveal(LIFE_SAVING);
            } else if (shipsLeft >= 4) {
                unlockIfNotUnlocked(LIFE_SAVING);
            }
        }

        int cruisersCounter = 0;
        int destroyersCounter = 0;
        for (Ship ship : ships) {
            if (ship.getSize() == 4 && !ship.isDead()) {
                unlockIfNotUnlocked(AIRCRAFTSMAN);
            } else if (ship.getSize() == 3 && !ship.isDead()) {
                if (cruisersCounter == 1) {
                    unlockIfNotUnlocked(CRUISER_COMMANDER);
                }
                cruisersCounter++;
            } else if (ship.getSize() == 2 && !ship.isDead()) {
                if (destroyersCounter == 2) {
                    unlockIfNotUnlocked(DESTROYER);
                }
                destroyersCounter++;
            }
        }
    }

    private static int countAliveShips(Collection<Ship> ships) {
        int left = 0;
        for (Ship ship : ships) {
            if (!ship.isDead()) {
                left++;
            }
        }
        return left;
    }

    private void unlock(String achievementId) {
        Ln.d("unlocking achievement: " + AchievementsManager.name(achievementId));
        mSettings.unlockAchievement(achievementId);
        mGaTracker.send(new AnalyticsEvent("achievement", achievementId).build());
        if (mApiClient.isConnected()) {
            Games.Achievements.unlock(mApiClient, achievementId);
        }
    }

    private void increment(String achievementId, int steps) {
        Ln.d("incrementing achievement: " + AchievementsManager.name(achievementId) + " by " + steps);
        if (mApiClient.isConnected()) {
            Games.Achievements.increment(mApiClient, achievementId, steps);
        }
    }

    private void reveal(String achievementId) {
        Ln.d("revealing achievement: " + AchievementsManager.name(achievementId));
        AchievementsUtils.setRevealed(achievementId);
        if (mApiClient.isConnected()) {
            Games.Achievements.reveal(mApiClient, achievementId);
        }
    }

    private static String name(String achievementId) {
        return AchievementsUtils.debugName(achievementId);
    }


    /**
     * Async migrate the data in Cloud Save (stateKey APP_STATE_KEY) to a Snapshot in the Saved
     * Games service with unique snap 'Snapshot-{APP_STATE_KEY}'.  If no such Snapshot exists,
     * create a Snapshot and populate all fields.  If the Snapshot already exists, update the
     * appropriate data and metadata.  After migrate, the UI will be cleared and the data will be
     * available to load from Snapshots.
     */
    public void cloudSaveMigrate() {
        final boolean createIfMissing = true;

        // Note: when migrating your users from Cloud Save to Saved Games, you will need to perform
        // the migration process at most once per device.  You should keep track of the migration
        // status locally for each AppState data slot (using SharedPreferences or similar)
        // to avoid repeating network calls or migrating the same AppState data multiple times.

        // Compute SnapshotMetadata fields based on the information available from AppState.  In
        // this case there is no data available to auto-generate a description, cover image, or
        // playedTime.  It is strongly recommended that you generate unique and meaningful
        // values for these fields based on the data in your app.
        final String snapshotName = makeSnapshotName(AchievementsUtils.STATE_KEY);
        final String description = "Saved game #" + AchievementsUtils.STATE_KEY;
        final long playedTimeMillis = 60 * 60 * 1000;
        final Bitmap bitmap = null;//BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);

        AsyncTask<Void, Void, Boolean> migrateTask = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
//                showProgressDialog("Migrating");
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                // Get AppState Data
                AppStateManager.StateResult load = AppStateManager.load(mApiClient, AchievementsUtils.STATE_KEY).await();

                if (!load.getStatus().isSuccess()) {
                    Ln.w("Could not load App State for migration.");
                    return false;
                }

                // Get Data from AppState
                byte[] data = load.getLoadedResult().getLocalData();

                // Open the snapshot, creating if necessary
                Snapshots.OpenSnapshotResult open = Games.Snapshots.open(mApiClient, snapshotName, createIfMissing).await();

                if (!open.getStatus().isSuccess()) {
                    Ln.w("Could not open Snapshot for migration.");
                    // TODO: Handle Snapshot conflicts
                    // Note: one reason for failure to open a Snapshot is conflicting saved games.
                    // This is outside the scope of this sample, however you should resolve such
                    // conflicts in your own app by following the steps outlined here:
                    // https://developers.google.com/games/services/android/savedgames#handling_saved_game_conflicts
                    return false;
                }

                // Write the new data to the snapshot
                Snapshot snapshot = open.getSnapshot();
                snapshot.getSnapshotContents().writeBytes(data);

                // Change metadata
                SnapshotMetadataChange metadataChange = new SnapshotMetadataChange.Builder()
                        .fromMetadata(snapshot.getMetadata())
                        .setCoverImage(bitmap)
                        .setDescription(description)
                        .setPlayedTimeMillis(playedTimeMillis)
                        .build();

                Snapshots.CommitSnapshotResult commit = Games.Snapshots.commitAndClose(mApiClient, snapshot, metadataChange).await();

                if (!commit.getStatus().isSuccess()) {
                    Ln.w("Failed to commit Snapshot.");
                    return false;
                }

                // No failures
                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
//                    displayMessage(getString(R.string.cloud_save_migrate_success), false);
                } else {
//                    displayMessage(getString(R.string.cloud_save_migrate_failure), true);
                }

//                dismissProgressDialog();
            }
        };
        migrateTask.execute();
    }

    /**
     * Generate a unique Snapshot name from an AppState stateKey.
     *
     * @param appStateKey the stateKey for the Cloud Save data.
     * @return a unique Snapshot name that maps to the stateKey.
     */
    private String makeSnapshotName(int appStateKey) {
        return "Snapshot-" + String.valueOf(appStateKey);
    }

    /**
     * Load a Snapshot from the Saved Games service based on its unique name.  After load, the UI
     * will update to display the Snapshot data and SnapshotMetadata.
     *
     * @param snapshotName the unique name of the Snapshot.
     */
    private void savedGamesLoad(String snapshotName) {
        PendingResult<Snapshots.OpenSnapshotResult> pendingResult = Games.Snapshots.open(
                mApiClient, snapshotName, false);

//        showProgressDialog("Loading Saved Game");
        ResultCallback<Snapshots.OpenSnapshotResult> callback =
                new ResultCallback<Snapshots.OpenSnapshotResult>() {
                    @Override
                    public void onResult(Snapshots.OpenSnapshotResult openSnapshotResult) {
                        if (openSnapshotResult.getStatus().isSuccess()) {
//                            displayMessage(getString(R.string.saved_games_load_success), false);
                            byte[] data = new byte[0];
                            try {
                                data = openSnapshotResult.getSnapshot().getSnapshotContents().readFully();
                            } catch (IOException e) {
//                                displayMessage("Exception reading snapshot: " + e.getMessage(), true);
                            }
                            setData(new String(data));
                            displaySnapshotMetadata(openSnapshotResult.getSnapshot().getMetadata());
                        } else {
//                            displayMessage(getString(R.string.saved_games_load_failure), true);
                        }
//                        dismissProgressDialog();
                    }
                };
        pendingResult.setResultCallback(callback);
    }

    private void setData(String data) {
//        if (data == null) {
//            dataEditText.setText("");
//        } else {
//            dataEditText.setText(data);
//        }
    }

    private void displaySnapshotMetadata(SnapshotMetadata metadata) {
        if (metadata == null) {
            return;
        }

        String metadataStr = "Source: Saved Games" + '\n'
                + "Description: " + metadata.getDescription() + '\n'
                + "Name: " + metadata.getUniqueName() + '\n'
                + "Last Modified: " + String.valueOf(metadata.getLastModifiedTimestamp()) + '\n'
                + "Played Time: " + String.valueOf(metadata.getPlayedTime()) + '\n'
                + "Cover Image URL: " + metadata.getCoverImageUrl();
    }

    /**
     * Launch the UI to select a Snapshot from the user's Saved Games.  The result of this
     * selection will be returned to onActivityResult.
     */
    public void savedGamesSelect(Activity activity) {
        final boolean allowAddButton = false;
        final boolean allowDelete = false;
        Intent intent = Games.Snapshots.getSelectSnapshotIntent(
                mApiClient, "Saved Games", allowAddButton, allowDelete,
                Snapshots.DISPLAY_LIMIT_NONE);

//        showProgressDialog("Loading");
        activity.startActivityForResult(intent, BattleshipActivity.RC_SELECT_SNAPSHOT);
    }

    public void onActivityResult2(int resultCode, Intent data) {
        Ln.d("RC_SELECT_SNAPSHOT, resultCode = " + resultCode);
        if (resultCode == Activity.RESULT_OK) {
            // Successfully returned from Snapshot selection UI
            if (data != null) {
                Bundle bundle = data.getExtras();
                SnapshotMetadata selected = Games.Snapshots.getSnapshotFromBundle(bundle);
                if (selected == null) {
                    // No snapshot in the Intent bundle, display error message
//                        displayMessage(getString(R.string.saved_games_select_failure), true);
                    setData(null);
                    displaySnapshotMetadata(null);
                } else {
                    // Found Snapshot Metadata in Intent bundle.  Load Snapshot by name.
                    String snapshotName = selected.getUniqueName();
                    savedGamesLoad(snapshotName);
                }
            }
        } else {
            // User canceled the select intent or it failed for some other reason
//                displayMessage(getString(R.string.saved_games_select_cancel), true);
            setData(null);
            displaySnapshotMetadata(null);
        }
    }

}
