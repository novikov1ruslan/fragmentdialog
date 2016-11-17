package com.ivygames.morskoiboi;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.screen.boardsetup.BoardSetupUtils;
import com.ivygames.morskoiboi.variant.RussianRules;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collection;
import java.util.Random;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.MockitoAnnotations.initMocks;

public class PlacementTest {

    private Placement mPlacement;
    private int mNumberOfDistinctShips;
    private Rules rules = new RussianRules();
    private Board mBoard = new Board();

    @Mock
    private Random mRandom;

    @BeforeClass
    public static void runBeforeClass() {

    }

    @Before
    public void setup() {
        initMocks(this);
        mPlacement = new Placement(mRandom, rules.allowAdjacentShips());
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
    public void whenNotPossibleToPutShipOnBoard__BoardDoesNotHaveTheShip() {
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
    public void WhenShipIsPlaced__BoardHasNoConflictingCells() {
        Board board = new Board();
        Random random = new Random() {

            boolean even;

            @Override
            public int nextInt(int i) {
                if (even) {
                    even = false;
                    return 0;
                } else {
                    even = true;
                    return i / 2;
                }
            }
        };
        Placement placement = new Placement(random, rules.allowAdjacentShips());
        placement.putShipOnBoard(new Ship(2, Ship.Orientation.HORIZONTAL), board);
        placement.putShipOnBoard(new Ship(2, Ship.Orientation.HORIZONTAL), board);
        placement.putShipOnBoard(new Ship(2, Ship.Orientation.HORIZONTAL), board);
        placement.putShipOnBoard(new Ship(2, Ship.Orientation.HORIZONTAL), board);
        placement.putShipOnBoard(new Ship(2, Ship.Orientation.HORIZONTAL), board);
        placement.putShipOnBoard(new Ship(2, Ship.Orientation.HORIZONTAL), board);
        placement.putShipOnBoard(new Ship(2, Ship.Orientation.HORIZONTAL), board);
        placement.putShipOnBoard(new Ship(2, Ship.Orientation.HORIZONTAL), board);
        placement.putShipOnBoard(new Ship(2, Ship.Orientation.HORIZONTAL), board);
        placement.putShipOnBoard(new Ship(2, Ship.Orientation.HORIZONTAL), board);
        placement.putShipOnBoard(new Ship(2, Ship.Orientation.HORIZONTAL), board);
        placement.putShipOnBoard(new Ship(2, Ship.Orientation.HORIZONTAL), board);
        placement.putShipOnBoard(new Ship(2, Ship.Orientation.HORIZONTAL), board);

        boolean success = placement.putShipOnBoard(new Ship(2, Ship.Orientation.HORIZONTAL), board);
        assertThat(success, is(true));
        assertThat(BoardSetupUtils.hasConflictingCell(board, rules.allowAdjacentShips()), is(false));
    }

    @Test
    public void canRotateHorizontalShip() {
        Ship ship = new Ship(2, Ship.Orientation.HORIZONTAL);
        putShipAt(ship, 5, 5);

        Placement.rotateShipAt(mBoard, 5, 5);

        assertThat(ship.isHorizontal(), is(false));
    }

    @Test
    public void canRotateVerticalShip() {
        Ship ship = new Ship(2, Ship.Orientation.VERTICAL);
        putShipAt(ship, 5, 5);

        Placement.rotateShipAt(mBoard, 5, 5);

        assertThat(ship.isHorizontal(), is(true));
    }

    @Test
    public void testCannotRotateShip() {
        Ship ship = new Ship(4, Ship.Orientation.HORIZONTAL);
        putShipAt(ship, 5, 7);

        Placement.rotateShipAt(mBoard, 5, 7);

        assertFalse(ship.isHorizontal());
        assertEquals(5, ship.getX());
        assertEquals(6, ship.getY());
    }

    @Test
    public void testIsInProximity() {
        Ship ship = new Ship(1);
        ship.setX(5);
        ship.setY(5);
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (i >= 4 && i <= 6 && j >= 4 && j <= 6) {
                    Assert.assertTrue(i + "," + j, ShipTestUtils.isInProximity(ship, i, j));
                } else {
                    Assert.assertFalse(i + "," + j, ShipTestUtils.isInProximity(ship, i, j));
                }
            }
        }
    }

    private void putShipAt(Ship ship, int x, int y) {
        Placement.putShipAt(mBoard, ship, x, y);
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
