package com.ivygames.morskoiboi.screen.gameplay;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.MotionEvent;

import com.ivygames.morskoiboi.model.Vector2;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class EnemyBoardPresenterTest {

    private static final float CELL_RATIO = 2f;
    private static final int H_OFFSET = 10;
    private static final int V_OFFSET = 20;
    private static final int H_PADDING = 6;
    private static final int V_PADDING = 8;

    private EnemyBoardPresenter mPresenter;
    @Mock
    private ShotListener shotListener;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mPresenter = new EnemyBoardPresenter(10, 2);
        mPresenter.setShotListener(shotListener);
        mPresenter.measure(320, 480, H_OFFSET, V_OFFSET, H_PADDING, V_PADDING);
    }

    @Test
    public void testSetShotListener() throws Exception {

    }

    @Test
    public void testGetAnimationDestination() throws Exception {
        Rect animationDestination = mPresenter.getAnimationDestination(Vector2.get(5, 6), CELL_RATIO);
        Rect expected = new Rect(155, 276, 215, 336);
        assertThat(animationDestination, equalTo(expected));
    }

    @Test
    public void testGetAimRectDst() throws Exception {
        Rect aimRectDst = mPresenter.getAimRectDst(Vector2.get(5, 5));
        Rect expected = new Rect(170, 260, 201, 291);
        assertThat(aimRectDst, equalTo(expected));
    }

    @Test
    public void testGetTouchedJ() throws Exception {
        mPresenter.touch(getMotionEvent(100f, 200f, MotionEvent.ACTION_DOWN));
        int j = mPresenter.getTouchedJ();
        assertThat(j, is(3));
    }

    @Test
    public void testGetTouchedI() throws Exception {
        mPresenter.touch(getMotionEvent(100f, 200f, MotionEvent.ACTION_DOWN));
        int i = mPresenter.getTouchedI();
        assertThat(i, is(2));
    }

    @Test
    public void testGetBoardRect() throws Exception {
        assertThat(mPresenter.getBoardRect(), equalTo(new Rect(15, 105, 325, 415)));
    }

    @Test
    public void testOnTouch() throws Exception {
        mPresenter.touch(getMotionEvent(100f, 200f, MotionEvent.ACTION_DOWN));
        verify(shotListener, times(1)).onAimingStarted();
    }

    @Test
    public void testOnTouch2() throws Exception {
        mPresenter.touch(getMotionEvent(100f, 200f, MotionEvent.ACTION_UP));
        verify(shotListener, times(1)).onAimingFinished(2, 3);
    }

    @Test
    public void testOnTouch3() throws Exception {
        mPresenter.touch(getMotionEvent(100f, 200f, MotionEvent.ACTION_MOVE));
        verify(shotListener, never()).onAimingStarted();
        verify(shotListener, never()).onAimingFinished(anyInt(), anyInt());
    }

    @Test
    public void testStartedDragging() throws Exception {
        mPresenter.touch(getMotionEvent(100f, 200f, MotionEvent.ACTION_UP));
        assertThat(mPresenter.startedDragging(), is(false));
        mPresenter.touch(getMotionEvent(100f, 200f, MotionEvent.ACTION_DOWN));
        assertThat(mPresenter.startedDragging(), is(true));
        mPresenter.touch(getMotionEvent(100f, 200f, MotionEvent.ACTION_UP));
        assertThat(mPresenter.startedDragging(), is(false));
        mPresenter.touch(getMotionEvent(100f, 200f, MotionEvent.ACTION_MOVE));
        assertThat(mPresenter.startedDragging(), is(false));
        mPresenter.touch(getMotionEvent(100f, 200f, MotionEvent.ACTION_DOWN));
        assertThat(mPresenter.startedDragging(), is(true));
        mPresenter.touch(getMotionEvent(100f, 200f, MotionEvent.ACTION_MOVE));
        assertThat(mPresenter.startedDragging(), is(true));
    }

    @Test
    public void testUnlock() throws Exception {
        mPresenter.touch(getMotionEvent(100f, 200f, MotionEvent.ACTION_DOWN));
        reset(shotListener);
        mPresenter.unlock();
        verify(shotListener, times(1)).onAimingStarted();
        mPresenter.touch(getMotionEvent(100f, 200f, MotionEvent.ACTION_DOWN));
        reset(shotListener);
        mPresenter.unlock();
        verify(shotListener, times(1)).onAimingStarted();
        mPresenter.touch(getMotionEvent(100f, 200f, MotionEvent.ACTION_UP));
        reset(shotListener);
        mPresenter.unlock();
        verify(shotListener, never()).onAimingStarted();
    }

    @NonNull
    private static MotionEvent getMotionEvent(float x, float y, int action) {
        MotionEvent event = mock(MotionEvent.class);
        when(event.getX()).thenReturn(x);
        when(event.getY()).thenReturn(y);
        when(event.getAction()).thenReturn(action);
        return event;
    }
}