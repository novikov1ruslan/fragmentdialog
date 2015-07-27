package com.ivygames.morskoiboi.variant;

import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.utils.GameUtils;

import java.util.Collection;

public class AmericanPlacement extends AbstractPlacement {

    @Override
    public Collection<Ship> generateFullFleet() {
        return GameUtils.generateFullFleet(new int[]{5, 4, 3, 3, 2});
    }
}
