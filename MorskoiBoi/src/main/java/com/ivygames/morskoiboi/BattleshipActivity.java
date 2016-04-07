package com.ivygames.morskoiboi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.ivygames.morskoiboi.achievement.AchievementsManager;
import com.ivygames.morskoiboi.billing.PurchaseManager;
import com.ivygames.morskoiboi.billing.PurchaseStatusListener;
import com.ivygames.morskoiboi.billing.PurchaseUtils;
import com.ivygames.morskoiboi.model.ChatMessage;
import com.ivygames.morskoiboi.progress.ProgressManager;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.main.MainScreen;
import com.ivygames.morskoiboi.utils.UiUtils;
import com.ruslan.fragmentdialog.FragmentAlertDialog;

import org.commons.logger.Ln;

import de.greenrobot.event.EventBus;
import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;

public class BattleshipActivity extends Activity implements ConnectionCallbacks,
        OnConnectionFailedListener,
        InvitationManager.InvitationReceivedListener, PurchaseStatusListener {

    public static final int RC_SELECT_PLAYERS = 10000;
    public static final int RC_INVITATION_INBOX = 10001;
    public final static int RC_WAITING_ROOM = 10002;
    public static final int RC_ENSURE_DISCOVERABLE = 3;

    // Request code used to invoke sign in user interactions.
    private static final int RC_SIGN_IN = 9001;
    public static final int RC_UNUSED = 0;
    public static final int PLUS_ONE_REQUEST_CODE = 20001;
    public static final int RC_ENABLE_BT = 2;
    public static final int RC_PURCHASE = 10003;

    private static final int SERVICE_RESOLVE = 9002;
    private final InvitationManager mInvitationManager = new InvitationManager(this);
    private final PurchaseManager mPurchaseManager = new PurchaseManager(this);

    private static final Configuration CONFIGURATION_LONG = new Configuration.Builder().setDuration(Configuration.DURATION_LONG).build();

    private boolean mRecreating;
    private GameSettings mSettings;

    /**
     * volume stream is saved on onResume and restored on onPause
     */
    private int mVolumeControlStream;

    private GoogleApiClientWrapper mGoogleApiClient;

    // Are we currently resolving a connection failure?
    private boolean mResolvingConnectionFailure;

    private AchievementsManager mAchievementsManager;
    private ProgressManager mProgressManager;

    private boolean mStarted;
    private ViewGroup mLayout;
    private FrameLayout mContainer;

    private BattleshipScreen mCurrentScreen;
    private View mTutView;

    private boolean mResumed;
    private MusicPlayer mMusicPlayer;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecreating = savedInstanceState != null;
        if (mRecreating) {
            Ln.i("app is recreating, restart it");
            finish();
            return;
        }
        mMusicPlayer = MusicPlayer.create(this, R.raw.intro_music);

        mGoogleApiClient = GoogleApiFactory.getApiClient();
        mGoogleApiClient.setConnectionCallbacks(this);
        mGoogleApiClient.setOnConnectionFailedListener(this);

        if (DeviceUtils.isTablet(getResources())) {
            Ln.d("device is tablet");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            Ln.d("device is handset");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        mAchievementsManager = new AchievementsManager(mGoogleApiClient);
        mProgressManager = new ProgressManager(mGoogleApiClient);

        mSettings = GameSettings.get();

        Ln.d("google play services available = " + DeviceUtils.isGoogleServicesAvailable(this));

        mLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.battleship, null);
        setContentView(mLayout);
        mContainer = (FrameLayout) mLayout.findViewById(R.id.container);

        setScreen(new MainScreen(this, getApiClient()));

        if (mSettings.shouldAutoSignIn()) {
            Ln.d("should auto-signin - connecting...");
            mGoogleApiClient.connect();
        }

        if (mSettings.noAds()) {
            hideAds();
        } else {
            AdProviderFactory.init(this);
            if (DeviceUtils.isGoogleServicesAvailable(this) && PurchaseUtils.isBillingAvailable(getPackageManager())) {
                mPurchaseManager.query(this);
            } else {
                Ln.e("gpgs_not_available");
                hideNoAdsButton();
            }
        }

//        FacebookSdk.sdkInitialize(getApplicationContext());
        Ln.i("game fully created");
    }

    public void playMusic(int music) {
        mMusicPlayer.play(music);
    }

    public void stopMusic() {
        mMusicPlayer.stop();
    }

    private void hideNoAdsButton() {
        if (mCurrentScreen instanceof MainScreen) {
            ((MainScreen) mCurrentScreen).hideNoAdsButton();
        }
    }

    public void hideAds() {
        AdProviderFactory.getAdProvider().destroy();
        AdProviderFactory.setAdProvider(new NoAdsAdProvider());
        findViewById(R.id.banner).setVisibility(View.GONE);
        hideNoAdsButton();
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

        Ln.d("UI partially visible - keep screen On");
        keepScreenOn();
        if (mGoogleApiClient.isConnected()) {
            Ln.d("API is connected - register invitation listener");
            mInvitationManager.registerInvitationListener(mGoogleApiClient);
        }
        EventBus.getDefault().register(this);
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
        AdProviderFactory.getAdProvider().resume(this);

        mMusicPlayer.play(mCurrentScreen.getMusic());
//        AppEventsLogger.activateApp(this); // #FB
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mRecreating) {
            Ln.v("recreating");
            return;
        }
        Ln.v("pausing");

        mCurrentScreen.onPause();
        mResumed = false;

        setVolumeControlStream(mVolumeControlStream);
        AdProviderFactory.getAdProvider().pause();

        mMusicPlayer.pause();
//        AppEventsLogger.deactivateApp(this);
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
            mGoogleApiClient.unregisterInvitationListener();
        }
        EventBus.getDefault().unregister(this);
        Ln.d("game fully obscured - stop keeping screen On");
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
        AdProviderFactory.getAdProvider().destroy();

        mPurchaseManager.destroy();

        mGoogleApiClient.disconnect();
        mGoogleApiClient.unregisterConnectionCallbacks(this);
        mGoogleApiClient.unregisterConnectionFailedListener(this);

        mMusicPlayer.release();
        Ln.d("game destroyed");
    }

    public void dismissTutorial() {
        if (mTutView == null) {
            Ln.d("no tutorial view to remove");
            return;
        }

        Ln.v("tutorial view present - removing");
        mContainer.removeView(mTutView);
        mTutView = null;
    }

    public void showTutorial(View view) {
        if (mTutView == null) {
            mTutView = view;
            if (mTutView != null) {
                mContainer.addView(mTutView);
            }
        } else {
            Ln.d("tutorial view already shown: " + mTutView);
        }
    }

    @Override
    public void onBackPressed() {
        Ln.v("top screen = " + mCurrentScreen);

        if (mTutView != null) {
            Ln.v("tutorial view present - removing");
            mContainer.removeView(mTutView);
            mTutView = null;
            return;
        }

        if (mCurrentScreen instanceof BackPressListener) {
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
                Ln.d("connection issue is resolved - reconnecting");
                mGoogleApiClient.connect();
            } else {
                Ln.w("connection issue could not be resolved");
                mResolvingConnectionFailure = false;
            }
        } else if (requestCode == RC_PURCHASE) {
            mPurchaseManager.onActivityResult(requestCode, resultCode, data);
        } else {
            mCurrentScreen.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
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
        // TODO:
        mResolvingConnectionFailure = mGoogleApiClient.resolveConnectionFailure(this, connectionResult, RC_SIGN_IN, getString(R.string.error));
        Ln.d("has resolution = " + mResolvingConnectionFailure);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Ln.d("connection suspended - trying to reconnect: " + GpgsUtils.connectionCauseToString(cause));
        // GoogleApiClient will automatically attempt to restore the connection.
        // Applications should disable UI components that require the service, and wait for a call to onConnected(Bundle) to re-enable them.
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Ln.d("signed in");
        mResolvingConnectionFailure = false;
        mSettings.enableAutoSignIn();

        if (TextUtils.isEmpty(mSettings.getPlayerName())) {
            String name = mGoogleApiClient.getDisplayName();
            Ln.i("player's name is not set - setting to G+ name [" + name + "]");
            mSettings.setPlayerName(name);
        }

        mAchievementsManager.loadAchievements();
        mProgressManager.loadProgress();

        if (mCurrentScreen instanceof SignInListener) {
            ((SignInListener) mCurrentScreen).onSignInSucceeded();
        }

        if (mStarted) {
            Ln.d("started - register invitation listener");
            mInvitationManager.registerInvitationListener(mGoogleApiClient);
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

    public GoogleApiClientWrapper getApiClient() {
        return mGoogleApiClient;
    }

    public void onEventMainThread(ChatMessage message) {
        if (mStarted) {
            View layout = UiUtils.inflateChatCroutonLayout(getLayoutInflater(), message.getText(), mLayout);
            Crouton.make(this, layout).setConfiguration(CONFIGURATION_LONG).show();
        }
    }

    public final void setScreen(BattleshipScreen screen) {
        View oldView = null;

        if (mCurrentScreen != null) {
            if (mCurrentScreen.getMusic() != screen.getMusic()) {
                mMusicPlayer.stop();
            }

            oldView = mCurrentScreen.getView();
            mCurrentScreen.onPause();
            mCurrentScreen.onStop();
            mCurrentScreen.onDestroy();
        }

        mCurrentScreen = screen;
        View view = mCurrentScreen.onCreateView(mContainer);

        mContainer.addView(view);
        if (oldView != null) {
            mContainer.removeView(oldView);
        }

        if (mStarted) {
            mCurrentScreen.onStart();
            if (mResumed) {
                mCurrentScreen.onResume();
            }
        }

        mMusicPlayer.play(mCurrentScreen.getMusic());
    }

    @Override
    public void showReceivedInvitationCrouton(String displayName) {
        View view = UiUtils.inflateInfoCroutonLayout(getLayoutInflater(), getString(R.string.received_invitation, displayName), mLayout);
        Crouton.make(BattleshipActivity.this, view).setConfiguration(CONFIGURATION_LONG).show();
    }

    public InvitationManager getInvitationManager() {
        return mInvitationManager;
    }

    public PurchaseManager getPurchaseManager() {
        return mPurchaseManager;
    }

    @Override
    public void onPurchaseFailed() {
        FragmentAlertDialog.showNote(getFragmentManager(), FragmentAlertDialog.TAG, R.string.purchase_error);
    }

    @Override
    public void onHasNoAds() {
        Ln.d("Purchase is premium upgrade. Congratulating user.");
        mSettings.setNoAds();
        hideAds();
    }

}
