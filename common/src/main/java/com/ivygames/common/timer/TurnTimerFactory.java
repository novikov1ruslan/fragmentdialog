package com.ivygames.common.timer;

import android.support.annotation.NonNull;

interface TurnTimerFactory {
    TurnTimer newTimer(int timeLeft, @NonNull TimerListener timerListener);
}
