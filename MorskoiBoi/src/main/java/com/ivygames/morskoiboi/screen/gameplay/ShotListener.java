package com.ivygames.morskoiboi.screen.gameplay;

interface ShotListener {
    void onShot(int i, int j);

    void onAimingStarted();

    void onAimingFinished();
}
