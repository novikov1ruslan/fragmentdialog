package com.ivygames.morskoiboi.variant;

import android.graphics.Bitmap;

import com.ivygames.morskoiboi.Bitmaps;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.model.Cell;
import com.ivygames.morskoiboi.model.Game;
import com.ivygames.morskoiboi.model.Ship;

import java.util.Collection;

public class AmericanRules extends AbstractRules {

    private static final int[] TOTAL_SHIPS = {5, 4, 3, 3, 2};

    @Override
    public boolean isCellConflicting(Cell cell) {
        return cell.getProximity() >= Cell.RESERVED_PROXIMITY_VALUE * 2;
    }

    @Override
    public int[] getTotalShips() {
        return TOTAL_SHIPS;
    }

    @Override
    public int calcTotalScores(Collection<Ship> ships, Game game) {
        // TODO: implement
        return 0;
    }

    @Override
    public Bitmap getBitmapForShipSize(int size) {
        Bitmaps bitmaps = Bitmaps.getInstance();
        switch (size) {
            case 5:
                return bitmaps.getBitmap(R.drawable.aircraft_carrier);
            case 4:
                return bitmaps.getBitmap(R.drawable.battleship);
            case 3:
                return bitmaps.getBitmap(R.drawable.frigate);
            case 2:
            default:
                return bitmaps.getBitmap(R.drawable.gunboat);
        }
    }

    @Override
    public int[] newShipTypesArray() {
        return new int[]{5, 4, 3, 2};
    }
}
