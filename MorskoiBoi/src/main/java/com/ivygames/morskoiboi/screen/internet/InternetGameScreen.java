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
import com.ivygames.morskoiboi.AndroidDevice;
import com.ivygames.morskoiboi.BackPressListener;
import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.Dependencies;
import com.ivygames.morskoiboi.GameHandler;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.GoogleApiClientWrapper;
import com.ivygames.morskoiboi.InvitationReceiver;
import com.ivygames.morskoiboi.Placement;
import com.ivygames.morskoiboi.PlayerOpponent;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.RulesFactory;
import com.ivygames.morskoiboi.ai.PlacementFactory;
import com.ivygames.morskoiboi.invitations.InvitationManager;
import com.ivygames.morskoiboi.model.Model;
import com.ivygames.morskoiboi.rt.InternetGame;
import com.ivygames.morskoiboi.rt.InternetGame.InternetGameListener;
import com.ivygames.morskoiboi.rt.InternetOpponent;
import com.ivygames.morskoiboi.rt.InvitationEvent;
import com.ivygames.morskoiboi.screen.BackToSelectGameCommand;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.SimpleActionDialog;
import com.ruslan.fragmentdialog.FragmentAlertDialog;

import org.commons.logger.Ln;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class InternetGameScreen extends BattleshipScreen implements InternetGameLayoutListener,
        InternetGameListener, BackPressListener, InvitationReceiver {
    private static final String TAG = "INTERNET_GAME";
    private static final String DIALOG = FragmentAlertDialog.TAG;

    private static final int MIN_PLAYERS = 2;

    private InternetGame mInternetGame;
    private boolean mKeyLock;
    private InternetGameLayout mLayout;
    private WaitFragment mWaitFragment;

    @NonNull
    private final AndroidDevice mDevice = Dependencies.getDevice();
    @NonNull
    private final GoogleApiClientWrapper mApiClient;
    @NonNull
    private final InvitationManager mInvitationManager;
    @NonNull
    private final GameSettings mSettings = Dependencies.getSettings();
    @NonNull
    private final Rules mRules = RulesFactory.getRules();
    @NonNull
    private final Placement mPlacement = PlacementFactory.getAlgorithm();

    public InternetGameScreen(@NonNull BattleshipActivity parent) {
        super(parent);
        mApiClient = Dependencies.getApiClient();
        mInvitationManager = Dependencies.getInvitationManager();
    }

    @Override
    public View onCreateView(@NonNull ViewGroup container) {
        mLayout = (InternetGameLayout) inflate(R.layout.internet_game, container);
        mLayout.setPlayerName(mSettings.getPlayerName());
        mLayout.setScreenActions(this);
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
        EventBus.getDefault().register(this);
        showInvitationIfHas(mInvitationManager.hasInvitation());
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onEventMainThread(InvitationEvent event) {
        showInvitationIfHas(mInvitationManager.hasInvitation());
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
        mInvitationManager.loadInvitations();
        showWaitingRoom(room);
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
        Intent intent = mApiClient.getSelectOpponentsIntent(InternetGame.MIN_OPPONENTS, InternetGame.MAX_OPPONENTS, false);
        if (mDevice.canResolveIntent(intent)) {
            startActivityForResult(intent, BattleshipActivity.RC_SELECT_PLAYERS);
        }
    }

    private void createGame() {
        mInternetGame = new InternetGame(mApiClient, this);
        InternetOpponent opponent = new InternetOpponent(mInternetGame, getString(R.string.player));
        mInternetGame.setRealTimeMessageReceivedListener(opponent);
        Model.instance.setOpponents(new PlayerOpponent(fetchPlayerName(), mPlacement, mRules), opponent);
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
        Intent intent = mApiClient.getInvitationInboxIntent();
        startActivityForResult(intent, BattleshipActivity.RC_INVITATION_INBOX);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED) {
            Ln.i("reconnect required - returning to select game screen");
            hideWaitingScreen();
            new BackToSelectGameCommand(parent(), mInternetGame).run();
            mApiClient.disconnect();
            return;
        }

        Ln.v("result=" + resultCode);
        switch (requestCode) {
            case BattleshipActivity.RC_SELECT_PLAYERS:
                handleSelectPlayersResult(resultCode, data);
                break;
            case BattleshipActivity.RC_INVITATION_INBOX:
                handleInvitationInboxResult(resultCode, data);
                break;
            case BattleshipActivity.RC_WAITING_ROOM:
                handleWaitingRoomResult(resultCode);
                break;
        }
    }

    private void handleWaitingRoomResult(int resultCode) {
        hideWaitingScreen();
        if (resultCode == Activity.RESULT_OK) {
            Ln.d("starting game");
            setScreen(GameHandler.newBoardSetupScreen(mInternetGame));
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
        Intent intent = mApiClient.getWaitingRoomIntent(room, MIN_PLAYERS);
        startActivityForResult(intent, BattleshipActivity.RC_WAITING_ROOM);
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

        setScreen(GameHandler.newSelectGameScreen());
    }

    protected final void showWaitingScreen() {
        Ln.d("please wait... ");
        mWaitFragment = new WaitFragment();
        mFm.beginTransaction().add(R.id.container, mWaitFragment).commitAllowingStateLoss();
    }

    protected final void hideWaitingScreen() {
        if (isWaitShown()) {
            Ln.d("hiding waiting screen");
            mFm.beginTransaction().remove(mWaitFragment).commitAllowingStateLoss();
            mWaitFragment = null;
        } else {
            Ln.w("waiting screen isn't shown");
        }
    }

    protected final boolean isWaitShown() {
        return mWaitFragment != null;
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
