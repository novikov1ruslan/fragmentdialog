package com.ivygames.morskoiboi.ui;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;

import com.ivygames.morskoiboi.DeviceUtils;
import com.ivygames.morskoiboi.GameConstants;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.PlayUtils;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.VibratorFacade;
import com.ivygames.morskoiboi.analytics.UiEvent;
import com.ivygames.morskoiboi.ui.BattleshipActivity.BackPressListener;
import com.ivygames.morskoiboi.ui.BattleshipActivity.SignInListener;
import com.ivygames.morskoiboi.ui.view.SettingsLayout;
import com.ivygames.morskoiboi.ui.view.SettingsLayout.SettingsScreenActions;

import org.commons.logger.Ln;

public class SettingsScreen extends BattleshipScreen implements SettingsScreenActions, SignInListener, BackPressListener {
    private static final String TAG = "SETTINGS";

    private static final String EMAIL = "ivy.games.studio@gmail.com";

    private GameSettings mSettings;
    private SettingsLayout mLayout;
    private VibratorFacade mVibrator;

    @Override
    public void onCreate() {
        super.onCreate();
        mVibrator = new VibratorFacade(this);
        mSettings = GameSettings.get();
    }

    @Override
    public View onCreateView(ViewGroup container) {
        mLayout = (SettingsLayout) inflate(R.layout.settings, container);
        mLayout.setScreenActionsListener(this);
        mLayout.setSound(mSettings.isSoundOn());
        if (mVibrator.hasVibrator()) {
            Ln.v("show vibration setting setting");
            mLayout.setVibration(mSettings.isVibrationOn());
        } else {
            Ln.v("device does not support vibration - hide setting");
            mLayout.hideVibrationSetting();
        }

        Intent intent = getEmailIntent();
        if (!DeviceUtils.resolverAvailableForIntent(intent)) {
            mLayout.hideReportProblemButton();
        }

        Ln.d(this + " screen created");
        return mLayout;
    }

    @Override
    public View getView() {
        return mLayout;
    }

    private Intent getEmailIntent() {
        Uri uri = Uri.parse("mailto:" + EMAIL);
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(uri);
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + " (" + DeviceUtils.getVersionName(getResources()) + ")");
//        intent.putExtra(Intent.EXTRA_TEXT, "hi android jack!");
        return Intent.createChooser(intent, getString(R.string.report_problem));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mApiClient.isConnected()) {
            mLayout.showSignOutBar();
        } else {
            mLayout.showSignInBar();
        }
    }

    @Override
    public void onSignInSucceeded() {
        mLayout.showSignOutBar();
    }

    @Override
    public void onSoundChanged() {
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

    @Override
    public void onRate() {
        UiEvent.send("settings_rate");
        mSettings.setRated();
        PlayUtils.rateApp(getActivity());
    }

    @Override
    public void onReportProblem() {
        UiEvent.send("report_problem");
        Intent intent = getEmailIntent();
        if (DeviceUtils.resolverAvailableForIntent(intent)) {
            startActivity(intent);
        } else {
            Ln.e("email resolver is not available");
        }
    }

    @Override
    public void onSignIn() {
        UiEvent.send(GameConstants.GA_ACTION_SIGN_IN, "settings");
        mApiClient.connect();
    }

    @Override
    public void onSignOut() {
        UiEvent.send("sign_out", "settings");
        mApiClient.disconnect();
        mLayout.showSignInBar();
    }

    @Override
    public void onBackPressed() {
        mParent.setScreen(new MainScreen());
    }

    @Override
    public String toString() {
        return TAG + debugSuffix();
    }
}
