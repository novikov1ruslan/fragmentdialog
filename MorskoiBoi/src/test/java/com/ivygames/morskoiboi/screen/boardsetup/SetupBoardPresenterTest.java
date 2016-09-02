package com.ivygames.morskoiboi.screen.boardsetup;

import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.Dependencies;
import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.ai.PlacementFactory;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.screen.view.AimingG;
import com.ivygames.morskoiboi.variant.RussianRules;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.util.PriorityQueue;
import java.util.Random;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class SetupBoardPresenterTest {
    private static final int V_OFFSET = 20;
    private static final int H_PADDING = 6;
    private static final int V_PADDING = 8;
    private static final Rect VALID_PICKED_SHIP_RECT = new Rect(69, 85, 131, 116);

    private SetupBoardPresenter mPresenter;

    private static final Point IN_DOCK_AREA = new Point(100, 100);
    private static final Point IN_BOARD_AREA = new Point(200, 200);
    private Placement mPlacement;
    private RussianRules rules;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mPresenter = new SetupBoardPresenter(10, 2);
        mPresenter.measure(320, 480, H_PADDING, V_PADDING);
        mPresenter.setBoardVerticalOffset(V_OFFSET);
        rules = new RussianRules();
        Dependencies.inject(rules);
        mPlacement = new Placement(new Random(), rules);
        PlacementFactory.setPlacementAlgorithm(mPlacement);
    }

    @Test
    public void dropping_ship_without_picking__has_no_effect() {
        Board board = new Board();
        mPlacement.populateBoardWithShips(board, rules.generateFullFleet());
        mPresenter.dropShip(board);
    }

    @Test
    public void trying_to_drop_ship_that_does_not_fit__returns_it_to_the_dock() {
        PriorityQueue<Ship> fleet = pickDockedShip();
        mPresenter.touch(IN_DOCK_AREA);

        Board board = new Board();
        mPresenter.dropShip(board);
        assertThat(fleet.size(), is(1));
        assertThat(board.getShips().size(), is(0));
    }

    @Test
    public void trying_to_drop_vertical_ship_that_does_not_fit__returns_it_to_the_dock_horizontally() {
        Ship ship = new Ship(2, Ship.Orientation.VERTICAL);
        PriorityQueue<Ship> fleet = pickDockedShip(ship);
        mPresenter.touch(IN_DOCK_AREA);
        Board board = new Board();
        mPresenter.dropShip(board);
        assertThat(fleet.size(), is(1));
        assertThat(board.getShips().size(), is(0));
        assertThat(ship.isHorizontal(), is(true));
    }

    @Test
    public void dropping_ship_on_board__moves_it_from_dock_to_board() {
        PriorityQueue<Ship> fleet = pickDockedShip();
        mPresenter.touch(IN_BOARD_AREA);
        Board board = new Board();
        mPresenter.dropShip(board);
        assertThat(fleet.size(), is(0));
        assertThat(board.getShips().size(), is(1));
    }

    @Test
    public void after_docked_ship_pickup_and_touch__there_is_valid_picked_ship_rect() {
        pickDockedShip();
        mPresenter.touch(IN_DOCK_AREA);
        assertThat(mPresenter.getPickedShipRect(), equalTo(VALID_PICKED_SHIP_RECT));
    }

    @Test
    public void after_touch_and_docked_ship_pickup__there_is_valid_picked_ship_rect() {
        mPresenter.touch(IN_DOCK_AREA);
        pickDockedShip();
        assertThat(mPresenter.getPickedShipRect(), equalTo(VALID_PICKED_SHIP_RECT));
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
    public void coordinate_is_in_dock_area() {
        assertThat(mPresenter.isInDockArea(IN_DOCK_AREA), is(true));
    }

    @Test
    public void coordinate_is_not_in_dock_area() {
        assertThat(mPresenter.isInDockArea(IN_BOARD_AREA), is(false));
    }

    @Test
    public void testGetRectForDockedShip() {
        setFleet(new Ship(2));
        Rect rect = mPresenter.getRectForDockedShip();
        assertThat(rect, equalTo(new Rect(49, 45, 111, 76)));
    }

    @Test
    public void testGetShipDisplayAreaCenter() {
        Point center = mPresenter.getShipDisplayAreaCenter();
        assertThat(center, equalTo(new Point(240, 60)));
    }

    @Test
    public void testGetRectForCell() {
        Rect rectForCell = mPresenter.getRectForCell(5, 5);
        assertThat(rectForCell, equalTo(new Rect(162, 322, 192, 352)));
    }

    @Test
    public void when_ship_not_picked_up__there_is_no_aiming() {
        assertThat(mPresenter.getAiming(), nullValue());
    }

    @Test
    public void when_aim_in_not_on_board__there_is_no_aiming() {
        pickDockedShip();
        mPresenter.touch(IN_DOCK_AREA);
        assertThat(mPresenter.getAiming(), nullValue());
    }

    @Test
    public void when_ship_picked_up_and_aim_is_on_board__there_is_aiming() {
        pickDockedShip();
        mPresenter.touch(IN_BOARD_AREA);
        AimingG aiming = mPresenter.getAiming();
        assertThat(aiming, equalTo(new AimingG(new Rect(160, 165, 222, 475), new Rect(5, 196, 315, 227))));
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
        mPresenter.touch(IN_BOARD_AREA);
        Board board = new Board();
        mPresenter.dropShip(board);
        assertThat(mPresenter.hasPickedShip(), is(false));
        mPresenter.pickShipFromBoard(board, IN_BOARD_AREA);
        assertThat(mPresenter.hasPickedShip(), is(true));
    }

    @Test
    public void rotateShipAt() {
        Ship ship = new Ship(2, Ship.Orientation.HORIZONTAL);
        pickDockedShip(ship);
        mPresenter.touch(IN_BOARD_AREA);
        Board board = new Board();
        mPresenter.dropShip(board);
        mPresenter.rotateShipAt(board, IN_BOARD_AREA);
        assertThat(ship.isHorizontal(), is(false));
    }

    @Test
    public void isOnBoard() {
        assertThat(mPresenter.isOnBoard(IN_DOCK_AREA), is(false));
        assertThat(mPresenter.isOnBoard(IN_BOARD_AREA), is(true));
    }

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
        mPresenter.notifyDataChanged();
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