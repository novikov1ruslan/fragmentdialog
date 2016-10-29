package com.ivygames.common.timer;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TimerUpdaterTest {

    @Test
    public void WhenTimerUpdaterIsCreate__ListenerIsUpdatedWithTimeout() {
        TimerListener listener = mock(TimerListener.class);

        TimerUpdater timerUpdater = new TimerUpdater(100, 1, listener) ;

        verify(listener).setCurrentTime(100);
    }

    @Test
    public void InitiallyRemainedTime__IsTotalTime() {
        TimerListener listener = mock(TimerListener.class);

        TimerUpdater timerUpdater = new TimerUpdater(100, 1, listener) ;

        assertThat(timerUpdater.getRemainedTime(), is(100));
    }

    @Test
    public void AfterTick__RemainedTimeDecreasesByResolution() {
        TimerListener listener = mock(TimerListener.class);

        TimerUpdater timerUpdater = new TimerUpdater(100, 1, listener) ;
        timerUpdater.tick();

        assertThat(timerUpdater.getRemainedTime(), is(99));
    }

    @Test
    public void WithEveryTick_IfTimeHasLeft__TimeIsUpdated() {
        TimerListener listener = mock(TimerListener.class);

        TimerUpdater timerUpdater = new TimerUpdater(100, 1, listener) ;
        timerUpdater.tick();

        verify(listener).setCurrentTime(99);
    }


    @Test
    public void IfNoTimeLeft__TimerExpires() {
        TimerListener listener = mock(TimerListener.class);

        TimerUpdater timerUpdater = new TimerUpdater(2, 1, listener) ;
        timerUpdater.tick();
        timerUpdater.tick();

        verify(listener).onTimerExpired();
    }

}