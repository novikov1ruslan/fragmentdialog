package com.ivygames.morskoiboi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.ivygames.common.AndroidDevice;
import com.ivygames.common.AndroidUtils;
import com.ivygames.common.ads.AdProvider;
import com.ivygames.common.ads.NoAdsAdProvider;
import com.ivygames.common.billing.PurchaseManager;
import com.ivygames.common.billing.PurchaseStatusListener;
import com.ivygames.common.googleapi.ApiClient;
import com.ivygames.common.googleapi.ApiConnectionListener;
import com.ivygames.common.invitations.GameInvitation;
import com.ivygames.common.invitations.InvitationListener;
import com.ivygames.common.multiplayer.RealTimeMultiplayer;
import com.ivygames.common.music.MusicPlayer;
import com.ivygames.common.ui.ScreenManager;
import com.ivygames.morskoiboi.achievement.AchievementsManager;
import com.ivygames.morskoiboi.model.ChatMessage;
import com.ivygames.morskoiboi.player.ChatListener;
import com.ivygames.morskoiboi.progress.ProgressManager;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.ScreenCreator;
import com.ivygames.morskoiboi.screen.main.MainScreen;
import com.ivygames.morskoiboi.screen.view.InfoCroutonLayout;
import com.ruslan.fragmentdialog.FragmentAlertDialog;

import org.commons.logger.Ln;

import java.util.Set;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;

public class BattleshipActivity extends Activity implements ApiConnectionListener, ChatListener {
    private static final boolean _DEBUG_ALWAYS_SHOW_ADS = false;
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
    private static final String SKU_NO_ADS = "no_ads";

    public static final int RC_SELECT_PLAYERS = 10000;
    public static final int RC_INVITATION_INBOX = 10001;
    public final static int RC_WAITING_ROOM = 10002;
    public static final int RC_ENSURE_DISCOVERABLE = 3;

    // Request code used to invoke sign in user interactions.
    public static final int RC_SIGN_IN = 9001;
    public static final int SERVICE_RESOLVE = 9002;

    public static final int RC_UNUSED = 0;
    public static final int PLUS_ONE_REQUEST_CODE = 20001;
    public static final int RC_ENABLE_BT = 2;
    public static final int RC_PURCHASE = 10003;

    @NonNull
    private static final Configuration CONFIGURATION_LONG = new Configuration.Builder().setDuration(Configuration.DURATION_LONG).build();

    private PurchaseManager mPurchaseManager;

    private boolean mRecreating;

    private final GameSettings mSettings = Dependencies.getSettings();

    /**
     * volume stream is saved on onResume and restored on onPause
     */
    private int mVolumeControlStream;

    @NonNull
    private final ApiClient mApiClient = Dependencies.getApiClient();

    @NonNull
    private final AchievementsManager mAchievementsManager = Dependencies.getAchievementsManager();

    private RealTimeMultiplayer mMultiplayer = Dependencies.getMultiplayer();

    @NonNull
    private final ProgressManager mProgressManager = Dependencies.getProgressManager();

    private MusicPlayer mMusicPlayer;
    private View mBanner;

    private ScreenManager mScreenManager;

    private BattleshipScreen mCurrentScreen;

    private ViewGroup mLayout;
    @NonNull
    private final InvitationListener mInvitationListener = new InvitationListenerImpl();
    @NonNull
    private AdProvider mAdProvider = new NoAdsAdProvider();
    @NonNull
    private final AndroidDevice mDevice = Dependencies.getDevice();

    private static InfoCroutonLayout inflateInfoCroutonLayout(LayoutInflater inflater, CharSequence message, ViewGroup root) {
        InfoCroutonLayout infoCroutonLayout = (InfoCroutonLayout) inflater.inflate(R.layout.info_crouton, root, false);
        infoCroutonLayout.setMessage(message);
        return infoCroutonLayout;
    }

    private static InfoCroutonLayout inflateChatCroutonLayout(LayoutInflater inflater, CharSequence message, ViewGroup root) {
        InfoCroutonLayout infoCroutonLayout = (InfoCroutonLayout) inflater.inflate(R.layout.chat_crouton, root, false);
        infoCroutonLayout.setMessage(message);
        return infoCroutonLayout;
    }

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (_DEBUG_ALWAYS_SHOW_ADS && !BuildConfig.DEBUG) {
            throw new IllegalStateException("ads off in release build");
        }
        mRecreating = savedInstanceState != null;
        if (mRecreating) {
            Ln.i("app is recreating, restart it");
            finish();
            return;
        }

        AndroidDevice device = Dependencies.getDevice();

        if (device.isTablet()) {
            Ln.v("device is tablet");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            Ln.v("device is handset");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        mMusicPlayer = MusicPlayer.create(this, R.raw.intro_music);

        Ln.d("google play services available = " + device.isGoogleServicesAvailable());

        mLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.battleship, null);
        setContentView(mLayout);
        mScreenManager = new ScreenManager((ViewGroup) mLayout.findViewById(R.id.container));
        mBanner = mLayout.findViewById(R.id.banner);

        mApiClient.setConnectionListener(this);
        mApiClient.setActivity(this);
        if (mSettings.shouldAutoSignIn()) {
            Ln.d("should auto-signin - connecting...");
            mApiClient.connect();
        }

        mPurchaseManager = new PurchaseManager(this, RC_PURCHASE, BASE64_ENCODED_PUBLIC_KEY);
        setupAds(device);

//        FacebookSdk.sdkInitialize(getApplicationContext());
        Ln.i("game fully created");

        ScreenCreator.setActivity(this);
        ScreenCreator.setApiClient(mApiClient);
        ScreenCreator.setSettings(mSettings);

        setScreen(ScreenCreator.newMainScreen());
    }

    private void setupAds(@NonNull AndroidDevice device) {
        Ln.v("setting up ads...");
        if (mSettings.noAds()) {
            if (!_DEBUG_ALWAYS_SHOW_ADS) {
                hideAdsUi();
            }
        } else {
            mAdProvider = AdProviderFactory.create(this, mDevice);
            if (!_DEBUG_ALWAYS_SHOW_ADS) {
                if (device.isBillingAvailable()) {
                    mPurchaseManager.query(SKU_NO_ADS, new PurchaseStatusListenerImpl());
                } else {
                    Ln.e("gpgs_not_available");
                    hideNoAdsButton();
                }
            }
        }
        Dependencies.inject(mAdProvider);
        Ln.v("...ads setup complete");
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
            Ln.d("no ads purchased successfully");
            mSettings.setNoAds();
            hideAdsUi();
            mAdProvider.destroy();
        }
    }

    private void hideAdsUi() {
        mBanner.setVisibility(View.GONE);
        hideNoAdsButton();
    }

    private void hideNoAdsButton() {
        if (mCurrentScreen instanceof MainScreen) {
            ((MainScreen) mCurrentScreen).hideNoAdsButton();
        }
    }

    @Override
    public void showChatCrouton(ChatMessage message) {
        if (mScreenManager.isStarted()) {
            View layout = inflateChatCroutonLayout(getLayoutInflater(), message.getText(), mLayout);
            Crouton.make(this, layout).setConfiguration(CONFIGURATION_LONG).show();
        }
    }

    public void playMusic(int music) {
        mMusicPlayer.play(music);
    }

    public void stopMusic() {
        mMusicPlayer.stop();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        AndroidUtils.printIntent(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mRecreating) {
            Ln.v("recreating");
            return;
        }
        keepScreenOn();

        mScreenManager.onStart();

        mMultiplayer.addInvitationListener(mInvitationListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRecreating) {
            Ln.v("recreating");
            return;
        }

        mScreenManager.onResume();

        mVolumeControlStream = getVolumeControlStream();

        // Set the hardware buttons to control the music
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mAdProvider.resume();

        mMusicPlayer.play(mCurrentScreen.getMusic());
//        AppEventsLogger.activateApp(this); // #FB

        if (mApiClient.isConnected()) {
            Ln.v("connected: load invitations");
            mMultiplayer.loadInvitations();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mRecreating) {
            Ln.v("recreating");
            return;
        }

        setVolumeControlStream(mVolumeControlStream);
        mAdProvider.pause();

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
        mMultiplayer.removeInvitationListener(mInvitationListener);
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
        mAdProvider.destroy();

        mPurchaseManager.destroy();
        mApiClient.disconnect();

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

        mApiClient.onActivityResult(requestCode, resultCode);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Ln.d("connection issue is resolved - reconnecting");
                mApiClient.connect();
            }
        } else if (requestCode == RC_PURCHASE) {
            mPurchaseManager.handleActivityResult(resultCode, data);
        } else {
            mCurrentScreen.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onConnected() {
        Ln.d("signed in");
        mSettings.enableAutoSignIn();

        if (TextUtils.isEmpty(mSettings.getPlayerName())) {
            String name = mApiClient.getDisplayName();
            Ln.i("player's name is not set - setting to G+ name [" + name + "]");
            mSettings.setPlayerName(name);
        }

        mAchievementsManager.loadAchievements();
        mProgressManager.synchronize();

        mScreenManager.onSignInSucceeded();

        mMultiplayer.loadInvitations();
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

    private class InvitationListenerImpl implements InvitationListener {
        @Override
        public void onInvitationReceived(@NonNull GameInvitation invitation) {
            View view = inflateInfoCroutonLayout(getLayoutInflater(), getString(R.string.received_invitation, invitation.name), mLayout);
            Crouton.make(BattleshipActivity.this, view).setConfiguration(CONFIGURATION_LONG).show();
        }

        @Override
        public void onInvitationsUpdated(@NonNull Set<String> invitationIds) {

        }
    }

    @Override
    public String toString() {
        return BattleshipActivity.class.getSimpleName() + "#" + (hashCode() % 1000);
    }
}
