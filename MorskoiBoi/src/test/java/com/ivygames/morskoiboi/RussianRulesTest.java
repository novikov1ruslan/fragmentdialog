package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;

import com.ivygames.battleship.BoardUtils;
import com.ivygames.battleship.ShipUtils;
import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.ship.LocatedShip;
import com.ivygames.battleship.ship.Ship;
import com.ivygames.common.multiplayer.MultiplayerRoom;
import com.ivygames.morskoiboi.ai.AndroidGame;
import com.ivygames.morskoiboi.bluetooth.BluetoothConnection;
import com.ivygames.morskoiboi.bluetooth.BluetoothGame;
import com.ivygames.morskoiboi.config.RulesUtils;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.ScoreStatistics;
import com.ivygames.morskoiboi.rt.InternetGame;
import com.ivygames.morskoiboi.russian.RussianRules;
import com.ivygames.morskoiboi.russian.RussianScoresCalculator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class RussianRulesTest {

    private static final int TOTAL_SHIPS = 10;
    private static final int TOTAL_SQUARES_OCCUPIED_BY_SHIPS = 20;
    private static final long MIN_TIME = 20000;
    private static final long MAX_TIME = 300000;

    private RussianRules mRules = new RussianRules();
    private Placement placement;

    private final Game mBluetoothGame = new BluetoothGame(mock(BluetoothConnection.class));
    private final Game mAndroidGame = new AndroidGame();
    private final Game mInternetGame = new InternetGame(mock(MultiplayerRoom.class));

    @Mock
    private ScoreStatistics statistics;
    private int[] allShipsSizes;
    private OrientationBuilder orientationBuilder;
    private ScoresCalculator scoresCalculator = new RussianScoresCalculator();
    private int mNumberOfShips;

    @Before
    public void setUp() {
        initMocks(this);

        Random random = mock(Random.class);
        orientationBuilder = new OrientationBuilder(random);
        allShipsSizes = mRules.getAllShipsSizes();
        placement = new Placement(random, mRules.allowAdjacentShips());
        mNumberOfShips = mRules.getAllShipsSizes().length;
    }

    @Test
    public void board_is_set_when_it_has_full_russian_fleet_and_no_conflicting_cells() {
        Board board = new Board();

        placement.populateBoardWithShips(board, ShipUtils.generateFullFleet(allShipsSizes, orientationBuilder));

        assertThat(BoardUtils.isBoardSet(board, mRules, mNumberOfShips), is(true));
    }

    @Test
    public void empty_board_is_not_set() {
        assertThat(BoardUtils.isBoardSet(new Board(), mRules, mNumberOfShips), is(false));
    }

    @Test
    public void board_is_not_set_when_it_has_less_than_full_russian_fleet() {
        Board board = new Board();
        Collection<Ship> ships = ShipUtils.generateFullFleet(allShipsSizes, orientationBuilder);
        ships.remove(ships.iterator().next());
        placement.populateBoardWithShips(board, ships);

        assertThat(BoardUtils.isBoardSet(board, mRules, mNumberOfShips), is(false));
    }

    @Test
    public void board_is_not_set_when_it_has_conflicting_cells_when_all_the_fleet_is_on_a_board() {
        Board board = new Board();
        Collection<Ship> ships = ShipUtils.generateFullFleet(allShipsSizes, orientationBuilder);
        for (Ship ship : ships) {
            board.addShip(new LocatedShip(ship, 0, 0));
        }
        assertThat(board.getShips().size(), is(10));
        assertThat(BoardUtils.isBoardSet(board, mRules, mNumberOfShips), is(false));
    }

    @Test
    public void EmptyCellIsNotConflicting() {
        Board board = new Board();
        assertThat(BoardUtils.isCellConflicting(board, 5, 5, mRules.allowAdjacentShips()), is(false));
    }

    @Test
    public void cell_is_not_conflicting_if_it_only_touched_by_1_ship() {
        Board board = new Board();
        Ship ship = new Ship(1);
        board.addShip(new LocatedShip(ship, 5, 5));
        assertThat(BoardUtils.isCellConflicting(board, 5, 5, mRules.allowAdjacentShips()), is(false));
    }

    @Test
    public void cell_is_not_conflicting_if_it_only_touched_by_1_ship2() {
        Board board = new Board();
        Ship ship = new Ship(1);
        board.addShip(new LocatedShip(ship, 1, 5));
        assertThat(BoardUtils.isCellConflicting(board, 1, 5, mRules.allowAdjacentShips()), is(false));
    }

    @Test
    public void cell_is_conflicting_if_it_is_occupied_by_ship_A_and_ship_B_is_touching_the_cell() {
        Board board = new Board();
        Ship ship = new Ship(1);
        board.addShip(new LocatedShip(ship, 6, 6));

        Ship ship1 = new Ship(1);
        board.addShip(new LocatedShip(ship1, 5, 5));

        assertThat(BoardUtils.isCellConflicting(board, 5, 5, mRules.allowAdjacentShips()), is(true));
        assertThat(BoardUtils.isCellConflicting(board, 6, 6, mRules.allowAdjacentShips()), is(true));
    }

    @Test
    public void WhenShipsOverlap__ThereIsAConflict() {
        Board board = new Board();
        board.addShip(new LocatedShip(new Ship(1), 5, 5));

        board.addShip(new LocatedShip(new Ship(1), 5, 5));

        assertThat(BoardUtils.isCellConflicting(board, 5, 5, mRules.allowAdjacentShips()), is(true));
    }

    @Test
    public void max_scores_for_android_game_is_31250() {
        ScoreStatistics statistics = mockPerfectGame();
        when(statistics.getTimeSpent()).thenReturn(MIN_TIME);
        assertThat(RulesUtils.calcTotalScores(ShipUtils.generateFullFleet(allShipsSizes, orientationBuilder), mAndroidGame,
                statistics, false, scoresCalculator), is(31250));
    }

    @Test
    public void max_scores_for_surrendered_game_is_5000() {
        ScoreStatistics statistics = mockPerfectGame();
        when(statistics.getTimeSpent()).thenReturn(MIN_TIME);
        assertThat(RulesUtils.calcTotalScores(ShipUtils.generateFullFleet(allShipsSizes, orientationBuilder), mAndroidGame,
                statistics, true, scoresCalculator), is(5000));
    }

    @Test
    public void max_score_for_BT_game_is_5000() {
        ScoreStatistics statistics = new ScoreStatistics();
        int MAX_BT_SCORE = 5000;
        assertThat(RulesUtils.calcTotalScores(new ArrayList<Ship>(), mBluetoothGame, statistics,
                false, scoresCalculator), is(MAX_BT_SCORE));
        assertThat(RulesUtils.calcTotalScores(new ArrayList<Ship>(), mBluetoothGame, statistics,
                true, scoresCalculator), lessThan(MAX_BT_SCORE));
    }

    @Test
    public void max_score_for_internet_game_is_5000() {
        ScoreStatistics statistics = new ScoreStatistics();

        int MAX_INTERNET_SCORE = 10000;
        assertThat(RulesUtils.calcTotalScores(new ArrayList<Ship>(), mInternetGame, statistics,
                false, scoresCalculator), is(MAX_INTERNET_SCORE));
        assertThat(RulesUtils.calcTotalScores(new ArrayList<Ship>(), mInternetGame, statistics,
                true, scoresCalculator), lessThan(MAX_INTERNET_SCORE));
    }

    @Test
    public void it_is_impossible_to_score_more_than_31250() {
        ScoreStatistics statistics = mockPerfectGame();

        when(statistics.getTimeSpent()).thenReturn(MIN_TIME/2);
        assertThat(RulesUtils.calcTotalScores(ShipUtils.generateFullFleet(allShipsSizes, orientationBuilder), mAndroidGame, statistics,
                false, scoresCalculator), is(31250));
    }

    @Test
    public void exactly_min_scores_equals_230() {
        Collection<Ship> ships = new ArrayList<>();
        ships.add(new Ship(1));
        when(statistics.getCombo()).thenReturn(0);
        when(statistics.getShells()).thenReturn(0);
        when(statistics.getTimeSpent()).thenReturn(MAX_TIME);

        int i = RulesUtils.calcTotalScores(ships, mAndroidGame, statistics, false, scoresCalculator);
        assertThat(i, is(230));
    }

    @Test
    public void it_is_impossible_to_score_less_than_230() {
        Collection<Ship> ships = new ArrayList<>();
        ships.add(new Ship(1));
        when(statistics.getCombo()).thenReturn(0);
        when(statistics.getShells()).thenReturn(0);
        when(statistics.getTimeSpent()).thenReturn(MAX_TIME*2);

        assertThat(RulesUtils.calcTotalScores(ships, mAndroidGame, statistics,
                false, scoresCalculator), is(230));
    }

    @Test
    public void scores_2xCombo_1_4_ships_30xShells_150seconds_is_8737() {
        Collection<Ship> ships = ships_1_4();
        ScoreStatistics statistics = game_2xCombo_30xShells_150seconds();
        assertThat(RulesUtils.calcTotalScores(ships, mAndroidGame, statistics,
                false, scoresCalculator), is(8737));
    }

    @Test
    public void surrendered_game_scores_2x_less() {
        Collection<Ship> ships = ships_1_4();
        ScoreStatistics statistics = game_2xCombo_30xShells_150seconds();
        assertThat(RulesUtils.calcTotalScores(ships, mAndroidGame, statistics,
                true, scoresCalculator), is(4368));
    }

    @Test
    public void dead_ships_do_not_count() {
        Collection<Ship> ships = ships_1_4();
        ships.add(mockDeadShip());
        ScoreStatistics statistics = game_2xCombo_30xShells_150seconds();

        assertThat(RulesUtils.calcTotalScores(ships, mAndroidGame, statistics,
                false, scoresCalculator), is(8737));
    }

    @Test
    public void russian_fleet_has_following_ships_4_3_3_2_2_2_1_1_1_1() {
        assertThat(mRules.getAllShipsSizes(), is(new int[]{4,3,3,2,2,2,1,1,1,1}));
    }

    @Test
    public void board_is_NOT_defeated_if_it_has_less_than_10_ships() {
        Board board = mock(Board.class);
        Set<Ship> ships = mock(Set.class);
        when(ships.size()).thenReturn(9);
        when(board.getShips()).thenReturn(ships);
        assertThat(BoardUtils.isItDefeatedBoard(board, mNumberOfShips), is(false));
    }

    @Test
    public void board_is_NOT_defeated_if_it_has_at_least_1_alive_ship() {
        Board board = mock(Board.class);
        Set<Ship> ships = mock_9_dead_1_alive_ship();
        when(board.getShips()).thenReturn(ships);

        assertThat(BoardUtils.isItDefeatedBoard(board, mNumberOfShips), is(false));
    }

    @Test
    public void board_is_defeated_if_it_has_10_dead_ships() {
        Board board = mock(Board.class);
        Set<Ship> ships = mock_10_dead_ships();
        when(board.getShips()).thenReturn(ships);

        assertThat(BoardUtils.isItDefeatedBoard(board, mNumberOfShips), is(true));
    }

    @Test
    public void SurrenderPenaltyForTheFullFleet_1000() {
        Collection<Ship> fullFleet = ShipUtils.generateFullFleet(allShipsSizes, orientationBuilder);
        assertThat(RulesUtils.calcSurrenderPenalty(mRules.getAllShipsSizes(), fullFleet), is(1000));
    }

    @Test
    public void SurrenderPenaltyFor_1_is_2900() {
        Collection<Ship> fullFleet = new ArrayList<>();
        fullFleet.add(new Ship(1));
        assertThat(RulesUtils.calcSurrenderPenalty(mRules.getAllShipsSizes(), fullFleet), is(2900));
    }

    @NonNull
    private Set<Ship> mock_10_dead_ships() {
        Set<Ship> ships = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            ships.add(mockDeadShip());
        }
        return ships;
    }

    @NonNull
    private Set<Ship> mock_9_dead_1_alive_ship() {
        Set<Ship> ships = new HashSet<>();
        for (int i = 0; i < 9; i++) {
            ships.add(mockDeadShip());
        }
        ships.add(mockAliveShip());
        return ships;
    }

    @NonNull
    private Ship mockDeadShip() {
        Ship ship = mock(Ship.class);
        when(ship.isDead()).thenReturn(true);
        return ship;
    }

    @NonNull
    private Ship mockAliveShip() {
        Ship ship = mock(Ship.class);
        when(ship.isDead()).thenReturn(false);
        return ship;
    }

    @NonNull
    private ScoreStatistics mockPerfectGame() {
        ScoreStatistics statistics = mock(ScoreStatistics.class);
        when(statistics.getCombo()).thenReturn(TOTAL_SHIPS - 1);
        when(statistics.getShells()).thenReturn(100 - TOTAL_SQUARES_OCCUPIED_BY_SHIPS);
        return statistics;
    }

    @NonNull
    protected Collection<Ship> ships_1_4() {
        Collection<Ship> ships = new ArrayList<>();
        ships.add(new Ship(1));
        ships.add(new Ship(4));
        return ships;
    }

    @NonNull
    private ScoreStatistics game_2xCombo_30xShells_150seconds() {
        ScoreStatistics statistics = mock(ScoreStatistics.class);
        when(statistics.getCombo()).thenReturn(2);
        when(statistics.getShells()).thenReturn(30);
        when(statistics.getTimeSpent()).thenReturn(MAX_TIME/2);
        return statistics;
    }
}
