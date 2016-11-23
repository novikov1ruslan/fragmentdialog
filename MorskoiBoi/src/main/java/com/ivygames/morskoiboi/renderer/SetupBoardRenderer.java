package com.ivygames.morskoiboi.renderer;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.NonNull;

import com.ivygames.battleship.board.Vector;
import com.ivygames.battleship.ship.Ship;
import com.ivygames.morskoiboi.Bitmaps;
import com.ivygames.morskoiboi.GraphicsUtils;
import com.ivygames.morskoiboi.R;

import java.util.HashMap;
import java.util.Map;

public class SetupBoardRenderer extends BaseBoardRenderer {

    @NonNull
    private final Resources mResources;
    @NonNull
    private final SetupBoardGeometryProcessor mProcessor;
    @NonNull
    private final Rect mSrc = new Rect();
    @NonNull
    private final Paint mConflictCellPaint;
    @NonNull
    private final Map<Integer, Bitmap> mVerticalBitmaps = new HashMap<>();
    @NonNull
    private final Matrix mRotationMatrix = new Matrix();
    @NonNull
    private final Rect mDest = new Rect();
    @NonNull
    private final Paint mLinePaint;
    @NonNull
    private final Paint mDockedShipPaint;
    @NonNull
    private final Paint mShipBorderPaint;

    public SetupBoardRenderer(@NonNull Resources res, @NonNull SetupBoardGeometryProcessor processor) {
        super(res, processor);
        mResources = res;

        mProcessor = processor;
        mConflictCellPaint = GraphicsUtils.newFillPaint(res, R.color.conflict_cell);
        mShipBorderPaint = GraphicsUtils.newStrokePaint(res, R.color.line, R.dimen.ship_border);
        mDockedShipPaint = GraphicsUtils.newStrokePaint(res, R.color.ship_border, R.dimen.ship_border);
        mLinePaint = GraphicsUtils.newStrokePaint(res, R.color.line);
        mRotationMatrix.postRotate(90);
    }

//    @Override
//    public void drawShip(@NonNull Canvas canvas, @NonNull Ship ship) {
//        Rect rectForShip = mProcessor.getRectForShip(ship);
//        canvas.drawRect(rectForShip, mShipBorderPaint);
//
//        drawShipInRect(canvas, ship, rectForShip);
//    }

    private void drawShipInRect(@NonNull Canvas canvas, @NonNull Ship ship, Rect rect) {
        mDest.set(rect.left + 1, rect.top + 1, rect.right - 1, rect.bottom - 1);
        Bitmap bitmap = getTopBitmapForShipSize(ship);
        mSrc.right = bitmap.getWidth();
        mSrc.bottom = bitmap.getHeight();
        canvas.drawBitmap(bitmap, mSrc, mDest, null);
    }

    private Bitmap getTopBitmapForShipSize(@NonNull Ship ship) {
        Bitmap horizontalBitmap = Bitmaps.getTopBitmapForShipSize(mResources, ship.size);
        if (ship.isHorizontal()) {
            return horizontalBitmap;
        } else {
            Bitmap verticalBitmap = mVerticalBitmaps.get(ship.size);
            if (verticalBitmap == null) {
                verticalBitmap = rotate(horizontalBitmap);
                mVerticalBitmaps.put(ship.size, verticalBitmap);
            }

            return verticalBitmap;
        }
    }

    private Bitmap rotate(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, mRotationMatrix, true);
    }

    public void renderConflictingCell(@NonNull Canvas canvas, @NonNull Vector v) {
        renderConflictingCell(canvas, v.x, v.y);
    }

    public void renderConflictingCell(@NonNull Canvas canvas, int i, int j) {
        Rect invalidRect = mProcessor.getRectForCell(i, j);
        canvas.drawRect(invalidRect, mConflictCellPaint);
    }

    public void drawDockedShip(@NonNull Canvas canvas, @NonNull Ship dockedShip) {
        Bitmap bitmap = Bitmaps.getSideBitmapForShipSize(mResources, dockedShip.size);
        Point center = mProcessor.getShipDisplayAreaCenter();
        int displayLeft = center.x - bitmap.getWidth() / 2;
        int displayTop = center.y - bitmap.getHeight() / 2;
        canvas.drawBitmap(bitmap, displayLeft, displayTop, null);

        Rect rectForDockedShip = mProcessor.getRectForDockedShip(dockedShip);
        for (int i = 1; i < dockedShip.size; i++) {
            int x = rectForDockedShip.left + i * mProcessor.getCellSize();
            canvas.drawLine(x, rectForDockedShip.top, x, rectForDockedShip.bottom, mLinePaint);
        }
        canvas.drawRect(rectForDockedShip, mDockedShipPaint);
    }

    public void drawPickedShip(@NonNull Canvas canvas, @NonNull Ship ship, int x, int y) {
        Rect pickedShipRect = mProcessor.getPickedShipRect(ship, x, y);
        canvas.drawRect(pickedShipRect, mDockedShipPaint);

//        drawShipInRect(canvas, ship, pickedShipRect);
    }

    public void drawAiming(@NonNull Canvas canvas, @NonNull Ship ship, @NonNull Vector v) {
        drawAiming(canvas, mProcessor.getAimingForShip(ship, v.x, v.y), false);
    }

    @NonNull
    public Vector getPickedShipCoordinate(@NonNull Ship ship, int x, int y) {
        return mProcessor.getPickedShipCoordinate(ship, x, y);
    }

    public boolean isInDockArea(int x, int y) {
        return mProcessor.isInDockArea(x, y);
    }

}
