package com.ivygames.morskoiboi.renderer;

import android.graphics.Rect;

import com.ivygames.battleship.board.Coordinate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class EnemyBoardGeometryProcessorTest {

    private static final float CELL_RATIO = 2f;
    private static final int V_OFFSET = 20;
    private static final int H_PADDING = 6;
    private static final int V_PADDING = 8;

    private EnemyBoardGeometryProcessor mProcessor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mProcessor = new EnemyBoardGeometryProcessor(10, 2);
        mProcessor.measure(320, 480, H_PADDING, V_PADDING);
        mProcessor.setBoardVerticalOffset(V_OFFSET);
    }

    @Test
    public void testGetAnimationDestination() {
        Rect animationDestination = mProcessor.getAnimationDestination(Coordinate.get(5, 6), CELL_RATIO);
        Rect expected = new Rect(145, 276, 205, 336);
        assertThat(animationDestination, equalTo(expected));
    }

    @Test
    public void testGetAimRectDst() {
        Rect aimRectDst = mProcessor.getAimRectDst(Coordinate.get(5, 5));
        Rect expected = new Rect(160, 260, 191, 291);
        assertThat(aimRectDst, equalTo(expected));
    }

    @Test
    public void testGetTouchedJ() {
        int j = mProcessor.yToJ(200);

        assertThat(j, is(3));
    }

    @Test
    public void testGetTouchedI() {
        int i = mProcessor.xToI(100);

        assertThat(i, is(3));
    }

    @Test
    public void testGetBoardRect() {
        assertThat(mProcessor.getBoardRect(), equalTo(new Rect(5, 105, 315, 415)));
    }

}