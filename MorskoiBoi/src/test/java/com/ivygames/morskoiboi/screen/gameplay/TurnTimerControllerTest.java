package com.ivygames.morskoiboi.screen.gameplay;

import android.support.annotation.NonNull;

import com.ivygames.common.analytics.ExceptionHandler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class TurnTimerControllerTest {

    private static final int DEFAULT_TIMEOUT = 10;

    private TurnTimerController controller;
    @Mock
    private TurnTimerFactory factory;
    @Mock
    private TurnTimerAsync timer;
    @Mock
    private TurnListener listener;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ExceptionHandler.setDryRun(true);

        when(factory.newTimer(anyInt(), any(TimerListener.class))).thenReturn(timer);
        controller = new TurnTimerController(DEFAULT_TIMEOUT, factory);
        controller.setListener(listener);
    }

    @Test
    public void WhenStartCalled__NewTurnTimerCreatedAndExecuted() {
        controller.start();
        verify(factory, times(1)).newTimer(anyInt(), any(TimerListener.class));
        verify(timer, times(1)).execute();
    }

    @Test
    public void WhenStartCalledMoreThanOnce__2ndCallIsIgnored() {
        controller.start();
        controller.start();
        verify(factory, times(1)).newTimer(anyInt(), any(TimerListener.class));
        verify(timer, times(1)).execute();
    }

    @Test
    public void WhenPauseCalled__TimerCancelled() {
        controller.start();
        controller.pause();
        verify(timer, times(1)).cancel(true);
        verify(listener, times(1)).onCanceled();
    }

    @Test
    public void WhenPauseCalledWithoutStart__CallIsIgnored() {
        controller.pause();
        verify(timer, never()).cancel(anyBoolean());
        verify(listener, never()).onCanceled();
    }

    @Test
    public void AfterTimerIsPaused__StartContinuesTheTimer() {
        controller.start();

        reset(factory);
        when(factory.newTimer(anyInt(), any(TimerListener.class))).thenReturn(timer);
        reset(timer);
        when(timer.getRemainedTime()).thenReturn(100);

        controller.pause();
        controller.start();
        verify(factory, times(1)).newTimer(eq(100), any(TimerListener.class));
        verify(timer, times(1)).execute();
    }

    @Test
    public void WhenStopCalled__TimerCancelled() {
        controller.start();
        controller.stop();
        verify(timer, times(1)).cancel(true);
    }

    @Test
    public void WhenStopCalledWithoutStart__CallIsIgnored() {
        controller.stop();
        verify(timer, never()).cancel(anyBoolean());
        verify(listener, never()).onCanceled();
    }

    @Test
    public void WhenTimerExpiresOnce__ListenerNotified() {
        factory = new TurnTimerFactory() {
            @Override
            public TurnTimer newTimer(int timeLeft, @NonNull TimerListener timerListener) {
                return new TurnTimerSync(timeLeft, timerListener, 1);
            }
        };
        controller = new TurnTimerController(DEFAULT_TIMEOUT, factory);
        controller.setListener(listener);
        controller.start();
        verify(listener, times(1)).onTimerExpired();
    }

    @Test
    public void WhenTimerExpiresTwice__ListenerNotified2x() {
        factory = new TurnTimerFactory() {
            @Override
            public TurnTimer newTimer(int timeLeft, @NonNull TimerListener timerListener) {
                return new TurnTimerSync(timeLeft, timerListener, 2);
            }
        };
        controller = new TurnTimerController(DEFAULT_TIMEOUT, factory);
        controller.setListener(listener);
        controller.start();
        verify(listener, times(2)).onTimerExpired();
    }

    @Test
    public void WhenTimerExpires3x__PlayerIsIdle() {
        factory = new TurnTimerFactory() {
            @Override
            public TurnTimer newTimer(int timeLeft, @NonNull TimerListener timerListener) {
                return new TurnTimerSync(timeLeft, timerListener, 3);
            }
        };
        controller = new TurnTimerController(DEFAULT_TIMEOUT, factory);
        controller.setListener(listener);
        controller.start();
        verify(listener, times(1)).onPlayerIdle();
    }
}
