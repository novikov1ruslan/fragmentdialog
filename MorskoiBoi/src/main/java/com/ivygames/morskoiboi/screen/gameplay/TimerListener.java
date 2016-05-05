package com.ivygames.morskoiboi.screen.gameplay;

interface TimerListener {
    void onTimerExpired();

    void setCurrentTime(int mTimeout);
}
