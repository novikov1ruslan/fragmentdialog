package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.ai.PlacementAlgorithm;
import com.ivygames.morskoiboi.ai.PlacementFactory;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.variant.Placement;
import com.ivygames.morskoiboi.variant.RussianRules;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RussianRulesTest {

    private static final int TOTAL_SHIPS = 10;
    private static final int TOTAL_SQUARES_OCCUPIED_BY_SHIPS = 20;
    private static final long MIN_TIME = 20000;
    private static final long MAX_TIME = 300000;

    private Rules mRules;
    private PlacementAlgorithm mAlgorithm;

    @BeforeClass
    public static void runBeforeClass() {

    }

    @Before
    public void setUp() {
        RussianRules rules = new RussianRules(null);
        RulesFactory.setRules(rules);
        PlacementFactory.setPlacementAlgorithm(new Placement(new Random(1), rules));
        mAlgorithm = PlacementFactory.getAlgorithm();
        mRules = RulesFactory.getRules();
    }

    @Test
    public void board_is_set_when_it_has_full_russian_fleet_and_no_conflicting_cells() {
        assertThat(mRules.isBoardSet(mAlgorithm.generateBoard()), is(true));
    }

    @Test
    public void empty_board_is_not_set() {
        assertThat(mRules.isBoardSet(new Board()), is(false));
    }

    @Test
    public void board_is_not_set_when_it_has_less_than_full_russian_fleet() {
        Board board = mAlgorithm.generateBoard();
        Ship ship = board.getShips().iterator().next();
        board.removeShipFrom(ship.getX(), ship.getY());
        assertThat(mRules.isBoardSet(board), is(false));
    }

    @Test
    public void board_is_not_set_when_it_has_conflicting_cells_although_all_the_fleet_is_on_a_board() {
        Board board = mAlgorithm.generateBoard();
        Collection<Ship> shipsCopy = new ArrayList<>(board.getShips());
        for (Ship ship : shipsCopy) {
            board.removeShipFrom(ship.getX(), ship.getY());
            mAlgorithm.putShipAt(board, ship, 0, 0);
        }
        assertThat(board.getShips().size(), is(10));
        assertThat(mRules.isBoardSet(board), is(false));
    }

    @Test
    public void newly_created_cell_is_not_conflicting() {
        assertThat(mRules.isCellConflicting(new Cell()), is(false));
    }

    @Test
    public void cell_from_empty_board_is_not_conflicting() {
        Board board = new Board();
        assertThat(mRules.isCellConflicting(board.getCell(5, 5)), is(false));
    }

    @Test
    public void cell_is_not_conflicting_if_it_only_touched_by_1_ship() {
        Board board = new Board();
        mAlgorithm.putShipAt(board, new Ship(1), 5, 5);
        assertThat(mRules.isCellConflicting(board.getCell(5, 5)), is(false));
    }

    @Test
    public void cell_is_conflicting_if_it_is_occupied_by_ship_A_and_ship_B_is_touching_the_cell() {
        Board board = new Board();
        mAlgorithm.putShipAt(board, new Ship(1), 5, 5);
        mAlgorithm.putShipAt(board, new Ship(1), 6, 6);
        assertThat(mRules.isCellConflicting(board.getCell(5, 5)), is(true));
    }

    @Test
    public void max_scores_for_android_game_is_31250() {
        Game game = mockPerfectGame();
        setGameType(game, Game.Type.VS_ANDROID);
        when(game.getTimeSpent()).thenReturn(MIN_TIME);
        assertThat(mRules.calcTotalScores(mRules.generateFullFleet(), game, false), is(31250));
    }

    @Test
    public void max_scores_for_surrendered_game_is_5000() {
        Game game = mockPerfectGame();
        setGameType(game, Game.Type.VS_ANDROID);
        when(game.getTimeSpent()).thenReturn(MIN_TIME);
        assertThat(mRules.calcTotalScores(mRules.generateFullFleet(), game, true), is(5000));
    }

    @Test
    public void max_score_for_BT_game_is_5000() {
        Game game = mock(Game.class);
        setGameType(game, Game.Type.BLUETOOTH);
        int MAX_BT_SCORE = 5000;
        assertThat(mRules.calcTotalScores(new ArrayList<Ship>(), game, false), is(MAX_BT_SCORE));
        assertThat(mRules.calcTotalScores(new ArrayList<Ship>(), game, true), lessThan(MAX_BT_SCORE));
    }

    @Test
    public void max_score_for_internet_game_is_5000() {
        Game game = mock(Game.class);
        setGameType(game, Game.Type.INTERNET);
        int MAX_INTERNET_SCORE = 10000;
        assertThat(mRules.calcTotalScores(new ArrayList<Ship>(), game, false), is(MAX_INTERNET_SCORE));
        assertThat(mRules.calcTotalScores(new ArrayList<Ship>(), game, true), lessThan(MAX_INTERNET_SCORE));
    }

    @Test
    public void it_is_impossible_to_score_more_than_31250() {
        Game game = mockPerfectGame();
        setGameType(game, Game.Type.VS_ANDROID);
        when(game.getTimeSpent()).thenReturn(MIN_TIME/2);
        assertThat(mRules.calcTotalScores(mRules.generateFullFleet(), game, false), is(31250));
    }

    @Test
    public void exactly_min_scores_equals_230() {
        Collection<Ship> ships = new ArrayList<>();
        ships.add(new Ship(1));
        Game game = mock(Game.class);
        when(game.getCombo()).thenReturn(0);
        when(game.getShells()).thenReturn(0);
        when(game.getTimeSpent()).thenReturn(MAX_TIME);
        setGameType(game, Game.Type.VS_ANDROID);

        int i = mRules.calcTotalScores(ships, game, false);
        assertThat(i, is(230));
    }

    @Test
    public void it_is_impossible_to_score_less_than_230() {
        Collection<Ship> ships = new ArrayList<>();
        ships.add(new Ship(1));
        Game game = mock(Game.class);
        when(game.getCombo()).thenReturn(0);
        when(game.getShells()).thenReturn(0);
        when(game.getTimeSpent()).thenReturn(MAX_TIME*2);
        setGameType(game, Game.Type.VS_ANDROID);

        assertThat(mRules.calcTotalScores(ships, game, false), is(230));
    }

    @Test
    public void scores_2xCombo_1_4_ships_30xShells_150seconds_is_8737() {
        Collection<Ship> ships = ships_1_4();
        Game game = game_2xCombo_30xShells_150seconds();
        setGameType(game, Game.Type.VS_ANDROID);
        assertThat(mRules.calcTotalScores(ships, game, false), is(8737));
    }

    @Test
    public void surrendered_game_scores_2x_less() {
        Collection<Ship> ships = ships_1_4();
        Game game = game_2xCombo_30xShells_150seconds();
        setGameType(game, Game.Type.VS_ANDROID);
        assertThat(mRules.calcTotalScores(ships, game, true), is(4368));
    }

    @Test
    public void dead_ships_do_not_count() {
        Collection<Ship> ships = ships_1_4();
        ships.add(mockDeadShip());
        Game game = game_2xCombo_30xShells_150seconds();
        setGameType(game, Game.Type.VS_ANDROID);

        assertThat(mRules.calcTotalScores(ships, game, false), is(8737));
    }

    @Test
    public void russian_fleet_has_following_ships_4_3_3_2_2_2_1_1_1_1() {
        assertThat(mRules.getAllShipsSizes(), is(new int[]{4,3,3,2,2,2,1,1,1,1}));
    }

    @Test
    public void russian_fleet_has_following_ship_types_4_3_2_1() {
        assertThat(mRules.getDistinctShipsSizes(), is(new int[]{4,3,2,1}));
    }

    @Test
    public void board_is_NOT_defeated_if_it_has_less_than_10_ships() {
        Board board = mock(Board.class);
        Collection<Ship> ships = mock(Collection.class);
        when(ships.size()).thenReturn(9);
        when(board.getShips()).thenReturn(ships);
        assertThat(mRules.isItDefeatedBoard(board), is(false));
    }

    @Test
    public void board_is_NOT_defeated_if_it_has_at_least_1_alive_ship() {
        Board board = mock(Board.class);
        Collection<Ship> ships = mock_9_dead_1_alive_ship();
        when(board.getShips()).thenReturn(ships);

        assertThat(mRules.isItDefeatedBoard(board), is(false));
    }

    @Test
    public void board_is_defeated_if_it_has_10_dead_ships() {
        Board board = mock(Board.class);
        Collection<Ship> ships = mock_10_dead_ships();
        when(board.getShips()).thenReturn(ships);

        assertThat(mRules.isItDefeatedBoard(board), is(true));
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

    private Ship mockDeadShip() {
        Ship ship = mock(Ship.class);
        when(ship.isDead()).thenReturn(true);
        return ship;
    }

    private Ship mockAliveShip() {
        Ship ship = mock(Ship.class);
        when(ship.isDead()).thenReturn(false);
        return ship;
    }

    private void setGameType(Game game, Game.Type type) {
        when(game.getType()).thenReturn(type);
    }

    @NonNull
    private Game mockPerfectGame() {
        Game game = mock(Game.class);
        when(game.getCombo()).thenReturn(TOTAL_SHIPS - 1);
        when(game.getShells()).thenReturn(100 - TOTAL_SQUARES_OCCUPIED_BY_SHIPS);
        return game;
    }

    @NonNull
    protected Collection<Ship> ships_1_4() {
        Collection<Ship> ships = new ArrayList<>();
        ships.add(new Ship(1));
        ships.add(new Ship(4));
        return ships;
    }

    @NonNull
    protected Game game_2xCombo_30xShells_150seconds() {
        Game game = mock(Game.class);
        when(game.getCombo()).thenReturn(2);
        when(game.getShells()).thenReturn(30);
        when(game.getTimeSpent()).thenReturn(MAX_TIME/2);
        return game;
    }
}
