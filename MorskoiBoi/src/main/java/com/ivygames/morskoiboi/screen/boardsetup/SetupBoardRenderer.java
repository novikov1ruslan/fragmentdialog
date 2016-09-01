package com.ivygames.morskoiboi.screen.boardsetup;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.ivygames.morskoiboi.Bitmaps;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.screen.view.BaseBoardRenderer;

class SetupBoardRenderer extends BaseBoardRenderer {

    private Bitmap m2er;
    private Rect mSrc = new Rect();

    public SetupBoardRenderer(Resources res) {
        super(res);
        m2er = Bitmaps.getBitmap(res, R.drawable._2er);
    }

    @Override
    public void drawShip(Canvas canvas, Rect ship) {
//        super.drawShip(canvas, ship);
        mSrc.right = m2er.getWidth();
        mSrc.bottom = m2er.getHeight();

        canvas.save();
        int h = ship.height();
        int w = ship.width();
        int cells = 3;
        if ((w / h) == cells || (h / w) == cells) {
//                if ((w / h) == cells) {
//                    canvas.rotate(90f);
//                }
            canvas.drawBitmap(m2er, mSrc, ship, null);
        }
        canvas.restore();
    }
}
