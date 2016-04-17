package com.ivygames.morskoiboi.variant;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.Bitmaps;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.Ship;

import java.util.Collection;

public class AmericanRules extends AbstractRules {

    private static final int[] TOTAL_SHIPS = {5, 4, 3, 3, 2};
    private final Resources mResources;

    public AmericanRules(Resources resources) {
        this.mResources = resources;
    }

    @Override
    public boolean isCellConflicting(Cell cell) {
        return cell.isReserved() && cell.getProximity() >= Cell.RESERVED_PROXIMITY_VALUE * 2;
    }

    @Override
    public int[] getTotalShips() {
        return TOTAL_SHIPS;
    }

    @Override
    public int calcTotalScores(@NonNull Collection<Ship> ships, @NonNull Game game) {
        // TODO: implement
        return 0;
    }

    @Override
    public Bitmap getBitmapForShipSize(int size) {
        Bitmaps bitmaps = Bitmaps.getInstance();
        switch (size) {
            case 5:
                return bitmaps.getBitmap(mResources, R.drawable.aircraft_carrier);
            case 4:
                return bitmaps.getBitmap(mResources, R.drawable.battleship);
            case 3:
                return bitmaps.getBitmap(mResources, R.drawable.frigate);
            case 2:
            default:
                return bitmaps.getBitmap(mResources, R.drawable.gunboat);
        }
    }

    @Override
    public int[] newShipTypesArray() {
        return new int[]{5, 4, 3, 2};
    }
}
