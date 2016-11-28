package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;

import com.ivygames.battleship.ship.Ship;

import java.util.Collection;

public interface ScoresCalculator {
    int calcScoresForAndroidGame(@NonNull Collection<Ship> ships, @NonNull ScoreStatistics statistics);
}
