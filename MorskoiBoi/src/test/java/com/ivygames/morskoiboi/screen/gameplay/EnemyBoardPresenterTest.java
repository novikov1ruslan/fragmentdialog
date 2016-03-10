package com.ivygames.morskoiboi.screen.gameplay;

import android.graphics.Rect;

import com.ivygames.morskoiboi.model.Vector2;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.text.IsEqualIgnoringWhiteSpace.equalToIgnoringWhiteSpace;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class EnemyBoardPresenterTest {

    private static final float CELL_RATIO = 2f;
    private static final int H_OFFSET = 10;
    private static final int V_OFFSET = 20;
    private static final int H_PADDING = 6;
    private static final int V_PADDING = 8;

    private EnemyBoardPresenter mPresenter;

    @Before
    public void setup() {
        mPresenter = new EnemyBoardPresenter(10, 2);
        mPresenter.measure(320, 480, H_OFFSET, V_OFFSET, H_PADDING, V_PADDING);
    }

    @Test
    public void testSetShotListener() throws Exception {

    }

    @Test
    public void testGetAnimationDestination() throws Exception {
        Rect animationDestination = mPresenter.getAnimationDestination(Vector2.get(5, 6), CELL_RATIO);
        Rect expected = new Rect(155, 276, 215, 336);
        assertThat(animationDestination.toString(), equalToIgnoringWhiteSpace(expected.toString()));
    }

    @Test
    public void testGetAimRectDst() throws Exception {
        mAim = Vector2.get(5, 5);
        Rect aimRectDst = mPresenter.getAimRectDst();

        Rect expected = new Rect();
        assertThat(aimRectDst, equalTo(expected));
    }

    @Test
    public void testGetTouchedJ() throws Exception {

    }

    @Test
    public void testGetTouchedI() throws Exception {

    }

    @Test
    public void testGetBoardRect() throws Exception {

    }

    @Test
    public void testHasAim() throws Exception {

    }

    @Test
    public void testSetAim() throws Exception {

    }

    @Test
    public void testRemoveAim() throws Exception {

    }

    @Test
    public void testOnTouch() throws Exception {

    }

    @Test
    public void testStartedDragging() throws Exception {

    }

    @Test
    public void testUnlock() throws Exception {

    }
}