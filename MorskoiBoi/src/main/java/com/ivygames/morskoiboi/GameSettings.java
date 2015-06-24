package com.ivygames.morskoiboi;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.ivygames.morskoiboi.model.Progress;

import org.commons.logger.Ln;
import org.json.JSONException;

import java.util.Map;
import java.util.Set;

public class GameSettings {

    private static final int STATE_UNLOCKED = 0;
    private static final int STATE_REVEALED = 1;
    private static final int STATE_HIDDEN = 2;

    private static final String SOUND = "SOUND";
    private static final String VIBRATION = "VIBRATION";
    private static final String GAMES_PLAYED = "GAMES_PLAYED";
    private static final String RATING_BAR = "RATING_BAR";
    private static final String SHOW_TIPS = "SHOW_TIPS";
    private static final String SHOW_PROGRESS_TIPS = "SHOW_PROGRESS_TIPS";
    private static final String ACHIEVEMENT = "ACHIEVEMENT_";
    private static final String PLAYER_NAME = "PLAYER_NAME";
    private static final String PROGRESS = "PROGRESS";
    private static final String PROGRESS_PENALTY = "PROGRESS_PENALTY";
    private static final String NO_ADS = "NO_ADS";
    private static final String SHOULD_AUTO_SIGN_IN = "SIGNED_IN";

    private static final int MINIMAL_RATING_BAR = 10;
    private static final int RATING_STEP = 5;
    private static final int MAX_RATING_BAR = 50;

    private static final GameSettings INSTANCE = new GameSettings();

    private final Internal internal;

    public static GameSettings get() {
        return INSTANCE;
    }

    private GameSettings() {
        internal = Internal.getInstance();
    }

    public void clear() {
        internal.clear();
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

    public boolean showTips() {
        return internal.getBoolean(SHOW_TIPS, true);
    }

    public void setShowTips(boolean show) {
        internal.putBoolean(SHOW_TIPS, show);
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
        internal.putString(PROGRESS, progress.toJson().toString());
    }

    public void setProgress(String progress) {
        internal.putString(PROGRESS, progress);
    }

    public Progress getProgress() {
        String json = internal.getString(PROGRESS, "");
        Progress progress;
        if (TextUtils.isEmpty(json)) {
            progress = new Progress(0);
        } else {
            try {
                progress = Progress.fromJson(json);
            } catch (JSONException je) {
                throw new RuntimeException(je);
            }
        }

        return progress;
    }

    public boolean noAds() {
        return internal.getBoolean(NO_ADS, false);
    }

    public void setNoAds() {
        internal.putBoolean(NO_ADS, true);
    }

    public boolean showProgressTip() {
        return internal.getBoolean(SHOW_PROGRESS_TIPS, true);
    }

    public void progressLearned() {
        internal.putBoolean(SHOW_PROGRESS_TIPS, false);
    }

    private static class Internal implements SharedPreferences, SharedPreferences.Editor {

        private final SharedPreferences sp;
        private final SharedPreferences.Editor editor;

        private static final Internal INSTANCE = new Internal();

        public static Internal getInstance() {
            return INSTANCE;
        }

        @SuppressLint("CommitPrefEdits")
        private Internal() {
            sp = PreferenceManager.getDefaultSharedPreferences(BattleshipApplication.get());
            editor = sp.edit();
        }

        @Override
        public boolean contains(String key) {
            throw new RuntimeException("Not implemented");
        }

        @Override
        public Editor edit() {
            return this;
        }

        @Override
        public Map<String, ?> getAll() {
            throw new RuntimeException("Not implemented");
        }

        @Override
        public boolean getBoolean(String key, boolean defValue) {
            return sp.getBoolean(key, defValue);
        }

        @Override
        public float getFloat(String key, float defValue) {
            return sp.getFloat(key, defValue);
        }

        @Override
        public int getInt(String key, int defValue) {
            return sp.getInt(key, defValue);
        }

        @Override
        public long getLong(String key, long defValue) {
            return sp.getLong(key, defValue);
        }

        @Override
        public String getString(String key, String defValue) {
            return sp.getString(key, defValue);
        }

        @Override
        public Set<String> getStringSet(String arg0, Set<String> arg1) {
            throw new RuntimeException("Not implemented");
        }

        @Override
        public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
            throw new RuntimeException("Not implemented");
        }

        @Override
        public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
            throw new RuntimeException("Not implemented");
        }

        @Override
        public void apply() {
            throw new RuntimeException("Not implemented");
        }

        @Override
        public Editor clear() {
            editor.clear();
            editor.apply();
            return editor;
        }

        @Override
        public boolean commit() {
            throw new RuntimeException("Not implemented");
        }

        @Override
        public Editor putBoolean(String key, boolean value) {
            Ln.d("<-- (" + key + ',' + value + ')');
            editor.putBoolean(key, value);
            editor.apply();
            return editor;
        }

        @Override
        public Editor putFloat(String key, float value) {
            Ln.d("<-- (" + key + ',' + value + ')');
            editor.putFloat(key, value);
            editor.apply();
            return editor;
        }

        @Override
        public Editor putInt(String key, int value) {
            Ln.d("<-- (" + key + ',' + value + ')');
            editor.putInt(key, value);
            editor.apply();
            return editor;
        }

        @Override
        public Editor putLong(String key, long value) {
            Ln.d("<-- (" + key + ',' + value + ')');
            editor.putLong(key, value);
            editor.apply();
            return editor;
        }

        @Override
        public Editor putString(String key, String value) {
            Ln.d("<-- (" + key + ',' + value + ')');
            editor.putString(key, value);
            editor.apply();
            return editor;
        }

        @Override
        public Editor putStringSet(String arg0, Set<String> arg1) {
            throw new RuntimeException("Not implemented");
        }

        @Override
        public Editor remove(String key) {
            // Ln.d('(' + key + ')');
            // editor.remove(key);
            // editor.apply();
            Ln.e("remove is not needed in this game");
            return editor;
        }
    }

}
