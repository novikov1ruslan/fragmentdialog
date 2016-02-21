package com.ivygames.morskoiboi.screen.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.Button;

import com.ivygames.morskoiboi.R;

import org.commons.logger.Ln;

public class InvitationButton extends Button {

    private final Bitmap mInvitation;
    private int mInvitationLeft;
    private int mInvitationTop;
    private boolean mShowInvitation;

    public InvitationButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInvitation = createInvitationBitmap();
    }

    public InvitationButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mInvitation = createInvitationBitmap();
    }

    private Bitmap createInvitationBitmap() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.invitation);
        if (bitmap == null) {
            Ln.e("decoding invitation bitmap");
        }
        return bitmap;
    }

    public void showInvitation() {
        mShowInvitation = true;
        invalidate();
    }

    public void hideInvitation() {
        mShowInvitation = false;
        invalidate();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (mShowInvitation) {
            if (mInvitation != null) {
                canvas.drawBitmap(mInvitation, mInvitationLeft, mInvitationTop, null);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mInvitation != null) {
            mInvitationLeft = w - mInvitation.getWidth() - getPaddingRight();
            mInvitationTop = (h - mInvitation.getHeight()) / 2;
        }
    }
}
