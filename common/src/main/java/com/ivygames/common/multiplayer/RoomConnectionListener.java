package com.ivygames.common.multiplayer;

import android.support.annotation.NonNull;

public interface RoomConnectionListener {
    void onConnectionLost(@NonNull MultiplayerEvent event);

    void onRoomConnected(@NonNull String roomId, @NonNull String recipientId);

    void onP2PConnected(@NonNull String participantId);
}
