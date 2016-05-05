package com.ivygames.morskoiboi.screen.gameplay;

import android.support.annotation.NonNull;

public class TurnTimerFactory {
    public TurnTimer newTimer(int timeLeft, @NonNull TimerListener timerListener) {
        return new TurnTimer(timeLeft, timerListener);
    }
}
