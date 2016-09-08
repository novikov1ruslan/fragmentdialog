package com.ivygames.morskoiboi.renderer;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.Bitmaps;
import com.ivygames.morskoiboi.GraphicsUtils;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;

import java.util.HashMap;
import java.util.Map;

public class SetupBoardRenderer extends BaseBoardRenderer {

    private Resources mResources;
    private final SetupBoardGeometryProcessor mPresenter;
    private Rect mSrc = new Rect();
    @NonNull
    private final Paint mConflictCellPaint;
    @NonNull
    private final Paint mShipBorderPaint;
    @NonNull
    private final Map<Integer, Bitmap> mVerticalBitmaps = new HashMap<>();
    @NonNull
    private final Matrix mRotationMatrix = new Matrix();
    @NonNull
    private final Rect mDest = new Rect();

    public SetupBoardRenderer(@NonNull Resources res, @NonNull SetupBoardGeometryProcessor processor) {
        super(res, processor);
        mResources = res;

        mPresenter = processor;
        mConflictCellPaint = GraphicsUtils.newFillPaint(res, R.color.conflict_cell);
        mShipBorderPaint = GraphicsUtils.newStrokePaint(res, R.color.line, R.dimen.ship_border);
        mRotationMatrix.postRotate(90);
    }

    @Override
    public Rect drawShip(@NonNull Canvas canvas, @NonNull Ship ship) {
        Rect rect = super.drawShip(canvas, ship);

        mDest.set(rect.left + 1, rect.top + 1, rect.right - 1, rect.bottom - 1);
        Bitmap bitmap = getTopBitmapForShipSize(ship);
        mSrc.right = bitmap.getWidth();
        mSrc.bottom = bitmap.getHeight();
        canvas.drawBitmap(bitmap, mSrc, mDest, null);

        return rect;
    }

    private Bitmap getTopBitmapForShipSize(@NonNull Ship ship) {
        Bitmap horizontalBitmap = Bitmaps.getTopBitmapForShipSize(mResources, ship.getSize());
        if (ship.isHorizontal()) {
            return horizontalBitmap;
        } else {
            Bitmap verticalBitmap = mVerticalBitmaps.get(ship.getSize());
            if (verticalBitmap == null) {
                verticalBitmap = rotate(horizontalBitmap);
                mVerticalBitmaps.put(ship.getSize(), verticalBitmap);
            }

            return verticalBitmap;
        }
    }

    private Bitmap rotate(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, mRotationMatrix, true);
    }

    @NonNull
    @Override
    protected Paint getShipPaint() {
        return mShipBorderPaint;
    }

    public void renderConflictingCell(@NonNull Canvas canvas, int i, int j) {
        Rect invalidRect = mPresenter.getRectForCell(i, j);
        canvas.drawRect(invalidRect, mConflictCellPaint);
    }

    public void drawDockedShip(@NonNull Canvas canvas, @NonNull Ship dockedShip) {
        Bitmap bitmap = Bitmaps.getSideBitmapForShipSize(mResources, dockedShip.getSize());
        Point center = mPresenter.getShipDisplayAreaCenter();
        int displayLeft = center.x - bitmap.getWidth() / 2;
        int displayTop = center.y - bitmap.getHeight() / 2;
        canvas.drawBitmap(bitmap, displayLeft, displayTop, null);

        drawRect(canvas, mPresenter.getRectForDockedShip(dockedShip));
    }

    public void drawPickedShip(@NonNull Canvas canvas, @NonNull Ship ship, int x, int y) {
        drawRect(canvas, mPresenter.getPickedShipRect(ship, x, y));
    }

    public void drawAiming(@NonNull Canvas canvas, @NonNull Ship ship, @NonNull Vector2 coordinate) {
        AimingG aiming = mPresenter.getAimingForShip(ship, coordinate.getX(), coordinate.getY());
        drawAiming(canvas, aiming);
    }

    @NonNull
    public Vector2 updatePickedGeometry(@NonNull Ship ship, int x, int y) {
        return mPresenter.updatePickedGeometry(ship, x, y);
    }

    public boolean isInDockArea(int x, int y) {
        return mPresenter.isInDockArea(x, y);
    }

}
