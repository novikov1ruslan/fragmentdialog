package com.ivygames.morskoiboi.screen.gameplay;

public interface TurnTimer {
    void execute();

    boolean cancel(boolean b);

    int getRemainedTime();
}
