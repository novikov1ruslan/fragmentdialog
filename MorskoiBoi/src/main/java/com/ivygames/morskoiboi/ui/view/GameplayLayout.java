package com.ivygames.morskoiboi.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.ui.GameplayLayoutInterface;
import com.ivygames.morskoiboi.ui.view.EnemyBoardView.ShotListener;

import org.commons.logger.Ln;

import java.util.Collection;

public class GameplayLayout extends LinearLayout implements View.OnClickListener, GameplayLayoutInterface {

    private FleetBoardView mMyBoardView;
    private EnemyBoardView mEnemyBoardView;
    private FleetView mFleet;
    private TextView mPlayerNameView;
    private TextView mEnemyNameView;
    private View mChatButton;
    private ImageButton mSoundButton;
//    private ImageButton mVibrationButton;

    private final Animation mShake;
    private long mStartTime;
    private long mUnlockedTime;
    private boolean mGameIsOn;
    private Bitmap mBwBitmap;
    private TimerViewInterface mTimerView;
    private GameplayLayoutListener mListener;
    private TextView mSettingBoardText;

    public GameplayLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mShake = AnimationUtils.loadAnimation(context, R.anim.shake);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mMyBoardView = (FleetBoardView) findViewById(R.id.board_view_fleet);
        mEnemyBoardView = (EnemyBoardView) findViewById(R.id.board_view_enemy);
        mFleet = (FleetView) findViewById(R.id.status);
        mChatButton = findViewById(R.id.chat_button);
        mChatButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onChatClicked();
                }
            }
        });
        mPlayerNameView = (TextView) findViewById(R.id.player);
        mEnemyNameView = (TextView) findViewById(R.id.enemy);
        mTimerView = (TimerViewInterface) findViewById(R.id.timer);

//        mVibrationButton = (ImageButton) findViewById(R.id.vibration_btn);
//        if (mVibrationButton != null) {
//            Ln.v("vibration control present");
//            mVibrationButton.setOnClickListener(this);
//        }
        mSoundButton = (ImageButton) findViewById(R.id.sound_btn);
        if (mSoundButton != null) {
            mSoundButton.setOnClickListener(this);
        }

        mSettingBoardText = (TextView) findViewById(R.id.setting_board_notification);
    }

    public void setSound(boolean on) {
        if (mSoundButton != null) {
            mSoundButton.setImageResource(on ? R.drawable.sound_on : R.drawable.sound_off);
        }
    }

//    public void setVibration(boolean on) {
//        if (mVibrationButton != null) {
//            mVibrationButton.setImageResource(on ? R.drawable.vibrate_on : R.drawable.vibrate_off);
//        }
//    }

    public void setAim(Vector2 aim) {
        mEnemyBoardView.setAim(aim);
    }

    public void removeAim() {
        mEnemyBoardView.removeAim();
    }

    public long getUnlockedTime() {
        return mUnlockedTime;
    }

    public void setPlayerName(CharSequence name) {
        if (mPlayerNameView != null) {
            mPlayerNameView.setText(name);
        }
    }

    public void setEnemyName(CharSequence name) {
        if (mEnemyNameView != null) {
            mEnemyNameView.setText(name);
        }
    }

    public void setPlayerBoard(Board board) {
        mMyBoardView.setBoard(board);
    }

    public void setEnemyBoard(Board board) {
        mEnemyBoardView.setBoard(board);
        invalidate();
    }

    public void setMyShips(Collection<Ship> ships) {
        if (mFleet == null) {
            return;
        }
        mFleet.setMyShips(ships);
    }

    public void setEnemyShips(Collection<Ship> ships) {
        if (mFleet == null) {
            return;
        }
        mFleet.setEnemyShips(ships);
    }

    public void setShotListener(ShotListener listener) {
        mEnemyBoardView.setShotListener(listener);
    }

    public void unLock() {
        mGameIsOn = true;
        mEnemyBoardView.unLock();
        mStartTime = SystemClock.elapsedRealtime();
    }

    public boolean isLocked() {
        return mEnemyBoardView.isLocked();
    }

    public void lock() {
        mEnemyBoardView.lock();

        // game is on after the first unlock
        if (!mGameIsOn) {
            return;
        }

        long d = SystemClock.elapsedRealtime() - mStartTime;
        mUnlockedTime += d;
        Ln.v("d = " + d + ", mUnlockedTime=" + mUnlockedTime);
    }

    /**
     * locks and sets border
     */
    public void enemyTurn() {
        lock();
        mEnemyBoardView.hideTurnBorder();
        mMyBoardView.showTurnBorder();
    }

    /**
     * unlocks and sets border
     */
    public void playerTurn() {
        unLock();
        mEnemyBoardView.showTurnBorder();
        mMyBoardView.hideTurnBorder();
    }

    public void invalidateEnemyBoard() {
        mEnemyBoardView.invalidate();
    }

    public void invalidatePlayerBoard() {
        mMyBoardView.invalidate();
    }

    public void shakePlayerBoard() {
        mMyBoardView.startAnimation(mShake);
    }

    public void shakeEnemyBoard() {
        mEnemyBoardView.startAnimation(mShake);
    }

    public void win() {
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(2);
        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(cm);
        gameOver(cf);
    }

    public void lost() {
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(cm);
        gameOver(cf);
    }

    private void gameOver(ColorMatrixColorFilter cf) {
        Bitmap bitmap = createScreenBitmap();
        if (bitmap != null) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                child.setVisibility(GONE);
            }

            // ColorMatrix cm1 = new ColorMatrix();
            // cm1.set(new float[] {
            // 1.5f, 1.5f, 1.5f, 0, 0,
            // 1.5f, 1.5f, 1.5f, 0, 0,
            // 1.5f, 1.5f, 1.5f, 0, 0,
            // -1f, -1f, -1f, 0f, 1f});

            ImageView bw = (ImageView) findViewById(R.id.bw);
            bw.setColorFilter(cf);
            bw.setImageBitmap(bitmap);

            bw.setVisibility(VISIBLE);
            bw.startAnimation(mShake);
        }
    }

    private Bitmap createScreenBitmap() {
        setDrawingCacheEnabled(true);
        buildDrawingCache();
        Bitmap drawingCache = getDrawingCache();
        Bitmap bitmap = null;
        if (drawingCache != null) {
            /*
             * [main] java.lang.NullPointerException at android.graphics.Bitmap.createBitmap(Bitmap.java:455) at com.ivygames
			 * .morskoiboi.ui.view.GameplayLayout.createBwBitmap(GameplayLayout .java:242)
			 */
            bitmap = Bitmap.createBitmap(drawingCache);
        }
        setDrawingCacheEnabled(false);

        return bitmap;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mBwBitmap != null) {
            mBwBitmap.recycle();
            mBwBitmap = null;
        }
    }

    public void setShotResult(PokeResult result) {
        mEnemyBoardView.setShotResult(result);
    }

    public void setTotalTime(int seconds) {
        mTimerView.setTotalTime(seconds);
    }

    @Override
    public void setCurrentTime(int seconds) {
        mTimerView.setCurrentTime(seconds);
    }

    public void setAlarmTime(int alarmTimeSeconds) {
        mTimerView.setAlarmThreshold(alarmTimeSeconds);
    }

    public void setListener(GameplayLayoutListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mListener == null) {
            return;
        }

        switch (v.getId()) {
//            case R.id.vibration_btn:
//                mListener.onVibrationChanged();
//                break;

            case R.id.sound_btn:
                mListener.onSoundChanged();
                break;

            default:
                Ln.w("unprocessed game button=" + v.getId());
                break;
        }
    }

    public void hideChatButton() {
        mChatButton.setVisibility(GONE);
    }

//    public void hideVibrationSetting() {
//        if (mVibrationButton != null) {
////            mVibrationButton.setVisibility(GONE);
//        }
//    }

    public void showOpponentSettingBoardNotification(String message) {
        mSettingBoardText.setText(message);
        mSettingBoardText.setVisibility(VISIBLE);
    }

    public void hideOpponentSettingBoardNotification() {
        mSettingBoardText.setVisibility(GONE);
    }
}
