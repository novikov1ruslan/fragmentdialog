package com.ivygames.morskoiboi.ui.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.ui.GameplayLayoutInterface;

public abstract class AbstractGameplayLayout extends ViewGroup implements GameplayLayoutInterface, TimeConsumer {

    private static final float ASPECT_RATIO = 1.5f;
    private Rect aspectRect = new Rect();
    private Rect enemyRect = new Rect();
    private Rect myRect = new Rect();
    private Rect statusRect = new Rect();
    private Rect chatRect = new Rect();
    private Rect turnRect = new Rect();
    //    private Paint aspectPaint = new Paint();
    private View enemyBoard;
    private View myBoard;
    private View statusBoard;
    private View chat;
    private View turn;

    public AbstractGameplayLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
//        aspectPaint.setStyle(Paint.Style.FILL);
//        aspectPaint.setColor(0xFF0000);
//        aspectPaint.setARGB(255, 255, 0, 0);
//        setWillNotDraw(false);
    }

//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        canvas.drawRect(aspectRect, aspectPaint);
//    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        enemyBoard = findViewById(R.id.enemy_board);
        myBoard = findViewById(R.id.board_view_fleet);
        statusBoard = findViewById(R.id.status_container);

        chat = findViewById(R.id.chat_button);
        turn = findViewById(R.id.timer);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        enemyBoard.layout(enemyRect.left, enemyRect.top, enemyRect.right, enemyRect.bottom);
        myBoard.layout(myRect.left, myRect.top, myRect.right, myRect.bottom);
        statusBoard.layout(statusRect.left, statusRect.top, statusRect.right, statusRect.bottom);
        chat.layout(chatRect.left, chatRect.top, chatRect.right, chatRect.bottom);
        turn.layout(turnRect.left, turnRect.top, turnRect.right, turnRect.bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        float w = getMeasuredWidth();
        float h = getMeasuredHeight();

        float aspect = h / w;
        int desiredWidth = getMeasuredWidth();
        int desiredHeight = getMeasuredHeight();
        if (aspect > ASPECT_RATIO) {
            desiredHeight = (int) (desiredWidth * ASPECT_RATIO);
        } else {
            desiredWidth = (int) (desiredHeight / ASPECT_RATIO);
        }

        aspectRect.left = (int) ((w - desiredWidth) / 2);
        aspectRect.right = aspectRect.left + desiredWidth;
        aspectRect.top = (int) ((h - desiredHeight) / 2);
        aspectRect.bottom = aspectRect.top + desiredHeight;

        enemyRect.left = aspectRect.left;
        enemyRect.right = aspectRect.right;
        enemyRect.bottom = aspectRect.bottom;
        enemyRect.top = enemyRect.bottom - aspectRect.width();

        myRect.left = aspectRect.left;
        myRect.right = myRect.left + desiredWidth / 2;
        myRect.bottom = enemyRect.top;

        statusRect.left = myRect.right;
        statusRect.right = statusRect.left + desiredWidth / 2;
        statusRect.bottom = enemyRect.top;

        int buttonHeight = statusRect.height() / 4;
        int buttonWidth = statusRect.width() / 2;

        turnRect.left = statusRect.left;
        turnRect.bottom = statusRect.bottom;
        turnRect.top = turnRect.bottom - buttonHeight;
        turnRect.right = turnRect.left + buttonWidth;

        chatRect.left = turnRect.right;
        chatRect.top = turnRect.top;
//        chatRect.right = chatRect.left + buttonHeight;
        chatRect.right = chatRect.left + buttonWidth;
        chatRect.bottom = chatRect.top + buttonHeight;

        // fixing
        statusRect.bottom = chatRect.top;

        int widthSpec = MeasureSpec.makeMeasureSpec(enemyRect.width(), MeasureSpec.EXACTLY);
        int heightSpec = MeasureSpec.makeMeasureSpec(enemyRect.height(), MeasureSpec.EXACTLY);
        enemyBoard.measure(widthSpec, heightSpec);

        widthSpec = MeasureSpec.makeMeasureSpec(myRect.width(), MeasureSpec.AT_MOST);
        heightSpec = MeasureSpec.makeMeasureSpec(myRect.height(), MeasureSpec.AT_MOST);
        myBoard.measure(widthSpec, heightSpec);

        widthSpec = MeasureSpec.makeMeasureSpec(statusRect.width(), MeasureSpec.AT_MOST);
        heightSpec = MeasureSpec.makeMeasureSpec(statusRect.height(), MeasureSpec.AT_MOST);
        statusBoard.measure(widthSpec, heightSpec);

        widthSpec = MeasureSpec.makeMeasureSpec(chatRect.width(), MeasureSpec.EXACTLY);
        heightSpec = MeasureSpec.makeMeasureSpec(chatRect.height(), MeasureSpec.EXACTLY);
        chat.measure(widthSpec, heightSpec);

        widthSpec = MeasureSpec.makeMeasureSpec(turnRect.width(), MeasureSpec.EXACTLY);
        heightSpec = MeasureSpec.makeMeasureSpec(turnRect.height(), MeasureSpec.EXACTLY);
        turn.measure(widthSpec, heightSpec);
    }
}
