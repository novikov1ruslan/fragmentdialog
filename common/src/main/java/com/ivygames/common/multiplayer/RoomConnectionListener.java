package com.ivygames.common.multiplayer;

import android.support.annotation.NonNull;

interface RoomConnectionListener {
    void onConnectionLost(@NonNull MultiplayerEvent event);
}
