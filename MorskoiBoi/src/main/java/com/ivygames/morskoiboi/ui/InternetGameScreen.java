package com.ivygames.morskoiboi.ui;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.PlayerOpponent;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.analytics.ExceptionEvent;
import com.ivygames.morskoiboi.analytics.UiEvent;
import com.ivygames.morskoiboi.model.Model;
import com.ivygames.morskoiboi.rt.InternetGame;
import com.ivygames.morskoiboi.rt.InternetGame.InternetGameListener;
import com.ivygames.morskoiboi.rt.InternetOpponent;
import com.ivygames.morskoiboi.rt.InvitationEvent;
import com.ivygames.morskoiboi.rt.RtUtils;
import com.ivygames.morskoiboi.ui.BattleshipActivity.BackPressListener;
import com.ivygames.morskoiboi.ui.view.InternetGameLayout;
import com.ivygames.morskoiboi.ui.view.InternetGameLayout.InternetGameLayoutListener;
import com.ruslan.fragmentdialog.FragmentAlertDialog;

import org.commons.logger.Ln;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class InternetGameScreen extends BattleshipScreen implements InternetGameLayoutListener, InternetGameListener, BackPressListener {
    private static final String TAG = "INTERNET_GAME";
    private static final String DIALOG = FragmentAlertDialog.TAG;

    private static final int MIN_PLAYERS = 2;

    private static final int RC_SELECT_PLAYERS = 10000;
    private static final int RC_INVITATION_INBOX = 10001;
    private final static int RC_WAITING_ROOM = 10002;

    private InternetGame mInternetGame;
    private boolean mKeyLock;
    private InternetGameLayout mLayout;

    @Override
    public View onCreateView(ViewGroup container) {
        mLayout = (InternetGameLayout) getLayoutInflater().inflate(R.layout.internet_game, container, false);
        mLayout.setPlayerName(GameSettings.get().getPlayerName());
        mLayout.setScreenActions(this);
        Ln.d(this + " screen created");
        return mLayout;
    }

    @Override
    public View getView() {
        return mLayout;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        showInvitationIfHas(mParent.hasInvitation());
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(InvitationEvent event) {
        showInvitationIfHas(mParent.hasInvitation());
    }

    private void showInvitationIfHas(boolean hasInvitations) {
        if (hasInvitations) {
            Ln.d(this + ": there is a pending invitation ");
            mLayout.showInvitation();
        } else {
            Ln.v(this + ": there are no pending invitations");
            mLayout.hideInvitation();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mKeyLock = false;
    }

    @Override
    public void onWaitingForOpponent(Room room) {
        mParent.loadInvitations();
        showWaitingRoom(room);
    }

    @Override
    public void onError(int statusCode) {
        hideWaitingScreen();
        Ln.w("error status code: " + RtUtils.name(statusCode));

        if (statusCode == GamesStatusCodes.STATUS_REAL_TIME_INACTIVE_ROOM) {
            FragmentAlertDialog.showNote(mFm, DIALOG, R.string.match_canceled);
        } else if (statusCode == GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_FAILED) {
            FragmentAlertDialog.showNote(mFm, DIALOG, R.string.network_error);
        } else if (statusCode == GamesStatusCodes.STATUS_CLIENT_RECONNECT_REQUIRED) {
            mApiClient.disconnect();
            SimpleActionDialog.create(R.string.error, new BackToSelectGameCommand(mParent)).show(mFm, DIALOG);
        } else {
            // STATUS_REAL_TIME_CONNECTION_FAILED
            // STATUS_INTERNAL_ERROR
            mGaTracker.send(new ExceptionEvent("internet_game", RtUtils.name(statusCode)).build());
            SimpleActionDialog.create(R.string.error, new BackToSelectGameCommand(mParent)).show(mFm, DIALOG);
        }
    }

    @Override
    public void invitePlayer() {
        if (mKeyLock) {
            Ln.w("keys are locked");
            return;
        }

        mKeyLock = true;
        mGaTracker.send(new UiEvent("invitePlayer").build());
        createGame();

        showWaitingScreen();
        Intent intent = Games.RealTimeMultiplayer.getSelectOpponentsIntent(mApiClient, InternetGame.MIN_OPPONENTS, InternetGame.MAX_OPPONENTS, false);
        startActivityForResult(intent, RC_SELECT_PLAYERS);
    }

    private void createGame() {
        mInternetGame = new InternetGame(mApiClient, this);
        InternetOpponent mOpponent = new InternetOpponent(mInternetGame);
        mInternetGame.setRealTimeMessageReceivedListener(mOpponent);
        Model.instance.setOpponents(new PlayerOpponent(GameSettings.get().getPlayerName()), mOpponent);
    }

    @Override
    public void viewInvitations() {
        if (mKeyLock) {
            Ln.w("keys are locked");
            return;
        }

        mKeyLock = true;
        mGaTracker.send(new UiEvent("viewInvitations").build());
        Ln.d("requesting invitations screen...");
        createGame();

        showWaitingScreen();
        Intent intent = Games.Invitations.getInvitationInboxIntent(mApiClient);
        startActivityForResult(intent, RC_INVITATION_INBOX);
    }

    @Override
    public void quickGame() {
        if (mKeyLock) {
            Ln.w("keys are locked");
            return;
        }

        mKeyLock = true;
        mGaTracker.send(new UiEvent("quickGame").build());
        createGame();

        showWaitingScreen();
        mInternetGame.quickGame();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED) {
            Ln.i("reconnect required - returning to select game screen");
            hideWaitingScreen();
            new BackToSelectGameCommand(mParent).run();
            mApiClient.disconnect();
            return;
        }

        Ln.v("result=" + resultCode);
        switch (requestCode) {
            case RC_SELECT_PLAYERS:
                handleSelectPlayersResult(resultCode, data);
                break;
            case RC_INVITATION_INBOX:
                handleInvitationInboxResult(resultCode, data);
                break;
            case RC_WAITING_ROOM:
                handleWaitingRoomResult(resultCode);
                break;
        }
    }

    private void handleWaitingRoomResult(int resultCode) {
        hideWaitingScreen();
        if (resultCode == Activity.RESULT_OK) {
            Ln.d("starting game");
            Model.instance.game = mInternetGame;
            mParent.setScreen(new BoardSetupScreen());
        } else if (resultCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
            Ln.d("user explicitly chose to leave the room");
            // if the activity result is RESULT_LEFT_ROOM, it's the caller's responsibility to actually leave the room
            mInternetGame.finish();
        } else if (resultCode == Activity.RESULT_CANCELED) {
            /*
			 * Dialog was cancelled (user pressed back key, for instance). In our game, this means leaving the room too. In more elaborate games,this could mean
			 * something else (like minimizing the waiting room UI but continue in the handshake process).
			 */
            Ln.d("user closed the waiting room - leaving");
            mInternetGame.finish();
        }
    }

    /**
     * Handle the result of the invitation inbox UI, where the player can pick an invitation to accept. We react by accepting the selected invitation, if any.
     */
    private void handleInvitationInboxResult(int result, Intent data) {
        if (result != Activity.RESULT_OK) {
            Ln.d("invitation cancelled - hiding waiting screen; reason=" + result);
            hideWaitingScreen();
            return;
        }

        Invitation invitation = data.getExtras().getParcelable(Multiplayer.EXTRA_INVITATION);
        // showWaitingScreen();
        mInternetGame.accept(invitation);
    }

    // Handle the result of the "Select players UI" we launched when the user
    // clicked the
    // "Invite friends" button. We react by creating a room with those players.
    private void handleSelectPlayersResult(int result, Intent data) {
        if (result != Activity.RESULT_OK) {
            Ln.d("select players UI cancelled - hiding waiting screen; reason=" + result);
            hideWaitingScreen();
            return;
        }

        final ArrayList<String> invitees = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);
        Ln.d("opponent selected: " + invitees + ", creating room...");
        int minAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
        int maxAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);

        mInternetGame.create(invitees, minAutoMatchPlayers, maxAutoMatchPlayers);
    }

    /**
     * Show the waiting room UI to track the progress of other players as they enter the room and get connected.
     */
    private void showWaitingRoom(Room room) {
        Intent intent = Games.RealTimeMultiplayer.getWaitingRoomIntent(mApiClient, room, MIN_PLAYERS);
        startActivityForResult(intent, RC_WAITING_ROOM);
    }

    @Override
    public void onBackPressed() {
        if (mKeyLock) {
            Ln.w("keys are locked");
            return;
        }

        if (isWaitShown()) {
            Ln.d("blocking backpress");
            return;
        }

        if (mInternetGame != null) {
            mInternetGame.finish();
        }

        mParent.setScreen(new SelectGameScreen());
    }

    @Override
    public String toString() {
        return TAG + debugSuffix();
    }

}
