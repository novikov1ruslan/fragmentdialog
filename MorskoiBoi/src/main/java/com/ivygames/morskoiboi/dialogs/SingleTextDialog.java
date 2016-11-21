package com.ivygames.morskoiboi.dialogs;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ivygames.morskoiboi.R;

public class SingleTextDialog extends RelativeLayout {

    private TextView mText;

    public SingleTextDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SingleTextDialog(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mText = (TextView) findViewById(R.id.text);
    }

    public void setText(CharSequence text) {
        mText.setText(text);
    }

    public void setText(int resid) {
        mText.setText(resid);
    }

}
