package com.ivygames.common.multiplayer;

import android.support.annotation.NonNull;

public interface RoomConnectionListener {
    void onConnectionLost(@NonNull MultiplayerEvent event);
}
