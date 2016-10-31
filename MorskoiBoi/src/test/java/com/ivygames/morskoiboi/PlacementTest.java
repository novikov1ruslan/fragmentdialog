package com.ivygames.morskoiboi;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.variant.RussianRules;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;
import java.util.Random;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class PlacementTest {

    private Placement mPlacement;
    private int mNumberOfDistinctShips;
    private Rules rules = new RussianRules();
    private Board mBoard = new Board();
    private Random mRandom = new Random();

    @BeforeClass
    public static void runBeforeClass() {

    }

    @Before
	public void setup() {
        Dependencies.inject(rules);
        mPlacement = new Placement(mRandom, rules);
	}

    @Test
    public void after_generating_full_board_it_has_russian_fleet() {
        Board board = new Board();
        // TODO: this test tests populateBoardWithShips actually and not generation
        Collection<Ship> ships = ShipUtils.generateFullFleet(rules.getAllShipsSizes(),
                new ShipUtils.OrientationBuilder(mRandom));

        mPlacement.populateBoardWithShips(board, ships);

        assertAllTheShipsAreRussianFleet(board.getShips());
    }

    @Test
    public void whenPossibleToPutShipOnBoard__BoardHasTheShip() {
        Board board = new Board();
        Ship ship = new Ship(4, Ship.Orientation.HORIZONTAL);

        boolean success = mPlacement.putShipOnBoard(ship, board);
        assertThat(success, is(true));

        assertThat(board.getShips().iterator().next(), is(ship));
    }

    @Test
    public void whenNotPossibleToPutShipOnBoard__BoardDoeNotHaveTheShip() {
        Board board = new Board();
        mPlacement.putShipOnBoard(new Ship(4, Ship.Orientation.HORIZONTAL), board);
        mPlacement.putShipOnBoard(new Ship(4, Ship.Orientation.HORIZONTAL), board);
        mPlacement.putShipOnBoard(new Ship(4, Ship.Orientation.HORIZONTAL), board);
        mPlacement.putShipOnBoard(new Ship(4, Ship.Orientation.HORIZONTAL), board);
        mPlacement.putShipOnBoard(new Ship(4, Ship.Orientation.HORIZONTAL), board);
        mPlacement.putShipOnBoard(new Ship(4, Ship.Orientation.HORIZONTAL), board);
        mPlacement.putShipOnBoard(new Ship(4, Ship.Orientation.HORIZONTAL), board);
        mPlacement.putShipOnBoard(new Ship(4, Ship.Orientation.HORIZONTAL), board);
        mPlacement.putShipOnBoard(new Ship(4, Ship.Orientation.HORIZONTAL), board);
        mPlacement.putShipOnBoard(new Ship(4, Ship.Orientation.HORIZONTAL), board);

        boolean success = mPlacement.putShipOnBoard(new Ship(4, Ship.Orientation.HORIZONTAL), board);
        assertThat(success, is(false));
    }

    @Test
    public void testRemoveShipFrom2() {
        Ship ship = new Ship(1, Ship.Orientation.VERTICAL);
        putShipAt(mBoard, ship, 5, 5);
        mBoard.getCell(8, 8).setMiss();

        assertNull(mPlacement.removeShipFrom(mBoard, 4, 4));
        assertEquals(1, mBoard.getShips().size());

        assertNotNull(mPlacement.removeShipFrom(mBoard, 5, 5));
        assertEquals(0, mBoard.getShips().size());

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Cell cell = mBoard.getCell(i, j);
                if (i == 8 && j == 8) {
                    assertTrue(cell.isMiss());
                } else {
                    assertTrue(cell.isEmpty());
                }
            }
        }
    }

    @Test
    public void testRemoveShipFrom() {
        Ship ship = new Ship(1, Ship.Orientation.VERTICAL);
        putShipAt(ship, 5, 5);
        putShipAt(new Ship(1), 6, 6);

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (!((i == 5 && j == 5) || (i == 6 && j == 6))) {
                    assertNull(i + "," + j, mPlacement.removeShipFrom(mBoard, i, j));
                }
            }
        }

        assertEquals(2, mBoard.getShips().size());
        assertFalse(91 == mBoard.getEmptyCells().size());
        Ship ship2 = mPlacement.removeShipFrom(mBoard, 5, 5);
        assert ship2 != null;

        assertEquals(ship.getSize(), ship2.getSize());
        assertEquals(ship.isHorizontal(), ship2.isHorizontal());
        assertEquals(ship.getX(), ship2.getX());
        assertEquals(ship.getY(), ship2.getY());

        assertEquals(91, mBoard.getEmptyCells().size());
        assertEquals(1, mBoard.getShips().size());
    }

    @Test
    public void canRotateHorizontalShip() {
        Ship ship = new Ship(2, Ship.Orientation.HORIZONTAL);
        putShipAt(ship, 5, 5);
        mPlacement.rotateShipAt(mBoard, 5, 5);

        assertThat(ship.isHorizontal(), is(false));
        assertReservedOnlyInProximityOnCleanBoard(mBoard, ship);
    }

    @Test
    public void canRotateVerticalShip() {
        Ship ship = new Ship(2, Ship.Orientation.VERTICAL);
        putShipAt(ship, 5, 5);
        mPlacement.rotateShipAt(mBoard, 5, 5);

        assertThat(ship.isHorizontal(), is(true));
        assertReservedOnlyInProximityOnCleanBoard(mBoard, ship);
    }

    @Test
    public void testCannotRotateShip() {
        Ship ship = new Ship(4, Ship.Orientation.HORIZONTAL);
        putShipAt(ship, 5, 7);
        mPlacement.rotateShipAt(mBoard, 5, 7);

        assertFalse(ship.isHorizontal());
        assertEquals(5, ship.getX());
        assertEquals(6, ship.getY());
        assertReservedOnlyInProximityOnCleanBoard(mBoard, ship);
    }

    private void putShipAt(Ship ship, int x, int y) {
        putShipAt(mBoard, ship, x, y);
    }

    private void putShipAt(Board board, Ship ship, int x, int y) {
        mPlacement.putShipAt(board, ship, x, y);
    }

    private void assertAllTheShipsAreRussianFleet(Collection<Ship> distinct) {
        mNumberOfDistinctShips = distinct.size();
        assertThat(mNumberOfDistinctShips, is(10));

        assertThereIsNewDistinctShip(distinct, 4);
        assertThereIsNewDistinctShip(distinct, 3);
        assertThereIsNewDistinctShip(distinct, 3);
        assertThereIsNewDistinctShip(distinct, 2);
        assertThereIsNewDistinctShip(distinct, 2);
        assertThereIsNewDistinctShip(distinct, 2);
        assertThereIsNewDistinctShip(distinct, 1);
        assertThereIsNewDistinctShip(distinct, 1);
        assertThereIsNewDistinctShip(distinct, 1);
        assertThereIsNewDistinctShip(distinct, 1);
    }

    private static void assertReservedOnlyInProximity(Board board, Ship ship, int i, int j) {
        Cell cell = board.getCell(i, j);
        if (Ship.isInProximity(ship, i, j)) {
            assertTrue(cell.toString(), cell.isReserved());
        } else {
            assertTrue(cell.toString(), cell.isEmpty());
        }
    }

    private static void assertReservedOnlyInProximityOnCleanBoard(Board board, Ship ship) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                assertReservedOnlyInProximity(board, ship, i, j);
            }
        }
    }

    private void assertThereIsNewDistinctShip(Collection<Ship> distinct, int shipSize) {
        assertThat(removeShipFromSet(distinct, shipSize), is(true));
        mNumberOfDistinctShips--;
        assertThat(distinct.size(), is(mNumberOfDistinctShips));
    }

    private boolean removeShipFromSet(Collection<Ship> distinct, int size) {
        for (Ship ship :
                distinct) {
            if (ship.getSize() == size) {
                distinct.remove(ship);
                return true;
            }
        }
        return false;
    }

}
