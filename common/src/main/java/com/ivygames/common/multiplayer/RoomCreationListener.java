package com.ivygames.common.multiplayer;

import android.support.annotation.NonNull;

import com.google.android.gms.games.multiplayer.realtime.Room;

interface RoomCreationListener {
    /**
     * Called when room created or joined
     */
    void onRoomCreated(@NonNull Room room);
}
