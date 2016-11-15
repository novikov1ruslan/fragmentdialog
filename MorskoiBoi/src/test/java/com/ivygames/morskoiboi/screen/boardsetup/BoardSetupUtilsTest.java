package com.ivygames.morskoiboi.screen.boardsetup;

import com.ivygames.morskoiboi.Dependencies;
import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.BoardSerialization;
import com.ivygames.morskoiboi.model.BoardSerializationTest;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.variant.RussianRules;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
public class BoardSetupUtilsTest {

    @Test
    public void testOnlyHorizontalShips() {
        Collection<Ship> ships = new ArrayList<>();

        boolean onlyHorizontalShips = BoardSetupUtils.onlyHorizontalShips(ships);
        assertThat(onlyHorizontalShips, is(true));

        Ship vertical1 = new Ship(1, Ship.Orientation.VERTICAL);
        Ship horizontal2 = new Ship(2, Ship.Orientation.HORIZONTAL);
        Ship vertical2 = new Ship(2, Ship.Orientation.VERTICAL);

        ships = Arrays.asList(vertical1);
        onlyHorizontalShips = BoardSetupUtils.onlyHorizontalShips(ships);
        assertThat(onlyHorizontalShips, is(true));

        ships = Arrays.asList(horizontal2);
        onlyHorizontalShips = BoardSetupUtils.onlyHorizontalShips(ships);
        assertThat(onlyHorizontalShips, is(true));

        ships = Arrays.asList(vertical2);
        onlyHorizontalShips = BoardSetupUtils.onlyHorizontalShips(ships);
        assertThat(onlyHorizontalShips, is(false));

        ships = Arrays.asList(vertical2, horizontal2);
        onlyHorizontalShips = BoardSetupUtils.onlyHorizontalShips(ships);
        assertThat(onlyHorizontalShips, is(false));

        ships = Arrays.asList(vertical2, horizontal2, vertical1);
        onlyHorizontalShips = BoardSetupUtils.onlyHorizontalShips(ships);
        assertThat(onlyHorizontalShips, is(false));
    }

    @Test
    public void NeighbouringCells8() {
        Collection<Vector2> coordinates = BoardSetupUtils.getNeighboringCoordinates(1, 1);

        assertThat(coordinates.size(), is(8));
        assertThat(coordinates, containsInAnyOrder(
                Vector2.get(0, 0),
                Vector2.get(1, 0),
                Vector2.get(2, 0),

                Vector2.get(0, 1),
                Vector2.get(2, 1),

                Vector2.get(0, 2),
                Vector2.get(1, 2),
                Vector2.get(2, 2)));
    }

    @Test
    public void NeighbouringCellsCorner() {
        Collection<Vector2> coordinates = BoardSetupUtils.getNeighboringCoordinates(0, 0);

        assertThat(coordinates.size(), is(3));
        assertThat(coordinates, containsInAnyOrder(
                Vector2.get(0, 1),
                Vector2.get(1, 1),
                Vector2.get(1, 0)));
    }

    @Test
    public void NeighbouringCellsWall() {
        Collection<Vector2> coordinates = BoardSetupUtils.getNeighboringCoordinates(0, 1);

        assertThat(coordinates.size(), is(5));
        assertThat(coordinates, containsInAnyOrder(
                Vector2.get(0, 0),
                Vector2.get(0, 2),
                Vector2.get(1, 0),
                Vector2.get(1, 1),
                Vector2.get(1, 2)));
    }

    @Test
    public void testEmptyCells() {
        Random random = mock(Random.class);
        Dependencies.inject(new Placement(random, new RussianRules()));
        Board board = BoardSerialization.fromJson(BoardSerializationTest.EMPTY_BOARD);
        assertEquals(100, BoardSetupUtils.getCellsFreeFromShips(board, false).size());

        board = BoardSerialization.fromJson(BoardSerializationTest.BOARD_WITH_SHIP_x1_5_5);
        assertEquals(91, BoardSetupUtils.getCellsFreeFromShips(board, false).size());

        board = BoardSerialization.fromJson(BoardSerializationTest.BOARD_WITH_SHIP_x1_5_5_x2_5_5);
        assertEquals(85, BoardSetupUtils.getCellsFreeFromShips(board, false).size());
    }
}