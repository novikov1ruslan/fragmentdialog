package com.ivygames.morskoiboi.screen.gameplay;

import android.content.Context;
import android.util.AttributeSet;

import com.ivygames.morskoiboi.screen.view.CommonGameplayLayout;

public abstract class OldHandsetGameplayLayout extends CommonGameplayLayout implements GameplayLayoutInterface {

    private static final float ASPECT_RATIO = 1.5f;

    public OldHandsetGameplayLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int desiredWidth = calcAspectRect();

        mEnemyBoardRect.left = mAspectRect.left;
        mEnemyBoardRect.right = mAspectRect.right;
        mEnemyBoardRect.bottom = mAspectRect.bottom;
        mEnemyBoardRect.top = mEnemyBoardRect.bottom - mAspectRect.width();

        mMyBoardRect.left = mAspectRect.left;
        mMyBoardRect.right = mMyBoardRect.left + desiredWidth / 2;
        mMyBoardRect.top = mAspectRect.top;
        mMyBoardRect.bottom = mEnemyBoardRect.top;

        mStatusRect.left = mMyBoardRect.right;
        mStatusRect.right = mStatusRect.left + desiredWidth / 2;
        mStatusRect.top = mAspectRect.top;
        mStatusRect.bottom = mEnemyBoardRect.top;

        int buttonHeight = mStatusRect.height() / 4;
        int buttonWidth = mStatusRect.width() / 2;

        mTurnTimerRect.left = mStatusRect.left;
        mTurnTimerRect.bottom = mStatusRect.bottom;
        mTurnTimerRect.top = mTurnTimerRect.bottom - buttonHeight;
        mTurnTimerRect.right = mTurnTimerRect.left + buttonWidth;

        mChatRect.left = mTurnTimerRect.right;
        mChatRect.top = mTurnTimerRect.top;
        mChatRect.right = mChatRect.left + buttonWidth;
        mChatRect.bottom = mChatRect.top + buttonHeight;

        // fixing
        mStatusRect.bottom = mChatRect.top;

        int widthSpec = MeasureSpec.makeMeasureSpec(mEnemyBoardRect.width(), MeasureSpec.EXACTLY);
        int heightSpec = MeasureSpec.makeMeasureSpec(mEnemyBoardRect.height(), MeasureSpec.EXACTLY);
        mEnemyBoard.measure(widthSpec, heightSpec);

        widthSpec = MeasureSpec.makeMeasureSpec(mMyBoardRect.width(), MeasureSpec.AT_MOST);
        heightSpec = MeasureSpec.makeMeasureSpec(mMyBoardRect.height(), MeasureSpec.AT_MOST);
        mMyBoard.measure(widthSpec, heightSpec);

        widthSpec = MeasureSpec.makeMeasureSpec(mStatusRect.width(), MeasureSpec.AT_MOST);
        heightSpec = MeasureSpec.makeMeasureSpec(mStatusRect.height(), MeasureSpec.AT_MOST);
        mStatusBoard.measure(widthSpec, heightSpec);

        widthSpec = MeasureSpec.makeMeasureSpec(mChatRect.width(), MeasureSpec.EXACTLY);
        heightSpec = MeasureSpec.makeMeasureSpec(mChatRect.height(), MeasureSpec.EXACTLY);
        mChatView.measure(widthSpec, heightSpec);

        widthSpec = MeasureSpec.makeMeasureSpec(mTurnTimerRect.width(), MeasureSpec.EXACTLY);
        heightSpec = MeasureSpec.makeMeasureSpec(mTurnTimerRect.height(), MeasureSpec.EXACTLY);
        mTurnTimerView.measure(widthSpec, heightSpec);

        mEnemyBoard.layout(mEnemyBoardRect.left, mEnemyBoardRect.top, mEnemyBoardRect.right, mEnemyBoardRect.bottom);
        mMyBoard.layout(mMyBoardRect.left, mMyBoardRect.top, mMyBoardRect.right, mMyBoardRect.bottom);
        mStatusBoard.layout(mStatusRect.left, mStatusRect.top, mStatusRect.right, mStatusRect.bottom);
        mChatView.layout(mChatRect.left, mChatRect.top, mChatRect.right, mChatRect.bottom);
        mTurnTimerView.layout(mTurnTimerRect.left, mTurnTimerRect.top, mTurnTimerRect.right, mTurnTimerRect.bottom);

        widthSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY);
        heightSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY);
        mGameOver.measure(widthSpec, heightSpec);
        mGameOver.layout(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }

    private int calcAspectRect() {
        float w = getMeasuredWidth();
        float h = getMeasuredHeight();

        float aspect = h / w;
        float desiredWidth = w;
        float desiredHeight = h;
        if (aspect > ASPECT_RATIO) {
            desiredHeight = (int) (desiredWidth * ASPECT_RATIO);
        } else {
            desiredWidth = (int) (desiredHeight / ASPECT_RATIO);
        }

        mAspectRect.left = (int) ((w - desiredWidth) / 2);
        mAspectRect.right = (int) (mAspectRect.left + desiredWidth);
        mAspectRect.top = (int) ((h - desiredHeight) / 2);
        mAspectRect.bottom = (int) (mAspectRect.top + desiredHeight);
        return (int) desiredWidth;
    }
}
