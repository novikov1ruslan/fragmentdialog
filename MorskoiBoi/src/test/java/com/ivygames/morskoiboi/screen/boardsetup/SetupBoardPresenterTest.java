package com.ivygames.morskoiboi.screen.boardsetup;

import android.graphics.Point;

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
        mPresenter.measure(320, 480, H_OFFSET, V_OFFSET, H_PADDING, V_PADDING);
    }

    @Test
    public void testMeasure() throws Exception {

    }

    @Test
    public void testGetTopLeftPointInTopArea() throws Exception {
        Point point = mPresenter.getTopLeftPointInTopArea(3);
        Assert.assertThat(point, equalTo(new Point()));
    }

    @Test
    public void testGetShipDisplayAreaCenter() throws Exception {

    }

    @Test
    public void testGetPickedShipRect() throws Exception {

    }

    @Test
    public void testGetAimingForPickedShip() throws Exception {

    }

    @Test
    public void testPickNewShip() throws Exception {

    }

    @Test
    public void testSetTouch() throws Exception {

    }

    @Test
    public void testGetTouchJ() throws Exception {

    }

    @Test
    public void testGetTouchI() throws Exception {

    }

    @Test
    public void testIsInShipSelectionArea() throws Exception {

    }

    @Test
    public void testGetInvalidRect() throws Exception {

    }
}