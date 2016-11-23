package com.ivygames.battleship;

import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.board.BoardSerialization;
import com.ivygames.battleship.board.BoardSerializationTest;
import com.ivygames.battleship.board.Coordinate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Collection;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class BoardUtilsTest {

    @Test
    public void NeighbouringCells8() {
        Collection<Coordinate> coordinates = BoardUtils.getNeighboringCoordinates(1, 1);

        assertThat(coordinates.size(), is(8));
        assertThat(coordinates, containsInAnyOrder(
                Coordinate.get(0, 0),
                Coordinate.get(1, 0),
                Coordinate.get(2, 0),

                Coordinate.get(0, 1),
                Coordinate.get(2, 1),

                Coordinate.get(0, 2),
                Coordinate.get(1, 2),
                Coordinate.get(2, 2)));
    }

    @Test
    public void NeighbouringCellsCorner() {
        Collection<Coordinate> coordinates = BoardUtils.getNeighboringCoordinates(0, 0);

        assertThat(coordinates.size(), is(3));
        assertThat(coordinates, containsInAnyOrder(
                Coordinate.get(0, 1),
                Coordinate.get(1, 1),
                Coordinate.get(1, 0)));
    }

    @Test
    public void NeighbouringCellsWall() {
        Collection<Coordinate> coordinates = BoardUtils.getNeighboringCoordinates(0, 1);

        assertThat(coordinates.size(), is(5));
        assertThat(coordinates, containsInAnyOrder(
                Coordinate.get(0, 0),
                Coordinate.get(0, 2),
                Coordinate.get(1, 0),
                Coordinate.get(1, 1),
                Coordinate.get(1, 2)));
    }

    @Test
    public void testEmptyCells() {
        Board board = BoardSerialization.fromJson(BoardSerializationTest.EMPTY_BOARD);
        assertEquals(100, BoardUtils.getCoordinatesFreeFromShips(board, false).size());

        board = BoardSerialization.fromJson(BoardSerializationTest.BOARD_WITH_SHIP_x1_5_5);
        assertEquals(91, BoardUtils.getCoordinatesFreeFromShips(board, false).size());

        board = BoardSerialization.fromJson(BoardSerializationTest.BOARD_WITH_SHIP_x1_5_5_x2_5_5);
        assertEquals(85, BoardUtils.getCoordinatesFreeFromShips(board, false).size());
    }
}