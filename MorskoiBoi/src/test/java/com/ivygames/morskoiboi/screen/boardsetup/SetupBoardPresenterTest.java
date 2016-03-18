package com.ivygames.morskoiboi.screen.boardsetup;

import android.graphics.Point;
import android.graphics.Rect;

import com.ivygames.morskoiboi.RulesFactory;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.variant.RussianRules;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.util.PriorityQueue;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class SetupBoardPresenterTest {
    private static final int H_OFFSET = 10;
    private static final int V_OFFSET = 20;
    private static final int H_PADDING = 6;
    private static final int V_PADDING = 8;

    private SetupBoardPresenter mPresenter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mPresenter = new SetupBoardPresenter(10, 2);
        mPresenter.measure(320, 480, H_PADDING, V_PADDING);
        mPresenter.setBoardVerticalOffset(V_OFFSET);
        mPresenter.setBoardHorizontalOffset(H_OFFSET);
        RulesFactory.setRules(new RussianRules());
    }

    @Test
    public void testGetTopLeftPointInTopArea() {
//        Point point = mPresenter.getTopLeftPointInTopArea(3);
//        Assert.assertThat(point, equalTo(new Point()));
    }

    @Test
    public void testGetShipDisplayAreaCenter() {
        Point center = mPresenter.getShipDisplayAreaCenter();
        assertThat(center, equalTo(new Point(240, 60)));
    }

    @Test
    public void testGetPickedShipRect() {
        mPresenter.touch(100, 100);
        pickShip(new Ship(2));
        Rect shipRect = mPresenter.getPickedShipRect();
        assertThat(shipRect, equalTo(new Rect()));
    }

    @Test
    public void when_there_is_at_least_1_ship__dock_has_ships() {
        pickShip(new Ship(2));
        assertThat(mPresenter.hasPickedShip(), is(true));
    }

    private void pickShip(Ship ship) {
        PriorityQueue<Ship> fleet = new PriorityQueue<>(10, new ShipComparator());
        fleet.add(ship);
        mPresenter.setFleet(fleet);
        mPresenter.pickDockedShip();
    }

    @Test
    public void when_there_are_no_ships__dock_has_no_ships() {
        PriorityQueue<Ship> fleet = new PriorityQueue<>(10, new ShipComparator());
        mPresenter.setFleet(fleet);
        mPresenter.pickDockedShip();
        assertThat(mPresenter.hasPickedShip(), is(false));
    }
}