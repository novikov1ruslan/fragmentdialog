package com.ivygames.morskoiboi.ui;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;

import com.ivygames.morskoiboi.R;

import org.apache.commons.lang3.Validate;

public class GameOverScreen extends OnlineGameScreen {

    private Bitmap mBitmap;
    private View mLayout;
    private long mTimeout;
    private Runnable mCommand;

    public GameOverScreen(Bitmap bitmap, long timeout, Runnable command) {
        mTimeout = timeout;
        mCommand = command;
        mBitmap = Validate.notNull(bitmap);
    }

    @Override
    public View onCreateView(ViewGroup container) {
        mLayout = getLayoutInflater().inflate(R.layout.game_over, container, false);
//        ImageView image = (ImageView) mLayout.findViewById(R.id.bitmap);
//        image.setColorFilter(cf);
//        image.setImageBitmap(mBitmap);
        return mLayout;
    }

    @Override
    public View getView() {
        return mLayout;
    }
}
