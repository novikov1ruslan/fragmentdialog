package com.ivygames.morskoiboi.screen.gameplay;

import android.support.annotation.NonNull;

public class AsyncTurnTimerFactory implements TurnTimerFactory {
    public TurnTimer newTimer(int timeLeft, @NonNull TimerListener timerListener) {
        return new TurnTimerAsync(timeLeft, timerListener);
    }
}
