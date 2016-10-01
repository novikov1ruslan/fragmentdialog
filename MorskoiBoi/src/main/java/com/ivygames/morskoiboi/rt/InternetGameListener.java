package com.ivygames.morskoiboi.rt;

import android.support.annotation.NonNull;

import com.google.android.gms.games.multiplayer.realtime.Room;

public interface InternetGameListener {

    void onWaitingForOpponent(@NonNull Room room);

    void onError(int statusCode);
}
