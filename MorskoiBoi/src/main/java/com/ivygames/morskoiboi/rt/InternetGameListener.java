package com.ivygames.morskoiboi.rt;

import com.google.android.gms.games.multiplayer.realtime.Room;

public interface InternetGameListener {

    void onWaitingForOpponent(Room room);

    void onError(int statusCode);
}
