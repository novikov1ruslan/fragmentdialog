package com.ivygames.morskoiboi.ai;

public final class PlacementFactory {

    private static PlacementAlgorithm mPlacementAlgorithm;

    private PlacementFactory() {
        // factory
    }

    public static void setPlacementAlgorithm(PlacementAlgorithm algorithm) {

        mPlacementAlgorithm = algorithm;
    }

    public static PlacementAlgorithm getAlgorithm() {
        return mPlacementAlgorithm;
    }

}
