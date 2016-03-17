package com.ivygames.morskoiboi.screen.boardsetup;

import android.graphics.Point;
import android.graphics.Rect;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.CoreMatchers.*;

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
    }

    @Test
    public void testGetTopLeftPointInTopArea() {
//        Point point = mPresenter.getTopLeftPointInTopArea(3);
//        Assert.assertThat(point, equalTo(new Point()));
    }

    @Test
    public void testGetShipDisplayAreaCenter() {
        Point center = mPresenter.getShipDisplayAreaCenter();
        Assert.assertThat(center, equalTo(new Point(240, 60)));
    }

    @Test
    public void testGetPickedShipRect() {
//        mPresenter.pickNewShip()
        Rect shipRect = mPresenter.getPickedShipRect();
        Assert.assertThat(shipRect, equalTo(new Rect()));
    }


}