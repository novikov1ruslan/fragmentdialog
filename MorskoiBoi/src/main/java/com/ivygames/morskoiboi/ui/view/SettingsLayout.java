package com.ivygames.morskoiboi.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

import com.ivygames.morskoiboi.R;

import org.commons.logger.Ln;

public class SettingsLayout extends NotepadLinearLayout implements View.OnClickListener {
    public interface SettingsScreenActions {
        void onSoundChanged();

        void onVibrationChanged();

        void onSignIn();

        void onSignOut();

        void onReportProblem();

        void onRate();
    }

    private SettingsScreenActions mScreenActions;
    private View mSignOutBar;
    private View mSignInBar;
    private ImageButton mSoundButton;
    private ImageButton mVibrationButton;

    private View mReportProblem;

    public SettingsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScreenActionsListener(SettingsScreenActions screenActions) {
        mScreenActions = screenActions;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // mRemoveAdsButton = findViewById(R.id.no_ads_btn);
        mVibrationButton = (ImageButton) findViewById(R.id.vibration_btn);
        mVibrationButton.setOnClickListener(this);
        mSoundButton = (ImageButton) findViewById(R.id.sound_btn);
        mSoundButton.setOnClickListener(this);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_btn).setOnClickListener(this);
        mReportProblem = findViewById(R.id.report_problem);
        mReportProblem.setOnClickListener(this);
        findViewById(R.id.rate_btn).setOnClickListener(this);

        mSignOutBar = findViewById(R.id.sign_out_bar);
        mSignInBar = findViewById(R.id.sign_in_bar);
    }

    public void showSignOutBar() {
        mSignOutBar.setVisibility(View.VISIBLE);
        mSignInBar.setVisibility(View.GONE);
    }

    public void showSignInBar() {
        mSignOutBar.setVisibility(View.GONE);
        mSignInBar.setVisibility(View.VISIBLE);
    }

    public void setSound(boolean on) {
        mSoundButton.setImageResource(on ? R.drawable.sound_on : R.drawable.sound_off);
    }

    public void setVibration(boolean on) {
        mVibrationButton.setImageResource(on ? R.drawable.vibrate_on : R.drawable.vibrate_off);
    }

    @Override
    public void onClick(View v) {
        if (mScreenActions == null) {
            return;
        }

        switch (v.getId()) {
            case R.id.vibration_btn:
                mScreenActions.onVibrationChanged();
                break;

            case R.id.sound_btn:
                mScreenActions.onSoundChanged();
                break;

            case R.id.sign_in_button:
                mScreenActions.onSignIn();
                break;

            case R.id.sign_out_btn:
                mScreenActions.onSignOut();
                break;

            case R.id.report_problem:
                mScreenActions.onReportProblem();
                break;

            case R.id.rate_btn:
                mScreenActions.onRate();
                break;

            default:
                Ln.w("unprocessed settings button =" + v.getId());
                break;
        }
    }

    public void hideReportProblemButton() {
        mReportProblem.setVisibility(GONE);
    }

    public void hideVibrationSetting() {
        findViewById(R.id.vibration_container).setVisibility(GONE);
    }

    // public void hideIab() {
    // mRemoveAdsButton.setVisibility(GONE);
    // }
    //
    // public void showIab() {
    // mRemoveAdsButton.setVisibility(VISIBLE);
    // }
    //
    // public void temporarilyHideIab() {
    // mRemoveAdsButton.setVisibility(INVISIBLE);
    // }

}
