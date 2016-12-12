package com.ivygames.battleship.board;

import com.ivygames.battleship.BoardUtils;
import com.ivygames.battleship.ship.LocatedShip;
import com.ivygames.battleship.ship.Ship;
import com.ivygames.battleship.ship.Ship.Orientation;

import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
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
        assertThat(mBoard.getCell(0, 0), is(Cell.EMPTY));

        mBoard.setCell(Cell.HIT, Vector.get(0, 0));
        assertThat(mBoard.getCell(Vector.get(0, 0)), is(Cell.HIT));

        mBoard.setCell(Cell.MISS, 0, 0);
        assertThat(mBoard.getCell(0, 0), is(Cell.MISS));
    }

    @Test
    public void testGetShips() {
        assertEquals(0, mBoard.getShips().size());
        assertEquals(0, mBoard.getLocatedShips().size());

        mBoard.addShip(new Ship(1), 5, 5);
        assertEquals(1, mBoard.getShips().size());
        assertEquals(1, mBoard.getLocatedShips().size());

        mBoard.addShip(new Ship(2), 8, 9);
        assertEquals(2, mBoard.getShips().size());
        assertEquals(2, mBoard.getLocatedShips().size());
    }

    @Test
    public void EmptyBoardHasAllCellsEmpty() {
        assertAllCellsAreEmpty();
    }

    private void assertAllCellsAreEmpty() {
        List<Vector> emptyCells = mBoard.getCellsByType(Cell.EMPTY);
        assertThat(emptyCells.size(), is(mBoard.width() * mBoard.height()));
    }

    @Test
    public void EmptyBoardHasSomeMissCells() {
        mBoard.setCell(Cell.MISS, 7, 8);
        mBoard.setCell(Cell.MISS, 1, 3);

        List<Vector> emptyCells = mBoard.getCellsByType(Cell.MISS);

        assertThat(emptyCells.size(), is(2));
    }

    @Test
    public void afterBoardCleared__ItHasNoShipsAndAllCellsAreEmpty() {
        mBoard.setCell(Cell.MISS, 4, 3);
        mBoard.addShip(new Ship(2), 5, 7);

        mBoard.clearBoard();

        assertThat(mBoard.getShips().isEmpty(), is(true));
        assertAllCellsAreEmpty();
    }

    @Test
    public void testContainsCell() {
        assertTrue(BoardUtils.contains(0, 0));
        assertTrue(BoardUtils.contains(0, mBoard.height() - 1));
        assertTrue(BoardUtils.contains(mBoard.width() - 1, 0));
        assertTrue(BoardUtils.contains(mBoard.width() - 1, mBoard.height() - 1));

        assertFalse(BoardUtils.contains(-1, 0));
        assertFalse(BoardUtils.contains(mBoard.width(), 0));
        assertFalse(BoardUtils.contains(0, mBoard.height()));
        assertFalse(BoardUtils.contains(0, -1));
    }

    @Test
    public void boardTpStringDoesNotCrash() {
        mBoard.addShip(new Ship(4), 3, 3);

        mBoard.toString();
    }

}
