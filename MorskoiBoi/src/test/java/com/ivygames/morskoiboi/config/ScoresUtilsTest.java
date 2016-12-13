package com.ivygames.morskoiboi.config;

import android.support.annotation.NonNull;

import com.ivygames.ShipTestUtils;
import com.ivygames.battleship.RussianRules;
import com.ivygames.battleship.ShipUtils;
import com.ivygames.battleship.ship.Ship;
import com.ivygames.common.multiplayer.MultiplayerRoom;
import com.ivygames.morskoiboi.Game;
import com.ivygames.morskoiboi.RandomOrientationBuilder;
import com.ivygames.morskoiboi.ScoreStatistics;
import com.ivygames.morskoiboi.ScoresCalculator;
import com.ivygames.morskoiboi.ai.AndroidGame;
import com.ivygames.morskoiboi.bluetooth.BluetoothConnection;
import com.ivygames.morskoiboi.bluetooth.BluetoothGame;
import com.ivygames.morskoiboi.rt.InternetGame;
import com.ivygames.morskoiboi.russian.RussianScoresCalculator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ScoresUtilsTest {
    private static final int TOTAL_SHIPS = 10;
    private static final int TOTAL_SQUARES_OCCUPIED_BY_SHIPS = 20;
    private static final long MIN_TIME = 20000;
    private static final long MAX_TIME = 300000;

    private final Game mBluetoothGame = new BluetoothGame(mock(BluetoothConnection.class));
    private final Game mInternetGame = new InternetGame(mock(MultiplayerRoom.class));
    private final Game mAndroidGame = new AndroidGame();

    private final ScoresCalculator scoresCalculator = new RussianScoresCalculator();
    @Mock
    private ScoreStatistics statistics;
    private int[] allShipsSizes;
    private RandomOrientationBuilder orientationBuilder;

    @Before
    public void setUp() {
        initMocks(this);
        allShipsSizes = new RussianRules().getAllShipsSizes();
        Random random = mock(Random.class);
        orientationBuilder = new RandomOrientationBuilder(random);
    }

    @Test
    public void max_scores_for_android_game_is_31250() {
        ScoreStatistics statistics = mockPerfectGame();
        when(statistics.getTimeSpent()).thenReturn(MIN_TIME);
        int score = ScoresUtils.calculateScoresForGame(ShipUtils.createNewShips(allShipsSizes, orientationBuilder), statistics, mAndroidGame, scoresCalculator);
        assertThat(ScoresUtils.normalizeScores(score, false), is(31250));
    }

    @Test
    public void max_scores_for_surrendered_game_is_5000() {
        ScoreStatistics statistics = mockPerfectGame();
        when(statistics.getTimeSpent()).thenReturn(MIN_TIME);
        int score = ScoresUtils.calculateScoresForGame(ShipUtils.createNewShips(allShipsSizes, orientationBuilder), statistics, mAndroidGame, scoresCalculator);
        assertThat(ScoresUtils.normalizeScores(score, true), is(5000));
    }

    @Test
    public void max_score_for_BT_game_is_5000() {
        ScoreStatistics statistics = new ScoreStatistics();
        int MAX_BT_SCORE = 5000;
        int score1 = ScoresUtils.calculateScoresForGame(new ArrayList<Ship>(), statistics, mBluetoothGame, scoresCalculator);
        assertThat(ScoresUtils.normalizeScores(score1, false), is(MAX_BT_SCORE));
        int score = ScoresUtils.calculateScoresForGame(new ArrayList<Ship>(), statistics, mBluetoothGame, scoresCalculator);
        assertThat(ScoresUtils.normalizeScores(score, true), lessThan(MAX_BT_SCORE));
    }

    @Test
    public void max_score_for_internet_game_is_5000() {
        ScoreStatistics statistics = new ScoreStatistics();

        int MAX_INTERNET_SCORE = 10000;
        int score1 = ScoresUtils.calculateScoresForGame(new ArrayList<Ship>(), statistics, mInternetGame, scoresCalculator);
        assertThat(ScoresUtils.normalizeScores(score1, false), is(MAX_INTERNET_SCORE));
        int score = ScoresUtils.calculateScoresForGame(new ArrayList<Ship>(), statistics, mInternetGame, scoresCalculator);
        assertThat(ScoresUtils.normalizeScores(score, true), lessThan(MAX_INTERNET_SCORE));
    }

    @Test
    public void it_is_impossible_to_score_more_than_31250() {
        ScoreStatistics statistics = mockPerfectGame();

        when(statistics.getTimeSpent()).thenReturn(MIN_TIME/2);
        int score = ScoresUtils.calculateScoresForGame(ShipUtils.createNewShips(allShipsSizes, orientationBuilder), statistics, mAndroidGame, scoresCalculator);
        assertThat(ScoresUtils.normalizeScores(score, false), is(31250));
    }

    @Test
    public void exactly_min_scores_equals_230() {
        Collection<Ship> ships = new ArrayList<>();
        ships.add(new Ship(1));
        when(statistics.getCombo()).thenReturn(0);
        when(statistics.getShells()).thenReturn(0);
        when(statistics.getTimeSpent()).thenReturn(MAX_TIME);

        int score = ScoresUtils.calculateScoresForGame(ships, statistics, mAndroidGame, scoresCalculator);
        int i = ScoresUtils.normalizeScores(score, false);
        assertThat(i, is(230));
    }

    @Test
    public void it_is_impossible_to_score_less_than_230() {
        Collection<Ship> ships = new ArrayList<>();
        ships.add(new Ship(1));
        when(statistics.getCombo()).thenReturn(0);
        when(statistics.getShells()).thenReturn(0);
        when(statistics.getTimeSpent()).thenReturn(MAX_TIME*2);

        int score = ScoresUtils.calculateScoresForGame(ships, statistics, mAndroidGame, scoresCalculator);
        assertThat(ScoresUtils.normalizeScores(score, false), is(230));
    }

    @Test
    public void scores_2xCombo_1_4_ships_30xShells_150seconds_is_8737() {
        Collection<Ship> ships = ships_1_4();
        ScoreStatistics statistics = game_2xCombo_30xShells_150seconds();
        int score = ScoresUtils.calculateScoresForGame(ships, statistics, mAndroidGame, scoresCalculator);
        assertThat(ScoresUtils.normalizeScores(score, false), is(8737));
    }

    @Test
    public void surrendered_game_scores_2x_less() {
        Collection<Ship> ships = ships_1_4();
        ScoreStatistics statistics = game_2xCombo_30xShells_150seconds();
        int score = ScoresUtils.calculateScoresForGame(ships, statistics, mAndroidGame, scoresCalculator);
        assertThat(ScoresUtils.normalizeScores(score, true), is(4368));
    }

    @Test
    public void dead_ships_do_not_count() {
        Collection<Ship> ships = ships_1_4();
        ships.add(ShipTestUtils.deadShip());
        ScoreStatistics statistics = game_2xCombo_30xShells_150seconds();

        int score = ScoresUtils.calculateScoresForGame(ships, statistics, mAndroidGame, scoresCalculator);
        assertThat(ScoresUtils.normalizeScores(score, false), is(8737));
    }

    @Test
    public void SurrenderPenaltyForTheFullFleet_1000() {
        Collection<Ship> fullFleet = ShipUtils.createNewShips(allShipsSizes, orientationBuilder);
        assertThat(ScoresUtils.calcSurrenderPenalty(allShipsSizes, fullFleet), is(1000));
    }

    @Test
    public void SurrenderPenaltyFor_1_is_2900() {
        Collection<Ship> fullFleet = new ArrayList<>();
        fullFleet.add(new Ship(1));
        assertThat(ScoresUtils.calcSurrenderPenalty(allShipsSizes, fullFleet), is(2900));
    }


    @NonNull
    private ScoreStatistics mockPerfectGame() {
        ScoreStatistics statistics = mock(ScoreStatistics.class);
        when(statistics.getCombo()).thenReturn(TOTAL_SHIPS - 1);
        when(statistics.getShells()).thenReturn(100 - TOTAL_SQUARES_OCCUPIED_BY_SHIPS);
        return statistics;
    }

    @NonNull
    public static Collection<Ship> ships_1_4() {
        Collection<Ship> ships = new ArrayList<>();
        ships.add(new Ship(1));
        ships.add(new Ship(4));
        return ships;
    }

    @NonNull
    public static ScoreStatistics game_2xCombo_30xShells_150seconds() {
        ScoreStatistics statistics = mock(ScoreStatistics.class);
        when(statistics.getCombo()).thenReturn(2);
        when(statistics.getShells()).thenReturn(30);
        when(statistics.getTimeSpent()).thenReturn(MAX_TIME/2);
        return statistics;
    }
}