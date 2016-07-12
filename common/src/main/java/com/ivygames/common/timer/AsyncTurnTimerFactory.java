package com.ivygames.common.timer;

import android.support.annotation.NonNull;

import com.ivygames.common.timer.TimerListener;
import com.ivygames.common.timer.TurnTimer;

public class AsyncTurnTimerFactory implements TurnTimerFactory {
    public TurnTimer newTimer(int timeLeft, @NonNull TimerListener timerListener) {
        return new TurnTimerAsync(timeLeft, timerListener);
    }
}
