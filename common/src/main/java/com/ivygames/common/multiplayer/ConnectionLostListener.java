package com.ivygames.common.multiplayer;

import android.support.annotation.NonNull;

public interface ConnectionLostListener {
    void onConnectionLost(@NonNull MultiplayerEvent event);
}
