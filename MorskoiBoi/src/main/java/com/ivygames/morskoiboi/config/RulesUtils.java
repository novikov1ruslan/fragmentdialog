package com.ivygames.morskoiboi.config;

import android.support.annotation.NonNull;

import com.ivygames.battleship.ship.Ship;
import com.ivygames.morskoiboi.ScoresCalculator;
import com.ivygames.morskoiboi.achievement.AchievementsManager;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.ScoreStatistics;

import java.util.Collection;

public abstract class RulesUtils {

    private static final int SURRENDER_PENALTY_PER_DECK = 100;
    private static final int MIN_SURRENDER_PENALTY = 1000;
    private static final int MAX_SCORE_FOR_SURRENDERED_GAME = 5000;

    public static int calcSurrenderPenalty(int[] allShipsSizes, @NonNull Collection<Ship> remainedShips) {
        int decksLost = getShipsHealth(allShipsSizes) - getShipsHealth(remainedShips);
        return decksLost * SURRENDER_PENALTY_PER_DECK + MIN_SURRENDER_PENALTY;
    }

    private static int getShipsHealth(int[] ships) {
        int totalHealth = 0;
        for (int ship : ships) {
            totalHealth += ship;
        }
        return totalHealth;
    }

    private static int getShipsHealth(@NonNull Collection<Ship> ships) {
        int health = 0;
        for (Ship ship : ships) {
            health += ship.getHealth();
        }
        return health;
    }

    public static int calcTotalScores(@NonNull Collection<Ship> ships, @NonNull Game game,
                                      @NonNull ScoreStatistics statistics,
                                      boolean surrendered, @NonNull ScoresCalculator scoresCalculator) {
        int score = calculateScoresForGame(ships, statistics, game, scoresCalculator);

        if (surrendered) {
            score = score / 2;
            if (score > MAX_SCORE_FOR_SURRENDERED_GAME) {
                score = MAX_SCORE_FOR_SURRENDERED_GAME;
            }
        }
        return score;
    }

    private static int calculateScoresForGame(@NonNull Collection<Ship> ships, @NonNull ScoreStatistics statistics, @NonNull Game game, ScoresCalculator scoresCalculator) {
        int progress;
        if (game.getWinPoints() == Game.WIN_POINTS_SHOULD_BE_CALCULATED) {
            progress = scoresCalculator.calcScoresForAndroidGame(ships, statistics) * AchievementsManager.NORMAL_DIFFICULTY_PROGRESS_FACTOR;
        } else {
            progress = game.getWinPoints();
        }
        return progress;
    }
}
