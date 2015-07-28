package com.ivygames.morskoiboi.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.PlayUtils;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.analytics.UiEvent;
import com.ivygames.morskoiboi.rt.InvitationEvent;
import com.ivygames.morskoiboi.ui.BattleshipActivity.SignInListener;
import com.ivygames.morskoiboi.ui.view.MainScreenLayout;
import com.ivygames.morskoiboi.ui.view.MainScreenLayout.MainScreenActions;
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
//    private View mTutView;

    private static Intent createShareIntent(Context context, String greeting) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, greeting + PlayUtils.getPlayUrl(context));
        return shareIntent;
    }

    @Override
    public View onCreateView(ViewGroup container) {
        mLayout = (MainScreenLayout) inflate(R.layout.main, container);
        mLayout.setScreenActionsListener(this);
//        mTutView = mLayout.setTutView(inflate(R.layout.main_tut));

        Ln.d(this + " screen created");

        if (GameSettings.get().shouldProposeRating()) {
            Ln.d("ask the user to rate the app");
            showRateDialog();
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
        showInvitationIfHas(mParent.hasInvitation());
    }

    @Override
    public void onResume() {
        super.onResume();
        mLayout.onResume(BattleshipActivity.PLUS_ONE_REQUEST_CODE);
        if (mApiClient.isConnected()) {
            mLayout.showPlusOneButton();
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
        showInvitationIfHas(mParent.hasInvitation());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED) {
            Ln.i("reconnect required");
            mApiClient.disconnect();
        } else if (requestCode == BattleshipActivity.PLUS_ONE_REQUEST_CODE) {
            UiEvent.send(mGaTracker, "+1", "[" + resultCode + "]");
        }
    }

    @Override
    public void play() {
        mParent.setScreen(new SelectGameScreen());
    }

    @Override
    public void share() {
        mGaTracker.send(new UiEvent("share").build());
        startActivity(MainScreen.createShareIntent(getActivity(), getString(R.string.share_greeting)));
    }

    @Override
    public void showAchievements() {
        boolean signedIn = mApiClient.isConnected();
        mGaTracker.send(new UiEvent("achievements", signedIn ? 1 : 0).build());
        if (signedIn) {
            showAchievementsScreen();
        } else {
            Ln.d("user is not signed in - ask to sign in");
            showAchievementsDialog();
        }
    }

    @Override
    public void noAds() {
        // TODO: implement
    }

    private void showAchievementsScreen() {
        startActivityForResult(Games.Achievements.getAchievementsIntent(mApiClient), BattleshipActivity.RC_UNUSED);
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
                mGaTracker.send(new UiEvent("sign_in", "achievements").build());
                mAchievementsRequested = true;
                mApiClient.connect();
            }
        }).create().show(mFm, DIALOG);
    }

    @Override
    public void showHelp() {
        mParent.setScreen(new HelpScreen());
    }

    @Override
    public void showLeaderboards() {
        boolean signedIn = mApiClient.isConnected();
        mGaTracker.send(new UiEvent("showHiScores", signedIn ? 1 : 0).build());
        if (signedIn) {
            showLeaderboardsScreen();
        } else {
            Ln.d("user is not signed in - ask to sign in");
            showLeaderboardsDialog();
        }
    }

    private void showLeaderboardsScreen() {
        startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mApiClient, getString(R.string.leaderboard_normal)), BattleshipActivity.RC_UNUSED);
    }

    @Override
    public void showSettings() {
        mParent.setScreen(new SettingsScreen());
    }

    private void showLeaderboardsDialog() {
        new SignInDialog.Builder().setMessage(R.string.leaderboards_request).setPositiveButton(R.string.sign_in, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mGaTracker.send(new UiEvent("sign_in", "leaderboards").build());
                mLeaderboardRequested = true;
                mApiClient.connect();
            }
        }).create().show(mFm, DIALOG);
    }

    private void showRateDialog() {
        new AlertDialogBuilder().setMessage(R.string.rate_request).setPositiveButton(R.string.rate, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mGaTracker.send(new UiEvent("rate").build());
                GameSettings.get().setRated();
                PlayUtils.rateApp(getActivity());
            }

        }).setNegativeButton(R.string.later, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mGaTracker.send(new UiEvent("rate_later").build());
                GameSettings.get().rateLater();
            }
        }).create().show(mFm, DIALOG);
    }

    @Override
    public String toString() {
        return TAG + debugSuffix();
    }
}
