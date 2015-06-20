package com.ivygames.morskoiboi.rt;

import org.commons.logger.Ln;

import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;

class RoomLeaveListener implements RoomUpdateListener {

	private RoomLeaveListener() {
	}

	@Override
	public void onJoinedRoom(int arg0, Room arg1) {
	}

	@Override
	public void onRoomConnected(int arg0, Room arg1) {
	}

	@Override
	public void onRoomCreated(int arg0, Room arg1) {
	}

	@Override
	public void onLeftRoom(int statusCode, String roomId) {
		if (GamesStatusCodes.STATUS_OK == statusCode) {
			Ln.d("player successfully left");
		} else {
			Ln.w("error while leaving the room: " + statusCode);
		}
	}
}
