package com.ivygames.morskoiboi.screen.settings;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.ivygames.common.AndroidDevice;
import com.ivygames.common.PlayUtils;
import com.ivygames.common.Sharing;
import com.ivygames.common.SignInListener;
import com.ivygames.common.VibratorWrapper;
import com.ivygames.common.analytics.UiEvent;
import com.ivygames.common.googleapi.ApiClient;
import com.ivygames.common.ui.BackPressListener;
import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.Dependencies;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.ScreenCreator;
import com.ivygames.morskoiboi.screen.settings.SettingsLayout.SettingsScreenActions;

import org.commons.logger.Ln;

public class SettingsScreen extends BattleshipScreen implements SignInListener, BackPressListener {
    private static final String TAG = "SETTINGS";

    @NonNull
    private final ApiClient mApiClient;
    @NonNull
    private final GameSettings mSettings;

    @NonNull
    private final VibratorWrapper mVibrator;
    private SettingsLayout mLayout;

    @NonNull
    private final AndroidDevice mDevice;

    public SettingsScreen(@NonNull BattleshipActivity parent,
                          @NonNull ApiClient apiClient,
                          @NonNull GameSettings settings,
                          @NonNull VibratorWrapper vibratorFacade) {
        super(parent);
        mApiClient = apiClient;
        mSettings = settings;
        mDevice = Dependencies.getDevice();
        mVibrator = vibratorFacade;
    }

    @Override
    public View onCreateView(@NonNull ViewGroup container) {
        mLayout = (SettingsLayout) inflate(R.layout.settings, container);
        mLayout.setScreenActionsListener(mSettingsActions);
        mLayout.setSound(mSettings.isSoundOn());
        if (mVibrator.hasVibrator()) {
            Ln.v("show vibration setting setting");
            mLayout.setVibration(mSettings.isVibrationOn());
        } else {
            Ln.v("device does not support vibration - hide setting");
            mLayout.hideVibrationSetting();
        }

        Intent intent = getEmailIntent();
        if (!mDevice.canResolveIntent(intent)) {
            mLayout.hideReportProblemButton();
        }

        Ln.d(this + " screen created");
        return mLayout;
    }

    private Intent getEmailIntent() {
        return Sharing.getEmailIntent(getString(R.string.report_problem), getString(R.string.app_name), mDevice.getVersionName());
    }

    @NonNull
    @Override
    public View getView() {
        return mLayout;
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

    private SettingsScreenActions mSettingsActions = new SettingsScreenActions() {


        @Override
        public void onSoundChanged() {
            boolean on = !mSettings.isSoundOn();
            mSettings.setSound(on);
            mLayout.setSound(on);

            if (on) {
                parent().playMusic(getMusic());
            } else {
                parent().stopMusic();
            }
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
            Intent intent = PlayUtils.rateIntent(mParent.getPackageName());
            mParent.startActivity(intent);
        }

        @Override
        public void onReportProblem() {
            UiEvent.send("report_problem");
            Intent intent = getEmailIntent();
            if (mDevice.canResolveIntent(intent)) {
                startActivity(intent);
            } else {
                Ln.e("email resolver is not available");
            }
        }

        @Override
        public void onSignIn() {
            UiEvent.send(UiEvent.GA_ACTION_SIGN_IN, "settings");
            mApiClient.connect();
        }

        @Override
        public void onSignOut() {
            UiEvent.send("sign_out", "settings");
            mApiClient.disconnect();
            mLayout.showSignInBar();
        }
    };

    @Override
    public void onBackPressed() {
        setScreen(ScreenCreator.newMainScreen());
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
