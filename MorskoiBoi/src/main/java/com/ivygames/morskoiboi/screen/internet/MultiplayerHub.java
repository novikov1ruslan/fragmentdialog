package com.ivygames.morskoiboi.screen.internet;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.games.multiplayer.realtime.Room;
import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.GoogleApiClientWrapper;
import com.ivygames.morskoiboi.rt.InternetGame;

import org.commons.logger.Ln;

public class MultiplayerHub {
    private static final int MIN_PLAYERS = 2;

    @NonNull
    private final Activity mActivity;
    @NonNull
    private final GoogleApiClientWrapper mApiClient;
    private MultiplayerHubListener mListener;

    public MultiplayerHub(@NonNull Activity activity, @NonNull GoogleApiClientWrapper apiClient) {
        mActivity = activity;
        mApiClient = apiClient;
    }

    public void setResultListener(@NonNull MultiplayerHubListener listener) {
        mListener = listener;
    }

    public void showWaitingRoom(Room room) {
        Intent intent = mApiClient.getWaitingRoomIntent(room, MIN_PLAYERS);
        mActivity.startActivityForResult(intent, BattleshipActivity.RC_WAITING_ROOM);
    }

    public void invitePlayers() {
        Intent intent = mApiClient.getSelectOpponentsIntent(InternetGame.MIN_OPPONENTS, InternetGame.MAX_OPPONENTS, false);
        mActivity.startActivityForResult(intent, BattleshipActivity.RC_SELECT_PLAYERS);
    }

    public void showInvitations() {
        Intent intent = mApiClient.getInvitationInboxIntent();
        mActivity.startActivityForResult(intent, BattleshipActivity.RC_INVITATION_INBOX);
    }

    public void handleResult(int requestCode, int resultCode, Intent data) {
        Ln.v("result=" + resultCode);
        switch (requestCode) {
            case BattleshipActivity.RC_SELECT_PLAYERS:
                mListener.handleSelectPlayersResult(resultCode, data);
                break;
            case BattleshipActivity.RC_INVITATION_INBOX:
                mListener.handleInvitationInboxResult(resultCode, data);
                break;
            case BattleshipActivity.RC_WAITING_ROOM:
                mListener.handleWaitingRoomResult(resultCode);
                break;
        }
    }
}
