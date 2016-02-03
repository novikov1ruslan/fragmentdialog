package com.ivygames.morskoiboi;

import com.ivygames.morskoiboi.ai.PlacementAlgorithm;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.variant.RussianPlacement;
import com.ivygames.morskoiboi.variant.RussianRules;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class RussianPlacementTest {

	private Board mBoard;
    private PlacementAlgorithm mAlgorithm;
    private int mNumberOfDistinctShips;

    @BeforeClass
    public static void runBeforeClass() {

    }

    @Before
	public void setup() {
//        Random random = mock(Random.class);
//        when(random.nextInt(anyInt())).thenReturn(0).thenReturn(1);
        Random random = new Random(1);
        mAlgorithm = new RussianPlacement(random);
        RulesFactory.setRules(new RussianRules());
		mBoard = new Board();
	}

	@Test
	public void after_placing_a_ship_on_a_board_That_board_has_one_ship() {
		Ship ship = new Ship(2);
		assertThat(mAlgorithm.place(ship, mBoard), is(true));
		assertThat(mBoard.getShips().size(), is(1));
	}

    @Test
    public void no_ships_can_be_added_after_10_4squared_ships_already_placed_on_a_board() {
        for (int i = 0; i < 10; i++) {
            assert4SquaredCanBePut();
        }

        Ship ship = new Ship(1);
        assertThat(mAlgorithm.place(ship, mBoard), is(false));
    }

    private void assert4SquaredCanBePut() {
        Ship ship = new Ship(4, Ship.Orientation.VERTICAL);
        assertThat(mAlgorithm.place(ship, mBoard), is(true));
    }

    @Test
    public void russian_fleet_is_generated_4_3_3_2_2_2_1_1_1_1() {
        Collection<Ship> fleet = mAlgorithm.generateFullFleet();
        assertAllTheShipsAreRussianFleet(fleet);
    }

    @Test
    public void after_generating_full_board_it_has_russian_fleet() {
        Board board = mAlgorithm.generateBoard();
        assertAllTheShipsAreRussianFleet(board.getShips());
    }

    @Test
	public void some_russian_fleet_can_be_placed_on_a_board() {
		Ship ship = new Ship(4, Ship.Orientation.HORIZONTAL);
		mAlgorithm.place(ship, mBoard);
        assertThat(mBoard.getShips().size(), is(1));

		ship = new Ship(3, Ship.Orientation.VERTICAL);
		mAlgorithm.place(ship, mBoard);
        assertThat(mBoard.getShips().size(), is(2));

		ship = new Ship(3, Ship.Orientation.HORIZONTAL);
		mAlgorithm.place(ship, mBoard);
        assertThat(mBoard.getShips().size(), is(3));

		ship = new Ship(2, Ship.Orientation.VERTICAL);
		mAlgorithm.place(ship, mBoard);
        assertThat(mBoard.getShips().size(), is(4));

		ship = new Ship(2, Ship.Orientation.HORIZONTAL);
		mAlgorithm.place(ship, mBoard);
        assertThat(mBoard.getShips().size(), is(5));

		ship = new Ship(2, Ship.Orientation.VERTICAL);
		mAlgorithm.place(ship, mBoard);
        assertThat(mBoard.getShips().size(), is(6));

		ship = new Ship(1, Ship.Orientation.HORIZONTAL);
		mAlgorithm.place(ship, mBoard);
        assertThat(mBoard.getShips().size(), is(7));

		ship = new Ship(1, Ship.Orientation.VERTICAL);
		mAlgorithm.place(ship, mBoard);
        assertThat(mBoard.getShips().size(), is(8));

		ship = new Ship(1, Ship.Orientation.HORIZONTAL);
		mAlgorithm.place(ship, mBoard);
        assertThat(mBoard.getShips().size(), is(9));

		ship = new Ship(1, Ship.Orientation.VERTICAL);
		mAlgorithm.place(ship, mBoard);
        assertThat(mBoard.getShips().size(), is(10));
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
