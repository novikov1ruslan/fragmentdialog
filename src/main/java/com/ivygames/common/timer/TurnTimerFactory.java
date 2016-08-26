package com.ivygames.common.timer;

import android.support.annotation.NonNull;

import com.ivygames.common.timer.TimerListener;
import com.ivygames.common.timer.TurnTimer;

public interface TurnTimerFactory {
    TurnTimer newTimer(int timeLeft, @NonNull TimerListener timerListener);
}
