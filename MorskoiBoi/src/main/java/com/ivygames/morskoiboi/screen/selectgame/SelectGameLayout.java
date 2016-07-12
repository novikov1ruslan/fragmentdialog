package com.ivygames.morskoiboi.screen.selectgame;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ivygames.common.invitations.InvitationObserver;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.Rank;
import com.ivygames.morskoiboi.screen.view.InvitationButton;
import com.ivygames.morskoiboi.screen.view.NotepadRelativeLayout;

import org.commons.logger.Ln;

public class SelectGameLayout extends NotepadRelativeLayout implements View.OnClickListener, InvitationObserver {
    public interface SelectGameActions {

        void vsAndroid();

        void viaBlueTooth();

        void viaInternet();

        void showRanks();

        void dismissTutorial();

        void showHelp();

    }

    private SelectGameActions mScreenActions;

    private TextView mPlayerName;
    private ImageView mPlayerRank;
    private TextView mRankText;
    private InvitationButton mViaInternetButton;

    private View mTutView;

    public SelectGameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScreenActions(SelectGameActions screenActions) {
        mScreenActions = screenActions;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (isInEditMode()) {
            return;
        }

        findViewById(R.id.vs_android).setOnClickListener(this);
        findViewById(R.id.via_bluetooth).setOnClickListener(this);
        mViaInternetButton = (InvitationButton) findViewById(R.id.via_internet);
        mViaInternetButton.setOnClickListener(this);
        mPlayerName = (TextView) findViewById(R.id.player_name);
        mPlayerRank = (ImageView) findViewById(R.id.player_rank);
        mRankText = (TextView) findViewById(R.id.rank_text);
        findViewById(R.id.help_button).setOnClickListener(this);
        mPlayerRank.setOnClickListener(this);
    }

    public String getPlayerName() {
        return mPlayerName.getText().toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vs_android:
                mScreenActions.vsAndroid();
                break;
            case R.id.via_bluetooth:
                mScreenActions.viaBlueTooth();
                break;
            case R.id.via_internet:
                mScreenActions.viaInternet();
                break;
            case R.id.player_rank:
                mScreenActions.showRanks();
                break;
            case R.id.help_button:
                mScreenActions.showHelp();
                break;
            case R.id.got_it_button:
                mScreenActions.dismissTutorial();
                break;
            default:
                Ln.w("unprocessed select button =" + v.getId());
                break;
        }
    }

    public void hideBluetooth() {
        findViewById(R.id.via_bluetooth).setVisibility(GONE);
    }

    public void setPlayerName(CharSequence name) {
        mPlayerName.setText(name);
    }

    public void setRank(Rank rank) {
        mRankText.setText(rank.getNameRes());
        mPlayerRank.setImageResource(rank.getBitmapRes());
        if (rank.compareTo(Rank.ENSIGN) < 0) {
            mPlayerRank.setBackgroundResource(R.drawable.enlisted_rank_background);
        } else {
            mPlayerRank.setBackgroundResource(R.drawable.officer_rank_background);
        }
    }

    @Override
    public void hideInvitation() {
        mViaInternetButton.hideInvitation();
    }

    @Override
    public void showInvitation() {
        mViaInternetButton.showInvitation();
    }

    public View setTutView(View view) {
        mTutView = view;
        return view;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mTutView.measure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mTutView.layout(l, t, r, b);

        int[] location = new int[2];
        mPlayerRank.getLocationOnScreen(location);

        View tap = mTutView.findViewById(R.id.tap);
        tap.setX(location[0] + mPlayerRank.getWidth() / 2);
        tap.setY(location[1]);

        View tutText = mTutView.findViewById(R.id.show_rank_text);
        tutText.setY(location[1] / 2 - tutText.getHeight() / 2);

        float padding = getResources().getDimension(R.dimen.tut_screen_padding);
        View gotIt = mTutView.findViewById(R.id.got_it_button);
        gotIt.setY(tap.getY() + tap.getHeight() + padding);
        padding += getResources().getDimension(R.dimen.battleship_margin_horizontal);
        gotIt.setX(getWidth() - gotIt.getWidth() - padding);
        gotIt.setOnClickListener(this);
    }
}
