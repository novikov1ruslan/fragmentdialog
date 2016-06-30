package com.ivygames.morskoiboi.screen.internet;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.screen.InvitationObserver;
import com.ivygames.morskoiboi.screen.view.InvitationButton;
import com.ivygames.morskoiboi.screen.view.NotepadRelativeLayout;

import org.commons.logger.Ln;

public class InternetGameLayout extends NotepadRelativeLayout implements View.OnClickListener, InvitationObserver {

    private InternetGameLayoutListener mListener;
    private TextView mPlayerNameView;
    private InvitationButton mViaInternetButton;

    public InternetGameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScreenActions(InternetGameLayoutListener listener) {
        mListener = listener;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (isInEditMode()) {
            return;
        }

        findViewById(R.id.quick_game_button).setOnClickListener(this);
        findViewById(R.id.invite_player_button).setOnClickListener(this);
        mViaInternetButton = (InvitationButton) findViewById(R.id.view_invitations_button);
        mViaInternetButton.setOnClickListener(this);
        mPlayerNameView = (TextView) findViewById(R.id.player_name);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.quick_game_button:
                mListener.quickGame();
                break;
            case R.id.invite_player_button:
                mListener.invitePlayer();
                break;
            case R.id.view_invitations_button:
                mListener.viewInvitations();
                break;
            default:
                Ln.w("unprocessed internet button=" + v.getId());
                break;
        }
    }

    public void setPlayerName(CharSequence playerName) {
        mPlayerNameView.setText(playerName);
    }

    @Override
    public void hideInvitation() {
        mViaInternetButton.hideInvitation();
    }

    @Override
    public void showInvitation() {
        mViaInternetButton.showInvitation();
    }
}
