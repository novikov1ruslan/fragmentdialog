package com.ivygames.morskoiboi.screen.gameplay;

import android.support.annotation.NonNull;
import android.view.MotionEvent;

import com.ivygames.morskoiboi.screen.view.TouchState;
import com.ivygames.morskoiboi.screen.view.TouchStateTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class EnemyBoardPresenterTest {

    private EnemyBoardPresenter mPresenter;
    @Mock
    private ShotListener shotListener;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mPresenter = new EnemyBoardPresenter();
        mPresenter.setShotListener(shotListener);
    }

    @Test
    public void WhenUnlockedBoardTouchedDown__AimingStartedEventFired() {
        mPresenter.unlock();
        mPresenter.touch(getMotionEvent(100f, 200f, MotionEvent.ACTION_DOWN), 3, 3);
        verify(shotListener, times(1)).onAimingStarted();
    }

    @Test
    public void WhenUnlockedBoardTouchedUp__AimingFinishedEventFired() {
        mPresenter.unlock();
        mPresenter.touch(getMotionEvent(100f, 200f, MotionEvent.ACTION_UP), 3, 3);
        verify(shotListener, times(1)).onAimingFinished(3, 3);
    }

    @Test
    public void WhenBoardUnlockedAfterBeingTouchedDown__AimingStartedFired() {
        mPresenter.touch(getMotionEvent(100f, 200f, MotionEvent.ACTION_DOWN), 3, 3);
        mPresenter.unlock();
        verify(shotListener, times(1)).onAimingStarted();
    }

    @Test
    public void WhenBoardUnlockedAfterBeingTouchedMoved__AimingStartedFired() {
        mPresenter.touch(getMotionEvent(100f, 200f, MotionEvent.ACTION_MOVE), 3, 3);
        mPresenter.unlock();
        verify(shotListener, times(1)).onAimingStarted();
    }

    @Test
    public void WhenBoardUnlockedAfterBeingTouchedUp__AimingStartedNotFired() {
        mPresenter.touch(getMotionEvent(100f, 200f, MotionEvent.ACTION_UP), 3, 3);
        mPresenter.unlock();
        verify(shotListener, never()).onAimingStarted();
    }

    @Test
    public void DuringFingerMoveOnUnlockedBoard__NoShotListenerEventsFired() {
        mPresenter.unlock();
        mPresenter.touch(getMotionEvent(100f, 200f, MotionEvent.ACTION_MOVE), 3, 3);
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
        mPresenter.touch(getMotionEvent(100f, 200f, MotionEvent.ACTION_DOWN), 3, 3);
        mPresenter.touch(getMotionEvent(100f, 200f, MotionEvent.ACTION_UP), 3, 3);
        verify(shotListener, never()).onAimingStarted();
        verify(shotListener, never()).onAimingFinished(anyInt(), anyInt());
    }

    @NonNull
    private static TouchState getMotionEvent(float x, float y, int action) {
        return TouchStateTest.newTouchState(x, y, action);
    }

}