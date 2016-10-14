package com.ivygames.morskoiboi;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.ivygames.common.achievements.AchievementsSettings;
import com.ivygames.common.backup.BackupSharedPreferences;
import com.ivygames.morskoiboi.model.Progress;
import com.ivygames.morskoiboi.progress.ProgressUtils;

import org.commons.logger.Ln;
import org.json.JSONException;

public class GameSettings implements AchievementsSettings {

    private static final int STATE_UNLOCKED = 0;
    private static final int STATE_REVEALED = 1;
    private static final int STATE_HIDDEN = 2;

    private static final String SOUND = "SOUND";
    private static final String VIBRATION = "VIBRATION";
    private static final String GAMES_PLAYED = "GAMES_PLAYED";
    private static final String RATING_BAR = "RATING_BAR";
    private static final String SHOW_PROGRESS_HELP = "SHOW_PROGRESS_HELP";
    private static final String SHOW_SETUP_HELP = "SHOW_SETUP_HELP";
    private static final String ACHIEVEMENT = "ACHIEVEMENT_";
    private static final String PLAYER_NAME = "PLAYER_NAME";
    private static final String PROGRESS = "PROGRESS";
    private static final String PROGRESS_PENALTY = "PROGRESS_PENALTY";
    private static final String NO_ADS = "NO_ADS";
    private static final String SHOULD_AUTO_SIGN_IN = "SIGNED_IN";
    private static final String NEW_RANK_ACHIEVED = "NEW_RANK_ACHIEVED";
    private static final String PROGRESS_MIGRATED = "PROGRESS_MIGRATED";

    private static final int MINIMAL_RATING_BAR = 10;
    private static final int RATING_STEP = 5;
    private static final int MAX_RATING_BAR = 50;

    @NonNull
    private final SharedPreferences mPreferences;
    @NonNull
    private final Context mContext;
    @NonNull
    private final SharedPreferences.Editor mEditor;

    public GameSettings(@NonNull Context context) {
        mContext = context;
        mPreferences = new BackupSharedPreferences(context);
        mEditor = mPreferences.edit();
    }

    public void enableAutoSignIn() {
        mEditor.putBoolean(SHOULD_AUTO_SIGN_IN, true);
    }

    public boolean shouldAutoSignIn() {
        return mPreferences.getBoolean(SHOULD_AUTO_SIGN_IN, false);
    }

    public int getProgressPenalty() {
        return mPreferences.getInt(PROGRESS_PENALTY, 0);
    }

    public void setProgressPenalty(int penalty) {
        mEditor.putInt(PROGRESS_PENALTY, penalty);
    }

    public boolean isSoundOn() {
        return mPreferences.getBoolean(SOUND, true);
    }

    public void setSound(boolean on) {
        mEditor.putBoolean(SOUND, on);
    }

    public boolean isVibrationOn() {
        return mPreferences.getBoolean(VIBRATION, true);
    }

    public void setVibration(boolean on) {
        mEditor.putBoolean(VIBRATION, on);
    }

    public void incrementGamesPlayedCounter() {
        int played = mPreferences.getInt(GAMES_PLAYED, 0);
        Ln.d("incrementing played games counter: " + played);
        mEditor.putInt(GAMES_PLAYED, ++played);
    }

    public boolean shouldProposeRating() {
        int played = mPreferences.getInt(GAMES_PLAYED, 0);
        int ratingBar = mPreferences.getInt(RATING_BAR, MINIMAL_RATING_BAR);
        return played >= ratingBar;
    }

    public void setRated() {
        Ln.d("game has been rated ON");
        mEditor.putInt(RATING_BAR, Integer.MAX_VALUE);
    }

    public void rateLater() {
        // reset games player counter
        mEditor.putInt(GAMES_PLAYED, 0);

        // raise the bother bar
        int ratingBar = mPreferences.getInt(RATING_BAR, MINIMAL_RATING_BAR);
        int newRatingBar = ratingBar + RATING_STEP;
        if (newRatingBar > MAX_RATING_BAR) {
            newRatingBar = MAX_RATING_BAR;
        }
        mEditor.putInt(RATING_BAR, newRatingBar);
        Ln.d("game shall be rated later: after " + newRatingBar + " games");
    }

    @Override
    public void unlockAchievement(@NonNull String achievementId) {
        setAchievementState(achievementId, STATE_UNLOCKED);
    }

    @Override
    public boolean isAchievementUnlocked(@NonNull String achievementId) {
        return STATE_UNLOCKED == getAchievementState(achievementId);
    }

    @Override
    public void revealAchievement(@NonNull String achievementId) {
        setAchievementState(achievementId, STATE_REVEALED);
    }

    @Override
    public boolean isAchievementRevealed(@NonNull String achievementId) {
        return STATE_REVEALED == getAchievementState(achievementId);
    }

    private void setAchievementState(String achievementId, int state) {
        mEditor.putInt(ACHIEVEMENT + achievementId, state);
    }

    private int getAchievementState(String achievementId) {
        return mPreferences.getInt(ACHIEVEMENT + achievementId, STATE_HIDDEN);
    }

    public String getPlayerName() {
        String playerName = mPreferences.getString(PLAYER_NAME, "");
        if (TextUtils.isEmpty(playerName)) {
            playerName = mContext.getString(R.string.player);
            Ln.i("player name is empty - replaced by " + playerName);
        }
        return playerName;
    }

    public void setPlayerName(String name) {
        mEditor.putString(PLAYER_NAME, name);
    }

    public void setProgress(Progress progress) {
        mEditor.putString(PROGRESS, ProgressUtils.toJson(progress).toString());
    }

    @NonNull
    public Progress getProgress() {
        String json = mPreferences.getString(PROGRESS, "");
        Progress progress;
        if (TextUtils.isEmpty(json)) {
            progress = new Progress(0);
        } else {
            try {
                progress = ProgressUtils.fromJson(json);
            } catch (JSONException je) {
                throw new RuntimeException(je);
            }
        }

        return progress;
    }

    public boolean noAds() {
        return mPreferences.getBoolean(NO_ADS, false);
    }

    public void setNoAds() {
        mEditor.putBoolean(NO_ADS, true);
    }

    public boolean showProgressHelp() {
        return mPreferences.getBoolean(SHOW_PROGRESS_HELP, true);
    }

    public boolean showSetupHelp() {
        return mPreferences.getBoolean(SHOW_SETUP_HELP, true);
    }

    public void hideProgressHelp() {
        mEditor.putBoolean(SHOW_PROGRESS_HELP, false);
    }

    public void hideBoardSetupHelp() {
        mEditor.putBoolean(SHOW_SETUP_HELP, false);
    }

    public void newRankAchieved(boolean achieved) {
        mEditor.putBoolean(NEW_RANK_ACHIEVED, achieved);
    }

    public boolean isNewRankAchieved() {
        return mPreferences.getBoolean(NEW_RANK_ACHIEVED, false);
    }

    public boolean hasProgressMigrated() {
        return mPreferences.getBoolean(PROGRESS_MIGRATED, false);
    }

    public void setProgressMigrated() {
        mEditor.putBoolean(PROGRESS_MIGRATED, true);
    }

    @NonNull
    public Progress incrementProgress(int increment) {
        int oldScores = getProgress().getScores();
        Ln.d("incrementing progress (" + oldScores + ") by " + increment);

        int newScore = oldScores + increment;
        Progress newProgress = new Progress(newScore);
        setProgress(newProgress);

        return newProgress;
    }

    @Override
    public String toString() {
        return GameSettings.class.getSimpleName() + "#" + (hashCode() % 1000);
    }
}
