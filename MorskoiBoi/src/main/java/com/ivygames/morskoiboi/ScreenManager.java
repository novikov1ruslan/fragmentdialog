package com.ivygames.morskoiboi;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ivygames.morskoiboi.model.ChatMessage;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.main.MainScreen;
import com.ivygames.morskoiboi.utils.UiUtils;

import org.commons.logger.Ln;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;

public class ScreenManager {

    private static final Configuration CONFIGURATION_LONG = new Configuration.Builder().setDuration(Configuration.DURATION_LONG).build();

    private Activity mActivity;
    @NonNull
    private final ViewGroup mLayout;

    @NonNull
    private final FrameLayout mContainer;

    private BattleshipScreen mCurrentScreen;

    @Nullable
    private View mTutView;

    private boolean mStarted;
    private boolean mResumed;

    public ScreenManager(@NonNull Activity activity, @NonNull ViewGroup layout) {
        mActivity = activity;
        mLayout = layout;
        mContainer = (FrameLayout) mLayout.findViewById(R.id.container);
    }

    public void hideNoAdsButton() {
        if (mCurrentScreen instanceof MainScreen) {
            ((MainScreen) mCurrentScreen).hideNoAdsButton();
        }
    }

    public void onStart() {
        mCurrentScreen.onStart();
        mStarted = true;
    }

    public void onResume() {
        mCurrentScreen.onResume();
        mResumed = true;
    }

    public void onPause() {
        mCurrentScreen.onPause();
        mResumed = false;
    }

    public void onStop() {
        mCurrentScreen.onStop();
        mStarted = false;
    }

    public void dismissTutorial() {
        if (mTutView == null) {
            Ln.d("no tutorial view to remove");
            return;
        }

        Ln.v("tutorial view present - removing");
        mContainer.removeView(mTutView);
        mTutView = null;
    }


    public void showTutorial(@Nullable View view) {
        if (mTutView == null) {
            mTutView = view;
            if (mTutView != null) {
                mContainer.addView(mTutView);
            }
        } else {
            Ln.d("tutorial view already shown: " + mTutView);
        }
    }

    public boolean handleBackPress() {
        Ln.v("top screen = " + mCurrentScreen);

        if (mTutView != null) {
            Ln.v("tutorial view present - removing");
            mContainer.removeView(mTutView);
            mTutView = null;
            return true;
        }

        if (mCurrentScreen instanceof BackPressListener) {
            Ln.v("propagating backpress");
            if (mCurrentScreen.isResumed()) {
                ((BackPressListener) mCurrentScreen).onBackPressed();
            } else {
                Ln.w("back pressed to fast for " + mCurrentScreen);
            }
            return true;
        }
        return false;
    }

    public void showChatCrouton(ChatMessage message) {
        if (mStarted) {
            View layout = UiUtils.inflateChatCroutonLayout(mActivity.getLayoutInflater(), message.getText(), mLayout);
            Crouton.make(mActivity, layout).setConfiguration(CONFIGURATION_LONG).show();
        }
    }

    public void showInvitationCrouton(String message) {
        View view = UiUtils.inflateInfoCroutonLayout(mActivity.getLayoutInflater(), message, mLayout);
        Crouton.make(mActivity, view).setConfiguration(CONFIGURATION_LONG).show();
    }

    public BattleshipScreen setScreen(BattleshipScreen screen) {
        View oldView = null;

        if (mCurrentScreen != null) {
//            if (mCurrentScreen.getMusic() != screen.getMusic()) {
//                mMusicPlayer.stop();
//            }

            oldView = mCurrentScreen.getView();
            mCurrentScreen.onPause();
            mCurrentScreen.onStop();
            mCurrentScreen.onDestroy();
        }

        mCurrentScreen = screen;
        View view = mCurrentScreen.onCreateView(mContainer);

        mContainer.addView(view);
        if (oldView != null) {
            mContainer.removeView(oldView);
        }

        if (mStarted) {
            mCurrentScreen.onStart();
            if (mResumed) {
                mCurrentScreen.onResume();
            }
        }
        return mCurrentScreen;
    }

    public boolean isStarted() {
        return mStarted;
    }

    public void onSignInSucceeded() {
        if (mCurrentScreen instanceof SignInListener) {
            ((SignInListener) mCurrentScreen).onSignInSucceeded();
        }
    }
}
