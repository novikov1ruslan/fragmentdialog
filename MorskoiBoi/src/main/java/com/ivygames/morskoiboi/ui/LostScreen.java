package com.ivygames.morskoiboi.ui;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.ivygames.morskoiboi.GameConstants;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.SoundBar;
import com.ivygames.morskoiboi.analytics.UiEvent;
import com.ivygames.morskoiboi.model.Model;
import com.ivygames.morskoiboi.ui.BattleshipActivity.BackPressListener;
import com.ruslan.fragmentdialog.FragmentAlertDialog;

import org.commons.logger.Ln;

public class LostScreen extends OnlineGameScreen implements BackPressListener {
    private static final String TAG = "LOST";
    private static final String DIALOG = FragmentAlertDialog.TAG;

    private SoundBar mSoundBar;
    private View mView;

    @Override
    public void onCreate() {
        super.onCreate();
        mSoundBar = new SoundBar(getActivity().getAssets(), "lost.ogg");
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
                mGaTracker.send(new UiEvent("continue", "win").build());
                backToBoardSetup();
            }
        });

        mView.findViewById(R.id.no_button).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mGaTracker.send(new UiEvent("dont_continue", "win").build());
                doNotContinue();
            }
        });

        Ln.d(this + " screen created");
        return mView;
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        mSoundBar.autoPause();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        mSoundBar.autoResume();
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSoundBar.release();
    }

    @Override
    public void onBackPressed() {
        mGaTracker.send(new UiEvent(GameConstants.GA_ACTION_BACK, "lost").build());
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
        mParent.setScreen(new BoardSetupScreen());
    }

    @Override
    public String toString() {
        return TAG + debugSuffix();
    }
}
