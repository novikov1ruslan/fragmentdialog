package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.ScoreStatistics;
import com.ivygames.morskoiboi.model.Ship;

import java.util.Collection;

public interface Rules {
    @NonNull
    int[] getAllShipsSizes();

    int calcTotalScores(@NonNull Collection<Ship> ships, @NonNull Game game,
                        @NonNull ScoreStatistics statistics, boolean surrendered);

    boolean allowAdjacentShips();
}
