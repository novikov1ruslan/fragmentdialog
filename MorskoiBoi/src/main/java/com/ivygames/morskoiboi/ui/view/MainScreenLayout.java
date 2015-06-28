package com.ivygames.morskoiboi.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.ivygames.morskoiboi.R;

import org.commons.logger.Ln;

public class MainScreenLayout extends NotepadRelativeLayout implements View.OnClickListener {

    private View mTutView;

    public interface MainScreenActions {
        void play();

        void showLeaderboards();

        void showHelp();

        void showSettings();

        void share();

        void showAchievements();
    }

    private MainScreenActions mScreenActions;
    private InvitationButton mPlayButton;

    public MainScreenLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScreenActionsListener(MainScreenActions screenActions) {
        mScreenActions = screenActions;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mPlayButton = (InvitationButton) findViewById(R.id.play);
        mPlayButton.setOnClickListener(this);
        findViewById(R.id.high_score).setOnClickListener(this);
        findViewById(R.id.help).setOnClickListener(this);
        findViewById(R.id.share_button).setOnClickListener(this);
        findViewById(R.id.settings_button).setOnClickListener(this);
        findViewById(R.id.achievements_button).setOnClickListener(this);
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

            default:
                Ln.w("unprocessed main button =" + v.getId());
                break;
        }
    }

    public void hideInvitation() {
        mPlayButton.hideInvitation();
    }

    public void showInvitation() {
        mPlayButton.showInvitation();
    }

    public View setTutView(View view) {
        mTutView = view;
        return view;
    }
}
