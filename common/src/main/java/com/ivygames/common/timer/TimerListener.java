package com.ivygames.common.timer;

interface TimerListener {
    void onTimerExpired();

    void setCurrentTime(int mTimeout);
}
