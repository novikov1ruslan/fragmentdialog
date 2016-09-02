package com.ivygames.morskoiboi.screen.gameplay;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.MotionEvent;

import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.screen.view.TouchState;
import com.ivygames.morskoiboi.screen.view.TouchStateTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class EnemyBoardPresenterTest {

    private static final float CELL_RATIO = 2f;
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
        mPresenter.measure(320, 480, H_PADDING, V_PADDING);
        mPresenter.setBoardVerticalOffset(V_OFFSET);
    }

    @Test
    public void testGetAnimationDestination() {
        Rect animationDestination = mPresenter.getAnimationDestination(Vector2.get(5, 6), CELL_RATIO);
        Rect expected = new Rect(145, 276, 205, 336);
        assertThat(animationDestination, equalTo(expected));
    }

    @Test
    public void testGetAimRectDst() {
        Rect aimRectDst = mPresenter.getAimRectDst(Vector2.get(5, 5));
        Rect expected = new Rect(160, 260, 191, 291);
        assertThat(aimRectDst, equalTo(expected));
    }

    @Test
    public void testGetTouchedJ() {
        TouchState touchState = getMotionEvent(100f, 200f, MotionEvent.ACTION_DOWN);
        mPresenter.touch(touchState);
        int j = mPresenter.yToJ(touchState.getY());
        assertThat(j, is(3));
    }

    @Test
    public void testGetTouchedI() {
        TouchState touchState = getMotionEvent(100f, 200f, MotionEvent.ACTION_DOWN);
        mPresenter.touch(touchState);
        int i = mPresenter.xToI(touchState.getX());
        assertThat(i, is(3));
    }

    @Test
    public void testGetBoardRect() {
        assertThat(mPresenter.getBoardRect(), equalTo(new Rect(5, 105, 315, 415)));
    }

    @Test
    public void WhenUnlockedBoardTouchedDown__AimingStartedEventFired() {
        mPresenter.unlock();
        mPresenter.touch(getMotionEvent(100f, 200f, MotionEvent.ACTION_DOWN));
        verify(shotListener, times(1)).onAimingStarted();
    }

    @Test
    public void WhenUnlockedBoardTouchedUp__AimingFinishedEventFired() {
        mPresenter.unlock();
        mPresenter.touch(getMotionEvent(100f, 200f, MotionEvent.ACTION_UP));
        verify(shotListener, times(1)).onAimingFinished(3, 3);
    }

    @Test
    public void WhenBoardUnlockedAfterBeingTouchedDown__AimingStartedFired() {
        mPresenter.touch(getMotionEvent(100f, 200f, MotionEvent.ACTION_DOWN));
        mPresenter.unlock();
        verify(shotListener, times(1)).onAimingStarted();
    }

    @Test
    public void WhenBoardUnlockedAfterBeingTouchedMoved__AimingStartedFired() {
        mPresenter.touch(getMotionEvent(100f, 200f, MotionEvent.ACTION_MOVE));
        mPresenter.unlock();
        verify(shotListener, times(1)).onAimingStarted();
    }

    @Test
    public void WhenBoardUnlockedAfterBeingTouchedUp__AimingStartedNotFired() {
        mPresenter.touch(getMotionEvent(100f, 200f, MotionEvent.ACTION_UP));
        mPresenter.unlock();
        verify(shotListener, never()).onAimingStarted();
    }

    @Test
    public void DuringFingerMoveOnUnlockedBoard__NoShotListenerEventsFired() {
        mPresenter.unlock();
        mPresenter.touch(getMotionEvent(100f, 200f, MotionEvent.ACTION_MOVE));
        verify(shotListener, never()).onAimingStarted();
        verify(shotListener, never()).onAimingFinished(anyInt(), anyInt());
    }

    @Test
    public void BoardIsCreatedLocked() {
        assertThat(mPresenter.isLocked(), is(true));
    }

    @Test
    public void WhenBoardIsUnlocked__IsLockedReturnsFalse() {
        mPresenter.lock();
        mPresenter.unlock();
        assertThat(mPresenter.isLocked(), is(false));
    }

    @Test
    public void WhenBoardIsLocked__IsLockedReturnsTrue() {
        mPresenter.lock();
        mPresenter.unlock();
        mPresenter.lock();
        assertThat(mPresenter.isLocked(), is(true));
    }

    @Test
    public void WhenBoardIsLocked__ShotListenerNotActive() {
        mPresenter.touch(getMotionEvent(100f, 200f, MotionEvent.ACTION_DOWN));
        mPresenter.touch(getMotionEvent(100f, 200f, MotionEvent.ACTION_UP));
        verify(shotListener, never()).onAimingStarted();
        verify(shotListener, never()).onAimingFinished(anyInt(), anyInt());
    }

    @NonNull
    private static TouchState getMotionEvent(float x, float y, int action) {
        return TouchStateTest.newTouchState(x, y, action);
    }

}