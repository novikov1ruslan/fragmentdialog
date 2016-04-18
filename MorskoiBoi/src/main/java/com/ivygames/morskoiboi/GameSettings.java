package com.ivygames.morskoiboi;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.ivygames.morskoiboi.model.Progress;
import com.ivygames.morskoiboi.progress.ProgressUtils;

import org.commons.logger.Ln;
import org.json.JSONException;

public class GameSettings {

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
    private final EditableSharedPreferences internal;

    public GameSettings(@NonNull Context context) {
        internal = new EditableSharedPreferences(context);
    }

    public void enableAutoSignIn() {
        internal.putBoolean(SHOULD_AUTO_SIGN_IN, true);
    }

    public boolean shouldAutoSignIn() {
        return internal.getBoolean(SHOULD_AUTO_SIGN_IN, false);
    }

    public int getProgressPenalty() {
        return internal.getInt(PROGRESS_PENALTY, 0);
    }

    public void setProgressPenalty(int penalty) {
        internal.putInt(PROGRESS_PENALTY, penalty);
    }

    public boolean isSoundOn() {
        return internal.getBoolean(SOUND, true);
    }

    public void setSound(boolean on) {
        internal.putBoolean(SOUND, on);
    }

    public boolean isVibrationOn() {
        return internal.getBoolean(VIBRATION, true);
    }

    public void setVibration(boolean on) {
        internal.putBoolean(VIBRATION, on);
    }

    public void incrementGamesPlayedCounter() {
        int played = internal.getInt(GAMES_PLAYED, 0);
        Ln.d("incrementing played games counter: " + played);
        internal.putInt(GAMES_PLAYED, ++played);
    }

    public boolean shouldProposeRating() {
        int played = internal.getInt(GAMES_PLAYED, 0);
        int ratingBar = internal.getInt(RATING_BAR, MINIMAL_RATING_BAR);
        return played >= ratingBar;
    }

    public void setRated() {
        Ln.d("game has been rated ON");
        internal.putInt(RATING_BAR, Integer.MAX_VALUE);
    }

    public void rateLater() {
        // reset games player counter
        internal.putInt(GAMES_PLAYED, 0);

        // raise the bother bar
        int ratingBar = internal.getInt(RATING_BAR, MINIMAL_RATING_BAR);
        int newRatingBar = ratingBar + RATING_STEP;
        if (newRatingBar > MAX_RATING_BAR) {
            newRatingBar = MAX_RATING_BAR;
        }
        internal.putInt(RATING_BAR, newRatingBar);
        Ln.d("game shall be rated later: after " + newRatingBar + " games");
    }

    public void unlockAchievement(String achievementId) {
        setAchievementState(achievementId, STATE_UNLOCKED);
    }

    public boolean isAchievementUnlocked(String achievementId) {
        return STATE_UNLOCKED == getAchievementState(achievementId);
    }

    public void revealAchievement(String achievementId) {
        setAchievementState(achievementId, STATE_REVEALED);
    }

    public boolean isAchievementRevealed(String achievementId) {
        return STATE_REVEALED == getAchievementState(achievementId);
    }

    private void setAchievementState(String achievementId, int state) {
        internal.putInt(ACHIEVEMENT + achievementId, state);
    }

    private int getAchievementState(String achievementId) {
        return internal.getInt(ACHIEVEMENT + achievementId, STATE_HIDDEN);
    }

    public String getPlayerName() {
        return internal.getString(PLAYER_NAME, "");
    }

    public void setPlayerName(String name) {
        internal.putString(PLAYER_NAME, name);
    }

    public void setProgress(Progress progress) {
        internal.putString(PROGRESS, ProgressUtils.toJson(progress).toString());
    }

    @NonNull
    public Progress getProgress() {
        String json = internal.getString(PROGRESS, "");
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
        if (Constants.FREE) {
            return internal.getBoolean(NO_ADS, false);
        }

        return true;
    }

    public void setNoAds() {
        internal.putBoolean(NO_ADS, true);
    }

    public boolean showProgressHelp() {
        return internal.getBoolean(SHOW_PROGRESS_HELP, true);
    }

    public boolean showSetupHelp() {
        return internal.getBoolean(SHOW_SETUP_HELP, true);
    }

    public void hideProgressHelp() {
        internal.putBoolean(SHOW_PROGRESS_HELP, false);
    }

    public void hideBoardSetupHelp() {
        internal.putBoolean(SHOW_SETUP_HELP, false);
    }

    public void newRankAchieved(boolean achieved) {
        internal.putBoolean(NEW_RANK_ACHIEVED, achieved);
    }

    public boolean isNewRankAchieved() {
        return internal.getBoolean(NEW_RANK_ACHIEVED, false);
    }

    public boolean hasProgressMigrated() {
        return internal.getBoolean(PROGRESS_MIGRATED, false);
    }

    public void setProgressMigrated() {
        internal.putBoolean(PROGRESS_MIGRATED, true);
    }

}
