package com.ivygames.morskoiboi.ai;

public final class PlacementFactory {

    private static PlacementAlgorithm sPlacementAlgorithm;

    private PlacementFactory() {
        // factory
    }

    public static void setPlacementAlgorithm(PlacementAlgorithm algorithm) {

        sPlacementAlgorithm = algorithm;
    }

    public static PlacementAlgorithm getAlgorithm() {
        return sPlacementAlgorithm;
    }

}
