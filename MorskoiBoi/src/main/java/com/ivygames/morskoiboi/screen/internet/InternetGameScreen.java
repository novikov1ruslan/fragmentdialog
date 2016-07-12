package com.ivygames.morskoiboi.screen.internet;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.ivygames.common.analytics.ExceptionEvent;
import com.ivygames.common.analytics.UiEvent;
import com.ivygames.common.dialog.SimpleActionDialog;
import com.ivygames.common.googleapi.ApiClient;
import com.ivygames.common.invitations.InvitationManager;
import com.ivygames.common.invitations.InvitationPresenter;
import com.ivygames.common.multiplayer.MultiplayerHubListener;
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
import com.ivygames.morskoiboi.rt.InternetGameListener;
import com.ivygames.morskoiboi.rt.InternetOpponent;
import com.ivygames.morskoiboi.screen.BackToSelectGameCommand;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.ScreenCreator;
import com.ruslan.fragmentdialog.FragmentAlertDialog;

import org.commons.logger.Ln;

import java.util.ArrayList;

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
    private final InvitationManager mInvitationManager = Dependencies.getInvitationManager();
    @NonNull
    private final GameSettings mSettings = Dependencies.getSettings();
    @NonNull
    private final Rules mRules = Dependencies.getRules();
    @NonNull
    private final Placement mPlacement = PlacementFactory.getAlgorithm();
    @NonNull
    private final MultiplayerHub mMultiplayerHub;

    private InvitationPresenter mInvitationPresenter;
    private Session mSession;

    public InternetGameScreen(@NonNull BattleshipActivity parent, @NonNull MultiplayerHub hub) {
        super(parent);
        mMultiplayerHub = hub;
        mMultiplayerHub.setResultListener(mMultiplayerHubListener);
    }

    @Override
    public View onCreateView(@NonNull ViewGroup container) {
        mLayout = (InternetGameLayout) inflate(R.layout.internet_game, container);
        mLayout.setPlayerName(mSettings.getPlayerName());
        mLayout.setScreenActions(mInternetGameLayoutListener);

        mInvitationPresenter = new InvitationPresenter(mLayout, mInvitationManager);
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
        mInvitationManager.registerInvitationReceiver(mInvitationPresenter);
        mInvitationPresenter.updateInvitations();
    }

    @Override
    public void onStop() {
        super.onStop();
        mInvitationManager.unregisterInvitationReceiver(mInvitationPresenter);
    }

    @Override
    public void onResume() {
        super.onResume();
        mKeyLock = false;
    }

    private void createGame() {
        mInternetGame = new InternetGame(mApiClient, mInternetGameListener);
        InternetOpponent opponent = new InternetOpponent(mInternetGame, getString(R.string.player));
        mInternetGame.setRealTimeMessageReceivedListener(opponent);
        PlayerOpponent player = new PlayerOpponent(fetchPlayerName(), mPlacement, mRules);
        player.setChatListener(parent());
        mSession = new Session(player, opponent);
        Session.bindOpponents(player, opponent);
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

        mMultiplayerHub.handleResult(requestCode, resultCode, data);
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
    private final InternetGameListener mInternetGameListener = new InternetGameListener() {
        @Override
        public void onWaitingForOpponent(Room room) {
            // Show the waiting room UI to track the progress of other players as they enter the room and get connected.
            mMultiplayerHub.showWaitingRoom(room);
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
            mMultiplayerHub.invitePlayers();
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
            mMultiplayerHub.showInvitations();
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
            mInternetGame.quickGame();
        }
    };

    @NonNull
    private final MultiplayerHubListener mMultiplayerHubListener = new MultiplayerHubListener() {

        @Override
        public void handleWaitingRoomResult(int resultCode) {
            hideWaitingScreen();
            if (resultCode == Activity.RESULT_OK) {
                Ln.d("starting game");
                // TODO: call createGame() here
                setScreen(ScreenCreator.newBoardSetupScreen(mInternetGame, mSession));
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
        @Override
        public void handleInvitationInboxResult(int result, @NonNull Intent data) {
            if (result == Activity.RESULT_OK) {
                Invitation invitation = data.getExtras().getParcelable(Multiplayer.EXTRA_INVITATION);
                mInternetGame.accept(invitation);
            } else {
                Ln.d("invitation cancelled - hiding waiting screen; reason=" + result);
                hideWaitingScreen();
            }
            mInvitationManager.loadInvitations();
        }

        // Handle the result of the "Select players UI" we launched when the user
        // clicked the
        // "Invite friends" button. We react by creating a room with those players.
        @Override
        public void handleSelectPlayersResult(int result, @NonNull Intent data) {
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
