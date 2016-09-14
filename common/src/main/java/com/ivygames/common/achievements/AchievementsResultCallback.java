package com.ivygames.common.achievements;

import android.support.annotation.NonNull;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.achievement.Achievement;
import com.google.android.gms.games.achievement.AchievementBuffer;
import com.google.android.gms.games.achievement.Achievements.LoadAchievementsResult;

import org.commons.logger.Ln;

import java.util.ArrayList;
import java.util.List;

public final class AchievementsResultCallback implements ResultCallback<LoadAchievementsResult> {
    @NonNull
    private final AchievementsLoadListener mListener;

    AchievementsResultCallback(@NonNull AchievementsLoadListener listener) {
        mListener = listener;
    }

    @Override
    public void onResult(@NonNull LoadAchievementsResult result) {
        int statusCode = result.getStatus().getStatusCode();
        if (achievementsLoaded(statusCode)) {
            AchievementBuffer buffer = result.getAchievements();
            int count = buffer.getCount();
            Ln.d(count + " achievements loaded");
            if (count < 1) {
                return;
            }

            List<GameAchievement> achievements = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                achievements.add(toGameAchievement(buffer.get(i)));
            }
            buffer.release();
            mListener.onAchievementsLoaded(achievements);
        } else {
            Ln.w("achievements loading failed: " + statusCode);
        }
    }

    private GameAchievement toGameAchievement(@NonNull Achievement achievement) {
        return new GameAchievement(achievement.getAchievementId(), achievement.getName(),
                achievement.getState());
    }

    private boolean achievementsLoaded(int statusCode) {
        return statusCode == GamesStatusCodes.STATUS_OK || statusCode == GamesStatusCodes.STATUS_NETWORK_ERROR_STALE_DATA;
    }

}
