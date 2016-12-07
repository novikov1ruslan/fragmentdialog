package com.ivygames.battleship;

import android.support.annotation.NonNull;

import com.ivygames.ShipTestUtils;
import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.board.BoardSerialization;
import com.ivygames.battleship.board.BoardSerializationTest;
import com.ivygames.battleship.board.Cell;
import com.ivygames.battleship.board.Vector;
import com.ivygames.battleship.ship.LocatedShip;
import com.ivygames.battleship.ship.Ship;
import com.ivygames.morskoiboi.OrientationBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class BoardUtilsTest {

    private Board mBoard = new Board();

    private Rules mRules = new RussianRules();

    @Mock
    private Random mRandom;
    @Mock
    private OrientationBuilder mOrientationBuilder;

    @Before
    public void setup() {
        initMocks(this);
        when(mOrientationBuilder.nextOrientation()).thenReturn(Ship.Orientation.VERTICAL);
    }

    // TODO: test with allowing adjacent ships
    @Test
    public void TestFreeFromShipsCells() {
        Board board = BoardSerialization.fromJson(BoardSerializationTest.EMPTY_BOARD);
        assertEquals(100, BoardUtils.getCoordinatesFreeFromShips(board, false).size());

        board = BoardSerialization.fromJson(BoardSerializationTest.BOARD_WITH_SHIP_x1_5_5);
        assertEquals(91, BoardUtils.getCoordinatesFreeFromShips(board, false).size());

        board = BoardSerialization.fromJson(BoardSerializationTest.BOARD_WITH_SHIP_x1_5_5_x2_5_5);
        assertEquals(85, BoardUtils.getCoordinatesFreeFromShips(board, false).size());
    }

    @Test
    public void PossibleShotsDoNotInclude_Miss_Hit_OrCellNotFreeFromShips() {
        Board board = BoardSerialization.fromJson(BoardSerializationTest.BOARD_WITH_SHIP_x1_5_5);
        board.setCell(Cell.MISS, 8, 8);
        board.setCell(Cell.HIT, 9, 9);
        board.setCell(Cell.HIT, 5, 5);

        assertEquals(89, BoardUtils.getPossibleShots(board, false).size());
    }

    @Test
    public void CanRotateHorizontalShip() {
        Ship ship = new Ship(2, Ship.Orientation.HORIZONTAL);
        putShipAt(ship, 5, 5);

        BoardUtils.rotateShipAt(mBoard, 5, 5);

        assertThat(ship.isHorizontal(), is(false));
    }

    @Test
    public void CanRotateVerticalShip() {
        Ship ship = new Ship(2, Ship.Orientation.VERTICAL);
        putShipAt(ship, 5, 5);

        BoardUtils.rotateShipAt(mBoard, 5, 5);

        assertThat(ship.isHorizontal(), is(true));
    }

    @Test
    public void CanRotateHorizontalShipNearTheWall() {
        Ship ship = new Ship(4, Ship.Orientation.HORIZONTAL);
        putShipAt(ship, 5, 7);

        BoardUtils.rotateShipAt(mBoard, 5, 7);

        assertThat(ship.isHorizontal(), is(false));
        assertThat(getFirstShip().coordinate, is(Vector.get(5, 6)));
    }

    @Test
    public void CanRotateVerticalShip2() {
        Ship ship = new Ship(4, Ship.Orientation.VERTICAL);
        putShipAt(ship, 7, 5);

        BoardUtils.rotateShipAt(mBoard, 7, 5);

        assertThat(ship.isHorizontal(), is(true));
        assertThat(getFirstShip().coordinate, is(Vector.get(6, 5)));
    }

    @Test
    public void RotatingNonExistentShip__HasNoEffect() {
        Ship ship = new Ship(4, Ship.Orientation.HORIZONTAL);
        putShipAt(ship, 5, 7);

        BoardUtils.rotateShipAt(mBoard, 1, 1);

        assertThat(ship.isHorizontal(), is(true));
    }

    @Test
    public void BoardIsSetWhenItHasFullFleet_AndNoConflictingCells() {
        Rules rules = mockRules(new int[]{1, 2});
        mBoard.addShip(new Ship(1), 1, 1);
        mBoard.addShip(new Ship(2), 5, 5);

        assertThat(BoardUtils.isBoardSet(mBoard, rules), is(true));
    }

    @NonNull
    private Rules mockRules(int[] value) {
        Rules rules = mock(Rules.class);
        when(rules.getAllShipsSizes()).thenReturn(value);
        return rules;
    }

    @Test
    public void EmptyBoardIsNotSet() {
        assertThat(BoardUtils.isBoardSet(mBoard, mRules), is(false));
    }

    @Test
    public void BoardIsNotSetWhenItHasLessThanFullFleet() {
        Rules rules = mockRules(new int[]{1, 2});
        mBoard.addShip(new Ship(1), 1, 1);

        assertThat(BoardUtils.isBoardSet(mBoard, rules), is(false));
    }

    @Test
    public void WhenBoardHasConflictingCells__ItIsNotSet() {
        Rules rules = mockRules(new int[]{1, 2});
        mBoard.addShip(new LocatedShip(new Ship(2), 0, 0));
        mBoard.addShip(new LocatedShip(new Ship(1), 0, 0));

        assertThat(BoardUtils.isBoardSet(mBoard, rules), is(false));
    }

    @Test
    public void EmptyCellIsNotConflicting() {
        assertThat(BoardUtils.isCellConflicting(mBoard, 5, 5, mRules.allowAdjacentShips()), is(false));
    }

    @Test
    public void cell_is_not_conflicting_if_it_only_touched_by_1_ship() {
        mBoard.addShip(new LocatedShip(new Ship(1), 5, 5));

        assertThat(BoardUtils.isCellConflicting(mBoard, 5, 5, mRules.allowAdjacentShips()), is(false));
    }

    @Test
    public void cell_is_not_conflicting_if_it_only_touched_by_1_ship2() {
        mBoard.addShip(new LocatedShip(new Ship(1), 1, 5));

        assertThat(BoardUtils.isCellConflicting(mBoard, 1, 5, mRules.allowAdjacentShips()), is(false));
    }

    @Test
    public void cell_is_conflicting_if_it_is_occupied_by_ship_A_and_ship_B_is_touching_the_cell() {
        Ship ship = new Ship(1);
        mBoard.addShip(new LocatedShip(ship, 6, 6));

        Ship ship1 = new Ship(1);
        mBoard.addShip(new LocatedShip(ship1, 5, 5));

        assertThat(BoardUtils.isCellConflicting(mBoard, 5, 5, mRules.allowAdjacentShips()), is(true));
        assertThat(BoardUtils.isCellConflicting(mBoard, 6, 6, mRules.allowAdjacentShips()), is(true));
    }

    @Test
    public void WhenShipsOverlap__ThereIsAConflict() {
        mBoard.addShip(new LocatedShip(new Ship(1), 5, 5));

        mBoard.addShip(new LocatedShip(new Ship(1), 5, 5));

        assertThat(BoardUtils.isCellConflicting(mBoard, 5, 5, mRules.allowAdjacentShips()), is(true));
    }

    @Test
    public void board_is_NOT_defeated_if_it_has_less_than_all_ships() {
        Rules rules = mockRules(new int[]{1, 2});
        Ship ship = ShipTestUtils.deadShip();
        mBoard.addShip(ship, 1, 1);

        assertThat(BoardUtils.isItDefeatedBoard(mBoard, rules.getAllShipsSizes().length), is(false));
    }

    @Test
    public void board_is_NOT_defeated_if_it_has_at_least_1_alive_ship() {
        Board board = mock(Board.class);
        Set<Ship> ships = mock_9_dead_1_alive_ship();
        when(board.getShips()).thenReturn(ships);

        assertThat(BoardUtils.isItDefeatedBoard(board, mRules.getAllShipsSizes().length), is(false));
    }

    @Test
    public void board_is_defeated_if_it_has_10_dead_ships() {
        Board board = mock(Board.class);
        Set<Ship> ships = mock_10_dead_ships();
        when(board.getShips()).thenReturn(ships);

        assertThat(BoardUtils.isItDefeatedBoard(board, mRules.getAllShipsSizes().length), is(true));
    }

    @Test
    public void WhenThereIsNoShipToBePicked__NullReturned() {
        Ship ship = BoardUtils.pickShipFromBoard(mBoard, 5, 5);

        assertThat(ship, is(nullValue()));
    }

    @Test
    public void TryingToPickFromOutsideTheBoard__NullReturned() {
        Ship ship = BoardUtils.pickShipFromBoard(mBoard, mBoard.width() + 1, 5);

        assertThat(ship, is(nullValue()));
    }

    @Test
    public void WhenThereIsShipToBePicked__ItIsReturned() {
        Ship ship = new Ship(3);
        mBoard.addShip(new LocatedShip(ship, 5, 5));

        Ship ship2 = BoardUtils.pickShipFromBoard(mBoard, 5, 5);

        assertThat(ship2, is(ship));
    }

    // TODO: this test can be removed?
    @Test
    public void testBoardDefeated() {
        assertThat(BoardUtils.isItDefeatedBoard(mBoard, 1), is(false));

        Ship ship = new Ship(2);
        mBoard.addShip(new LocatedShip(ship, 5, 5));
        assertFalse(BoardUtils.isItDefeatedBoard(mBoard, 1));

        ship.shoot();
        assertFalse(BoardUtils.isItDefeatedBoard(mBoard, 1));

        ship.shoot();
        assertTrue(BoardUtils.isItDefeatedBoard(mBoard, 1));
    }

    @Test
    public void ShipLocationCorrectlyFound1() {
        mBoard.setCell(Cell.HIT, 5, 5);

        Vector shipLocation = BoardUtils.findShipLocation(mBoard);

        assertThat(shipLocation, is(Vector.get(5 ,5)));
    }

    @Test
    public void ShipLocationCorrectlyFound2() {
        mBoard.setCell(Cell.HIT, 5, 5);
        mBoard.setCell(Cell.HIT, 6, 5);

        Vector shipLocation = BoardUtils.findShipLocation(mBoard);

        assertThat(shipLocation, is(Vector.get(5 ,5)));
    }

    @Test
    public void ShipLocationCorrectlyFound3() {
        mBoard.setCell(Cell.HIT, 5, 5);
        mBoard.setCell(Cell.HIT, 5, 7);
        mBoard.setCell(Cell.HIT, 5, 6);

        Vector shipLocation = BoardUtils.findShipLocation(mBoard);

        assertThat(shipLocation, is(Vector.get(5 ,5)));
    }

    @NonNull
    private Set<Ship> mock_10_dead_ships() {
        Set<Ship> ships = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            ships.add(ShipTestUtils.deadShip());
        }
        return ships;
    }

    @NonNull
    private Set<Ship> mock_9_dead_1_alive_ship() {
        Set<Ship> ships = new HashSet<>();
        for (int i = 0; i < 9; i++) {
            ships.add(ShipTestUtils.deadShip());
        }
        ships.add(new Ship(1));
        return ships;
    }

    private void putShipAt(Ship ship, int x, int y) {
        mBoard.addShip(new LocatedShip(ship, x, y));
    }

    private LocatedShip getFirstShip() {
        return mBoard.getLocatedShips().iterator().next();
    }
}