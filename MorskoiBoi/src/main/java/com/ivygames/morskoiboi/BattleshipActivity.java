package com.ivygames.morskoiboi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.ivygames.common.AndroidDevice;
import com.ivygames.common.GpgsUtils;
import com.ivygames.common.billing.PurchaseManager;
import com.ivygames.common.billing.PurchaseStatusListener;
import com.ivygames.common.googleapi.ApiClient;
import com.ivygames.common.googleapi.GameInvitation;
import com.ivygames.common.invitations.InvitationManager;
import com.ivygames.common.invitations.InvitationReceivedListener;
import com.ivygames.common.music.MusicPlayer;
import com.ivygames.common.ui.ScreenManager;
import com.ivygames.morskoiboi.achievement.AchievementsManager;
import com.ivygames.morskoiboi.model.ChatMessage;
import com.ivygames.morskoiboi.player.ChatListener;
import com.ivygames.morskoiboi.progress.ProgressManager;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.ScreenCreator;
import com.ivygames.morskoiboi.screen.main.MainScreen;
import com.ivygames.morskoiboi.utils.UiUtils;
import com.ruslan.fragmentdialog.FragmentAlertDialog;

import org.commons.logger.Ln;

import java.util.Set;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;

public class BattleshipActivity extends Activity implements ConnectionCallbacks, ChatListener {
    // TODO:
     /*
      * base64EncodedPublicKey should be YOUR APPLICATION'S PUBLIC KEY (that you got from the Google Play developer console). This is not your developer public
 	 * key, it's the *app-specific* public key.
-	 *
+	 *
 	 * Instead of just storing the entire literal string here embedded in the program, construct the key at runtime from pieces or use bit manipulation (for
 	 * example, XOR with some other string) to hide the actual key. The key itself is not secret information, but we don't want to make it easy for an attacker
 	 * to replace the public key with one of their own and then fake messages from the server.
 	 */
    private static final String BASE64_ENCODED_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsZ8ufj+4+R1sqPrTudIeXZBD6NUtKo8fWLpbQHp9ib9jtIv3PVOzVuNKIsG7eXqn0U+vWX8WYtoPGmogYr4GDJqdzOQb2xq5ZEsAzXoE+Yeiqpp/ASUs1IU2Tw+cu30rKStgktnFeIfcFowPyHeSgSQlqBFUrL0A8oipc5oesao7OiGGCwpUf6OJuvyK0DmdhdYUMPRxTgp0v5+JnXhNEqgiU00W468vf4rfUGqQWUNN902fphf8oADJT5FdlculaQva5t+55RdpqtP8UAficOUXh1xyAn1KQ0APKOPU5x7wAe/z3bLdjE1Ik4g4KXyHLGfP5PMjkfqvgNeU2WsN4QIDAQAB";
    private final String SKU_NO_ADS = "no_ads";

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

    private static final Configuration CONFIGURATION_LONG = new Configuration.Builder().setDuration(Configuration.DURATION_LONG).build();

    private final PurchaseManager mPurchaseManager = new PurchaseManager(this, RC_PURCHASE);

    private boolean mRecreating;

    private final GameSettings mSettings = Dependencies.getSettings();

    /**
     * volume stream is saved on onResume and restored on onPause
     */
    private int mVolumeControlStream;

    @NonNull
    private final ApiClient mGoogleApiClient = Dependencies.getApiClient();

    @NonNull
    private final AchievementsManager mAchievementsManager = Dependencies.getAchievementsManager();

    @NonNull
    private final InvitationManager mInvitationManager = Dependencies.getInvitationManager();

    @NonNull
    private final ProgressManager mProgressManager = Dependencies.getProgressManager();

    // Are we currently resolving a connection failure?
    private boolean mResolvingConnectionFailure;

    private MusicPlayer mMusicPlayer;
    private View mBanner;

    private ScreenManager mScreenManager;

    private BattleshipScreen mCurrentScreen;

    @NonNull
    private final OnConnectionFailedListener mConnectionFailedListener = new OnConnectionFailedListenerImpl();
    private ViewGroup mLayout;
    @NonNull
    private final InvitationReceivedListener mInvitationReceivedListener = new InvitationReceivedListenerImpl();

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

        AndroidDevice device = Dependencies.getDevice();

        if (device.isTablet()) {
            Ln.d("device is tablet");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            Ln.d("device is handset");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        mMusicPlayer = MusicPlayer.create(this, R.raw.intro_music);

        Ln.d("google play services available = " + device.isGoogleServicesAvailable());

        mLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.battleship, null);
        setContentView(mLayout);
        mScreenManager = new ScreenManager((ViewGroup) mLayout.findViewById(R.id.container));
        mBanner = mLayout.findViewById(R.id.banner);

        mGoogleApiClient.setConnectionCallbacks(this);
        mGoogleApiClient.setOnConnectionFailedListener(mConnectionFailedListener);
        if (mSettings.shouldAutoSignIn()) {
            Ln.d("should auto-signin - connecting...");
            mGoogleApiClient.connect();
        }

        if (mSettings.noAds()) {
            hideAds();
        } else {
            AdProviderFactory.init(this);
            if (device.isBillingAvailable()) {
                mPurchaseManager.query(SKU_NO_ADS, new PurchaseStatusListenerImpl(), BASE64_ENCODED_PUBLIC_KEY);
            } else {
                Ln.e("gpgs_not_available");
                hideNoAdsButton();
            }
        }

//        FacebookSdk.sdkInitialize(getApplicationContext());
        Ln.i("game fully created");

        ScreenCreator.setActivity(this);
        ScreenCreator.setApiClient(mGoogleApiClient);
        ScreenCreator.setSettings(mSettings);

        setScreen(ScreenCreator.newMainScreen());
    }

    @Override
    public void showChatCrouton(ChatMessage message) {
        if (mScreenManager.isStarted()) {
            View layout = UiUtils.inflateChatCroutonLayout(getLayoutInflater(), message.getText(), mLayout);
            Crouton.make(this, layout).setConfiguration(CONFIGURATION_LONG).show();
        }
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

    private void hideAds() {
        AdProviderFactory.getAdProvider().destroy();
        AdProviderFactory.noAds();
        mBanner.setVisibility(View.GONE);
        hideNoAdsButton();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        AndroidDevice.printIntent(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mRecreating) {
            Ln.v("recreating");
            return;
        }
        Ln.d("UI partially visible - keep screen On");
        keepScreenOn();

        mScreenManager.onStart();

        mInvitationManager.registerInvitationReceiver(mInvitationReceivedListener);
        if (mGoogleApiClient.isConnected()) {
            Ln.d("API is connected - register invitation listener");
            mInvitationManager.loadInvitations();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRecreating) {
            Ln.v("recreating");
            return;
        }

        Ln.v("resuming");
        mScreenManager.onResume();

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
        setVolumeControlStream(mVolumeControlStream);
        AdProviderFactory.getAdProvider().pause();

        mScreenManager.onPause();

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
        stopKeepingScreenOn();

        mScreenManager.onStop();
        mInvitationManager.unregisterInvitationReceiver(mInvitationReceivedListener);
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

        mScreenManager.onDestroy();
//        if (Model.instance != null && Model.instance.game != null) {
//            Game game = Model.instance.game;
//            if (!game.hasFinished()) {
//                Ln.e("application destroyed while game is on");
////                game.finish();
//            }
//        }

        // screens will cancel all their croutons, but activity has its own
        Crouton.cancelAllCroutons();
        AdProviderFactory.getAdProvider().destroy();

        mPurchaseManager.destroy();

        mGoogleApiClient.unregisterConnectionCallbacks(this);
        mGoogleApiClient.unregisterConnectionFailedListener(mConnectionFailedListener);
        mGoogleApiClient.disconnect();

        mMusicPlayer.release();
        Ln.d("game destroyed");
    }

    public void dismissTutorial() {
        mScreenManager.dismissTutorial();
    }

    public void showTutorial(@Nullable View view) {
        mScreenManager.showTutorial(view);
    }

    @Override
    public void onBackPressed() {
        if (mScreenManager.handleBackPress()) {
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
            mPurchaseManager.handleActivityResult(resultCode, data);
        } else {
            mCurrentScreen.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Ln.d("connection suspended - trying to reconnect: " + GpgsUtils.connectionCauseToString(cause));
        // GoogleApiClient will automatically attempt to restore the connection.
        // Applications should disable UI components that require the service,
        // and wait for a call to onConnected(Bundle) to re-enable them.
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

        mScreenManager.onSignInSucceeded();

        mInvitationManager.loadInvitations();
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

    public final void setScreen(@NonNull BattleshipScreen screen) {
        if (mCurrentScreen != null) {
            if (mCurrentScreen.getMusic() != screen.getMusic()) {
                mMusicPlayer.stop();
            }
        }
        mCurrentScreen = screen;

        mScreenManager.setScreen(screen);
        mMusicPlayer.play(screen.getMusic());
    }

    public void purchase() {
        mPurchaseManager.purchase(SKU_NO_ADS, new PurchaseStatusListenerImpl());
    }

    private class PurchaseStatusListenerImpl implements PurchaseStatusListener {
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

    private class OnConnectionFailedListenerImpl implements OnConnectionFailedListener {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult result) {
            Ln.d("connection failed - result: " + result);

            switch (result.getErrorCode()) {
                case ConnectionResult.SERVICE_MISSING:
                case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                case ConnectionResult.SERVICE_DISABLED:
                    Ln.w("connection failed: " + result.getErrorCode());
                    Dialog errorDialog = GoogleApiAvailability.getInstance().getErrorDialog(BattleshipActivity.this, result.getErrorCode(), SERVICE_RESOLVE);
                    errorDialog.show();
                    return;
            }

            if (mResolvingConnectionFailure) {
                Ln.d("ignoring connection failure; already resolving.");
                return;
            }

            Ln.d("resolving connection failure");
            mResolvingConnectionFailure = mGoogleApiClient.resolveConnectionFailure(BattleshipActivity.this, result, RC_SIGN_IN, getString(R.string.error));
            Ln.d("has resolution = " + mResolvingConnectionFailure);
        }
    }

    private class InvitationReceivedListenerImpl implements InvitationReceivedListener {
        @Override
        public void onInvitationReceived(GameInvitation invitation) {
            View view = UiUtils.inflateInfoCroutonLayout(getLayoutInflater(), getString(R.string.received_invitation, invitation.name), mLayout);
            Crouton.make(BattleshipActivity.this, view).setConfiguration(CONFIGURATION_LONG).show();
        }

        @Override
        public void onInvitationsUpdated(Set<String> invitationIds) {

        }
    }

    @Override
    public String toString() {
        return BattleshipActivity.class.getSimpleName() + "#" + (hashCode() % 1000);
    }
}
