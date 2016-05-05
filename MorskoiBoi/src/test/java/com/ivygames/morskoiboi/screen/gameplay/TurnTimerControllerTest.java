package com.ivygames.morskoiboi.screen.gameplay;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class TurnTimerControllerTest {

    private TurnTimerController controller;
    private TurnTimerFactory factory;
    private com.ivygames.morskoiboi.screen.gameplay.TurnTimer timer;

    @Before
    public void setup() {
        factory = mock(TurnTimerFactory.class);
        timer = mock(TurnTimer.class);
        when(factory.newTimer(anyInt(), any(TimerListener.class))).thenReturn(timer);
        controller = new TurnTimerController(10, factory);
    }

    @Test
    public void WhenStartCalled__NewTurnTimerCreatedAndExecuted() {
        controller.start();
        verify(factory, times(1)).newTimer(anyInt(), any(TimerListener.class));
        verify(timer, times(1)).execute();
    }
}
