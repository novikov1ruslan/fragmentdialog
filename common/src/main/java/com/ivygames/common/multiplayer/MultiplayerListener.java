package com.ivygames.common.multiplayer;

public interface MultiplayerListener {
    void opponentInvitationCanceled();

    void invitationCanceled();

    void gameStarted();

    void playerLeft();
}
