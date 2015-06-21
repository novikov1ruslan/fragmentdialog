package com.ivygames.morskoiboi.ui;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger.LogLevel;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.appstate.AppStateManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.InvitationBuffer;
import com.google.android.gms.games.multiplayer.Invitations.LoadInvitationsResult;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.ivygames.morskoiboi.AdManager;
import com.ivygames.morskoiboi.BattleshipApplication;
import com.ivygames.morskoiboi.DeviceUtils;
import com.ivygames.morskoiboi.GameConstants;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.achievement.AchievementsManager;
import com.ivygames.morskoiboi.analytics.ExceptionEvent;
import com.ivygames.morskoiboi.analytics.WarningEvent;
import com.ivygames.morskoiboi.model.ChatMessage;
import com.ivygames.morskoiboi.rt.InvitationEvent;
import com.ivygames.morskoiboi.utils.UiUtils;

import org.commons.logger.Ln;
import org.commons.logger.WarningListener;

import java.util.HashSet;
import java.util.Set;

import de.greenrobot.event.EventBus;
import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;

public class BattleshipActivity extends FragmentActivity implements ConnectionCallbacks, OnConnectionFailedListener, OnInvitationReceivedListener {

	interface BackPressListener {

		void onBackPressed();
	}

	interface SignInListener {

		void onSignInSucceeded();
	}

	// Request code used to invoke sign in user interactions.
	private static final int RC_SIGN_IN = 9001;
	private static final int SERVICE_RESOLVE = 9002;

	private static final Configuration CONFIGURATION_LONG = new Configuration.Builder().setDuration(Configuration.DURATION_LONG).build();

	// private FragmentManager mFm;

	private Tracker mGaTracker;
	private boolean mRecreating;
	private GameSettings mSettings;

	/**
	 * volume stream is saved on onResume and restored on onPause
	 */
	private int mVolumeControlStream;
	private final Set<String> mIncomingInvitationIds = new HashSet<String>();

	private GoogleApiClient mGoogleApiClient;

	// Are we currently resolving a connection failure?
	private boolean mResolvingConnectionFailure;

	private AchievementsManager mAchievementsManager;

	private boolean mStarted;
	private ViewGroup mLayout;

	private final OnInvitationReceivedListener mInvitationReceivedListener = new OnInvitationReceivedListener() {

		@Override
		public void onInvitationReceived(Invitation invitation) {
			String displayName = invitation.getInviter().getDisplayName();
			Ln.d("received invitation from: " + displayName);
			showReceivedInvitationCrouton(displayName);
			mIncomingInvitationIds.add(invitation.getInvitationId());
			EventBus.getDefault().post(new InvitationEvent(mIncomingInvitationIds));
		}

		private void showReceivedInvitationCrouton(String displayName) {
			View view = UiUtils.inflateInfoCroutonLayout(getLayoutInflater(), getString(R.string.received_invitation, displayName), mLayout);
			Crouton.make(BattleshipActivity.this, view).setConfiguration(CONFIGURATION_LONG).show();
		}

		@Override
		public void onInvitationRemoved(String invitationId) {
			Ln.d("invitationId=" + invitationId + " withdrawn");
			mIncomingInvitationIds.remove(invitationId);
			EventBus.getDefault().post(new InvitationEvent(mIncomingInvitationIds));
		}
	};

	private final ResultCallback<LoadInvitationsResult> mInvitationsResultCallback = new ResultCallback<LoadInvitationsResult>() {
		@Override
		public void onResult(LoadInvitationsResult list) {
			mIncomingInvitationIds.clear();
			if (list != null && list.getInvitations().getCount() > 0) {
				InvitationBuffer invitations = list.getInvitations();
				Ln.v("loaded " + invitations.getCount() + " invitations");
				for (int i = 0; i < invitations.getCount(); i++) {
					mIncomingInvitationIds.add(invitations.get(i).getInvitationId());
				}
				list.getInvitations().release();
			} else {
				Ln.v("no invitations");
			}
			EventBus.getDefault().post(new InvitationEvent(mIncomingInvitationIds));
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mRecreating = savedInstanceState != null;
		if (mRecreating) {
			// mGaTracker.send(new WarningEvent(GameConstants.GA_ACTION_ANDROID,
			// "recreated").build());
			Ln.i("app is recreating, restart it");
			finish();
			return;
		}

		mGoogleApiClient = createGoogleApiClient();

		if (getResources().getBoolean(R.bool.is_tablet)) {
			Ln.d("device is tablet");
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			Ln.d("device is handset");
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		// Get the GoogleAnalytics singleton. Note that the SDK uses
		// the application context to avoid leaking the current context.
		GoogleAnalytics gaInstance = GoogleAnalytics.getInstance(BattleshipApplication.get());
		if (GameConstants.IS_TEST_MODE) {
			gaInstance.getLogger().setLogLevel(LogLevel.ERROR);
			gaInstance.setDryRun(true);
		}
		mGaTracker = gaInstance.newTracker(GameConstants.ANALYTICS_KEY);
		Ln.setWarningListener(new WarningListener() {

			@Override
			public void onWaring(String message, int level) {
				if (level == Log.WARN) {
					mGaTracker.send(new WarningEvent(message).build());
				} else {
					mGaTracker.send(new ExceptionEvent(message).build());
				}
			}
		});

		mGaTracker.enableAdvertisingIdCollection(true);

		mAchievementsManager = new AchievementsManager(mGoogleApiClient, mGaTracker);

		mSettings = GameSettings.get();

		mLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.battleship, null);
		setContentView(mLayout);
		mContainer = (FrameLayout) mLayout.findViewById(R.id.container);

		// ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);

		setScreen(new MainFragment());

		AdManager.instance.configure(this);

		if (mSettings.shouldAutoSignIn()) {
			Ln.d("should auto-signin - connecting...");
			mGoogleApiClient.connect();
		}

		Ln.i("game fully created");
	}

	// private boolean isGamePlayScreenDislpayed() {
	// return GameplayFragment.TAG.equals(getTopFragment().getTag());
	// }

	private GoogleApiClient createGoogleApiClient() {
		ConnectionCallbacks connectedListener = this;
		OnConnectionFailedListener connectionFailedListener = this;

		GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this, connectedListener, connectionFailedListener);
		builder.addApi(Games.API).addScope(Games.SCOPE_GAMES);
		builder.addApi(AppStateManager.API).addScope(AppStateManager.SCOPE_APP_STATE);
		builder.addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN);
		return builder.build();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		DeviceUtils.printIntent(intent);
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (mRecreating) {
			Ln.v("recreating");
			return;
		}

		mCurrentScreen.onStart();
		mStarted = true;

		Ln.d("UI partially visible - keep scren On");
		keepScreenOn();
		if (mGoogleApiClient.isConnected()) {
			Ln.d("API is connected - register invitation listener");
			registerInvitationListener();
		}
		EventBus.getDefault().register(this);
	}

	private void registerInvitationListener() {
		Games.Invitations.registerInvitationListener(mGoogleApiClient, mInvitationReceivedListener);
		loadInvitations();
	}

	public void loadInvitations() {
		Ln.d("loading invitations...");
		PendingResult<LoadInvitationsResult> invitations = Games.Invitations.loadInvitations(mGoogleApiClient);
		invitations.setResultCallback(mInvitationsResultCallback);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mRecreating) {
			Ln.v("recreating");
			return;
		}

		mCurrentScreen.onResume();
		mResumed = true;

		mVolumeControlStream = getVolumeControlStream();

		// Set the hardware buttons to control the music
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		AdManager.instance.resume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mRecreating) {
			Ln.v("recreating");
			return;
		}

		mCurrentScreen.onPause();
		mResumed = false;

		setVolumeControlStream(mVolumeControlStream);
		AdManager.instance.pause();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mRecreating) {
			Ln.v("recreating");
			return;
		}

		mCurrentScreen.onStop();
		mStarted = false;
		stopKeepingScreenOn();
		if (mGoogleApiClient.isConnected()) {
			Ln.d("API is connected - unregister invitation listener");
			Games.Invitations.unregisterInvitationListener(mGoogleApiClient);
		}
		EventBus.getDefault().unregister(this);
		Ln.d("game fully obscured - stop keeping scren On");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mRecreating) {
			Ln.d("destroyed during recreation - restart");
			mRecreating = false;
			startActivity(getIntent());
			return;
		}

		Crouton.cancelAllCroutons();
		AdManager.instance.destroy();

		mGoogleApiClient.disconnect();
		mGoogleApiClient.unregisterConnectionCallbacks(this);
		mGoogleApiClient.unregisterConnectionFailedListener(this);
		Ln.d("game destroyed");
	}

	@Override
	public void onBackPressed() {
		Ln.v("top screen = " + mCurrentScreen);
		if (mCurrentScreen instanceof MainFragment) {
			if (!mSettings.noAds()) {
				Ln.d("exiting from the game - show interstitial");
				AdManager.instance.showInterstitialAfterExit(this);
				return;
			}
		} else if (mCurrentScreen instanceof BackPressListener) {
			Ln.v("propagating backpress");
			if (mCurrentScreen.isResumed()) {
				((BackPressListener) mCurrentScreen).onBackPressed();
			} else {
				Ln.w("back pressed to fast for " + mCurrentScreen);
			}
			return;
		}

		super.onBackPressed();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (mRecreating) {
			Ln.w("activity result received while recreating - ignore");
			return;
		}

		if (requestCode == RC_SIGN_IN) {
			if (resultCode == RESULT_OK) {
				Ln.d("conection issue is resolved - reconnecting");
				mGoogleApiClient.connect();
			} else {
				Ln.w("conection issue could not be resolved");
				mResolvingConnectionFailure = false;
			}
		} else {
			mCurrentScreen.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Ln.d("connection failed - result: " + connectionResult);

		switch (connectionResult.getErrorCode()) {
		case ConnectionResult.SERVICE_MISSING:
		case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
		case ConnectionResult.SERVICE_DISABLED:
			Ln.w("connection failed: " + connectionResult.getErrorCode());
			Dialog errorDialog = GoogleApiAvailability.getInstance().getErrorDialog(this, connectionResult.getErrorCode(), SERVICE_RESOLVE);
			errorDialog.show();
			return;
		}

		if (mResolvingConnectionFailure) {
			Ln.d("ignoring connection failure; already resolving.");
			return;
		}

		Ln.d("resolving connection failure");
		mResolvingConnectionFailure = BaseGameUtils.resolveConnectionFailure(this, mGoogleApiClient, connectionResult, RC_SIGN_IN, getString(R.string.error)); // TODO:
	}

	@Override
	public void onConnectionSuspended(int cause) {
		Ln.d("connection suspended - trying to reconnect: " + cause);
		mResolvingConnectionFailure = false;
		mGoogleApiClient.connect();
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Ln.d("signed in");
		mResolvingConnectionFailure = false;
		mSettings.enableAutoSignIn();
		Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
		AdManager.instance.setPerson(currentPerson);

		if (TextUtils.isEmpty(mSettings.getPlayerName())) {
			String name = Games.Players.getCurrentPlayer(getApiClient()).getDisplayName();
			Ln.i("player's name is not set - setting to G+ name [" + name + "]");
			mSettings.setPlayerName(name);
		}

		mAchievementsManager.loadAchievements();

		if (mCurrentScreen instanceof SignInListener) {
			((SignInListener) mCurrentScreen).onSignInSucceeded();
		}

		if (mStarted) {
			Ln.d("started - register invitation listener");
			registerInvitationListener();
		}
	}

	/**
	 * Sets the flag to keep this screen on. It's recommended to do that during the handshake when setting up a game, because if the screen turns off, the game
	 * will be cancelled.
	 */
	private void keepScreenOn() {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	/**
	 * Clears the flag that keeps the screen on.
	 */
	private void stopKeepingScreenOn() {
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	public GoogleApiClient getApiClient() {
		return mGoogleApiClient;
	}

	public Tracker getTracker() {
		return mGaTracker;
	}

	@Override
	public void onInvitationReceived(Invitation invitation) {
		Ln.i("invitation received: " + invitation);
	}

	@Override
	public void onInvitationRemoved(String invitationId) {
		Ln.i("invitation withdrawn: " + invitationId);
	}

	public void onEventMainThread(ChatMessage message) {
		if (mStarted) {
			View layout = UiUtils.inflateChatCroutonLayout(getLayoutInflater(), message.getText(), mLayout);
			Crouton.make(this, layout).setConfiguration(CONFIGURATION_LONG).show();
		}
	}

	public boolean hasInvitation() {
		return mIncomingInvitationIds.size() > 0;
	}

	private FrameLayout mContainer;

	private BattleshipFragment mCurrentScreen;
	private boolean mResumed;

	public final void setScreen(Screen screen) {
		View oldView = null;
		if (mCurrentScreen != null) {
			oldView = mCurrentScreen.getView();
			mCurrentScreen.onPause();
			mCurrentScreen.onStop();
			mCurrentScreen.onDestroyView();
			mCurrentScreen.onDestroy();
		}

		mCurrentScreen = (BattleshipFragment) screen;
		mCurrentScreen.onAttach(this);
		mCurrentScreen.onCreate();
		View view = mCurrentScreen.onCreateView(mContainer);
		if (mStarted) {
			mCurrentScreen.onStart();
			if (mResumed) {
				mCurrentScreen.onResume();
			}
		}

		// if (oldView != null) {
		// LayoutTransition layoutTransition = new LayoutTransition();
		// mContainer.setLayoutTransition(layoutTransition);
		// }

		mContainer.addView(view);
		if (oldView != null) {
			mContainer.removeView(oldView);
		}
	}
}
