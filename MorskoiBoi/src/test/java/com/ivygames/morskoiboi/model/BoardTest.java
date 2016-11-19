package com.ivygames.morskoiboi.model;

import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.model.Ship.Orientation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Collection;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class BoardTest {

    private Board mBoard = new Board();

    @Test
    public void testEquals() {
        Board board1 = new Board();
        Board board2 = new Board();
        assertEquals(board1, board2);

        Ship ship = new Ship(3);
        Placement.putShipAt(board2, new Board.LocatedShip(ship, 5, 5));
        assertFalse(board1.equals(board2));

        Placement.putShipAt(board1, new Board.LocatedShip(ship, 5, 5));
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
    public void testGetHitsAround() {
        Collection<Vector2> hits = getHitsAround(mBoard, 5, 5);
        assertEquals(0, hits.size());

        mBoard.setCell(Cell.HIT, 5, 6);
        hits = getHitsAround(mBoard, 5, 5);
        assertEquals(1, hits.size());
        Vector2 hit = hits.iterator().next();
        assertEquals(5, hit.x);
        assertEquals(6, hit.y);
    }

    @Test
    public void testAllShipsAreDestroyed() {
        assertFalse(!Board.allAvailableShipsAreDestroyed(mBoard));

        Ship ship = new Ship(2);
        Placement.putShipAt(mBoard, new Board.LocatedShip(ship, 5, 5));
        assertFalse(Board.allAvailableShipsAreDestroyed(mBoard));

        ship.shoot();
        assertFalse(Board.allAvailableShipsAreDestroyed(mBoard));

        ship.shoot();
        assertTrue(Board.allAvailableShipsAreDestroyed(mBoard));
    }

    @Test
    public void testGetShipsAt() {
        Board board = new Board();

        Placement.putShipAt(board, new Board.LocatedShip(new Ship(1), 5, 5));

        assertThat(board.getShipsAt(5, 5).size(), is(1));
        assertThat(board.getShipsAt(5, 6).size(), is(0));
    }

    @Test
    public void testPutHorizontalShipSucceeded() {
        Ship ship = new Ship(2, Orientation.HORIZONTAL);

        Placement.putShipAt(mBoard, new Board.LocatedShip(ship, 8, 5));

        assertThat(ship, is(mBoard.getShipsAt(8, 5).iterator().next()));
        assertThat(ship, is(mBoard.getShipsAt(9, 5).iterator().next()));
    }

    @Test
    public void testRemoveHorizontalShipSucceeded() {
        Ship ship = new Ship(2, Orientation.HORIZONTAL);
        mBoard.addShip(new Board.LocatedShip(ship, 8, 5));

        mBoard.removeShip(new Board.LocatedShip(ship, 8, 5));

        assertThat(mBoard.getShips().size(), is(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutHorizontalShipFailed() {
        Ship ship = new Ship(2, Orientation.HORIZONTAL);
        Placement.putShipAt(mBoard, new Board.LocatedShip(ship, 9, 5));
    }

    @Test
    public void testPutVerticalShipSucceeded() {
        Ship ship = new Ship(3, Orientation.VERTICAL);

        Placement.putShipAt(mBoard, new Board.LocatedShip(ship, 3, 7));

        assertThat(ship, is(mBoard.getShipsAt(3, 7).iterator().next()));
        assertThat(ship, is(mBoard.getShipsAt(3, 8).iterator().next()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutVerticalShipFailed() {
        Ship ship = new Ship(3, Orientation.VERTICAL);
        Placement.putShipAt(mBoard, new Board.LocatedShip(ship, 3, 8));
    }

    @Test
    public void testGetCell() {
        Cell cell = mBoard.getCell(0, 0);
        assertNotNull(cell);

        mBoard.setCell(Cell.HIT, 0, 0);
        cell = mBoard.getCell(0, 0);
        assertTrue(cell == Cell.HIT);

        mBoard.setCell(Cell.MISS, 0, 0);
        cell = mBoard.getCell(0, 0);
        assertTrue(cell == Cell.MISS);
    }

    @Test
    public void testGetShips() {
        int totalShips = mBoard.getShips().size();
        assertEquals(0, totalShips);

        Placement.putShipAt(mBoard, new Board.LocatedShip(new Ship(1), 5, 5));
        totalShips = mBoard.getShips().size();
        assertEquals(1, totalShips);

        Placement.putShipAt(mBoard, new Board.LocatedShip(new Ship(2), 8, 9));
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
