package com.ivygames.morskoiboi.ui.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.ivygames.morskoiboi.R;

public abstract class CommonGameplayLayout extends ViewGroup {
    protected final Rect mAspectRect = new Rect();
    protected final Rect mEnemyBoardRect = new Rect();
    protected final Rect mMyBoardRect = new Rect();
    protected final Rect mStatusRect = new Rect();
    protected final Rect mChatRect = new Rect();
    protected final Rect mTurnTimerRect = new Rect();

    protected View mEnemyBoard;
    protected View mMyBoard;
    protected View mStatusBoard;
    protected View mChatView;
    protected View mTurnTimerView;

    public CommonGameplayLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mEnemyBoard = findViewById(R.id.enemy_board);
        mMyBoard = findViewById(R.id.board_view_fleet);
        mStatusBoard = findViewById(R.id.status_container);

        mChatView = findViewById(R.id.chat_button);
        mTurnTimerView = findViewById(R.id.timer);
    }
}
