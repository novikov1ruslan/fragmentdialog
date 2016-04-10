package com.ivygames.morskoiboi.screen.settings;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.ivygames.common.PlayUtils;
import com.ivygames.morskoiboi.AndroidDevice;
import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.BackPressListener;
import com.ivygames.morskoiboi.SignInListener;
import com.ivygames.morskoiboi.GameConstants;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.GoogleApiClientWrapper;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.VibratorFacade;
import com.ivygames.morskoiboi.analytics.UiEvent;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.main.MainScreen;
import com.ivygames.morskoiboi.screen.settings.SettingsLayout.SettingsScreenActions;

import org.commons.logger.Ln;

public class SettingsScreen extends BattleshipScreen implements SignInListener, BackPressListener {
    private static final String TAG = "SETTINGS";
    private static final String EMAIL = "ivy.games.studio@gmail.com";

    @NonNull
    private final GoogleApiClientWrapper mApiClient;
    @NonNull
    private final GameSettings mSettings;

    private VibratorFacade mVibrator;
    private SettingsLayout mLayout;

    @NonNull
    private final AndroidDevice mDevice;

    public SettingsScreen(@NonNull BattleshipActivity parent,
                          @NonNull GoogleApiClientWrapper apiClient,
                          @NonNull GameSettings settings) {
        super(parent);
        mApiClient = apiClient;
        mSettings = settings;
        mDevice = getParent().getDevice();
    }

    public void setVibrator(VibratorFacade vibrator) {
        mVibrator = vibrator;
    }

    @Override
    public View onCreateView(ViewGroup container) {
        mLayout = (SettingsLayout) inflate(R.layout.settings, container);
        mLayout.setScreenActionsListener(mSettingsActions);
        mLayout.setSound(mSettings.isSoundOn());
        if (mVibrator != null && mVibrator.hasVibrator()) {
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

    @Override
    public View getView() {
        return mLayout;
    }

    private Intent getEmailIntent() {
        Uri uri = Uri.parse("mailto:" + EMAIL);
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(uri);
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + " (" + mDevice.getVersionName() + ")");
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

    private SettingsScreenActions mSettingsActions = new SettingsScreenActions() {


        @Override
        public void onSoundChanged() {
            boolean on = !mSettings.isSoundOn();
            mSettings.setSound(on);
            mLayout.setSound(on);

            if (on) {
                mParent.playMusic(getMusic());
            } else {
                mParent.stopMusic();
            }
        }

        @Override
        public void onVibrationChanged() {
            boolean on = !mSettings.isVibrationOn();
            mSettings.setVibration(on);
            mLayout.setVibration(on);
            if (mVibrator != null) {
                mVibrator.vibrate(300);
            }
        }

        @Override
        public void onRate() {
            UiEvent.send("settings_rate");
            mSettings.setRated();
            Intent intent = PlayUtils.rateIntent(getParent().getPackageName());
            getParent().startActivity(intent);
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
            UiEvent.send(GameConstants.GA_ACTION_SIGN_IN, "settings");
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
        mParent.setScreen(new MainScreen(getParent(), getParent().getApiClient()));
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
