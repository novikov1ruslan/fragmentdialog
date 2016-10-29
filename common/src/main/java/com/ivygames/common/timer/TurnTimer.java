package com.ivygames.common.timer;

interface TurnTimer {
    void execute();

    void cancel();

    int getRemainedTime();
}
