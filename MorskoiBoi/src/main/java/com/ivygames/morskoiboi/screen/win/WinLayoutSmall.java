package com.ivygames.morskoiboi.screen.win;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.screen.view.NotepadLinearLayout;
import com.ivygames.morskoiboi.utils.GameUtils;

import java.util.Collection;

public class WinLayoutSmall extends NotepadLinearLayout {

    private TextView mTimeView;
    private TextView mScoreView;
    private View mScoreContainer;
    private View mSignInBar;
    private View mSignInButton;
    protected View mYesButton;
    private View mNoButton;
    private View mContinueButton;

    public WinLayoutSmall(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTimeView = (TextView) findViewById(R.id.time);
        mScoreView = (TextView) findViewById(R.id.total_scores);
        mYesButton = findViewById(R.id.yes_button);
        mNoButton = findViewById(R.id.no_button);
        mContinueButton = findViewById(R.id.continue_button);

        mSignInButton = findViewById(R.id.sign_in_button);
        mSignInBar = findViewById(R.id.sign_in_bar);

        mScoreContainer = findViewById(R.id.scores_container);
    }

    public void setSignInClickListener(OnClickListener listener) {
        mSignInButton.setOnClickListener(listener);
    }

    public void setYesClickListener(OnClickListener listener) {
        mYesButton.setOnClickListener(listener);
    }

    public void setNoClickListener(OnClickListener listener) {
        mNoButton.setOnClickListener(listener);
        mContinueButton.setOnClickListener(listener);
    }

    public void setShips(Collection<Ship> ships) {
        // for extension
    }

    public void setDuration(long millis) {
        mTimeView.setText(GameUtils.formatDuration(millis));
    }

    public void setTotalScore(int score) {
        mScoreView.setText(String.valueOf(score));
    }

    public void hideSignInForAchievements() {
        mSignInBar.setVisibility(View.INVISIBLE);
    }

    public void showSignInForAchievements() {
        mSignInBar.setVisibility(View.VISIBLE);
    }

    public void hideDuration() {
        mTimeView.setVisibility(View.GONE);
    }

    public void hideScores() {
        mScoreContainer.setVisibility(View.GONE);
    }

    public void hideYesNoButtons() {
        findViewById(R.id.continue_panel).setVisibility(GONE);
    }

    public void showContinueButton() {
        findViewById(R.id.surrender_continue_panel).setVisibility(VISIBLE);
    }

}
