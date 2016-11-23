package com.ivygames.battleship.board;

import com.ivygames.battleship.BoardUtils;
import com.ivygames.battleship.ship.Ship;
import com.ivygames.battleship.ship.Ship.Orientation;

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
        assertThat(BoardTestUtils.similar(board1, board2), is(true));

        Ship ship = new Ship(3);
        board2.addShip(new LocatedShip(ship, 5, 5));
        assertFalse(BoardTestUtils.similar(board1, board2));

        board1.addShip(new LocatedShip(ship, 5, 5));
        assertThat(BoardTestUtils.similar(board1, board2), is(true));
    }

    @Test
    public void testWidth() {
        assertEquals(10, mBoard.width());
    }

    @Test
    public void testHeight() {
        assertEquals(10, mBoard.height());
    }

    @Test
    public void testGetHitsAround() {
        Collection<Vector> hits = getHitsAround(mBoard, 5, 5);
        assertEquals(0, hits.size());

        mBoard.setCell(Cell.HIT, 5, 6);
        hits = getHitsAround(mBoard, 5, 5);
        assertEquals(1, hits.size());
        Vector hit = hits.iterator().next();
        assertEquals(5, hit.x);
        assertEquals(6, hit.y);
    }

    @Test
    public void testAllShipsAreDestroyed() {
        assertFalse(!BoardUtils.allAvailableShipsAreDestroyed(mBoard));

        Ship ship = new Ship(2);
        mBoard.addShip(new LocatedShip(ship, 5, 5));
        assertFalse(BoardUtils.allAvailableShipsAreDestroyed(mBoard));

        ship.shoot();
        assertFalse(BoardUtils.allAvailableShipsAreDestroyed(mBoard));

        ship.shoot();
        assertTrue(BoardUtils.allAvailableShipsAreDestroyed(mBoard));
    }

    @Test
    public void testGetShipsAt() {
        Board board = new Board();

        board.addShip(new LocatedShip(new Ship(1), 5, 5));

        assertThat(board.getShipsAt(5, 5).size(), is(1));
        assertThat(board.getShipsAt(5, 6).size(), is(0));
    }

    @Test
    public void testPutHorizontalShipSucceeded() {
        Ship ship = new Ship(2, Orientation.HORIZONTAL);

        mBoard.addShip(new LocatedShip(ship, 8, 5));

        assertThat(ship, is(mBoard.getShipsAt(8, 5).iterator().next()));
        assertThat(ship, is(mBoard.getShipsAt(9, 5).iterator().next()));
    }

    @Test
    public void testRemoveHorizontalShipSucceeded() {
        Ship ship = new Ship(2, Orientation.HORIZONTAL);
        mBoard.addShip(new LocatedShip(ship, 8, 5));

        mBoard.removeShip(new LocatedShip(ship, 8, 5));

        assertThat(mBoard.getShips().size(), is(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutHorizontalShipFailed() {
        Ship ship = new Ship(2, Orientation.HORIZONTAL);
        mBoard.addShip(new LocatedShip(ship, 9, 5));
    }

    @Test
    public void testPutVerticalShipSucceeded() {
        Ship ship = new Ship(3, Orientation.VERTICAL);

        mBoard.addShip(new LocatedShip(ship, 3, 7));

        assertThat(ship, is(mBoard.getShipsAt(3, 7).iterator().next()));
        assertThat(ship, is(mBoard.getShipsAt(3, 8).iterator().next()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutVerticalShipFailed() {
        Ship ship = new Ship(3, Orientation.VERTICAL);
        mBoard.addShip(new LocatedShip(ship, 3, 8));
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

        mBoard.addShip(new LocatedShip(new Ship(1), 5, 5));
        totalShips = mBoard.getShips().size();
        assertEquals(1, totalShips);

        mBoard.addShip(new LocatedShip(new Ship(2), 8, 9));
        totalShips = mBoard.getShips().size();
        assertEquals(2, totalShips);
    }

    @Test
    public void testCanPutShipAt() {
        Ship ship = new Ship(1);
        for (int i = -1; i < 11; i++) {
            for (int j = -1; j < 11; j++) {
                if (i >= 0 && i < 10 && j >= 0 && j < 10) {
                    assertTrue(BoardUtils.shipFitsTheBoard(ship, i, j));
                } else {
                    assertFalse(BoardUtils.shipFitsTheBoard(ship, i, j));
                }
            }
        }
    }

    @Test
    public void testContainsCell() {

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                assertTrue(BoardUtils.contains(i, j));
            }
        }

        assertFalse(BoardUtils.contains(-1, 0));
        assertFalse(BoardUtils.contains(10, 0));
        assertFalse(BoardUtils.contains(0, 10));
        assertFalse(BoardUtils.contains(0, -1));
    }

    public void addIfHit(Board board, Collection<Vector> hits, int x, int y) {
        if (BoardUtils.contains(x, y) && board.getCell(x, y) == Cell.HIT) {
            hits.add(Vector.get(x, y));
        }
    }

    public Collection<Vector> getHitsAround(Board board, int x, int y) {
        Collection<Vector> hits = new ArrayList<>();
        addIfHit(board, hits, x + 1, y);
        addIfHit(board, hits, x - 1, y);
        addIfHit(board, hits, x, y + 1);
        addIfHit(board, hits, x, y - 1);

        return hits;
    }

}
