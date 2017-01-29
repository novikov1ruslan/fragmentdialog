package com.ivygames.morskoiboi;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.appodeal.ads.Appodeal;
import com.ivygames.common.AndroidDevice;
import com.ivygames.common.ads.AdProvider;

import org.commons.logger.Ln;

public class AppodealAdProvider implements AdProvider {
    private static final String APP_KEY = "8b8582518838a35e16efcca260202182bc31b890a63879f8";

    private int mNextAdIndex;
    private int[] mAdTypes = {
            Appodeal.INTERSTITIAL,
            Appodeal.INTERSTITIAL,
            Appodeal.INTERSTITIAL
//            Appodeal.SKIPPABLE_VIDEO
    };
    @NonNull
    private final Activity mActivity;
    @NonNull
    private final AndroidDevice mDevice;
    private boolean mNeedToShowAfterPlayAd;
    private boolean mNoAds;

    public AppodealAdProvider(@NonNull Activity activity, @NonNull AndroidDevice device) {
        mActivity = activity;
        mDevice = device;
        Ln.d("initializing appodeal");
        Appodeal.disableLocationPermissionCheck();
        Appodeal.disableWriteExternalStoragePermissionCheck();
        Appodeal.confirm(Appodeal.SKIPPABLE_VIDEO);
        int fullScreenAds = getFullScreenAds();
        Appodeal.initialize(activity, APP_KEY, Appodeal.BANNER | fullScreenAds);
//        Appodeal.setSkippableVideoCallbacks(new AppodealSkippableVideoCallback());
        Appodeal.setInterstitialCallbacks(new AppodealInterstitialCallback());
        Appodeal.setBannerCallbacks(new AppodealBannerCallbacks());

        Appodeal.show(activity, Appodeal.BANNER_TOP);
    }

    private int getFullScreenAds() {
        int adTypes = 0;
        for (int adType : mAdTypes) {
            adTypes |= adType;
        }
        return adTypes;
    }

    @Override
    public void needToShowAfterPlayAd() {
        if (mNoAds) {
            Ln.v("no ads");
        } else {
            Ln.v("request to show after play ad");
            mNeedToShowAfterPlayAd = true;
        }
    }

    @Override
    public void showAfterPlayAd() {
        if (mNoAds) {
            Ln.v("no ads");
        } else {
            if (!mNeedToShowAfterPlayAd) {
                return;
            }

            int adType = mAdTypes[mNextAdIndex];
            if (Appodeal.isLoaded(adType)) {
                showAdType(adType);
            } else {
                Ln.d(AppodealUtils.typeToName(adType) + " ad not loaded");
                mNextAdIndex = showFirstAvailableAd();
            }

            if (mNextAdIndex >= mAdTypes.length) {
                mNextAdIndex = 0;
            }
        }
    }

    private int showFirstAvailableAd() {
        for (int i = 0; i < mAdTypes.length; i++) {
            int adType = mAdTypes[i];
            if (Appodeal.isLoaded(adType)) {
                showAdType(adType);
                return i;
            }
        }

        return mAdTypes.length;
    }

    private void showAdType(int adType) {
        if (adType == Appodeal.SKIPPABLE_VIDEO && !mDevice.isWifiConnected()) {
            Ln.d("video ad skipped");
            return;
        }
        Ln.d("showing " + AppodealUtils.typeToName(adType));
        Appodeal.show(mActivity, adType);
        mNeedToShowAfterPlayAd = false;
        mNextAdIndex++;
    }

    @Override
    public void resume() {
        if (mNoAds) {
            Ln.v("no ads");
        } else {
            Appodeal.onResume(mActivity, Appodeal.BANNER);
        }
    }

    @Override
    public void pause() {
        if (mNoAds) {
            Ln.v("no ads");
        }
    }

    @Override
    public void destroy() {
        if (mNoAds) {
            Ln.v("no ads");
        }
        mNoAds = true;
        if (!mActivity.isFinishing()) {
            Appodeal.hide(mActivity, Appodeal.BANNER_TOP);
            Ln.i("hiding banner");
        }
    }
}
