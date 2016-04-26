package com.ivygames.morskoiboi.screen.gameplay;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.ivygames.common.analytics.AnalyticsEvent;
import com.ivygames.common.analytics.UiEvent;
import com.ivygames.morskoiboi.AdProviderFactory;
import com.ivygames.morskoiboi.BackPressListener;
import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.Bidder;
import com.ivygames.morskoiboi.Cancellable;
import com.ivygames.morskoiboi.Dependencies;
import com.ivygames.morskoiboi.GameHandler;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.PlayerOpponent;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.RulesFactory;
import com.ivygames.morskoiboi.VibratorFacade;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.ChatMessage;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.Game.Type;
import com.ivygames.morskoiboi.model.GameEvent;
import com.ivygames.morskoiboi.model.Model;
import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.rt.InternetService;
import com.ivygames.morskoiboi.screen.BackToSelectGameCommand;
import com.ivygames.morskoiboi.screen.OnlineGameScreen;
import com.ivygames.morskoiboi.screen.SimpleActionDialog;
import com.ivygames.morskoiboi.utils.GameUtils;
import com.ruslan.fragmentdialog.AlertDialogBuilder;
import com.ruslan.fragmentdialog.FragmentAlertDialog;

import org.commons.logger.Ln;
import org.json.JSONException;

import java.util.Collection;

import de.greenrobot.event.EventBus;
import de.keyboardsurfer.android.widget.crouton.Crouton;

import static com.ivygames.common.analytics.ExceptionHandler.reportException;

public class GameplayScreen extends OnlineGameScreen implements BackPressListener {
    private static final String TAG = "GAMEPLAY";

    private static final String DIALOG = FragmentAlertDialog.TAG;

    private static final int LOST_GAME_WITH_REVEAL_DELAY = 5000; // milliseconds
    private static final int LOST_GAME_WO_REVEAL_DELAY = 3000; // milliseconds
    private static final int WON_GAME_DELAY = 3000; // milliseconds

    private static final int VIBRATION_ON_KILL = 500;

    private static final int READY_TO_START = 0;
    public static final int SHOT_HANG_DETECTION_TIMEOUT = 10000; // milliseconds
    public static final int TURN_HANG_DETECTION_TIMEOUT = 60000; // milliseconds
    public static final int ALLOWED_SKIPPED_TURNS = 2;

    @NonNull
    private final Game mGame;
    @NonNull
    private final PlayerOpponent mPlayer;
    @NonNull
    private final Opponent mEnemy;
    @NonNull
    private final Handler mUiThreadHandler = new Handler(Looper.getMainLooper());
    @NonNull
    private final GameSettings mSettings = Dependencies.getSettings();
    @NonNull
    private final VibratorFacade mVibrator;
    @NonNull
    private Board mEnemyPublicBoard;
    @NonNull
    private Board mPlayerPrivateBoard;
    @NonNull
    private final ChatAdapter mChatAdapter;
    @NonNull
    private final GameplaySoundManager mSoundManager;
    @NonNull
    private final Runnable mBackToSelectGameCommand;

    private GameplayLayoutInterface mLayout;
    private boolean mBackPressEnabled;

    private TurnTimer mTurnTimer;
    private int mTimeLeft = READY_TO_START;

    private boolean mOpponentSurrendered;
    @NonNull
    private final Rules mRules = RulesFactory.getRules();
    @NonNull
    private final Runnable mShowLostScreenCommand = new Runnable() {

        @Override
        public void run() {
            setScreen(GameHandler.newLostScreen());
        }
    };
    @NonNull
    private final TurnTimer.TimerListener mTurnTimerListener = new TurnTimer.TimerListener() {

        @Override
        public void onTimerExpired() {
            mTurnTimer = null;
            mTimeLeft = READY_TO_START;
            mTimerExpiredCounter++;
            if (mTimerExpiredCounter > ALLOWED_SKIPPED_TURNS) {
                AnalyticsEvent.send("surrendered_passively");
                int penalty = calcSurrenderPenalty();
                Ln.d("player surrender passively with penalty: " + penalty);
                surrender(penalty);
            } else {
                Ln.d("turn skipped");
                showOpponentTurn();
                startDetectingTurnTimeout();
                mEnemy.go();
            }
        }
    };

    private int mTimerExpiredCounter;

    @NonNull
    private final Intent mMatchStatusIntent;
    @NonNull
    private final Runnable mShotHangDetectionTask = new Runnable() {
        @Override
        public void run() {
            Ln.w("shot_hanged");
            showConnectionLostDialog();
        }
    };
    @NonNull
    private final Runnable mTurnHangDetectionTask = new Runnable() {
        @Override
        public void run() {
            Ln.w("turn_hanged");
            showConnectionLostDialog();
        }
    };

    public GameplayScreen(BattleshipActivity parent) {
        super(parent);
        mMatchStatusIntent = new Intent(parent, InternetService.class);
        AdProviderFactory.getAdProvider().needToShowInterstitialAfterPlay();
        mSoundManager = new GameplaySoundManager(this, (AudioManager) mParent.getSystemService(Context.AUDIO_SERVICE));
        mSoundManager.prepareSoundPool(parent.getAssets());
        mBackPressEnabled = true;
        mPlayer = Model.instance.player;
        mEnemy = Model.instance.opponent;
        mGame = Model.instance.game;
        mEnemyPublicBoard = mPlayer.getEnemyBoard();
        mPlayerPrivateBoard = mPlayer.getBoard();

        mVibrator = new VibratorFacade(mParent);

        mBackToSelectGameCommand = new BackToSelectGameCommand(parent());

        mMatchStatusIntent.putExtra(InternetService.EXTRA_CONTENT_TITLE, getString(R.string.match_against) + " " + mEnemy.getName());
        mChatAdapter = new ChatAdapter(getLayoutInflater());

        Ln.d("game data prepared");
    }

    @NonNull
    @Override
    public View getView() {
        return (View) mLayout;
    }

    private Intent getServiceIntent(String contentText) {
        mMatchStatusIntent.putExtra(InternetService.EXTRA_CONTENT_TEXT, contentText);
        return mMatchStatusIntent;
    }

    @Override
    public View onCreateView(@NonNull ViewGroup container) {
        mLayout = (GameplayLayoutInterface) getLayoutInflater().inflate(R.layout.gameplay, container, false).findViewById(R.id.gameplay_layout);
        mLayout.setShipsSizes(mRules.getAllShipsSizes());
        if (mGame.getType() != Type.INTERNET) {
            Ln.d("not internet game - hide chat button");
            mLayout.hideChatButton();
        }

        mLayout.setLayoutListener(new GameplayLayoutListenerImpl());

        // --- for tablet ---
        mLayout.setSound(mSettings.isSoundOn());
        // ------------------

        updateMyStatus();
        mLayout.setPlayerBoard(mPlayerPrivateBoard);
        updateEnemyStatus();
        mLayout.setEnemyBoard(mEnemyPublicBoard);
        mLayout.setAlarmTime(GameplaySoundManager.ALARM_TIME_SECONDS * 1000);
        mLayout.lock();

        mLayout.setPlayerName(mPlayer.getName());
        mLayout.setEnemyName(mEnemy.getName());

        if (mPlayer.isOpponentReady()) {
            if (mPlayer.isOpponentTurn()) {
                Ln.d("opponent's turn");
                showOpponentTurn();
                startDetectingTurnTimeout();
            }
            else {
                Ln.d("player's turn");
            }
        } else {
            Ln.d("opponent is still setting board");
            // we have to show this message in the same method (before) mEnemy.setOpponent() is called
            showOpponentSettingBoardNotification();
        }

        mLayout.setShotListener(new BoardShotListener(mPlayer));
        UiProxyOpponent uiProxy = new UiProxyOpponent(mPlayer);
        // TODO: make android opponent return in UI thread
        mHandlerOpponent = new HandlerOpponent(mUiThreadHandler, uiProxy);
        mEnemy.setOpponent(mHandlerOpponent);

        Ln.d("screen is fully created - exchange protocol versions and start bidding");
        mEnemy.setOpponentVersion(Opponent.CURRENT_VERSION);
        mPlayer.startBidding();

        Ln.d(this + " screen created");
        return (View) mLayout;
    }

    @Override
    public void onPause() {
        super.onPause();
        Ln.d(this + " is partially obscured - pausing sounds");
//        mSoundManager.autoPause();
        if (mGame.getType() == Type.VS_ANDROID) {
            // timer is not running if it is not player's turn, but cancel it just in case
            pauseTurnTimer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Ln.d(this + " is fully visible - resuming sounds");
//        mSoundManager.autoResume();
        if (/* isTimerPaused() && */!mLayout.isLocked() && mGame.getType() == Type.VS_ANDROID) {
            Ln.v("showing pause dialog");
            mLayout.lock();
            showPauseDialog();
        }
    }

    private boolean isTimerPaused() {
        return mTimeLeft != READY_TO_START;
    }

    private void showPauseDialog() {
        Runnable pauseCommand = new Runnable() {

            @Override
            public void run() {
                Ln.v("continue pressed");
                if (isTimerPaused()) {
                    resumeTurnTimer();
                } else {
                    startTurnTimer();
                }
                mLayout.unLock();
            }
        };
        SimpleActionDialog.create(R.string.pause, R.string.continue_str, pauseCommand).show(mFm, DIALOG);
    }

    private void showOpponentSettingBoardNotification() {
        String message = getString(R.string.opponent_setting_board, mEnemy.getName());
        mLayout.showOpponentSettingBoardNotification(message);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Fragment fragment = mFm.findFragmentByTag(DIALOG);
        if (fragment != null && !mParent.isFinishing()) {
            Ln.v("removing dialog: " + fragment);
            mFm.beginTransaction().remove(fragment).commitAllowingStateLoss();
        }

        Crouton.cancelAllCroutons();

        stopTurnTimer();
        mHandlerOpponent.stop();
        if (mEnemy instanceof Cancellable) {
            ((Cancellable) mEnemy).cancel();
        }
        mSoundManager.release();
        mParent.stopService(mMatchStatusIntent);
        Ln.d(this + " screen destroyed");

        stopDetectingShotTimeout();
        stopDetectingTurnTimeout();
    }

    @Override
    public void onEventMainThread(GameEvent event) {
        if (event == GameEvent.OPPONENT_LEFT) {
            stopTurnTimer();
            mParent.stopService(mMatchStatusIntent);
            if (mPlayer.isOpponentReady()) {
                Ln.d("opponent surrendered - notifying player, (shortly game will finish)");
                AnalyticsEvent.send("opponent_surrendered");
                mOpponentSurrendered = true;
                showOpponentSurrenderedDialog();
            } else {
                super.onEventMainThread(event);
            }
        } else if (event == GameEvent.CONNECTION_LOST) {
            EventBus.getDefault().removeAllStickyEvents();
            stopTurnTimer();
            mParent.stopService(mMatchStatusIntent);
            if (mGame.hasFinished()) {
                Ln.d(event + " received, but the game has already finished - skipping this event");
            } else {
                super.onEventMainThread(event);
            }
        }

        stopDetectingShotTimeout();
        stopDetectingTurnTimeout();
    }

    private void pauseTurnTimer() {
        if (mTurnTimer != null) {
            mTimeLeft = mTurnTimer.getTimeLeft();
            Ln.d("timer pausing with " + mTimeLeft);
            mTurnTimer.cancel(true);
            mTurnTimer = null;
        }
    }

    private void stopTurnTimer() {
        if (mTurnTimer != null) {
            mTurnTimer.cancel(true);
            mTimeLeft = READY_TO_START;
            Ln.v("timer stopped");
            mTurnTimer = null;
        }
    }

    /**
     * only called for android game
     */
    private void resumeTurnTimer() {
        if (!isResumed()) {
            Ln.w("timer resume cancelled due to background");
            return;
        }

        if (mTurnTimer != null) {
            String message = "already resumed";
            reportException(message);
            pauseTurnTimer();
        }

        Ln.v("resuming timer for " + mTimeLeft);
        mTurnTimer = new TurnTimer(mTimeLeft, mLayout, mTurnTimerListener, mSoundManager);
        mTimeLeft = READY_TO_START;
        mTurnTimer.execute();
    }

    @Override
    public void onBackPressed() {
        if (!mBackPressEnabled) {
            Ln.d("backpress temporarily disabled");
            return;
        }

        if (shouldNotifyOpponent()) {
            if (mPlayer.isOpponentReady()) {
                Ln.d("Captain, do you really want to surrender?");
                showSurrenderDialog();
            } else {
                Ln.d("Do you want to abandon the game with XXX?");
                showWantToLeaveRoomDialog();
            }
        } else {
            Ln.d("Are you sure you want to abandon the battle?");
            showAbandonGameDialog();
        }
    }

    private void showAbandonGameDialog() {
        showLeaveGameDialog(getString(R.string.abandon_game_question));
    }

    private void showWantToLeaveRoomDialog() {
        showLeaveGameDialog(getString(R.string.want_to_leave_room, mEnemy.getName()));
    }

    private void showLeaveGameDialog(String message) {
        new AlertDialogBuilder().setMessage(message).setPositiveButton(R.string.ok, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                UiEvent.send("left_from_game", "ok");
                Ln.d("player decided to leave the game");
                mBackToSelectGameCommand.run();
            }
        }).setNegativeButton(R.string.cancel).create().show(mFm, DIALOG);
    }

    private void showSurrenderDialog() {
        final int penalty = calcSurrenderPenalty();
        String message = getString(R.string.surrender_question, -penalty);

        new AlertDialogBuilder().setMessage(message).setPositiveButton(R.string.ok, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                UiEvent.send("surrender", "ok");
                Ln.d("player chose to surrender");
                surrender(penalty);
            }
        }).setNegativeButton(R.string.cancel).create().show(mFm, DIALOG);
    }

    private int calcSurrenderPenalty() {
        int decksLost = getTotalHealth() - getRemainedHealth();
        return decksLost * Game.SURRENDER_PENALTY_PER_DECK + Game.MIN_SURRENDER_PENALTY;
    }

    private int getRemainedHealth() {
        int health = 0;
        for (Ship ship : mPlayerPrivateBoard.getShips()) {
            health += ship.getHealth();
        }
        return health;
    }

    private int getTotalHealth() {
        int[] ships = mRules.getAllShipsSizes();
        int totalHealth = 0;
        for (int ship : ships) {
            totalHealth += ship;
        }
        return totalHealth;
    }

    private class BoardShotListener implements ShotListener {
        @NonNull
        private final PlayerOpponent mPlayer;
        private boolean debug_aiming_started;

        BoardShotListener(@NonNull PlayerOpponent opponent) {
            mPlayer = opponent;
        }

        @Override
        public void onAimingFinished(int x, int y) {
            if (mLayout.isLocked()) {
                // ignore callbacks when layout is locked
                return;
            }

            Ln.v("aiming finished");
            debug_aiming_started = false;
            mSoundManager.stopKantropSound();

            if (mLayout.isLocked()) {
                // ignore callbacks when layout is locked
                return;
            }

            if (!Board.containsCell(x, y)) {
                Ln.d("pressing outside the board: " + x + "," + y);
                return;
            }

            Cell cell = mEnemyPublicBoard.getCell(x, y);
            if (!cell.isEmpty()) {
                Ln.d(cell + " is not empty");
                // TODO: play sound
                return;
            }

            startDetectingShotTimeout();

            stopTurnTimer();
            mTimerExpiredCounter = 0;

            Vector2 aim = Vector2.get(x, y);
            Ln.d("shooting at: " + aim + cell + ", timer cancelled, locking board");
            mLayout.lock();
            mLayout.setAim(aim);
            mSoundManager.playWhistleSound();
            mPlayer.shoot(x, y);
        }

        @Override
        public void onAimingStarted() {
            if (mLayout.isLocked()) {
                // ignore callbacks when layout is locked
                return;
            }

            Ln.v("aiming started");
            // FIXME
            if (debug_aiming_started) {
                Ln.e("aiming started");
            }
            debug_aiming_started = true;
            mSoundManager.playKantrop();
        }

    }

    private void showOpponentTurn() {
        mParent.startService(getServiceIntent(getString(R.string.opponent_s_turn)));
        mLayout.enemyTurn();
    }

    private void showConnectionLostDialog() {
        SimpleActionDialog.create(R.string.connection_lost, mBackToSelectGameCommand).show(mFm, DIALOG);
    }

    /**
     * methods of this class are called in UI thread
     */
    private class UiProxyOpponent implements Opponent {

        @NonNull
        private final PlayerOpponent mPlayer;

        public UiProxyOpponent(@NonNull PlayerOpponent opponent) {
            mPlayer = opponent;
        }

        @Override
        public void go() {
            mPlayer.go();
            showPlayerTurn();
            if (mGame.getType() != Type.VS_ANDROID || isResumed()) {
                Ln.d("player's turn - starting timer");
                startTurnTimer(); // for all practical scenarios - start will only be called from here
            } else {
                Ln.d("player's turn, but screen is paused - DO NOT START TIMER");
            }
            hideOpponentSettingBoardNotification();
        }

        private void showPlayerTurn() {
            mParent.startService(getServiceIntent(getString(R.string.your_turn)));
            mLayout.playerTurn();
        }

        private void hideOpponentSettingBoardNotification() {
            Ln.d("hiding \"opponent setting board\" notification");
            mLayout.hideOpponentSettingBoardNotification();
        }

        @Override
        public void onShotResult(@NonNull final PokeResult result) {
            stopDetectingShotTimeout();

            Ln.v(result);
            mPlayer.onShotResult(result);
            mGame.updateWithNewShot(result.ship, result.cell);

            mLayout.removeAim();
            mLayout.setShotResult(result);

            mLayout.invalidateEnemyBoard();

            // TODO: call this only if ship sank
            updateEnemyStatus();

            if (shipSank(result.ship)) {
                Ln.v("enemy ship is sunk!! - shake enemy board");
                mLayout.shakeEnemyBoard();
                mSoundManager.playKillSound();
                vibrate(VIBRATION_ON_KILL);

                if (mRules.isItDefeatedBoard(mEnemyPublicBoard)) {
                    Ln.d("enemy has lost!!!");
                    disableBackPress();

                    mGame.setTimeSpent(mLayout.getUnlockedTime());
                    mEnemy.onLost(mPlayerPrivateBoard);
                    resetPlayer();

                    mLayout.win();
                    showWinScreenDelayed();
                }
            } else if (result.cell.isMiss()) {
                Ln.d(mPlayer + ": I missed - passing the turn to " + mEnemy);
                mSoundManager.playSplash();
                showOpponentTurn();
                startDetectingTurnTimeout();
                mEnemy.go();
            } else {
                Ln.v("it's a hit! - player continues");
                mSoundManager.playHitSound();
            }
        }

        private boolean shipSank(Ship ship) {
            return ship != null;
        }

        @Override
        public void onShotAt(@NonNull Vector2 aim) {
            stopDetectingTurnTimeout();
            PokeResult result = mPlayer.onShotAtForResult(aim);

            updateMyStatus();

            if (shipSank(result.ship)) {
                // Ln.v("player's ship is sunk: " + result);
                mLayout.shakePlayerBoard();
                mSoundManager.playKillSound();
                vibrate(VIBRATION_ON_KILL);
            } else if (result.cell.isMiss()) {
                // Ln.v("opponent misses: " + result);
                mSoundManager.playSplash();
            } else {
                // Ln.v("player's ship is hit: " + result);
                mLayout.invalidatePlayerBoard();
                mSoundManager.playHitSound();
            }

            // If the opponent's version does not support board reveal, just switch screen in 3 seconds. In the later version of the protocol opponent
            // notifies about players defeat sending his board along.
            if (!versionSupportsBoardReveal()) {
                if (mRules.isItDefeatedBoard(mPlayerPrivateBoard)) {
                    Ln.v("opponent version doesn't support board reveal = " + mPlayer.getOpponentVersion());
                    AnalyticsEvent.send("reveal_not_supported");
                    resetPlayer();
                    lost(LOST_GAME_WO_REVEAL_DELAY);
                }
            }
        }

        private boolean versionSupportsBoardReveal() {
            return mPlayer.getOpponentVersion() >= GameUtils.PROTOCOL_VERSION_SUPPORTS_BOARD_REVEAL;
        }

        @Override
        public void setOpponent(@NonNull Opponent opponent) {
            // do nothing
        }

        @Override
        public void onEnemyBid(final int bid) {
            Ln.d("opponent's bid received: " + bid);
            hideOpponentSettingBoardNotification();
            mPlayer.onEnemyBid(bid);
            if (mPlayer.isOpponentTurn()) {
                showOpponentTurn();
                startDetectingTurnTimeout();
            }
        }

        @Override
        public String getName() {
            return mPlayer.getName();
        }

        @Override
        public void onLost(@NonNull Board board) {
            if (!mRules.isItDefeatedBoard(mPlayerPrivateBoard)) {
                Ln.v("player private board: " + mPlayerPrivateBoard);
                reportException("lost while not defeated");
            }
            // revealing the enemy board
            mEnemyPublicBoard = board;
            updateEnemyStatus();
            mLayout.setEnemyBoard(mEnemyPublicBoard);
            resetPlayer();
            lost(LOST_GAME_WITH_REVEAL_DELAY);
        }

        private void resetPlayer() {
            Ln.d("match is over - blocking the player for further messages until start of the next round");
            copyPlayerBoard();
            copyEnemyBoard();
            mPlayer.reset(new Bidder().newBid());
            // need to de-associate UI from the enemy opponent
            mEnemy.setOpponent(mPlayer);
        }

        @Override
        public void setOpponentVersion(int ver) {
            mPlayer.setOpponentVersion(ver);
        }

        @Override
        public void onNewMessage(@NonNull String text) {
            mChatAdapter.add(ChatMessage.newEnemyMessage(text));
            mPlayer.onNewMessage(text);
        }

        private void lost(long ms) {
            disableBackPress();
            mLayout.lost();
            showLostScreenDelayed(ms);
        }

        private void showLostScreenDelayed(long mseconds) {
            mUiThreadHandler.postDelayed(mShowLostScreenCommand, mseconds);
        }

        private void disableBackPress() {
            Ln.d("disabling backpress");
            mBackPressEnabled = false;
        }

        @Override
        public String toString() {
            return mPlayer.toString();
        }

    }

    private void copyPlayerBoard() {
        try {
            mPlayerPrivateBoard = Board.fromJson(mPlayerPrivateBoard.toJson());
        } catch (JSONException je) {
            Ln.e(je, "could not copy player's board");
        }
    }

    private void copyEnemyBoard() {
        try {
            mEnemyPublicBoard = Board.fromJson(mEnemyPublicBoard.toJson());
        } catch (JSONException je) {
            Ln.e(je, "could not copy enemy's board");
        }
    }

    private void vibrate(int duration) {
        if (mSettings.isVibrationOn() && isResumed()) {
            mVibrator.vibrate(duration);
        }
    }

    private void stopDetectingShotTimeout() {
        mUiThreadHandler.removeCallbacks(mShotHangDetectionTask);
    }

    private void startDetectingShotTimeout() {
        mUiThreadHandler.postDelayed(mShotHangDetectionTask, SHOT_HANG_DETECTION_TIMEOUT);
    }

    private void stopDetectingTurnTimeout() {
        mUiThreadHandler.removeCallbacks(mTurnHangDetectionTask);
    }

    private void startDetectingTurnTimeout() {
        mUiThreadHandler.postDelayed(mTurnHangDetectionTask, TURN_HANG_DETECTION_TIMEOUT);
    }

    public void updateMyStatus() {
        mLayout.updateMyWorkingShips(GameUtils.getWorkingShips(mPlayerPrivateBoard.getShips()));
    }

    private void updateEnemyStatus() {
        Collection<Ship> killedShips = mEnemyPublicBoard.getShips();
        Collection<Ship> fleet = GameUtils.generateShipsForSizes(mRules.getAllShipsSizes());
        for (Ship ship : killedShips) {
            GameUtils.removeShipFromFleet(fleet, ship);
        }
        mLayout.updateEnemyWorkingShips(fleet);
    }

    private void startTurnTimer() {
        if (mTurnTimer != null) {
            reportException("already running");
            stopTurnTimer();
        }

        Ln.d("starting timer");
        mTurnTimer = new TurnTimer(mGame.getTurnTimeout(), mLayout, mTurnTimerListener, mSoundManager);
        mTurnTimer.execute();
    }

    private final Runnable mShowWinCommand = new Runnable() {

        @Override
        public void run() {
            setScreen(GameHandler.newWinScreen(mPlayerPrivateBoard.getShips(), mOpponentSurrendered));
        }
    };

    private HandlerOpponent mHandlerOpponent;

    private void showWinScreenDelayed() {
        mUiThreadHandler.postDelayed(mShowWinCommand, WON_GAME_DELAY);
    }

    private void showOpponentSurrenderedDialog() {
        SimpleActionDialog.create(R.string.opponent_surrendered, mShowWinCommand).show(mFm, DIALOG);
    }

    private void surrender(final int penalty) {
        mSettings.setProgressPenalty(mSettings.getProgressPenalty() + penalty);
        mBackToSelectGameCommand.run();
    }

    @Override
    public String toString() {
        return TAG + debugSuffix();
    }

    private class GameplayLayoutListenerImpl implements GameplayLayoutListener {

        @Override
        public void onChatClicked() {
            UiEvent.send("chat", "open");
            new ChatDialog.Builder(mChatAdapter).setName(mPlayer.getName()).setPositiveButton(R.string.send, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    UiEvent.send("chat", "sent");
                    String text = ((ChatDialog) dialog).getChatMessage().toString();
                    if (TextUtils.isEmpty(text)) {
                        Ln.d("chat text is empty - not sending");
                    } else {
                        mChatAdapter.add(ChatMessage.newMyMessage(text));
                        mEnemy.onNewMessage(text);
                    }
                }
            }).setNegativeButton(R.string.cancel).create().show(mFm, DIALOG);
        }

        @Override
        public void onSoundChanged() {
            boolean on = !mSettings.isSoundOn();
            mSettings.setSound(on);
            mLayout.setSound(on);
            if (!on) {
                mSoundManager.stopPlaying();
            }
        }

    }
}
