package com.ivygames.common.multiplayer;

public interface MultiplayerListener {
    void userCanceledInvitation();

    void invitationCanceled();

    void gameStarted();

    void playerLeft();
}
