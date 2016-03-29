package com.ivygames.morskoiboi.screen.lost;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.BattleshipActivity.BackPressListener;
import com.ivygames.morskoiboi.GameConstants;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.SoundBar;
import com.ivygames.morskoiboi.SoundBarFactory;
import com.ivygames.morskoiboi.analytics.UiEvent;
import com.ivygames.morskoiboi.model.Model;
import com.ivygames.morskoiboi.screen.BackToSelectGameCommand;
import com.ivygames.morskoiboi.screen.DialogUtils;
import com.ivygames.morskoiboi.screen.OnlineGameScreen;
import com.ivygames.morskoiboi.screen.boardsetup.BoardSetupScreen;
import com.ruslan.fragmentdialog.FragmentAlertDialog;

import org.commons.logger.Ln;

public class LostScreen extends OnlineGameScreen implements BackPressListener {
    private static final String TAG = "LOST";
    private static final String DIALOG = FragmentAlertDialog.TAG;

    private final SoundBar mSoundBar;

    private View mView;

    public LostScreen(BattleshipActivity parent) {
        super(parent);
        mSoundBar = SoundBarFactory.create(getParent().getAssets(), "lost.ogg");
        mSoundBar.play();
        GameSettings.get().incrementGamesPlayedCounter();
    }

    @Override
    public View getView() {
        return mView;
    }

    @Override
    public View onCreateView(ViewGroup container) {
        mView = getLayoutInflater().inflate(R.layout.lost, container, false);
        mView.findViewById(R.id.yes_button).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                UiEvent.send("continue", "win");
                backToBoardSetup();
            }
        });

        mView.findViewById(R.id.no_button).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                UiEvent.send("dont_continue", "win");
                doNotContinue();
            }
        });

        Ln.d(this + " screen created");
        return mView;
    }

    @Override
    public void onPause() {
        super.onPause();
        mSoundBar.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mSoundBar.resume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSoundBar.release();
    }

    @Override
    public void onBackPressed() {
        UiEvent.send(GameConstants.GA_ACTION_BACK, "lost");
        doNotContinue();
    }

    private void doNotContinue() {
        if (shouldNotifyOpponent()) {
            showWantToLeaveRoomDialog();
        } else {
            new BackToSelectGameCommand(mParent).run();
        }
    }

    private void showWantToLeaveRoomDialog() {
        String displayName = Model.instance.opponent.getName();
        String message = getString(R.string.want_to_leave_room, displayName);
        DialogUtils.newOkCancelDialog(message, new BackToSelectGameCommand(mParent)).show(mFm, DIALOG);
    }

    private void backToBoardSetup() {
        Ln.d("getting back to " + BoardSetupScreen.TAG);
        Model.instance.game.clearState();
        mParent.setScreen(new BoardSetupScreen(getParent()));
    }

    @Override
    public String toString() {
        return TAG + debugSuffix();
    }
}
