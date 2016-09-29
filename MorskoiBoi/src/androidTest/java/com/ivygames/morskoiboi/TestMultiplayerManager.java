package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;

import com.google.android.gms.games.multiplayer.realtime.Room;
import com.ivygames.common.googleapi.ApiClient;
import com.ivygames.common.multiplayer.MultiplayerManager;
import com.ivygames.common.multiplayer.RoomListener;

public class TestMultiplayerManager extends MultiplayerManager {
    public TestMultiplayerManager(@NonNull ApiClient apiClient) {
        super(apiClient);
    }

    @Override
    public void showInvitations(int requestCode, @NonNull RoomListener roomListener) {
//        super.showInvitations(requestCode, roomListener);
    }

    @Override
    public void invitePlayers(int requestCode, @NonNull RoomListener roomListener) {
//        super.invitePlayers(requestCode, roomListener);
    }

    @Override
    public void showWaitingRoom(@NonNull Room room, int requestCode) {
//        super.showWaitingRoom(room, requestCode);
    }
}
