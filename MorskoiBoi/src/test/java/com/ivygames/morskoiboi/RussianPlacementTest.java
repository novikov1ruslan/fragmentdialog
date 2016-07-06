package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.utils.GameUtils;
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

public class RussianPlacementTest {

    private Placement mAlgorithm;
    private int mNumberOfDistinctShips;
    private Rules rules;
    private Board mBoard;

    @BeforeClass
    public static void runBeforeClass() {

    }

    @Before
	public void setup() {
        mBoard = new Board();
        rules = new RussianRules();
        Dependencies.inject(rules);
        mAlgorithm = new Placement(new Random(1), rules);
	}

    @Test
    public void after_generating_full_board_it_has_russian_fleet() {
        Board board = new Board();
        mAlgorithm.populateBoardWithShips(board, generateFullFleet());
        assertAllTheShipsAreRussianFleet(board.getShips());
    }

    @Test
    public void testRemoveShipFrom2() {
        Ship ship = new Ship(1, Ship.Orientation.VERTICAL);
        putShipAt(mBoard, ship, 5, 5);
        mBoard.getCell(8, 8).setMiss();

        assertNull(mAlgorithm.removeShipFrom(mBoard, 4, 4));
        assertEquals(1, mBoard.getShips().size());

        assertNotNull(mAlgorithm.removeShipFrom(mBoard, 5, 5));
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
                    assertNull(i + "," + j, mAlgorithm.removeShipFrom(mBoard, i, j));
                }
            }
        }

        assertEquals(2, mBoard.getShips().size());
        assertFalse(91 == mBoard.getEmptyCells().size());
        Ship ship2 = mAlgorithm.removeShipFrom(mBoard, 5, 5);
        assert ship2 != null;

        assertEquals(ship.getSize(), ship2.getSize());
        assertEquals(ship.isHorizontal(), ship2.isHorizontal());
        assertEquals(ship.getX(), ship2.getX());
        assertEquals(ship.getY(), ship2.getY());

        assertEquals(91, mBoard.getEmptyCells().size());
        assertEquals(1, mBoard.getShips().size());
    }

    @Test
    public void testCanRotateShip() {
        Ship ship = new Ship(2, Ship.Orientation.HORIZONTAL);
        putShipAt(ship, 5, 5);
        mAlgorithm.rotateShipAt(mBoard, 5, 5);

        assertFalse(ship.isHorizontal());
        assertReservedOnlyInProximityOnCleanBoard(mBoard, ship);
    }

    @Test
    public void testCannotRotateShip() {
        Ship ship = new Ship(4, Ship.Orientation.HORIZONTAL);
        putShipAt(ship, 5, 7);
        mAlgorithm.rotateShipAt(mBoard, 5, 7);

        assertFalse(ship.isHorizontal());
        assertEquals(5, ship.getX());
        assertEquals(6, ship.getY());
        assertReservedOnlyInProximityOnCleanBoard(mBoard, ship);
    }

    private void putShipAt(Ship ship, int x, int y) {
        putShipAt(mBoard, ship, x, y);
    }

    private void putShipAt(Board board, Ship ship, int x, int y) {
        mAlgorithm.putShipAt(board, ship, x, y);
    }

    @NonNull
    private Collection<Ship> generateFullFleet() {
        return GameUtils.generateShipsForSizes(rules.getAllShipsSizes());
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
