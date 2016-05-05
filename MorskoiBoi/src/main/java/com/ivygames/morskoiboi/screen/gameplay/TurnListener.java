package com.ivygames.morskoiboi.screen.gameplay;

interface TurnListener extends TimerListener {

    void onCanceled();

    void onPlayerIdle();
}
