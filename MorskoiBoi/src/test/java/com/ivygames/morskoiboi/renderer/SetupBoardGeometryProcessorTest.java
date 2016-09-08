package com.ivygames.morskoiboi.renderer;

import android.graphics.Point;
import android.graphics.Rect;

import com.ivygames.morskoiboi.model.Ship;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class SetupBoardGeometryProcessorTest {
    private static final int V_OFFSET = 20;
    private static final int H_PADDING = 6;
    private static final int V_PADDING = 8;
    private static final Rect VALID_PICKED_SHIP_RECT = new Rect(69, 85, 131, 116);

    private SetupBoardGeometryProcessor mPresenter;

    private static final Point IN_DOCK_AREA = new Point(100, 100);
    private static final Point IN_BOARD_AREA = new Point(200, 200);

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mPresenter = new SetupBoardGeometryProcessor(10, 2);
        mPresenter.measure(320, 480, H_PADDING, V_PADDING);
        mPresenter.setBoardVerticalOffset(V_OFFSET);
    }

    @Test
    public void after_docked_ship_pickup_and_touch__there_is_valid_picked_ship_rect() {
        Ship ship = new Ship(2);
        assertThat(mPresenter.getPickedShipRect(ship, IN_DOCK_AREA), equalTo(VALID_PICKED_SHIP_RECT));
    }

    @Test
    public void after_touch_and_docked_ship_pickup__there_is_valid_picked_ship_rect() {
        Ship pickedShip = new Ship(2);
        mPresenter.getPickedShipCoordinate(pickedShip, IN_DOCK_AREA);

        assertThat(mPresenter.getPickedShipRect(pickedShip, IN_DOCK_AREA), equalTo(VALID_PICKED_SHIP_RECT));
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
        Ship ship = new Ship(2);
        Rect rect = mPresenter.getRectForDockedShip(ship);
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

}