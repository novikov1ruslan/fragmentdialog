package com.ivygames.morskoiboi.screen.boardsetup;

import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;

import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.RulesFactory;
import com.ivygames.morskoiboi.ai.PlacementFactory;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.screen.view.Aiming;
import com.ivygames.morskoiboi.variant.RussianPlacement;
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
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class SetupBoardPresenterTest {
    private static final int H_OFFSET = 0;
    private static final int V_OFFSET = 20;
    private static final int H_PADDING = 6;
    private static final int V_PADDING = 8;

    private SetupBoardPresenter mPresenter;

    private static final Point IN_DOCK_AREA = new Point(100, 100);
    private static final Point IN_BOARD_AREA = new Point(200, 200);

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mPresenter = new SetupBoardPresenter(10, 2);
        mPresenter.measure(320, 480, H_PADDING, V_PADDING);
        mPresenter.setBoardVerticalOffset(V_OFFSET);
        mPresenter.setBoardHorizontalOffset(H_OFFSET);
        Rules rules = new RussianRules();
        RulesFactory.setRules(rules);
        PlacementFactory.setPlacementAlgorithm(new RussianPlacement(new Random(), rules.getTotalShips()));
    }

    @Test
    public void dropping_ship_without_picking__has_no_effect() {
        Board board = PlacementFactory.getAlgorithm().generateBoard();
        mPresenter.dropShip(board);
    }

    @Test
    public void trying_to_drop_ship_that_does_not_fit__returns_it_to_the_dock() {
        Board board = new Board();
        PriorityQueue<Ship> fleet = setFleet(new Ship(2));
        mPresenter.touch(IN_DOCK_AREA);
        mPresenter.pickDockedShip();
        mPresenter.dropShip(board);
        assertThat(fleet.size(), is(1));
        assertThat(board.getShips().size(), is(0));
    }

    @Test
    public void dropping_ship_on_board__moves_it_from_dock_to_board() {
        Board board = new Board();
        PriorityQueue<Ship> fleet = setFleet(new Ship(2));
        mPresenter.touch(IN_BOARD_AREA);
        mPresenter.pickDockedShip();
        mPresenter.dropShip(board);
        assertThat(fleet.size(), is(0));
        assertThat(board.getShips().size(), is(1));
    }

    @Test
    public void after_touch_and_docked_ship_pickup__there_is_valid_picked_ship_rect() {
        setFleet(new Ship(2));
        mPresenter.touch(IN_DOCK_AREA);
        mPresenter.pickDockedShip();
        assertThat(mPresenter.getPickedShipRect(), equalTo(new Rect(69, 85, 131, 116)));
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
    public void testGetAiming() {
        Aiming aiming = mPresenter.getAiming();
        assertThat(aiming, equalTo(new Aiming(new Rect(), new Rect())));
    }

    private PriorityQueue<Ship> setFleet(Ship ship) {
        PriorityQueue<Ship> fleet = new PriorityQueue<>(10, new ShipComparator());
        fleet.add(ship);
        mPresenter.setFleet(fleet);
        return fleet;
    }
}