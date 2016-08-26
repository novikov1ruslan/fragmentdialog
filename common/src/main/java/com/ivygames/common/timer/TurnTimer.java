package com.ivygames.common.timer;

public interface TurnTimer {
    void execute();

    boolean cancel(boolean b);

    int getRemainedTime();
}
