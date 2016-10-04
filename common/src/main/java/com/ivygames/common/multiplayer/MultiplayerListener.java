package com.ivygames.common.multiplayer;

public interface MultiplayerListener {

    void invitationCanceled();

    void gameStarted();

    void playerLeft();
}
