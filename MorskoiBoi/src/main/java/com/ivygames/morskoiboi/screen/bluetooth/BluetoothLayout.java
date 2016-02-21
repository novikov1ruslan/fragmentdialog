package com.ivygames.morskoiboi.screen.bluetooth;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.screen.view.NotepadLinearLayout;

import org.commons.logger.Ln;

public class BluetoothLayout extends NotepadLinearLayout implements View.OnClickListener {

    public interface BluetoothActions {
        void createGame();

        void joinGame();
    }

    private BluetoothActions mListener;

    public BluetoothLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setListener(BluetoothActions screenActions) {
        mListener = screenActions;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        findViewById(R.id.create_game_btn).setOnClickListener(this);
        findViewById(R.id.join_game_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_game_btn:
                mListener.createGame();
                break;

            case R.id.join_game_btn:
                mListener.joinGame();
                break;

            default:
                Ln.w("unprocessed bt button=" + v.getId());
                break;
        }
    }

    public void enable() {
        setEnabled(true);
    }

    public void disable() {
        setEnabled(false);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        findViewById(R.id.create_game_btn).setEnabled(enabled);
        findViewById(R.id.join_game_btn).setEnabled(enabled);
    }
}
