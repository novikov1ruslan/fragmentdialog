package com.ivygames.morskoiboi.screen.boardsetup;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.Bitmaps;
import com.ivygames.morskoiboi.GraphicsUtils;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.screen.view.AimingG;
import com.ivygames.morskoiboi.screen.view.BaseBoardRenderer;

class SetupBoardRenderer extends BaseBoardRenderer {

    private Resources mResources;
    private final SetupBoardPresenter mPresenter;
//    private Bitmap m2er;
//    private Rect mSrc = new Rect();
    @NonNull
    private final Paint mConflictCellPaint;

    public SetupBoardRenderer(@NonNull Resources res, @NonNull SetupBoardPresenter presenter) {
        super(res, presenter);
        mResources = res;

        mPresenter = presenter;
        mConflictCellPaint = GraphicsUtils.newFillPaint(res, R.color.conflict_cell);
    }

//    @Override
//    public void drawShip(Canvas canvas, Rect ship) {
////        super.drawShip(canvas, ship);
//        mSrc.right = m2er.getWidth();
//        mSrc.bottom = m2er.getHeight();
//
//        canvas.save();
//        int h = ship.height();
//        int w = ship.width();
//        int cells = 3;
//        if ((w / h) == cells || (h / w) == cells) {
////                if ((w / h) == cells) {
////                    canvas.rotate(90f);
////                }
//            canvas.drawBitmap(m2er, mSrc, ship, null);
//        }
//        canvas.restore();
//    }

    public void renderConflictingCell(@NonNull Canvas canvas, int i, int j) {
        Rect invalidRect = mPresenter.getRectForCell(i, j);
        canvas.drawRect(invalidRect, mConflictCellPaint);
    }

    public void drawDockedShip(@NonNull Canvas canvas, @NonNull Ship dockedShip) {
        Bitmap bitmap = Bitmaps.getBitmapForShipSize(mResources, dockedShip.getSize());
        Point center = mPresenter.getShipDisplayAreaCenter();
        int displayLeft = center.x - bitmap.getWidth() / 2;
        int displayTop = center.y - bitmap.getHeight() / 2;
        canvas.drawBitmap(bitmap, displayLeft, displayTop, null);

        drawShip(canvas, mPresenter.getRectForDockedShip(dockedShip));
    }

    public void drawPickedShip(@NonNull Canvas canvas) {
        if (mPresenter.hasPickedShip()) {
            drawShip(canvas, mPresenter.getPickedShipRect());
        }
    }

    public void drawAiming(@NonNull Canvas canvas, @NonNull Vector2 coordinate) {
        AimingG aiming = mPresenter.getAiming(coordinate);
        if (aiming != null) {
            drawAiming(canvas, aiming);
        }
    }

    public Vector2 updatePickedGeometry(int x, int y) {
        return mPresenter.updatePickedGeometry(x, y);
    }

    public boolean isInDockArea(int x, int y) {
        return mPresenter.isInDockArea(x, y);
    }

    public int getTouchJ(int y) {
        return mPresenter.getTouchJ(y);
    }

    public int getTouchI(int x) {
        return mPresenter.getTouchI(x);
    }
}
