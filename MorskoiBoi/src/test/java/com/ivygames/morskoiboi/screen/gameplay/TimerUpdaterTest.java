package com.ivygames.morskoiboi.screen.gameplay;

import com.ivygames.common.timer.TimerListener;
import com.ivygames.common.timer.TimerUpdater;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class TimerUpdaterTest {

    private static final int DEFAULT_TIMEOUT = 2 * 60 * 1000;
    private static final int RESOLUTION = 1000;

    private TimerUpdater timerUpdater;

    @Mock
    private TimerListener listener;

    @Before
    public void setUp() {
        initMocks(this);
        timerUpdater = new TimerUpdater(DEFAULT_TIMEOUT, RESOLUTION, listener);
    }

    @Test
    public void WhenUpdaterIsCreatedListenerUpdatedWithDefaultTimeout() {
        verify(listener, times(1)).setCurrentTime(DEFAULT_TIMEOUT);
    }

    @Test
    public void InitiallyRemainedTimeIsDefault() {
        assertThat(timerUpdater.getRemainedTime(), is(DEFAULT_TIMEOUT));
    }

    @Test
    public void AfterTickRemainedTimeDecreasedByResolution() {
        timerUpdater.tick();
        assertThat(timerUpdater.getRemainedTime(), is(DEFAULT_TIMEOUT - RESOLUTION));
    }

    @Test
    public void AfterTickIfTimeRemained__UpdateListener() {
        timerUpdater.tick();
        verify(listener, times(1)).setCurrentTime(DEFAULT_TIMEOUT - RESOLUTION);
    }

    @Test
    public void AfterTick_IfTimeNegative__UpdateListenerWith0() {
        timerUpdater = new TimerUpdater(DEFAULT_TIMEOUT, DEFAULT_TIMEOUT + 1, listener);
        timerUpdater.tick();
        verify(listener, times(1)).setCurrentTime(0);
    }

    @Test
    public void WhenTimerExpired__ListenerIsNotified() {
        for (int i = 0; i < 2 * 60; i++){
            timerUpdater.tick();
        }
        verify(listener, times(1)).onTimerExpired();
    }

}