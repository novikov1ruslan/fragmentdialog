package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.model.Ship;

import java.util.Collection;

public class ShipUtils {
    @NonNull
    public static Collection<Ship> generateFullHorizontalFleet(@NonNull Rules rules) {
        return Ship.setOrientationForShips(generateFullFleet(rules), Ship.Orientation.HORIZONTAL);
    }

    @NonNull
    public static Collection<Ship> generateFullFleet(@NonNull Rules rules) {
        return rules.generateFullFleet();
    }
}
