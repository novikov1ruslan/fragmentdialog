package com.ivygames.morskoiboi.model;

import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.model.Ship.Orientation;
import com.ivygames.morskoiboi.variant.RussianRules;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class BoardTest {

    private Board mBoard = new Board();
    private Placement mPlacement;

    @Before
    public void setUp() throws Exception {
        Random random = mock(Random.class);
        when(random.nextInt(anyInt())).thenReturn(0);
        Rules rules = new RussianRules();
        mPlacement = new Placement(random, rules);
    }

    @Test
    public void testEquals() {
        Board board1 = new Board();
        Board board2 = new Board();
        assertEquals(board1, board2);

        Ship ship = new Ship(3);
        mPlacement.putShipAt(board2, ship, 5, 5);
        assertFalse(board1.equals(board2));

        mPlacement.putShipAt(board1, ship, 5, 5);
        assertEquals(board1, board2);
    }

    @Test
    public void testWidth() {
        assertEquals(10, mBoard.horizontalDimension());
    }

    @Test
    public void testHeight() {
        assertEquals(10, mBoard.verticalDimension());
    }

    @Test
    public void testGetCellsAround() {
        Cell reserved = Cell.RESERVED;
        mBoard.setCell(reserved, 5, 4);
        mBoard.setCell(reserved, 5, 6);
        mBoard.setCell(reserved, 4, 5);
        mBoard.setCell(reserved, 6, 5);
        Collection<Cell> cells = getCellsAround(mBoard, 5, 5);
        assertEquals(4, cells.size());
        for (Cell cell : cells) {
            assertTrue(cell == Cell.RESERVED);
        }
    }

    @Test
    public void testGetHitsAround() {
        Collection<Vector2> hits = getHitsAround(mBoard, 5, 5);
        assertEquals(0, hits.size());

        mBoard.setCell(Cell.HIT, 5, 6);
        hits = getHitsAround(mBoard, 5, 5);
        assertEquals(1, hits.size());
        Vector2 hit = hits.iterator().next();
        assertEquals(5, hit.getX());
        assertEquals(6, hit.getY());
    }

    @Test
    public void testAllShipsAreDestroyed() {
        assertFalse(!Board.allAvailableShipsAreDestroyed(mBoard));

        Ship ship = new Ship(2);
        mPlacement.putShipAt(mBoard, ship, 5, 5);
        assertFalse(Board.allAvailableShipsAreDestroyed(mBoard));

        ship.shoot();
        assertFalse(Board.allAvailableShipsAreDestroyed(mBoard));

        ship.shoot();
        assertTrue(Board.allAvailableShipsAreDestroyed(mBoard));
    }

    @Test
    public void testGetShipsAt() {
        Board board = new Board();

        mPlacement.putShipAt(board, new Ship(1), 5, 5);

        assertThat(board.getShipsAt(5, 5).size(), is(1));
        assertThat(board.getShipsAt(5, 6).size(), Matchers.is(0));
    }

    @Test
    public void testPutHorizontalShipSucceeded() {
        Ship ship = new Ship(2, Orientation.HORIZONTAL);
        mPlacement.putShipAt(mBoard, ship, 8, 5);
        assertShipIsCorrectlyAlignedAt(ship, 8, 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutHorizontalShipFailed() {
        Ship ship = new Ship(2, Orientation.HORIZONTAL);
        mPlacement.putShipAt(mBoard, ship, 9, 5);
    }

    @Test
    public void testPutVerticalShipSucceeded() {
        Ship ship = new Ship(3, Orientation.VERTICAL);
        mPlacement.putShipAt(mBoard, ship, 3, 7);
        assertShipIsCorrectlyAlignedAt(ship, 3, 7);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutVerticalShipFailed() {
        Ship ship = new Ship(3, Orientation.VERTICAL);
        mPlacement.putShipAt(mBoard, ship, 3, 8);
    }

    // public void testPutShip() {
    // Ship ship = new Ship(3, Orientation.VERTICAL);
    // mBoard.putShip(ship);
    // assertPutShipSucceeded(mBoard, ship, ship.getX(), ship.getY());
    // }

//    private static void assertReservedOnlyInProximity(Board board, Ship ship, int i, int j) {
//        Cell cell = board.getCell(i, j);
//        if (ShipTestUtils.isInProximity(ship, i, j)) {
//            assertTrue(cell.toString(), cell == Cell.RESERVED);
//        } else {
//            assertTrue(cell.toString(), cell == Cell.EMPTY);
//        }
//    }

    private static void assertShipIsCorrectlyAlignedAt(Ship ship, int x, int y) {
        assertEquals(x, ship.getX());
        assertEquals(y, ship.getY());
//        assertReservedOnlyInProximityOnCleanBoard(board, ship);
    }

//    private static void assertReservedOnlyInProximityOnCleanBoard(Board board, Ship ship) {
//        for (int i = 0; i < 10; i++) {
//            for (int j = 0; j < 10; j++) {
//                assertReservedOnlyInProximity(board, ship, i, j);
//            }
//        }
//    }

    @Test
    public void testGetCell() {
        Cell cell = mBoard.getCell(0, 0);
        assertNotNull(cell);
        mBoard.setCell(Cell.RESERVED, 0, 0);
        cell = mBoard.getCell(0, 0);
        assertTrue(cell == Cell.RESERVED);
        mBoard.setCell(Cell.MISS, 0, 0);
        cell = mBoard.getCell(0, 0);
        assertTrue(cell == Cell.MISS);
    }

    @Test
    public void testGetShips() {
        int totalShips = mBoard.getShips().size();
        assertEquals(0, totalShips);

        mPlacement.putShipAt(mBoard, new Ship(1), 5, 5);
        totalShips = mBoard.getShips().size();
        assertEquals(1, totalShips);

        mPlacement.putShipAt(mBoard, new Ship(2), 8, 9);
        totalShips = mBoard.getShips().size();
        assertEquals(2, totalShips);
    }

    @Test
    public void testCanPutShipAt() {
        Ship ship = new Ship(1);
        for (int i = -1; i < 11; i++) {
            for (int j = -1; j < 11; j++) {
                if (i >= 0 && i < 10 && j >= 0 && j < 10) {
                    assertTrue(mBoard.shipFitsTheBoard(ship, i, j));
                } else {
                    assertFalse(mBoard.shipFitsTheBoard(ship, i, j));
                }
            }
        }
    }

    @Test
    public void testContainsCell() {

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                assertTrue(Board.contains(i, j));
            }
        }

        assertFalse(Board.contains(-1, 0));
        assertFalse(Board.contains(10, 0));
        assertFalse(Board.contains(0, 10));
        assertFalse(Board.contains(0, -1));
    }

    private static void addIfContains(Board board, Collection<Cell> cells, int x, int y) {
        if (Board.contains(x, y)) {
            cells.add(board.getCell(x, y));
        }
    }

    public static Collection<Cell> getCellsAround(Board board, int x, int y) {
        Collection<Cell> cells = new ArrayList<>();
        addIfContains(board, cells, x + 1, y);
        addIfContains(board, cells, x - 1, y);
        addIfContains(board, cells, x, y + 1);
        addIfContains(board, cells, x, y - 1);

        return cells;
    }

    public void addIfHit(Board board, Collection<Vector2> hits, int x, int y) {
        if (Board.contains(x, y) && board.getCell(x, y) == Cell.HIT) {
            hits.add(Vector2.get(x, y));
        }
    }

    public Collection<Vector2> getHitsAround(Board board, int x, int y) {
        Collection<Vector2> hits = new ArrayList<>();
        addIfHit(board, hits, x + 1, y);
        addIfHit(board, hits, x - 1, y);
        addIfHit(board, hits, x, y + 1);
        addIfHit(board, hits, x, y - 1);

        return hits;
    }

}
