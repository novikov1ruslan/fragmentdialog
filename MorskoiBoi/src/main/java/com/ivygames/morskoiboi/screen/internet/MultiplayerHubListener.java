package com.ivygames.morskoiboi.screen.internet;

import android.content.Intent;

public interface MultiplayerHubListener {
    void handleSelectPlayersResult(int resultCode, Intent data);

    void handleInvitationInboxResult(int resultCode, Intent data);

    void handleWaitingRoomResult(int resultCode);
}
