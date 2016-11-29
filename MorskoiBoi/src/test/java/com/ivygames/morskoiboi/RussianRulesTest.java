package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;

import com.ivygames.ShipTestUtils;
import com.ivygames.battleship.BoardUtils;
import com.ivygames.battleship.RussianRules;
import com.ivygames.battleship.ShipUtils;
import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.ship.LocatedShip;
import com.ivygames.battleship.ship.Ship;

import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RussianRulesTest {

    private final RussianRules mRules = new RussianRules();
    private Placement placement;

    private int[] allShipsSizes;
    private OrientationBuilder orientationBuilder;
    private int mNumberOfShips;

    @Before
    public void setUp() {
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

        assertThat(BoardUtils.isBoardSet(board, mRules), is(true));
    }

    @Test
    public void empty_board_is_not_set() {
        assertThat(BoardUtils.isBoardSet(new Board(), mRules), is(false));
    }

    @Test
    public void board_is_not_set_when_it_has_less_than_full_russian_fleet() {
        Board board = new Board();
        Collection<Ship> ships = ShipUtils.generateFullFleet(allShipsSizes, orientationBuilder);
        ships.remove(ships.iterator().next());
        placement.populateBoardWithShips(board, ships);

        assertThat(BoardUtils.isBoardSet(board, mRules), is(false));
    }

    @Test
    public void board_is_not_set_when_it_has_conflicting_cells_when_all_the_fleet_is_on_a_board() {
        Board board = new Board();
        Collection<Ship> ships = ShipUtils.generateFullFleet(allShipsSizes, orientationBuilder);
        for (Ship ship : ships) {
            board.addShip(new LocatedShip(ship, 0, 0));
        }
        assertThat(board.getShips().size(), is(10));
        assertThat(BoardUtils.isBoardSet(board, mRules), is(false));
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

    @NonNull
    private Set<Ship> mock_10_dead_ships() {
        Set<Ship> ships = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            ships.add(ShipTestUtils.mockDeadShip());
        }
        return ships;
    }

    @NonNull
    private Set<Ship> mock_9_dead_1_alive_ship() {
        Set<Ship> ships = new HashSet<>();
        for (int i = 0; i < 9; i++) {
            ships.add(ShipTestUtils.mockDeadShip());
        }
        ships.add(mockAliveShip());
        return ships;
    }

    @NonNull
    private Ship mockAliveShip() {
        Ship ship = mock(Ship.class);
        when(ship.isDead()).thenReturn(false);
        return ship;
    }
}
