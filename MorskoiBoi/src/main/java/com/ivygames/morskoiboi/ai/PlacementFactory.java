package com.ivygames.morskoiboi.ai;

import com.ivygames.morskoiboi.Placement;

public final class PlacementFactory {

    private static Placement sPlacementAlgorithm;

    private PlacementFactory() {
        // factory
    }

    public static void setPlacementAlgorithm(Placement algorithm) {

        sPlacementAlgorithm = algorithm;
    }

    public static Placement getAlgorithm() {
        return sPlacementAlgorithm;
    }

}
