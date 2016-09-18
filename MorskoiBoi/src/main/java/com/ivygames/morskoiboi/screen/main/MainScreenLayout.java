package com.ivygames.morskoiboi.screen.main;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewStub;

import com.google.android.gms.plus.PlusOneButton;
import com.ivygames.common.PlayUtils;
import com.ivygames.common.invitations.InvitationScreen;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.screen.view.InvitationButton;
import com.ivygames.morskoiboi.screen.view.NotepadRelativeLayout;

import org.commons.logger.Ln;

public class MainScreenLayout extends NotepadRelativeLayout implements View.OnClickListener, InvitationScreen {

    public interface MainScreenActions {
        void play();

        void showLeaderboards();

        void showHelp();

        void showSettings();

        void share();

        void showAchievements();

        void noAds();
    }

    private MainScreenActions mScreenActions;
    private InvitationButton mPlayButton;
    private PlusOneButton mPlusOneButton;
    private ViewStub mPlusStub;

    public MainScreenLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScreenActionsListener(MainScreenActions screenActions) {
        mScreenActions = screenActions;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mPlusStub = (ViewStub) findViewById(R.id.plus_one_button);
        mPlayButton = (InvitationButton) findViewById(R.id.play);
        mPlayButton.setOnClickListener(this);
        findViewById(R.id.high_score).setOnClickListener(this);
        findViewById(R.id.help).setOnClickListener(this);
        findViewById(R.id.share_button).setOnClickListener(this);
        findViewById(R.id.settings_button).setOnClickListener(this);
        findViewById(R.id.achievements_button).setOnClickListener(this);
        findViewById(R.id.no_ads).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play:
                mScreenActions.play();
                break;

            case R.id.high_score:
                mScreenActions.showLeaderboards();
                break;

            case R.id.help:
                mScreenActions.showHelp();
                break;

            case R.id.share_button:
                mScreenActions.share();
                break;

            case R.id.settings_button:
                mScreenActions.showSettings();
                break;

            case R.id.achievements_button:
                mScreenActions.showAchievements();
                break;

            case R.id.no_ads:
                mScreenActions.noAds();
                break;

            default:
                Ln.w("unprocessed main button =" + v.getId());
                break;
        }
    }

    @Override
    public void hideInvitation() {
        mPlayButton.hideInvitation();
    }

    @Override
    public void showInvitation() {
        mPlayButton.showInvitation();
    }

    public void showPlusOneButton(int requestCode) {
        if (mPlusOneButton == null) {
            mPlusOneButton = (PlusOneButton) mPlusStub.inflate();
            mPlusOneButton.initialize(PlayUtils.getPlayUrl(getContext().getPackageName()), requestCode);
        }
        mPlusOneButton.setVisibility(VISIBLE);
    }

    public void hideNoAdsButton() {
        findViewById(R.id.no_ads).setVisibility(GONE);
    }

}
