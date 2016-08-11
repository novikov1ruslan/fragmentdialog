package com.ivygames.morskoiboi;

import com.appodeal.ads.Appodeal;

public class AppodealUtils {
    private AppodealUtils() {
    }

    static String typeToName(int type) {
        switch (type) {
            case Appodeal.BANNER:
                return "BANNER";
            case Appodeal.BANNER_BOTTOM:
                return "BANNER_BOTTOM";
            case Appodeal.BANNER_TOP:
                return "BANNER_TOP";
            case Appodeal.BANNER_VIEW:
                return "BANNER_VIEW";
            case Appodeal.INTERSTITIAL:
                return "INTERSTITIAL";
            case Appodeal.MREC:
                return "MREC";
            case Appodeal.NATIVE:
                return "NATIVE";
            case Appodeal.NON_SKIPPABLE_VIDEO:
                return "NON_SKIPPABLE_VIDEO";
//            case Appodeal.REWARDED_VIDEO:
//                return "REWARDED_VIDEO";
            case Appodeal.SKIPPABLE_VIDEO:
                return "SKIPPABLE_VIDEO";
            case Appodeal.NONE:
                return "NONE";
            default:
                return "WRONG AD TYPE";
        }
    }
}
