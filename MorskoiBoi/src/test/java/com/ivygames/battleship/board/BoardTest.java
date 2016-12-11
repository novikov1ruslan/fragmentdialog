package com.ivygames.battleship.board;

import com.ivygames.battleship.BoardUtils;
import com.ivygames.battleship.ship.LocatedShip;
import com.ivygames.battleship.ship.Ship;
import com.ivygames.battleship.ship.Ship.Orientation;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class BoardTest {

    private Board mBoard = new Board();

    @Test
    public void afterSetting__CellIsSet() {
        mBoard.setCell(Cell.HIT, 5, 6);

        assertThat(mBoard.getCell(5, 6), is(Cell.HIT));
    }

    @Test
    public void gettingShip__ReturnsShip() {
        Ship ship = new Ship(1);
        mBoard.addShip(ship, 5, 6);

        LocatedShip locatedShip = mBoard.getShipAt(5, 6);

        assertThat(locatedShip.ship, is(ship));
        assertThat(locatedShip.coordinate.x, is(5));
        assertThat(locatedShip.coordinate.y, is(6));
    }

    @Test
    public void gettingShip__Fails() {
        mBoard.addShip(new Ship(1), 5, 5);

        assertThat(mBoard.getShipAt(5, 6), is(nullValue()));
    }

    @Test
    public void gettingMultipleShips__ReturnShips() {
        mBoard.addShip(new Ship(1), 5, 5);
        mBoard.addShip(new Ship(3), 5, 5);

        assertThat(mBoard.getShipsAt(5, 5).size(), is(2));
    }

    @Test
    public void gettingMultipleShips__Fails() {
        mBoard.addShip(new Ship(1), 5, 5);
        mBoard.addShip(new Ship(3), 5, 5);

        assertThat(mBoard.getShipsAt(6, 7).size(), is(0));
    }

    @Test
    public void removingShipSucceeded() {
        Ship ship = new Ship(2, Orientation.HORIZONTAL);
        mBoard.addShip(ship, 8, 5);

        mBoard.removeShip(ship);

        assertThat(mBoard.getShips().size(), is(0));
    }

    @Test
    public void removingShipFailed() {
        Ship ship1 = new Ship(1);
        mBoard.addShip(ship1, 8, 5);

        Ship ship2 = new Ship(1);
        mBoard.removeShip(ship2);

        assertThat(mBoard.getShips().size(), is(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutHorizontalShipFailed() {
        Ship ship = new Ship(2, Orientation.HORIZONTAL);

        mBoard.addShip(ship, 9, 5);
    }

    @Test
    public void puttingHorizontalShipSucceeds() {
        Ship ship = new Ship(2, Orientation.HORIZONTAL);

        mBoard.addShip(ship, 8, 5);

        assertThat(ship, is(mBoard.getShipAt(8, 5).ship));
        assertThat(ship, is(mBoard.getShipAt(9, 5).ship));
    }

    @Test
    public void testPutVerticalShipSucceeded() {
        Ship ship = new Ship(3, Orientation.VERTICAL);

        mBoard.addShip(ship, 3, 7);

        assertThat(ship, is(mBoard.getShipAt(3, 7).ship));
        assertThat(ship, is(mBoard.getShipAt(3, 8).ship));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutVerticalShipFailed() {
        Ship ship = new Ship(3, Orientation.VERTICAL);

        mBoard.addShip(ship, 3, 8);
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

        mBoard.addShip(new Ship(1), 5, 5);
        totalShips = mBoard.getShips().size();
        assertEquals(1, totalShips);

        mBoard.addShip(new Ship(2), 8, 9);
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
