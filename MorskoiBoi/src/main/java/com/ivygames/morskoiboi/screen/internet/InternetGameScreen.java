package com.ivygames.morskoiboi.screen.internet;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.ivygames.common.analytics.ExceptionEvent;
import com.ivygames.common.analytics.UiEvent;
import com.ivygames.common.dialog.SimpleActionDialog;
import com.ivygames.common.googleapi.ApiClient;
import com.ivygames.common.invitations.InvitationListener;
import com.ivygames.common.multiplayer.ScreenInvitationListener;
import com.ivygames.common.multiplayer.MultiplayerManager;
import com.ivygames.common.multiplayer.MultiplayerListener;
import com.ivygames.common.ui.BackPressListener;
import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.Dependencies;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.Session;
import com.ivygames.morskoiboi.ai.PlacementFactory;
import com.ivygames.morskoiboi.player.PlayerOpponent;
import com.ivygames.morskoiboi.rt.InternetGame;
import com.ivygames.morskoiboi.rt.WaitingRoomListener;
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
    private final Rules mRules = Dependencies.getRules();
    @NonNull
    private final Placement mPlacement = PlacementFactory.getAlgorithm();
    @NonNull
    private final MultiplayerManager mMultiplayer = Dependencies.getMultiplayer();

    private Session mSession;
    private InvitationListener mInvitationListener;
    private InternetOpponent mOpponent;

    public InternetGameScreen(@NonNull BattleshipActivity parent) {
        super(parent);
        mMultiplayer.setListener(mMultiplayerListener);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull ViewGroup container) {
        mLayout = (InternetGameLayout) inflate(R.layout.internet_game, container);
        mLayout.setPlayerName(mSettings.getPlayerName());
        mLayout.setScreenActions(mInternetGameLayoutListener);

        mInvitationListener = new ScreenInvitationListener(mMultiplayer, mLayout);
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

    private void createGame() {
        mInternetGame = new InternetGame(mApiClient, mWaitingRoomListener);
        mOpponent = new InternetOpponent(mInternetGame, getString(R.string.player));
        PlayerOpponent player = new PlayerOpponent(fetchPlayerName(), mPlacement, mRules);
        player.setChatListener(parent());
        mSession = new Session(player, mOpponent);
        Session.bindOpponents(player, mOpponent);
    }

    @NonNull
    private String fetchPlayerName() {
        String playerName = mSettings.getPlayerName();
        if (TextUtils.isEmpty(playerName)) {
            playerName = getString(R.string.player);
            Ln.i("player name is empty - replaced by " + playerName);
        }
        return playerName;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED) {
            Ln.i("reconnect required - returning to select game screen");
            hideWaitingScreen();
            new BackToSelectGameCommand(parent(), mInternetGame).run();
            mApiClient.disconnect();
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

        new BackToSelectGameCommand(parent(), mInternetGame).run();
    }

    @NonNull
    private final WaitingRoomListener mWaitingRoomListener = new WaitingRoomListener() {
        @Override
        public void onWaitingForOpponent(@NonNull Room room) {
            mMultiplayer.showWaitingRoom(BattleshipActivity.RC_WAITING_ROOM, room);
        }

        @Override
        public void onError(int statusCode) {
            hideWaitingScreen();
            Ln.w("error status code: " + GamesStatusCodes.getStatusString(statusCode));

            if (statusCode == GamesStatusCodes.STATUS_REAL_TIME_INACTIVE_ROOM) {
                FragmentAlertDialog.showNote(mFm, DIALOG, R.string.match_canceled);
            } else if (statusCode == GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_FAILED) {
                FragmentAlertDialog.showNote(mFm, DIALOG, R.string.network_error);
            } else if (statusCode == GamesStatusCodes.STATUS_CLIENT_RECONNECT_REQUIRED) {
                mApiClient.disconnect();
                SimpleActionDialog.create(R.string.error, new BackToSelectGameCommand(parent(), mInternetGame)).show(mFm, DIALOG);
            } else {
                // STATUS_REAL_TIME_CONNECTION_FAILED
                // STATUS_INTERNAL_ERROR
                ExceptionEvent.send("internet_game", GamesStatusCodes.getStatusString(statusCode));
                SimpleActionDialog.create(R.string.error, new BackToSelectGameCommand(parent(), mInternetGame)).show(mFm, DIALOG);
            }
        }
    };

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
            createGame();

            showWaitingScreen();
            mMultiplayer.invitePlayers(BattleshipActivity.RC_SELECT_PLAYERS, mInternetGame, mOpponent);
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
            createGame();

            showWaitingScreen();
            mMultiplayer.showInvitations(BattleshipActivity.RC_INVITATION_INBOX, mInternetGame, mOpponent);
        }

        @Override
        public void quickGame() {
            if (mKeyLock) {
                Ln.w("keys are locked");
                return;
            }

            mKeyLock = true;
            UiEvent.send("quickGame");
            createGame();

            showWaitingScreen();
            mMultiplayer.quickGame(mInternetGame, mOpponent);
        }
    };

    @NonNull
    private final MultiplayerListener mMultiplayerListener = new MultiplayerListener() {

        @Override
        public void playerLeft() {
            mInternetGame.finish();
            hideWaitingScreen();
        }

        @Override
        public void gameStarted() {
            setScreen(ScreenCreator.newBoardSetupScreen(mInternetGame, mSession));
        }

        @Override
        public void invitationCanceled() {
            hideWaitingScreen();
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

}
