package com.ivygames.morskoiboi.screen.main;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.games.GamesActivityResultCodes;
import com.ivygames.common.PlayUtils;
import com.ivygames.common.Sharing;
import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.SignInListener;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.GoogleApiClientWrapper;
import com.ivygames.morskoiboi.InvitationManager;
import com.ivygames.morskoiboi.billing.PurchaseManager;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.VibratorFacade;
import com.ivygames.morskoiboi.analytics.UiEvent;
import com.ivygames.morskoiboi.rt.InvitationEvent;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.SignInDialog;
import com.ivygames.morskoiboi.screen.help.HelpScreen;
import com.ivygames.morskoiboi.screen.main.MainScreenLayout.MainScreenActions;
import com.ivygames.morskoiboi.screen.selectgame.SelectGameScreen;
import com.ivygames.morskoiboi.screen.settings.SettingsScreen;
import com.ruslan.fragmentdialog.AlertDialogBuilder;
import com.ruslan.fragmentdialog.FragmentAlertDialog;

import org.commons.logger.Ln;

import de.greenrobot.event.EventBus;

public class MainScreen extends BattleshipScreen implements MainScreenActions, SignInListener {
    private static final String TAG = "MAIN";
    private static final String DIALOG = FragmentAlertDialog.TAG;

    private boolean mAchievementsRequested;
    private boolean mLeaderboardRequested;
    private MainScreenLayout mLayout;

    @NonNull
    private final GoogleApiClientWrapper mApiClient;

    @NonNull
    private final InvitationManager mInvitationManager;

    @NonNull
    private final PurchaseManager mPurchaseManager;

    public MainScreen(@NonNull BattleshipActivity parent, @NonNull GoogleApiClientWrapper apiClient) {
        super(parent);
        mApiClient = apiClient;
        mInvitationManager = parent.getInvitationManager();
        mPurchaseManager = parent.getPurchaseManager();
    }

    @Override
    public View onCreateView(ViewGroup container) {
        mLayout = (MainScreenLayout) inflate(R.layout.main, container);
        mLayout.setScreenActionsListener(this);

        Ln.d(this + " screen created");

        if (GameSettings.get().shouldProposeRating()) {
            Ln.d("ask the user to rate the app");
            showRateDialog();
        }

        if (GameSettings.get().noAds()) {
            hideNoAdsButton();
        }

        return mLayout;
    }

    @Override
    public View getTutView() {
        return null;
    }

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
    public void onResume() {
        super.onResume();
        mLayout.onResume(BattleshipActivity.PLUS_ONE_REQUEST_CODE);
        if (mApiClient.isConnected()) {
            mLayout.showPlusOneButton();
        }

        if (GameSettings.get().noAds()) {
            hideNoAdsButton();
        }
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
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(InvitationEvent event) {
        showInvitationIfHas(mInvitationManager.hasInvitation());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED) {
            Ln.i("reconnect required");
            mApiClient.disconnect();
        } else if (requestCode == BattleshipActivity.PLUS_ONE_REQUEST_CODE) {
            UiEvent.send("+1", "[" + resultCode + "]");
        }
    }

    @Override
    public void play() {
        mParent.setScreen(new SelectGameScreen(getParent()));
    }

    @Override
    public void share() {
        UiEvent.send("share");
        startActivity(Sharing.createShareIntent(getParent(), getString(R.string.share_greeting)));
    }

    @Override
    public void showAchievements() {
        boolean signedIn = mApiClient.isConnected();
        UiEvent.send("achievements", signedIn ? 1 : 0);
        if (signedIn) {
            showAchievementsScreen();
        } else {
            Ln.d("user is not signed in - ask to sign in");
            showAchievementsDialog();
        }
    }

    @Override
    public void noAds() {
        UiEvent.send("no_ads");
        mPurchaseManager.purchase((int) BattleshipActivity.RC_PURCHASE);
    }

    private void showAchievementsScreen() {
        startActivityForResult(mApiClient.getAchievementsIntent(), BattleshipActivity.RC_UNUSED);
    }

    @Override
    public void onSignInSucceeded() {
        mLayout.showPlusOneButton();

        if (mAchievementsRequested) {
            mAchievementsRequested = false;
            showAchievementsScreen();
        } else if (mLeaderboardRequested) {
            mLeaderboardRequested = false;
            showLeaderboardsScreen();
        }
    }

    private void showAchievementsDialog() {
        new SignInDialog.Builder().setMessage(R.string.achievements_request).setPositiveButton(R.string.sign_in, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                UiEvent.send("sign_in", "achievements");
                mAchievementsRequested = true;
                mApiClient.connect();
            }
        }).create().show(mFm, DIALOG);
    }

    @Override
    public void showHelp() {
        mParent.setScreen(new HelpScreen(getParent()));
    }

    @Override
    public void showLeaderboards() {
        boolean signedIn = mApiClient.isConnected();
        UiEvent.send("showHiScores", signedIn ? 1 : 0);
        if (signedIn) {
            showLeaderboardsScreen();
        } else {
            Ln.d("user is not signed in - ask to sign in");
            showLeaderboardsDialog();
        }
    }

    private void showLeaderboardsScreen() {
        startActivityForResult(mApiClient.getLeaderboardIntent(getString(R.string.leaderboard_normal)), BattleshipActivity.RC_UNUSED);
    }

    @Override
    public void showSettings() {
        SettingsScreen settingsScreen = new SettingsScreen(getParent(), mApiClient, GameSettings.get());
        VibratorFacade vibratorFacade = new VibratorFacade(settingsScreen);
        settingsScreen.setVibrator(vibratorFacade);
        mParent.setScreen(settingsScreen);
    }

    private void showLeaderboardsDialog() {
        new SignInDialog.Builder().setMessage(R.string.leaderboards_request).setPositiveButton(R.string.sign_in, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                UiEvent.send("sign_in", "leaderboards");
                mLeaderboardRequested = true;
                mApiClient.connect();
            }
        }).create().show(mFm, DIALOG);
    }

    private void showRateDialog() {
        new AlertDialogBuilder().setMessage(R.string.rate_request).setPositiveButton(R.string.rate, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                UiEvent.send("rate");
                GameSettings.get().setRated();
                PlayUtils.rateApp(getParent());
            }

        }).setNegativeButton(R.string.later, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                UiEvent.send("rate_later");
                GameSettings.get().rateLater();
            }
        }).create().show(mFm, DIALOG);
    }

    public void hideNoAdsButton() {
        mLayout.hideNoAdsButton();
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
