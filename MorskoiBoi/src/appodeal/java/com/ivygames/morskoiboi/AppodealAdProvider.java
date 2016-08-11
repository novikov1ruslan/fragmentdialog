package com.ivygames.morskoiboi;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.appodeal.ads.Appodeal;
import com.ivygames.common.ads.AdProvider;

import org.commons.logger.Ln;

public class AppodealAdProvider implements AdProvider {

    @NonNull
    private final Activity mActivity;
    private boolean mNeedToShowAfterPlayAd;

    public AppodealAdProvider(@NonNull Activity activity) {
        mActivity = activity;
        Ln.d("initializing appodeal");
        Appodeal.disableLocationPermissionCheck();
        String appKey = "8b8582518838a35e16efcca260202182bc31b890a63879f8";
        Appodeal.initialize(activity, appKey, Appodeal.BANNER | Appodeal.INTERSTITIAL | Appodeal.NON_SKIPPABLE_VIDEO);

        Appodeal.setNonSkippableVideoCallbacks(new AppodealNonSkippableVideoCallback());
        Appodeal.setInterstitialCallbacks(new AppodealInterstitialCallback());
        Appodeal.setBannerCallbacks(new AppodealBannerCallbacks());

        Appodeal.show(activity, Appodeal.BANNER_TOP);
    }

    @Override
    public void needToShowAfterPlayAd() {
        Ln.v("request to show after play ad");
        mNeedToShowAfterPlayAd = true;
    }

    @Override
    public void showAfterPlayAd() {
        if (mNeedToShowAfterPlayAd) {
            mNeedToShowAfterPlayAd = showAdType(Appodeal.NON_SKIPPABLE_VIDEO);
        }
        if (mNeedToShowAfterPlayAd) {
            mNeedToShowAfterPlayAd = showAdType(Appodeal.INTERSTITIAL);
        }
    }

    private boolean showAdType(int addType) {
        if (Appodeal.isLoaded(addType)) {
            Ln.d("showing " + AppodealUtils.typeToName(addType));
            Appodeal.show(mActivity, addType);
            return false;
        }
        Ln.d(AppodealUtils.typeToName(addType) + " ad not loaded");
        return true;
    }

    @Override
    public void resume(Activity activity) {
        Appodeal.onResume(activity, Appodeal.BANNER);
    }

    @Override
    public void pause() {
    }

    @Override
    public void destroy() {
        Appodeal.hide(mActivity, Appodeal.BANNER);
    }

}
