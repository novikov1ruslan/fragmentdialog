package com.ivygames.morskoiboi.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.ivygames.morskoiboi.VibratorFacade;
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
import com.ivygames.morskoiboi.ui.view.GameplayLayout;
import com.ivygames.morskoiboi.ui.view.GameplayLayout.GameplayLayoutListener;
import com.ivygames.morskoiboi.ui.view.InfoCroutonLayout;
import com.ivygames.morskoiboi.utils.UiUtils;
import com.ruslan.fragmentdialog.AlertDialogBuilder;
import com.ruslan.fragmentdialog.FragmentAlertDialog;

import org.acra.ACRA;
import org.commons.logger.Ln;

import de.greenrobot.event.EventBus;
import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;

public class GameplayFragment extends OnlineGameFragment implements BackPressListener {
	private static final String TAG = "GAMEPLAY";

	private static final String DIALOG = FragmentAlertDialog.TAG;

	private static final int LOST_GAME_WITH_REVEAL_DELAY = 5000; // milliseconds
	private static final int LOST_GAME_WO_REVEAL_DELAY = 3000; // milliseconds
	private static final int WON_GAME_DELAY = 3000; // milliseconds

	private static final int VIBRATION_ON_KILL = 500;

	private static final Configuration CONFIGURATION_INFINITE = new Configuration.Builder().setDuration(Configuration.DURATION_INFINITE).build();

	private static final int READY_TO_START = 0;

	private Game mGame;
	private GameplayLayout mLayout;
	private PlayerOpponent mPlayer;
	private Opponent mEnemy;
	private Handler mUiThreadHandler;
	private GameSettings mSettings;
	private VibratorFacade mVibrator;
	private Board mEnemyPublicBoard;
	private Board mPlayerPrivateBoard;

	private Crouton mOpponentSettingBoardCrouton;

	private boolean mBackPressEnabled;

	private TurnTimer mTurnTimer;

	private ChatAdapter mChatAdapter;

	private final GameplaySoundManager mSoundManager = new GameplaySoundManager(this);

	private Runnable mBackToSelectGameCommand;

	private int mTimeLeft = READY_TO_START;

	private boolean mOpponentSurrendered;

	private final Runnable mShowLostScreenCommand = new Runnable() {

		@Override
		public void run() {
			mParent.setScreen(new LostFragment());
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

	private boolean mPlayerTurn;

	@Override
	public View getView() {
		return mLayout;
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
		mLayout = (GameplayLayout) getLayoutInflater().inflate(R.layout.gameplay, container, false);
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
							Ln.d("chat text is empty - not senting");
						} else {
							mChatAdapter.add(ChatMessage.newMyMessage(text));
							mEnemy.onNewMessage(text);
						}
					}
				}).setNegativeButton(R.string.cancel).create().show(mFm, DIALOG);
			}

			@Override
			public void onSoundCahnged() {
				boolean on = !mSettings.isSoundOn();
				mSettings.setSound(on);
				mLayout.setSound(on);
			}

			@Override
			public void onVibrationChanged() {
				boolean on = !mSettings.isVibrationOn();
				mSettings.setVibration(on);
				mLayout.setVibration(on);
				mVibrator.vibrate(300);
			}
		});

		// --- for tablet ---
		mLayout.setSound(mSettings.isSoundOn());
		if (mVibrator.hasVibrator()) {
			Ln.v("show vibration setting setting");
			mLayout.setVibration(mSettings.isVibrationOn());
		} else {
			Ln.v("device does not support vibration - hide setting");
			mLayout.hideVibrationSetting();
		}
		// ------------------

		mLayout.setTotalTime(mGame.getTurnTimeout());
		mLayout.setPlayerBoard(mPlayerPrivateBoard);
		mLayout.setEnemyBoard(mEnemyPublicBoard);
		mLayout.setAlarmTime(GameplaySoundManager.ALARM_TIME_SECONDS);
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
		return mLayout;
	}

	@Override
	public void onPause() {
		super.onPause();
		Ln.d(this + " is partially obscured - pausing sounds");
		mSoundManager.autoPause();
		if (mGame.getType() == Type.VS_ANDROID) {
			// timer is not running if it is not plyaer's turn, but cancel it just in case
			pauseTimer();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		Ln.d(this + " is fully visible - resuming sounds");
		mSoundManager.autoResume();
		if (/* isTimerPaused() && */mPlayerTurn && mGame.getType() == Type.VS_ANDROID) {
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
					resumeTimer();
				} else {
					startTimer();
				}
				mLayout.unLock();
			}
		};
		SimpleActionDialog.create(R.string.pause, R.string.continue_str, pauseCommand).show(mFm, DIALOG);
	}

	private void showOpponentSettingBoardNotification() {
		String message = getString(R.string.opponent_setting_board, mEnemy.getName());
		InfoCroutonLayout layout = UiUtils.inflateInfoCroutonLayout(getLayoutInflater(), message, mLayout);
		mOpponentSettingBoardCrouton = Crouton.make(getActivity(), layout).setConfiguration(CONFIGURATION_INFINITE);
		mOpponentSettingBoardCrouton.show();
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

		stopTimer();
		mHandlerOpponent.stop();
		mGame.finishMatch();
		mSoundManager.release();
		getActivity().stopService(mMatchStatusIntent);
		Ln.d(this + " screen destroyed");
	}

	@Override
	public void onEventMainThread(GameEvent event) {
		if (event == GameEvent.OPPONENT_LEFT) {
			stopTimer();
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
			stopTimer();
			getActivity().stopService(mMatchStatusIntent);
			if (mGame.hasFinished()) {
				Ln.d(event + " received, but the game has already finished - skipping this event");
			} else {
				super.onEventMainThread(event);
			}
		}
	}

	private void pauseTimer() {
		if (mTurnTimer != null) {
			mTimeLeft = mTurnTimer.getTimeLeft();
			Ln.v("timer pausing with " + mTimeLeft);
			mTurnTimer.cancel(true);
			mTurnTimer = null;
		}
	}

	private void stopTimer() {
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
	private void resumeTimer() {
		if (mTurnTimer != null) {
			ACRA.getErrorReporter().handleException(new RuntimeException("already resumed"));
			pauseTimer();
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
				Ln.d("Capitan, do you really want to surrender?");
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
		int decksLost = Board.TOTAL_HEALTH - mPlayerPrivateBoard.getHealth();
		return decksLost * Game.SURRENDER_PENALTY_PER_DECK + Game.MIN_SURRENDER_PENALTY;
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

			stopTimer();
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
		mPlayerTurn = false;
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
			mPlayer.go();
			notifyPlayerTurn();
			if (mGame.getType() != Type.VS_ANDROID || isResumed()) {
				Ln.d("player's turn - starting timer");
				startTimer(); // for all practical scenarios - start will only be called from here
			} else {
				Ln.d("player's turn, but tragment is paused - DO NOT START TIMER");
			}
			hideOpponentSettingBoardNotification();
		}

		private void notifyPlayerTurn() {
			getActivity().startService(getServiceIntent(getString(R.string.your_turn)));
			mLayout.playerTurn();
			mPlayerTurn = true;
		}

		private void hideOpponentSettingBoardNotification() {
			if (mOpponentSettingBoardCrouton != null) {
				Ln.d("hiding \"opponent setting board\" crouton");
				Crouton.hide(mOpponentSettingBoardCrouton);
				mOpponentSettingBoardCrouton = null;
			}
		}

		@Override
		public void onShotResult(final PokeResult result) {
			mPlayer.onShotResult(result);
			mGame.updateWithNewShot(result.ship, result.cell);

			mLayout.removeAim();
			mLayout.setShotResult(result);

			mLayout.invalidateEnemyBoard();
			mLayout.updateEnemyStatus();

			if (shipSank(result.ship)) {
				Ln.v("enemy ship is sunk!! - shake enemy board");
				mLayout.shakeEnemyBoard();
				mSoundManager.playKillSound();
				mVibrator.vibrate(VIBRATION_ON_KILL);

				if (Board.isItDefeatedBoard(mEnemyPublicBoard)) {
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
			PokeResult result = mPlayer.onShotAtForResult(aim);

			mLayout.updateMyStatus();

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
				Ln.v("opponent version doesn't support board reveal = " + mPlayer.getOpponentVersion());
				if (Board.isItDefeatedBoard(mPlayerPrivateBoard)) {
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
			if (!Board.isItDefeatedBoard(mPlayerPrivateBoard)) {
				Ln.v("player privaete board: " + mPlayerPrivateBoard);
				ACRA.getErrorReporter().handleException(new RuntimeException("lost whle not defeated"));
			}
			// revealing the enemy board
			mEnemyPublicBoard = board;
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

	private void startTimer() {
		if (mTurnTimer != null) {
			ACRA.getErrorReporter().handleException(new RuntimeException("already running"));
			stopTimer();
		}

		Ln.d("starting timer");
		mTurnTimer = new TurnTimer(mGame.getTurnTimeout(), mLayout, mTimerListener, mSoundManager);
		mTurnTimer.execute();
	}

	private final Runnable mShowWinCommand = new Runnable() {

		@Override
		public void run() {
			WinFragment fragment = new WinFragment();
			Bundle args = new Bundle();
			args.putBoolean(WinFragment.EXTRA_OPPONENT_SURRENDERED, mOpponentSurrendered);
			args.putString(WinFragment.EXTRA_BOARD, mPlayerPrivateBoard.toJson().toString());
			fragment.setArguments(args);
			mParent.setScreen(fragment);
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
