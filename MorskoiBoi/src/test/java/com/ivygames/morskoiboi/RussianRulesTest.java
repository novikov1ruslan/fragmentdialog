package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.ScoreStatistics;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.screen.boardsetup.BoardSetupUtils;
import com.ivygames.morskoiboi.variant.RulesUtils;
import com.ivygames.morskoiboi.variant.RussianRules;

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

public class RussianRulesTest {

    private static final int TOTAL_SHIPS = 10;
    private static final int TOTAL_SQUARES_OCCUPIED_BY_SHIPS = 20;
    private static final long MIN_TIME = 20000;
    private static final long MAX_TIME = 300000;

    private RussianRules mRules;
    private Placement placement;

    @Mock
    private ScoreStatistics statistics;
    private int[] allShipsSizes;
    private ShipUtils.OrientationBuilder orientationBuilder;

    @Before
    public void setUp() {
        initMocks(this);

        Random random = mock(Random.class);
        orientationBuilder = new ShipUtils.OrientationBuilder(random);
        mRules = new RussianRules();
        allShipsSizes = mRules.getAllShipsSizes();
        placement = new Placement(random, mRules.allowAdjacentShips());
    }

    @Test
    public void board_is_set_when_it_has_full_russian_fleet_and_no_conflicting_cells() {
        Board board = new Board();

        placement.populateBoardWithShips(board, ShipUtils.generateFullFleet(allShipsSizes, orientationBuilder));

        assertThat(BoardSetupUtils.isBoardSet(board, mRules), is(true));
    }

    @Test
    public void empty_board_is_not_set() {
        assertThat(BoardSetupUtils.isBoardSet(new Board(), mRules), is(false));
    }

    @Test
    public void board_is_not_set_when_it_has_less_than_full_russian_fleet() {
        Board board = new Board();
        Collection<Ship> ships = ShipUtils.generateFullFleet(allShipsSizes, orientationBuilder);
        ships.remove(ships.iterator().next());
        placement.populateBoardWithShips(board, ships);

        assertThat(BoardSetupUtils.isBoardSet(board, mRules), is(false));
    }

    @Test
    public void board_is_not_set_when_it_has_conflicting_cells_when_all_the_fleet_is_on_a_board() {
        Board board = new Board();
        Collection<Ship> ships = ShipUtils.generateFullFleet(allShipsSizes, orientationBuilder);
        for (Ship ship : ships) {
            Placement.putShipAt(board, ship, 0, 0);
        }
        assertThat(board.getShips().size(), is(10));
        assertThat(BoardSetupUtils.isBoardSet(board, mRules), is(false));
    }

    @Test
    public void EmptyCellIsNotConflicting() {
        Board board = new Board();
        assertThat(BoardSetupUtils.isCellConflicting(board, 5, 5, mRules.allowAdjacentShips()), is(false));
    }

    @Test
    public void cell_is_not_conflicting_if_it_only_touched_by_1_ship() {
        Board board = new Board();
        Placement.putShipAt(board, new Ship(1), 5, 5);
        assertThat(BoardSetupUtils.isCellConflicting(board, 5, 5, mRules.allowAdjacentShips()), is(false));
    }

    @Test
    public void cell_is_not_conflicting_if_it_only_touched_by_1_ship2() {
        Board board = new Board();
        Placement.putShipAt(board, new Ship(1), 1, 5);
        assertThat(BoardSetupUtils.isCellConflicting(board, 1, 5, mRules.allowAdjacentShips()), is(false));
    }

    @Test
    public void cell_is_conflicting_if_it_is_occupied_by_ship_A_and_ship_B_is_touching_the_cell() {
        Board board = new Board();
        Placement.putShipAt(board, new Ship(1), 6, 6);

        Placement.putShipAt(board, new Ship(1), 5, 5);

        assertThat(BoardSetupUtils.isCellConflicting(board, 5, 5, mRules.allowAdjacentShips()), is(true));
        assertThat(BoardSetupUtils.isCellConflicting(board, 6, 6, mRules.allowAdjacentShips()), is(true));
    }

    @Test
    public void WhenShipsOverlap__ThereIsAConflict() {
        Board board = new Board();
        Placement.putShipAt(board, new Ship(1), 5, 5);

        Placement.putShipAt(board, new Ship(1), 5, 5);

        assertThat(BoardSetupUtils.isCellConflicting(board, 5, 5, mRules.allowAdjacentShips()), is(true));
    }

    @Test
    public void max_scores_for_android_game_is_31250() {
        ScoreStatistics statistics = mockPerfectGame();
        when(statistics.getTimeSpent()).thenReturn(MIN_TIME);
        assertThat(mRules.calcTotalScores(ShipUtils.generateFullFleet(allShipsSizes, orientationBuilder), Game.Type.VS_ANDROID,
                statistics, false), is(31250));
    }

    @Test
    public void max_scores_for_surrendered_game_is_5000() {
        ScoreStatistics statistics = mockPerfectGame();
        when(statistics.getTimeSpent()).thenReturn(MIN_TIME);
        assertThat(mRules.calcTotalScores(ShipUtils.generateFullFleet(allShipsSizes, orientationBuilder), Game.Type.VS_ANDROID,
                statistics, true), is(5000));
    }

    @Test
    public void max_score_for_BT_game_is_5000() {
        ScoreStatistics statistics = new ScoreStatistics();
        int MAX_BT_SCORE = 5000;
        assertThat(mRules.calcTotalScores(new ArrayList<Ship>(), Game.Type.BLUETOOTH, statistics,
                false), is(MAX_BT_SCORE));
        assertThat(mRules.calcTotalScores(new ArrayList<Ship>(), Game.Type.BLUETOOTH, statistics,
                true), lessThan(MAX_BT_SCORE));
    }

    @Test
    public void max_score_for_internet_game_is_5000() {
        ScoreStatistics statistics = new ScoreStatistics();

        int MAX_INTERNET_SCORE = 10000;
        assertThat(mRules.calcTotalScores(new ArrayList<Ship>(), Game.Type.INTERNET, statistics,
                false), is(MAX_INTERNET_SCORE));
        assertThat(mRules.calcTotalScores(new ArrayList<Ship>(), Game.Type.INTERNET, statistics,
                true), lessThan(MAX_INTERNET_SCORE));
    }

    @Test
    public void it_is_impossible_to_score_more_than_31250() {
        ScoreStatistics statistics = mockPerfectGame();

        when(statistics.getTimeSpent()).thenReturn(MIN_TIME/2);
        assertThat(mRules.calcTotalScores(ShipUtils.generateFullFleet(allShipsSizes, orientationBuilder), Game.Type.VS_ANDROID, statistics,
                false), is(31250));
    }

    @Test
    public void exactly_min_scores_equals_230() {
        Collection<Ship> ships = new ArrayList<>();
        ships.add(new Ship(1));
        when(statistics.getCombo()).thenReturn(0);
        when(statistics.getShells()).thenReturn(0);
        when(statistics.getTimeSpent()).thenReturn(MAX_TIME);

        int i = mRules.calcTotalScores(ships, Game.Type.VS_ANDROID, statistics, false);
        assertThat(i, is(230));
    }

    @Test
    public void it_is_impossible_to_score_less_than_230() {
        Collection<Ship> ships = new ArrayList<>();
        ships.add(new Ship(1));
        when(statistics.getCombo()).thenReturn(0);
        when(statistics.getShells()).thenReturn(0);
        when(statistics.getTimeSpent()).thenReturn(MAX_TIME*2);

        assertThat(mRules.calcTotalScores(ships, Game.Type.VS_ANDROID, statistics,
                false), is(230));
    }

    @Test
    public void scores_2xCombo_1_4_ships_30xShells_150seconds_is_8737() {
        Collection<Ship> ships = ships_1_4();
        ScoreStatistics statistics = game_2xCombo_30xShells_150seconds();
        assertThat(mRules.calcTotalScores(ships, Game.Type.VS_ANDROID, statistics,
                false), is(8737));
    }

    @Test
    public void surrendered_game_scores_2x_less() {
        Collection<Ship> ships = ships_1_4();
        ScoreStatistics statistics = game_2xCombo_30xShells_150seconds();
        assertThat(mRules.calcTotalScores(ships, Game.Type.VS_ANDROID, statistics,
                true), is(4368));
    }

    @Test
    public void dead_ships_do_not_count() {
        Collection<Ship> ships = ships_1_4();
        ships.add(mockDeadShip());
        ScoreStatistics statistics = game_2xCombo_30xShells_150seconds();

        assertThat(mRules.calcTotalScores(ships, Game.Type.VS_ANDROID, statistics,
                false), is(8737));
    }

    @Test
    public void russian_fleet_has_following_ships_4_3_3_2_2_2_1_1_1_1() {
        assertThat(mRules.getAllShipsSizes(), is(new int[]{4,3,3,2,2,2,1,1,1,1}));
    }

    @Test
    public void board_is_NOT_defeated_if_it_has_less_than_10_ships() {
        Board board = mock(Board.class);
        Collection<Ship> ships = mock(Collection.class);
        when(ships.size()).thenReturn(9);
        when(board.getShips()).thenReturn(ships);
        assertThat(BoardSetupUtils.isItDefeatedBoard(board, mRules), is(false));
    }

    @Test
    public void board_is_NOT_defeated_if_it_has_at_least_1_alive_ship() {
        Board board = mock(Board.class);
        Collection<Ship> ships = mock_9_dead_1_alive_ship();
        when(board.getShips()).thenReturn(ships);

        assertThat(BoardSetupUtils.isItDefeatedBoard(board, mRules), is(false));
    }

    @Test
    public void board_is_defeated_if_it_has_10_dead_ships() {
        Board board = mock(Board.class);
        Collection<Ship> ships = mock_10_dead_ships();
        when(board.getShips()).thenReturn(ships);

        assertThat(BoardSetupUtils.isItDefeatedBoard(board, mRules), is(true));
    }

    @Test
    public void SurrenderPenaltyForTheFullFleet_1000() {
        Collection<Ship> fullFleet = ShipUtils.generateFullFleet(allShipsSizes, orientationBuilder);
        assertThat(RulesUtils.calcSurrenderPenalty(fullFleet, mRules.getAllShipsSizes()), is(1000));
    }

    @Test
    public void SurrenderPenaltyFor_1_is_2900() {
        Collection<Ship> fullFleet = new ArrayList<>();
        fullFleet.add(new Ship(1));
        assertThat(RulesUtils.calcSurrenderPenalty(fullFleet, mRules.getAllShipsSizes()), is(2900));
    }

    @NonNull
    private Collection<Ship> mock_10_dead_ships() {
        Collection<Ship> ships = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ships.add(mockDeadShip());
        }
        return ships;
    }

    @NonNull
    private Collection<Ship> mock_9_dead_1_alive_ship() {
        Collection<Ship> ships = new ArrayList<>();
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
