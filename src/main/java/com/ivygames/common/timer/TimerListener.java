package com.ivygames.common.timer;

public interface TimerListener {
    void onTimerExpired();

    void setCurrentTime(int mTimeout);
}
