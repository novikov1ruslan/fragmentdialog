package com.ivygames.morskoiboi.screen.lost;

import android.content.Context;
import android.media.AudioManager;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.ivygames.common.analytics.UiEvent;
import com.ivygames.morskoiboi.BackPressListener;
import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.Dependencies;
import com.ivygames.morskoiboi.GameHandler;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.SoundBar;
import com.ivygames.morskoiboi.SoundBarFactory;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.Model;
import com.ivygames.morskoiboi.screen.OnlineGameScreen;
import com.ivygames.morskoiboi.screen.boardsetup.BoardSetupScreen;

import org.commons.logger.Ln;

public class LostScreen extends OnlineGameScreen implements BackPressListener {
    private static final String TAG = "LOST";

    @NonNull
    private final SoundBar mSoundBar;

    @NonNull
    private final GameSettings mSettings = Dependencies.getSettings();

    private View mView;

    public LostScreen(@NonNull BattleshipActivity parent, @NonNull Game game) {
        super(parent, game, Model.opponent.getName());
        AudioManager audioManager = (AudioManager) mParent.getSystemService(Context.AUDIO_SERVICE);
        mSoundBar = SoundBarFactory.create(mParent.getAssets(), "lost.ogg", audioManager);
        mSoundBar.play();
        mSettings.incrementGamesPlayedCounter();
    }

    @NonNull
    @Override
    public View getView() {
        return mView;
    }

    @Override
    public View onCreateView(@NonNull ViewGroup container) {
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
                UiEvent.send("don't_continue", "win");
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
        UiEvent.send(UiEvent.GA_ACTION_BACK, "lost");
        doNotContinue();
    }

    private void doNotContinue() {
        if (shouldNotifyOpponent()) {
            showWantToLeaveRoomDialog();
        } else {
            backToSelectGame();
        }
    }

    private void backToBoardSetup() {
        Ln.d("getting back to " + BoardSetupScreen.TAG);
        setScreen(GameHandler.newBoardSetupScreen(mGame));
    }

    @Override
    public String toString() {
        return TAG + debugSuffix();
    }
}
