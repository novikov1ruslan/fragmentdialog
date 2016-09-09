package com.ivygames.morskoiboi;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.appodeal.ads.Appodeal;
import com.ivygames.common.ads.AdProvider;

import org.commons.logger.Ln;

public class AppodealAdProvider implements AdProvider {

    private int mNextAdIndex;
    private int[] mAdTypes = {Appodeal.INTERSTITIAL, Appodeal.INTERSTITIAL, Appodeal.INTERSTITIAL,
            Appodeal.SKIPPABLE_VIDEO};
    @NonNull
    private final Activity mActivity;
    private boolean mNeedToShowAfterPlayAd;

    public AppodealAdProvider(@NonNull Activity activity) {
        mActivity = activity;
        Ln.d("initializing appodeal");
        Appodeal.disableLocationPermissionCheck();
        Appodeal.disableNetwork(activity, "cheetah");
        Appodeal.confirm(Appodeal.SKIPPABLE_VIDEO);
        String appKey = "8b8582518838a35e16efcca260202182bc31b890a63879f8";
        Appodeal.initialize(activity, appKey, Appodeal.BANNER | Appodeal.INTERSTITIAL | Appodeal.SKIPPABLE_VIDEO);
//        Appodeal.initialize(activity, appKey, Appodeal.BANNER | Appodeal.INTERSTITIAL);
//        Appodeal.setNonSkippableVideoCallbacks(new AppodealNonSkippableVideoCallback());
        Appodeal.setSkippableVideoCallbacks(new AppodealSkippableVideoCallback());
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
        if (!mNeedToShowAfterPlayAd) {
            return;
        }

        mNeedToShowAfterPlayAd = !showAdType(mAdTypes[mNextAdIndex]);
        if (mNeedToShowAfterPlayAd) {
            showFirstAvailableAd();
        } else {
            mNextAdIndex++;
        }

        if (mNextAdIndex >= mAdTypes.length) {
            mNextAdIndex = 0;
        }
    }

    private void showFirstAvailableAd() {
        mNextAdIndex = 0;
        while (mNeedToShowAfterPlayAd && mNextAdIndex < mAdTypes.length) {
            mNeedToShowAfterPlayAd = !showAdType(mAdTypes[mNextAdIndex]);
            mNextAdIndex++;
        }
    }

    private boolean showAdType(int addType) {
        Ln.v("trying to show ad type: " + AppodealUtils.typeToName(addType));

        if (Appodeal.isLoaded(addType)) {
            Ln.d("showing " + AppodealUtils.typeToName(addType));
            Appodeal.show(mActivity, addType);
            return true;
        }
        Ln.d(AppodealUtils.typeToName(addType) + " ad not loaded");
        return false;
    }

    @Override
    public void resume(@NonNull Activity activity) {
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
