package com.ivygames.morskoiboi.screen.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ivygames.morskoiboi.R;

public class InfoCroutonLayout extends FrameLayout {

    private TextView messageView;

    public InfoCroutonLayout(Context context) {
        super(context);
    }

    public InfoCroutonLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        messageView = (TextView) findViewById(R.id.info_message);
    }

    public void setMessage(CharSequence text) {
        messageView.setText(text);
    }
}
