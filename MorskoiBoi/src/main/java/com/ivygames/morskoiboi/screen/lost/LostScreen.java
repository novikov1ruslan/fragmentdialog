package com.ivygames.morskoiboi.screen.lost;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.ivygames.common.analytics.UiEvent;
import com.ivygames.common.music.NullSoundBar;
import com.ivygames.common.music.SoundBar;
import com.ivygames.common.music.SoundBarFactory;
import com.ivygames.common.ui.BackPressListener;
import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.Dependencies;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.Session;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.screen.OnlineGameScreen;
import com.ivygames.morskoiboi.screen.ScreenCreator;
import com.ivygames.morskoiboi.screen.boardsetup.BoardSetupScreen;

import org.commons.logger.Ln;

public class LostScreen extends OnlineGameScreen implements BackPressListener {
    private static final String TAG = "LOST";

    @NonNull
    private final SoundBar mSoundBar;

    @NonNull
    private final GameSettings mSettings = Dependencies.getSettings();

    private View mView;
    private Session mSession;

    public LostScreen(@NonNull BattleshipActivity parent, @NonNull Game game, @NonNull Session session) {
        super(parent, game, session.opponent.getName());
        mSession = session;

        mSoundBar = createSoundBar(parent, "lost.ogg");
        mSoundBar.play();
        mSettings.incrementGamesPlayedCounter();
    }

    private SoundBar createSoundBar(@NonNull Context context, @NonNull String soundName) {
        if (mSettings.isSoundOn()) {
            return SoundBarFactory.create(context, soundName);
        } else {
            return new NullSoundBar();
        }
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
        setScreen(ScreenCreator.newBoardSetupScreen(mGame, mSession));
    }

    @Override
    public String toString() {
        return TAG + debugSuffix();
    }
}
