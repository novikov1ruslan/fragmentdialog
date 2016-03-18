package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.ai.PlacementAlgorithm;
import com.ivygames.morskoiboi.ai.PlacementFactory;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.variant.RussianPlacement;
import com.ivygames.morskoiboi.variant.RussianRules;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;

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
        RulesFactory.setRules(new RussianRules());
        Rules rules = RulesFactory.getRules();
        PlacementFactory.setPlacementAlgorithm(new RussianPlacement(new Random(1), rules.getTotalShips()));
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
    public void exactly_max_scores_equals_31250() {
        Collection<Ship> ships = mAlgorithm.generateFullFleet();
        Game game = mockPerfectGame();
        when(game.getTimeSpent()).thenReturn(MIN_TIME);
        assertThat(mRules.calcTotalScores(ships, game), is(31250));
    }

    @Test
    public void it_is_impossible_to_score_more_than_31250() {
        Collection<Ship> ships = mAlgorithm.generateFullFleet();
        Game game = mockPerfectGame();
        when(game.getTimeSpent()).thenReturn(MIN_TIME/2);
        assertThat(mRules.calcTotalScores(ships, game), is(31250));
    }

    @NonNull
    private Game mockPerfectGame() {
        Game game = mock(Game.class);
        when(game.getCombo()).thenReturn(TOTAL_SHIPS - 1);
        when(game.getShells()).thenReturn(100 - TOTAL_SQUARES_OCCUPIED_BY_SHIPS);
        return game;
    }

    @Test
    public void exactly_min_scores_equals_230() {
        Collection<Ship> ships = new ArrayList<>();
        ships.add(new Ship(1));
        Game game = mock(Game.class);
        when(game.getCombo()).thenReturn(0);
        when(game.getShells()).thenReturn(0);
        when(game.getTimeSpent()).thenReturn(MAX_TIME);
        int i = mRules.calcTotalScores(ships, game);

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

        assertThat(mRules.calcTotalScores(ships, game), is(230));
    }

    @Test
    public void scores_2xCombo_1_4_ships_30xShells_150seconds_is_8737() {
        Collection<Ship> ships = new ArrayList<>();
        ships.add(new Ship(1));
        ships.add(new Ship(4));
        Game game = mock(Game.class);
        when(game.getCombo()).thenReturn(2);
        when(game.getShells()).thenReturn(30);
        when(game.getTimeSpent()).thenReturn(MAX_TIME/2);

        assertThat(mRules.calcTotalScores(ships, game), is(8737));
    }

    @Test
    public void dead_ships_do_not_count() {
        Collection<Ship> ships = new ArrayList<>();
        Ship deadShip = mockDeadShip();
        ships.add(deadShip);
        ships.add(new Ship(1));
        ships.add(new Ship(4));
        Game game = mock(Game.class);
        when(game.getCombo()).thenReturn(2);
        when(game.getShells()).thenReturn(30);
        when(game.getTimeSpent()).thenReturn(MAX_TIME/2);

        assertThat(mRules.calcTotalScores(ships, game), is(8737));
    }

    @Test
    public void russian_fleet_has_following_ships_4_3_3_2_2_2_1_1_1_1() {
        assertThat(mRules.getTotalShips(), is(new int[]{4,3,3,2,2,2,1,1,1,1}));
    }

    @Test
    public void russian_fleet_has_following_ship_types_4_3_2_1() {
        assertThat(mRules.newShipTypesArray(), is(new int[]{4,3,2,1}));
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
        Collection<Ship> ships = new ArrayList<Ship>();
        for (int i = 0; i < 10; i++) {
            ships.add(mockDeadShip());
        }
        return ships;
    }

    @NonNull
    private Collection<Ship> mock_9_dead_1_alive_ship() {
        Collection<Ship> ships = new ArrayList<Ship>();
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
}
