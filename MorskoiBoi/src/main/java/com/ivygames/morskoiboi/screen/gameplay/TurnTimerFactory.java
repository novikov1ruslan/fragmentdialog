package com.ivygames.morskoiboi.screen.gameplay;

import android.support.annotation.NonNull;

public interface TurnTimerFactory {
    TurnTimer newTimer(int timeLeft, @NonNull TimerListener timerListener);
}
