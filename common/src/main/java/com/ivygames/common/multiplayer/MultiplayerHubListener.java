package com.ivygames.common.multiplayer;

import android.content.Intent;
import android.support.annotation.NonNull;

public interface MultiplayerHubListener {
    void handleSelectPlayersResult(int resultCode, @NonNull Intent data);

    void handleInvitationInboxResult(int resultCode, @NonNull Intent data);

    void handleWaitingRoomResult(int resultCode);
}
