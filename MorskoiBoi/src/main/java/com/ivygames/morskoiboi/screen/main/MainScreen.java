package com.ivygames.morskoiboi.screen.main;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.games.GamesActivityResultCodes;
import com.ivygames.common.AndroidDevice;
import com.ivygames.common.PlayUtils;
import com.ivygames.common.Sharing;
import com.ivygames.common.analytics.UiEvent;
import com.ivygames.common.googleapi.ApiClient;
import com.ivygames.common.multiplayer.InvitationToShowListener;
import com.ivygames.common.multiplayer.RealTimeMultiplayer;
import com.ivygames.common.ui.SignInListener;
import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.Dependencies;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.RequestCodes;
import com.ivygames.morskoiboi.dialogs.SignInDialog;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.ScreenCreator;
import com.ivygames.morskoiboi.screen.main.MainScreenLayout.MainScreenActions;
import com.ruslan.fragmentdialog.AlertDialogBuilder;
import com.ruslan.fragmentdialog.FragmentAlertDialog;

import org.commons.logger.Ln;

public class MainScreen extends BattleshipScreen implements SignInListener {
    private static final String DIALOG = FragmentAlertDialog.TAG;

    private boolean mAchievementsRequested;
    private boolean mLeaderboardRequested;
    private MainScreenLayout mLayout;

    @NonNull
    private final ApiClient mApiClient = Dependencies.getApiClient();

    @NonNull
    private final GameSettings mSettings = Dependencies.getSettings();

    @NonNull
    private final RealTimeMultiplayer mMultiplayer = Dependencies.getMultiplayer();

    @NonNull
    private final AndroidDevice mDevice = Dependencies.getDevice();

    private InvitationToShowListener mScreenInvitationListener;

    public MainScreen(BattleshipActivity parent) {
        super(parent);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull ViewGroup container) {
        mLayout = (MainScreenLayout) inflate(R.layout.main, container);
        mLayout.setScreenActionsListener(mMainScreenActions);

        Ln.d(this + " screen created");

        if (mSettings.shouldProposeRating()) {
            Ln.d("ask the user to rate the app");
            showRateDialog();
        }

        mScreenInvitationListener = new InvitationToShowListener(mMultiplayer, mLayout);

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
        mMultiplayer.addInvitationListener(mScreenInvitationListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mApiClient.isConnected()) {
            mLayout.showPlusOneButton(RequestCodes.PLUS_ONE_REQUEST_CODE);
        }

        if (mSettings.noAds() || !mDevice.isBillingAvailable()) {
            hideNoAdsButton();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mMultiplayer.removeInvitationListener(mScreenInvitationListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED) {
            Ln.i("reconnect required");
            mApiClient.disconnect();
        } else if (requestCode == RequestCodes.PLUS_ONE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            UiEvent.send("+1", "[" + resultCode + "]");
        }
    }

    @NonNull
    private final MainScreenActions mMainScreenActions = new MainScreenActions() {

        @Override
        public void play() {
            setScreen(ScreenCreator.newSelectGameScreen());
        }

        @Override
        public void share() {
            UiEvent.send("share");
            startActivity(Sharing.createShareIntent(mParent.getPackageName(), getString(R.string.share_greeting)));
        }

        @Override
        public void showAchievements() {
            boolean signedIn = mApiClient.isConnected();
            UiEvent.send("achievements", signedIn ? 1 : 0);
            if (signedIn) {
                mApiClient.showAchievements(RequestCodes.RC_UNUSED);
            } else {
                Ln.d("user is not signed in - ask to sign in");
                showAchievementsDialog();
            }
        }

        @Override
        public void noAds() {
            UiEvent.send("no_ads");
            parent().purchase();
        }


        private void showAchievementsDialog() {
            new SignInDialog.Builder().setMessage(R.string.achievements_request)
                    .setPositiveButton(R.string.sign_in, new OnClickListener() {

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
            setScreen(ScreenCreator.newHelpScreen());
        }

        @Override
        public void showLeaderboards() {
            boolean signedIn = mApiClient.isConnected();
            UiEvent.send("showHiScores", signedIn ? 1 : 0);
            if (signedIn) {
                mApiClient.showLeaderboards(getString(R.string.leaderboard_normal), RequestCodes.RC_UNUSED);
            } else {
                Ln.d("user is not signed in - ask to sign in");
                showLeaderboardsDialog();
            }
        }

        @Override
        public void showSettings() {
            Vibrator vibrator = (Vibrator) mParent.getSystemService(Context.VIBRATOR_SERVICE);
            setScreen(ScreenCreator.newSettingsScreen(vibrator));
        }

    };

    @Override
    public void onSignInSucceeded() {
        mLayout.showPlusOneButton(RequestCodes.PLUS_ONE_REQUEST_CODE);

        if (mAchievementsRequested) {
            mAchievementsRequested = false;
            mApiClient.showAchievements(RequestCodes.RC_UNUSED);
        } else if (mLeaderboardRequested) {
            mLeaderboardRequested = false;
            mApiClient.showLeaderboards(getString(R.string.leaderboard_normal), RequestCodes.RC_UNUSED);
        }
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
        new AlertDialogBuilder().setMessage(R.string.rate_request)
                .setPositiveButton(R.string.rate, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UiEvent.send("rate");
                        mSettings.setRated();
                        Intent intent = PlayUtils.rateIntent(mParent.getPackageName());
                        mParent.startActivity(intent);
                    }

                }).setNegativeButton(R.string.later, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                UiEvent.send("rate_later");
                mSettings.rateLater();
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

}
