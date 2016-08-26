package com.ivygames.common.timer;

public interface TurnListener extends TimerListener {

    void onCanceled();

    void onPlayerIdle();
}
