package com.ivygames.morskoiboi.screen.boardsetup;

import android.support.annotation.NonNull;

import com.ivygames.battleship.BoardUtils;
import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.board.Vector;
import com.ivygames.battleship.ship.Ship;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import java.util.PriorityQueue;
import java.util.Random;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class SetupBoardPresenterTest {
    private SetupBoardPresenter mPresenter;

    @Mock
    private Random mRandom;

    @Before
    public void setup() {
        initMocks(this);
        mPresenter = new SetupBoardPresenter();
    }

    @Test
    public void dropping_ship_without_picking__has_no_effect() {
        Board board = new Board();

        mPresenter.dropShip(board, 5, 5);

        assertThat(board.getShipAt(5, 5), is(nullValue()));
    }

    @Test
    public void trying_to_drop_ship_that_does_not_fit__returns_it_to_the_dock() {
        PriorityQueue<Ship> fleet = pickDockedShip();

        Board board = new Board();
        mPresenter.dropShip(board, 9, 9);
        assertThat(fleet.size(), is(1));
        assertThat(board.getShips().size(), is(0));
    }

    @Test
    public void trying_to_drop_vertical_ship_that_does_not_fit__returns_it_to_the_dock_horizontally() {
        Ship ship = new Ship(2, Ship.Orientation.VERTICAL);
        PriorityQueue<Ship> fleet = pickDockedShip(ship);
        Board board = new Board();
        mPresenter.dropShip(board, 9, 9);

        assertThat(fleet.size(), is(1));
        assertThat(board.getShips().size(), is(0));
        assertThat(ship.isHorizontal(), is(true));
    }

    @Test
    public void dropping_ship_on_board__moves_it_from_dock_to_board() {
        PriorityQueue<Ship> fleet = pickDockedShip();
        Board board = new Board();
        mPresenter.dropShip(board, 5, 5);
        assertThat(fleet.size(), is(0));
        assertThat(board.getShips().size(), is(1));
    }

    @Test
    public void when_there_is_at_least_1_ship__dock_has_ships() {
        setFleet(new Ship(2));
        mPresenter.pickDockedShip();
        assertThat(mPresenter.hasPickedShip(), is(true));
    }

    @Test
    public void when_there_are_no_ships__dock_has_no_ships() {
        PriorityQueue<Ship> fleet = new PriorityQueue<>(10, new ShipComparator());
        mPresenter.setFleet(fleet);
        mPresenter.pickDockedShip();
        assertThat(mPresenter.hasPickedShip(), is(false));
    }

    @Test
    public void when_no_ship_is_docked__there_is_no_docked_ship() {
        assertThat(mPresenter.getDockedShip(), nullValue());
    }

    @Test
    public void when_a_ship_is_docked__there_is_docked_ship() {
        setFleet(new Ship(2));
        assertThat(mPresenter.getDockedShip(), notNullValue());
    }

    @Test
    public void pickShipFromBoard() {
        pickDockedShip();
        Board board = new Board();
        mPresenter.dropShip(board, 5, 5);
        assertThat(mPresenter.hasPickedShip(), is(false));

        mPresenter.pickShipFromBoard(board, 5, 5);
        assertThat(mPresenter.hasPickedShip(), is(true));
    }

    @Test
    public void rotateShipAt() {
        Ship ship = new Ship(2, Ship.Orientation.HORIZONTAL);
        pickDockedShip(ship);
        Board board = new Board();
        Vector coordinate = Vector.get(5, 5);
        mPresenter.dropShip(board, coordinate);
        BoardUtils.rotateShipAt(board, coordinate.x, coordinate.y);
        assertThat(ship.isHorizontal(), is(false));
    }

    @Test
    public void WhenDataChangedExternally__DockedShipDoesNotChange() {
        Ship ship = new Ship(3);
        PriorityQueue<Ship> ships = setFleet(ship);
        Ship dockedShip1 = mPresenter.getDockedShip();
        ships.clear();
        Ship dockedShip2 = mPresenter.getDockedShip();
        assertThat(dockedShip1, equalTo(dockedShip2));
    }

    public void WhenDataChangedExternally_AndPresenterNotified__DockedShipReflectsChange() {
        Ship ship = new Ship(3);
        PriorityQueue<Ship> ships = setFleet(ship);
        Ship dockedShip1 = mPresenter.getDockedShip();
        ships.clear();
        mPresenter.setDockedShip();
        Ship dockedShip2 = mPresenter.getDockedShip();
        assertThat(dockedShip1, not(dockedShip2));
    }

    @NonNull
    private PriorityQueue<Ship> setFleet(Ship ship) {
        PriorityQueue<Ship> fleet = new PriorityQueue<>(10, new ShipComparator());
        fleet.add(ship);
        mPresenter.setFleet(fleet);
        return fleet;
    }

    private PriorityQueue<Ship> pickDockedShip() {
        return pickDockedShip(new Ship(2));
    }

    private PriorityQueue<Ship> pickDockedShip(Ship ship) {
        PriorityQueue<Ship> fleet = setFleet(ship);
        mPresenter.pickDockedShip();
        return fleet;
    }
}