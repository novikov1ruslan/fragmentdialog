package com.ivygames.morskoiboi.screen.internet;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesStatusCodes;
import com.ivygames.common.analytics.ExceptionEvent;
import com.ivygames.common.analytics.UiEvent;
import com.ivygames.common.dialog.SimpleActionDialog;
import com.ivygames.common.googleapi.ApiClient;
import com.ivygames.common.invitations.InvitationListener;
import com.ivygames.common.multiplayer.GameCreationListener;
import com.ivygames.common.multiplayer.InvitationToShowListener;
import com.ivygames.common.multiplayer.MultiplayerSession;
import com.ivygames.common.multiplayer.QueuedRtmSender;
import com.ivygames.common.multiplayer.RealTimeMultiplayer;
import com.ivygames.common.multiplayer.RoomConnectionErrorListener;
import com.ivygames.common.ui.BackPressListener;
import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.Dependencies;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.Session;
import com.ivygames.morskoiboi.ai.PlacementFactory;
import com.ivygames.morskoiboi.player.PlayerOpponent;
import com.ivygames.morskoiboi.rt.InternetGame;
import com.ivygames.morskoiboi.rt.InternetOpponent;
import com.ivygames.morskoiboi.screen.BackToSelectGameCommand;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.ScreenCreator;
import com.ruslan.fragmentdialog.FragmentAlertDialog;

import org.commons.logger.Ln;

public class InternetGameScreen extends BattleshipScreen implements BackPressListener {
    private static final String TAG = "INTERNET_GAME";

    private static final String DIALOG = FragmentAlertDialog.TAG;

    private InternetGame mInternetGame;
    private boolean mKeyLock;
    private InternetGameLayout mLayout;
    private WaitFragment mWaitFragment;

    @NonNull
    private final ApiClient mApiClient = Dependencies.getApiClient();
    @NonNull
    private final GameSettings mSettings = Dependencies.getSettings();
    @NonNull
    private final RealTimeMultiplayer mMultiplayer = Dependencies.getMultiplayer();

    private Session mSession;
    private InvitationListener mInvitationListener;

    public InternetGameScreen(@NonNull BattleshipActivity parent) {
        super(parent);
        mMultiplayer.setGameCreationListener(new GameCreationListener() {

            @Override
            public void gameStarted() {
                setScreen(ScreenCreator.newBoardSetupScreen(mInternetGame, mSession));
            }

            @Override
            public void gameAborted() {
                hideWaitingScreen();
            }
        });

        createMultiplayerSession();
    }

    @NonNull
    private BackToSelectGameCommand newBackToSelectGameCommand() {
        return new BackToSelectGameCommand(parent());
    }

    private MultiplayerSession createMultiplayerSession() {
        MultiplayerSession session = new MultiplayerSession(new RoomConnectionErrorListenerImpl());
        mInternetGame = new InternetGame(mMultiplayer);

        QueuedRtmSender rtmSender = new QueuedRtmSender(mApiClient);
        session.setRtmSender(rtmSender);

        InternetOpponent opponent = new InternetOpponent(rtmSender, getString(R.string.player));
        session.setRtmListener(opponent);

        PlayerOpponent player = newPlayer();
        mSession = new Session(player, opponent);
        Session.bindOpponents(player, opponent);

        return session;
    }

    @NonNull
    private PlayerOpponent newPlayer() {
        PlayerOpponent player = new PlayerOpponent(mSettings.getPlayerName(),
                PlacementFactory.getAlgorithm(), Dependencies.getRules());
        player.setChatListener(parent());
        return player;
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull ViewGroup container) {
        mLayout = (InternetGameLayout) inflate(R.layout.internet_game, container);
        mLayout.setPlayerName(mSettings.getPlayerName());
        mLayout.setScreenActions(mInternetGameLayoutListener);

        mInvitationListener = new InvitationToShowListener(mMultiplayer, mLayout);
        Ln.d(this + " screen created");
        return mLayout;
    }

    @NonNull
    @Override
    public View getView() {
        return mLayout;
    }

    @Override
    public void onStart() {
        super.onStart();
        mMultiplayer.addInvitationListener(mInvitationListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mMultiplayer.removeInvitationListener(mInvitationListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        mKeyLock = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED) {
            Ln.i("reconnect required - returning to select game screen");
            hideWaitingScreen();
            mApiClient.disconnect();
            // TODO: missing UI test
            SimpleActionDialog.create(R.string.error, newBackToSelectGameCommand()).show(mFm, DIALOG);
            return;
        }

        mMultiplayer.handleResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (mKeyLock) {
            Ln.w("keys are locked");
            return;
        }

        if (mWaitFragment != null) {
            Ln.d("blocking backpress");
            return;
        }

        newBackToSelectGameCommand().run();
    }

    @NonNull
    private final InternetGameLayoutListener mInternetGameLayoutListener = new InternetGameLayoutListener() {

        @Override
        public void invitePlayer() {
            if (mKeyLock) {
                Ln.w("keys are locked");
                return;
            }

            mKeyLock = true;
            UiEvent.send("invitePlayer");

            showWaitingScreen();
            mMultiplayer.invitePlayers(BattleshipActivity.RC_SELECT_PLAYERS, createMultiplayerSession());
        }

        @Override
        public void viewInvitations() {
            if (mKeyLock) {
                Ln.w("keys are locked");
                return;
            }

            mKeyLock = true;
            UiEvent.send("viewInvitations");
            Ln.d("requesting invitations screen...");

            showWaitingScreen();
            mMultiplayer.showInvitations(BattleshipActivity.RC_INVITATION_INBOX, createMultiplayerSession());
        }

        @Override
        public void quickGame() {
            if (mKeyLock) {
                Ln.w("keys are locked");
                return;
            }

            mKeyLock = true;
            UiEvent.send("quickGame");

            showWaitingScreen();
            mMultiplayer.quickGame(createMultiplayerSession());
        }
    };

    private void showWaitingScreen() {
        Ln.d("please wait... ");
        mWaitFragment = new WaitFragment();
        mFm.beginTransaction().add(R.id.container, mWaitFragment).commitAllowingStateLoss();
    }

    private void hideWaitingScreen() {
        if (mWaitFragment != null) {
            Ln.d("hiding waiting screen");
            mFm.beginTransaction().remove(mWaitFragment).commitAllowingStateLoss();
            mWaitFragment = null;
        } else {
            Ln.w("waiting screen isn't shown");
        }
    }

    @Override
    public int getMusic() {
        return R.raw.intro_music;
    }

    @Override
    public String toString() {
        return TAG + debugSuffix();
    }

    private class RoomConnectionErrorListenerImpl implements RoomConnectionErrorListener {

        @Override
        public void onError(int statusCode) {
            ExceptionEvent.send("room_connection_error_" + statusCode);

            hideWaitingScreen();
            Ln.w("error: " + GamesStatusCodes.getStatusString(statusCode));

            if (statusCode == GamesStatusCodes.STATUS_REAL_TIME_INACTIVE_ROOM) {
                FragmentAlertDialog.showNote(mFm, DIALOG, R.string.match_canceled);
            } else if (statusCode == GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_FAILED) {
                FragmentAlertDialog.showNote(mFm, DIALOG, R.string.network_error);
            } else if (statusCode == GamesStatusCodes.STATUS_CLIENT_RECONNECT_REQUIRED) {
                mApiClient.disconnect();
                SimpleActionDialog.create(R.string.error, newBackToSelectGameCommand()).show(mFm, DIALOG);
            } else {
                // STATUS_REAL_TIME_CONNECTION_FAILED
                // STATUS_INTERNAL_ERROR
                ExceptionEvent.send("internet_game", GamesStatusCodes.getStatusString(statusCode));
                SimpleActionDialog.create(R.string.error, newBackToSelectGameCommand()).show(mFm, DIALOG);
            }
        }
    }
}
