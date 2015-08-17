package com.ivygames.morskoiboi.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.ivygames.morskoiboi.AdManager;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.GameplaySoundManager;
import com.ivygames.morskoiboi.HandlerOpponent;
import com.ivygames.morskoiboi.PlayerOpponent;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.RulesFactory;
import com.ivygames.morskoiboi.VibratorFacade;
import com.ivygames.morskoiboi.ai.PlacementFactory;
import com.ivygames.morskoiboi.analytics.AnalyticsEvent;
import com.ivygames.morskoiboi.analytics.UiEvent;
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
import com.ivygames.morskoiboi.ui.BattleshipActivity.BackPressListener;
import com.ivygames.morskoiboi.ui.TurnTimer.TimerListener;
import com.ivygames.morskoiboi.ui.view.ChatAdapter;
import com.ivygames.morskoiboi.ui.view.EnemyBoardView.ShotListener;
import com.ivygames.morskoiboi.ui.view.GameplayLayoutListener;
import com.ivygames.morskoiboi.utils.GameUtils;
import com.ruslan.fragmentdialog.AlertDialogBuilder;
import com.ruslan.fragmentdialog.FragmentAlertDialog;

import org.acra.ACRA;
import org.commons.logger.Ln;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import de.greenrobot.event.EventBus;
import de.keyboardsurfer.android.widget.crouton.Crouton;

public class GameplayScreen extends OnlineGameScreen implements BackPressListener {
    private static final String TAG = "GAMEPLAY";

    private static final String DIALOG = FragmentAlertDialog.TAG;

    private static final int LOST_GAME_WITH_REVEAL_DELAY = 5000; // milliseconds
    private static final int LOST_GAME_WO_REVEAL_DELAY = 3000; // milliseconds
    private static final int WON_GAME_DELAY = 3000; // milliseconds

    private static final int VIBRATION_ON_KILL = 500;

    private static final int READY_TO_START = 0;

    private Game mGame;
    private GameplayLayoutInterface mLayout;
    private PlayerOpponent mPlayer;
    private Opponent mEnemy;
    private Handler mUiThreadHandler;
    private GameSettings mSettings;
    private VibratorFacade mVibrator;
    private Board mEnemyPublicBoard;
    private Board mPlayerPrivateBoard;

    private boolean mBackPressEnabled;

    private TurnTimer mTurnTimer;

    private ChatAdapter mChatAdapter;

    private final GameplaySoundManager mSoundManager = new GameplaySoundManager(this);

    private Runnable mBackToSelectGameCommand;

    private int mTimeLeft = READY_TO_START;

    private boolean mOpponentSurrendered;

    private final Rules mRules = RulesFactory.getRules();

    private final Runnable mShowLostScreenCommand = new Runnable() {

        @Override
        public void run() {
            mParent.setScreen(new LostScreen());
        }
    };

    private final TimerListener mTimerListener = new TimerListener() {

        @Override
        public void onTimerExpired() {
            mTurnTimer = null;
            mTimeLeft = READY_TO_START;
            mTimerExpiredCounter++;
            if (mTimerExpiredCounter > 2) {
                mGaTracker.send(new AnalyticsEvent("surrendered_passively").build());
                int penalty = calcSurrenderPenalty();
                Ln.d("player surrender passively with penalty: " + penalty);
                surrender(penalty);
            } else {
                notifyOpponentTurn();
                mEnemy.go();
            }
        }
    };

    private int mTimerExpiredCounter;

    private Intent mMatchStatusIntent;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private final Runnable mShotHangDetectionTask = new Runnable() {
        @Override
        public void run() {
            ACRA.getErrorReporter().handleException(new RuntimeException("shot_hanged"));
        }
    };
    private final Runnable mTurnHangDetectionTask = new Runnable() {
        @Override
        public void run() {
            ACRA.getErrorReporter().handleException(new RuntimeException("turn_hanged"));
        }
    };

    @Override
    public View getView() {
        return (View) mLayout;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMatchStatusIntent = new Intent(getActivity(), InternetService.class);
        AdManager.instance.needToShowInterstitialAfterPlay();

        mSoundManager.prepareSoundPool(getActivity().getAssets());
        mBackPressEnabled = true;
        mPlayer = Model.instance.player;
        mEnemy = Model.instance.opponent;
        mGame = Model.instance.game;
        mEnemyPublicBoard = mPlayer.getEnemyBoard();
        mPlayerPrivateBoard = mPlayer.getBoard();

        mVibrator = new VibratorFacade(this);
        mUiThreadHandler = new Handler();
        mSettings = GameSettings.get();

        mBackToSelectGameCommand = new BackToSelectGameCommand(mParent);

        mMatchStatusIntent.putExtra(InternetService.EXTRA_CONTENT_TITLE, getString(R.string.match_against) + " " + mEnemy.getName());

        Ln.d("game data prepared");
    }

    private Intent getServiceIntent(String contentText) {
        mMatchStatusIntent.putExtra(InternetService.EXTRA_CONTENT_TEXT, contentText);
        return mMatchStatusIntent;
    }

    @Override
    public View onCreateView(ViewGroup container) {
        mLayout = (GameplayLayoutInterface) getLayoutInflater().inflate(R.layout.gameplay, container, false).findViewById(R.id.gameplay_layout);
        if (mGame.getType() != Type.INTERNET) {
            Ln.d("not internet game - hide chat button");
            mLayout.hideChatButton();
        }

        mChatAdapter = new ChatAdapter(getLayoutInflater());

        mLayout.setListener(new GameplayLayoutListener() {

            @Override
            public void onChatClicked() {
                mGaTracker.send(new UiEvent("chat", "open").build());
                new ChatDialog.Builder(mChatAdapter).setName(mPlayer.getName()).setPositiveButton(R.string.send, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mGaTracker.send(new UiEvent("chat", "sent").build());
                        ChatDialog chatDialog = (ChatDialog) dialog;
                        String text = chatDialog.getChatMessage().toString();
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

        });

        // --- for tablet ---
        mLayout.setSound(mSettings.isSoundOn());
        // ------------------

        mLayout.setTotalTime(mGame.getTurnTimeout());
        updateMyStatus();
        mLayout.setPlayerBoard(mPlayerPrivateBoard);
        updateEnemyStatus();
        mLayout.setEnemyBoard(mEnemyPublicBoard);
        mLayout.setAlarmTime(GameplaySoundManager.ALARM_TIME_SECONDS * 1000);
        mLayout.lock();

        mLayout.setPlayerName(mPlayer.getName());
        mLayout.setEnemyName(mEnemy.getName());

        if (mPlayer.isOpponentReady()) {
            boolean isOpponentTurn = mPlayer.isOpponentTurn();
            Ln.d("turn: " + (isOpponentTurn ? "opponent" : "player"));
            if (isOpponentTurn) {
                notifyOpponentTurn();
            }
        } else {
            Ln.d("opponent is still setting board");
            // we have to show this message in the same method (before) mEnemy.setOpponent() is called
            showOpponentSettingBoardNotification();
        }

        mLayout.setShotListener(new BoardShotListener(mPlayer));
        UiProxyOpponent uiProxy = new UiProxyOpponent(mPlayer);
        mHandlerOpponent = new HandlerOpponent(mUiThreadHandler, uiProxy);
        mEnemy.setOpponent(mHandlerOpponent);

        Ln.d("screen is fully created - exchange protocol versions and start bidding");
        mEnemy.setOpponentVersion(Opponent.CURRENT_VERSION);
        mPlayer.setReady();
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
    public void onDestroyView() {
        super.onDestroyView();
        Fragment fragment = mFm.findFragmentByTag(DIALOG);
        if (fragment != null && !getActivity().isFinishing()) {
            Ln.v("removing dialog: " + fragment);
            mFm.beginTransaction().remove(fragment).commitAllowingStateLoss();
        }

        Crouton.cancelAllCroutons();

        stopTurnTimer();
        mHandlerOpponent.stop();
        mGame.finishMatch();
        mSoundManager.release();
        getActivity().stopService(mMatchStatusIntent);
        Ln.d(this + " screen destroyed");

        mHandler.removeCallbacks(mShotHangDetectionTask);
        mHandler.removeCallbacks(mTurnHangDetectionTask);
    }

    @Override
    public void onEventMainThread(GameEvent event) {
        if (event == GameEvent.OPPONENT_LEFT) {
            stopTurnTimer();
            getActivity().stopService(mMatchStatusIntent);
            if (mPlayer.isOpponentReady()) {
                Ln.d("opponent surrendered - notifying player, (shortly game will finish)");
                mGaTracker.send(new AnalyticsEvent("opponent_surrendered").build());
                mOpponentSurrendered = true;
                showOpponentSurrenderedDialog();
            } else {
                super.onEventMainThread(event);
            }
        } else if (event == GameEvent.CONNECTION_LOST) {
            EventBus.getDefault().removeAllStickyEvents();
            stopTurnTimer();
            getActivity().stopService(mMatchStatusIntent);
            if (mGame.hasFinished()) {
                Ln.d(event + " received, but the game has already finished - skipping this event");
            } else {
                super.onEventMainThread(event);
            }
        }

        mHandler.removeCallbacks(mShotHangDetectionTask);
        mHandler.removeCallbacks(mTurnHangDetectionTask);
    }

    private void pauseTurnTimer() {
        if (mTurnTimer != null) {
            mTimeLeft = mTurnTimer.getTimeLeft();
            Ln.v("timer pausing with " + mTimeLeft);
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
        if (mTurnTimer != null) {
            ACRA.getErrorReporter().handleException(new RuntimeException("already resumed"));
            pauseTurnTimer();
        }

        Ln.v("resuming timer for " + mTimeLeft);
        mTurnTimer = new TurnTimer(mTimeLeft, mLayout, mTimerListener, mSoundManager);
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
        String message = getString(R.string.want_to_leave_room, mEnemy.getName());
        showLeaveGameDialog(message);
    }

    private void showLeaveGameDialog(String message) {
        new AlertDialogBuilder().setMessage(message).setPositiveButton(R.string.ok, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mGaTracker.send(new UiEvent("left_from_game", "ok").build());
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
                mGaTracker.send(new UiEvent("surrender", "ok").build());
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
        Collection<Ship> ships = PlacementFactory.getAlgorithm().generateFullFleet();
        int totalHealth = 0;
        for (Ship ship : ships) {
            totalHealth += ship.getSize();
        }
        return totalHealth;
    }

    private class BoardShotListener implements ShotListener {
        private final PlayerOpponent mPlayer;
        private boolean debug_aiming_started;

        BoardShotListener(PlayerOpponent opponent) {
            mPlayer = opponent;
        }

        @Override
        public void onShot(int x, int y) {
            if (!mEnemyPublicBoard.containsCell(x, y)) {
                Ln.d("pressing outside the board: " + x + "," + y);
                return;
            }

            Cell cell = mEnemyPublicBoard.getCell(x, y);
            if (!cell.isEmpty()) {
                Ln.d(cell + " is not empty");
                // TODO: play sound
                return;
            }

            mHandler.postDelayed(mShotHangDetectionTask, 10000);

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
            Ln.v("aiming started");
            // FIXME
            if (debug_aiming_started) {
                Ln.e("aiming started");
            }
            debug_aiming_started = true;
            mSoundManager.playKantrop();
        }

        @Override
        public void onAimingFinished() {
            Ln.v("aiming finished");
            debug_aiming_started = false;
            mSoundManager.stopKantropSound();
        }
    }

    private void notifyOpponentTurn() {
        getActivity().startService(getServiceIntent(getString(R.string.opponent_s_turn)));
        mLayout.enemyTurn();
    }

    /**
     * methods of this class are called in UI thread
     */
    private class UiProxyOpponent implements Opponent {
        private static final int PROTOCOL_VERSION_SUPPORTS_BOARD_REVEAL = 2;

        private final PlayerOpponent mPlayer;

        public UiProxyOpponent(PlayerOpponent opponent) {
            mPlayer = opponent;
        }

        @Override
        public void go() {
            mHandler.removeCallbacks(mTurnHangDetectionTask);
            mPlayer.go();
            notifyPlayerTurn();
            if (mGame.getType() != Type.VS_ANDROID || isResumed()) {
                Ln.d("player's turn - starting timer");
                startTurnTimer(); // for all practical scenarios - start will only be called from here
            } else {
                Ln.d("player's turn, but screen is paused - DO NOT START TIMER");
            }
            hideOpponentSettingBoardNotification();
        }

        private void notifyPlayerTurn() {
            getActivity().startService(getServiceIntent(getString(R.string.your_turn)));
            mLayout.playerTurn();
        }

        private void hideOpponentSettingBoardNotification() {
            Ln.d("hiding \"opponent setting board\" notification");
            mLayout.hideOpponentSettingBoardNotification();
        }

        @Override
        public void onShotResult(final PokeResult result) {
            mHandler.removeCallbacks(mShotHangDetectionTask);

            mPlayer.onShotResult(result);
            mGame.updateWithNewShot(result.ship, result.cell);

            mLayout.removeAim();
            mLayout.setShotResult(result);

            mLayout.invalidateEnemyBoard();
            updateEnemyStatus();

            if (shipSank(result.ship)) {
                Ln.v("enemy ship is sunk!! - shake enemy board");
                mLayout.shakeEnemyBoard();
                mSoundManager.playKillSound();
                mVibrator.vibrate(VIBRATION_ON_KILL);

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
                Ln.v("it's a miss - move is already passed by implementation - update the screen");
                notifyOpponentTurn();
                mSoundManager.playSplash();
                mHandler.postDelayed(mTurnHangDetectionTask, 60000);
            } else {
                Ln.v("it's a hit! - player continues");
                mSoundManager.playHitSound();
            }
        }

        private boolean shipSank(Ship ship) {
            return ship != null;
        }

        @Override
        public void onShotAt(Vector2 aim) {
            mHandler.removeCallbacks(mTurnHangDetectionTask);
            PokeResult result = mPlayer.onShotAtForResult(aim);

            updateMyStatus();

            if (shipSank(result.ship)) {
                // Ln.v("player's ship is sunk: " + result);
                mLayout.shakePlayerBoard();
                mSoundManager.playKillSound();
                mVibrator.vibrate(VIBRATION_ON_KILL);
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
                    resetPlayer();
                    lost(LOST_GAME_WO_REVEAL_DELAY);
                }
            }
        }

        private boolean versionSupportsBoardReveal() {
            return mPlayer.getOpponentVersion() >= PROTOCOL_VERSION_SUPPORTS_BOARD_REVEAL;
        }

        @Override
        public void setOpponent(Opponent opponent) {
            // do nothing
        }

        @Override
        public void bid(final int bid) {
            Ln.d("opponent's bid received: " + bid);
            hideOpponentSettingBoardNotification();
            mPlayer.bid(bid);
            if (mPlayer.isOpponentTurn()) {
                notifyOpponentTurn();
            }
        }

        @Override
        public String getName() {
            return mPlayer.getName();
        }

        @Override
        public void onLost(Board board) {
            if (!mRules.isItDefeatedBoard(mPlayerPrivateBoard)) {
                Ln.v("player private board: " + mPlayerPrivateBoard);
                ACRA.getErrorReporter().handleException(new RuntimeException("lost while not defeated"));
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
            mPlayer.reset();
            // need to de-associate UI from the enemy opponent
            mEnemy.setOpponent(mPlayer);
        }

        @Override
        public void setOpponentVersion(int ver) {
            mPlayer.setOpponentVersion(ver);
        }

        @Override
        public void onNewMessage(String text) {
            mChatAdapter.add(ChatMessage.newEnemyMessage(text));
            mPlayer.onNewMessage(text);
        }

        private void lost(long mseconds) {
            disableBackPress();
            mLayout.lost();
            showLostScreenDelayed(mseconds);
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

    public void updateMyStatus() {
        Collection<Ship> ships = mPlayerPrivateBoard.getShips();
        LinkedList<Ship> workingShips = new LinkedList<Ship>();
        for (Ship ship : ships) {
            if (!ship.isDead()) {
                workingShips.add(ship);
            }
        }
        mLayout.setMyShips(workingShips);
    }

    public void updateEnemyStatus() {
        Collection<Ship> killedShips = mEnemyPublicBoard.getShips();
        Collection<Ship> fullFleet = GameUtils.generateFullHorizontalFleet();
        for (Ship ship : killedShips) {
            Iterator<Ship> iterator = fullFleet.iterator();
            while (iterator.hasNext()) {
                Ship next = iterator.next();
                if (ship.getSize() == next.getSize()) {
                    iterator.remove();
                    break;
                }
            }
        }
        mLayout.setEnemyShips(fullFleet);
    }

    private void startTurnTimer() {
        if (mTurnTimer != null) {
            ACRA.getErrorReporter().handleException(new RuntimeException("already running"));
            stopTurnTimer();
        }

        Ln.d("starting timer");
        mTurnTimer = new TurnTimer(mGame.getTurnTimeout(), mLayout, mTimerListener, mSoundManager);
        mTurnTimer.execute();
    }

    private final Runnable mShowWinCommand = new Runnable() {

        @Override
        public void run() {
            Bundle args = new Bundle();
            args.putBoolean(WinScreen.EXTRA_OPPONENT_SURRENDERED, mOpponentSurrendered);
            args.putString(WinScreen.EXTRA_BOARD, mPlayerPrivateBoard.toJson().toString());
            mParent.setScreen(new WinScreen(args));
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

}
