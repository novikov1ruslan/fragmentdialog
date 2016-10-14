package com.ivygames.common.multiplayer;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.ivygames.common.invitations.InvitationListener;

import java.util.Set;

public interface RealTimeMultiplayer {
    void setGameCreationListener(@NonNull GameCreationListener listener);

    void invitePlayers(int requestCode, @NonNull MultiplayerRoom room);

    void showInvitations(int requestCode, @NonNull MultiplayerRoom room);

    void quickGame(@NonNull MultiplayerRoom room);

    void handleResult(int requestCode, int resultCode, @NonNull Intent data);

    void addInvitationListener(@NonNull InvitationListener listener);

    void loadInvitations();

    void removeInvitationListener(@NonNull InvitationListener listener);

    void registerConnectionLostListener(@NonNull ConnectionLostListener listener);

    boolean unregisterConnectionLostListener(@NonNull ConnectionLostListener listener);

    @NonNull
    Set<String> getInvitationIds();

    void setRoomConnectionErrorListener(@NonNull RoomConnectionErrorListener listener);

    void setRtmListener(@NonNull RealTimeMessageReceivedListener listener);

}
