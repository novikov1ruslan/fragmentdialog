package com.ivygames.morskoiboi;

import com.ivygames.battleship.BoardUtils;
import com.ivygames.battleship.Rules;
import com.ivygames.battleship.RussianRules;
import com.ivygames.battleship.ShipUtils;
import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.board.BoardTestUtils;
import com.ivygames.battleship.ship.LocatedShip;
import com.ivygames.battleship.ship.Ship;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collection;
import java.util.Random;

import static junit.framework.Assert.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.MockitoAnnotations.initMocks;

public class PlacementTest {

    private Placement mPlacement;
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
    public void AfterPopulatingBoardWithShips__AllShipsAreOnBoard() {
        Collection<Ship> ships = ShipUtils.createNewShips(new int[]{1, 2, 3, 4, 5},
                new OrientationBuilder(mRandom));

        Board board = new Board();
        mPlacement.populateBoardWithShips(board, ships);

        BoardTestUtils.similarShips(board.getShips(), ships);
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
        assertThat(BoardUtils.hasConflictingCell(board, rules.allowAdjacentShips()), is(false));
    }

    @Test
    public void canRotateHorizontalShip() {
        Ship ship = new Ship(2, Ship.Orientation.HORIZONTAL);
        putShipAt(ship, 5, 5);

        BoardUtils.rotateShipAt(mBoard, 5, 5);

        assertThat(ship.isHorizontal(), is(false));
    }

    @Test
    public void canRotateVerticalShip() {
        Ship ship = new Ship(2, Ship.Orientation.VERTICAL);
        putShipAt(ship, 5, 5);

        BoardUtils.rotateShipAt(mBoard, 5, 5);

        assertThat(ship.isHorizontal(), is(true));
    }

    @Test
    public void testCannotRotateShip() {
        Ship ship = new Ship(4, Ship.Orientation.HORIZONTAL);
        putShipAt(ship, 5, 7);

        BoardUtils.rotateShipAt(mBoard, 5, 7);

        assertFalse(ship.isHorizontal());
        assertThat(mBoard.getShipsAt(5, 6).contains(ship), is(true));
    }

    private void putShipAt(Ship ship, int x, int y) {
        mBoard.addShip(new LocatedShip(ship, x, y));
    }

}
