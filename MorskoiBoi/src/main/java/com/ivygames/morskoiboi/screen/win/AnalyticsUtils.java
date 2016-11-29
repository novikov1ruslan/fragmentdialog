package com.ivygames.morskoiboi.screen.win;

import com.ivygames.common.analytics.AnalyticsEvent;
import com.ivygames.morskoiboi.BuildConfig;
import com.ivygames.morskoiboi.Rank;

import org.commons.logger.Ln;

class AnalyticsUtils {
    static boolean trackPromotionEvent(int oldScores, int newScores) {
        Rank lastRank = Rank.getBestRankForScore(oldScores);
        Rank newRank = Rank.getBestRankForScore(newScores);
        if (newRank != lastRank) {
            String label = lastRank + " promoted to " + newRank;
            if (BuildConfig.DEBUG) {
                Ln.i("game is in test mode, not tracking promotion event: " + label);
            }
            else {
                AnalyticsEvent.send("promotion", label);
                Ln.i(label);
            }

            return true;
        }

        return false;
    }
}
