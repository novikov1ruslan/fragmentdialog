package com.ivygames.morskoiboi;

import android.app.Activity;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.InterstitialCallbacks;
import com.ivygames.common.ads.AdProvider;

import org.commons.logger.Ln;

public class AppodealAdProvider implements AdProvider {
    private Activity mActivity;
    private boolean mInterstitialAfterPlayShown = true;

    public AppodealAdProvider(final Activity activity) {
        mActivity = activity;

        Appodeal.disableLocationPermissionCheck();
        String appKey = "8b8582518838a35e16efcca260202182bc31b890a63879f8";
        Appodeal.initialize(activity, appKey, Appodeal.BANNER | Appodeal.INTERSTITIAL | Appodeal.NON_SKIPPABLE_VIDEO);
        Appodeal.show(activity, Appodeal.BANNER_TOP);

        Appodeal.setInterstitialCallbacks(new InterstitialCallbacks() {
            @Override
            public void onInterstitialLoaded(boolean loaded) {
            }

            @Override
            public void onInterstitialFailedToLoad() {
            }

            @Override
            public void onInterstitialShown() {
            }

            @Override
            public void onInterstitialClicked() {
            }

            @Override
            public void onInterstitialClosed() {
            }
        });
    }

    @Override
    public void needToShowInterstitialAfterPlay() {
        mInterstitialAfterPlayShown = false;
    }

    @Override
    public void showInterstitialAfterPlay() {
        if (mInterstitialAfterPlayShown) {
            Ln.v("already shown - skipping ads");
            return;
        }

        if (Appodeal.isLoaded(Appodeal.NON_SKIPPABLE_VIDEO)) {
            Appodeal.show(mActivity, Appodeal.INTERSTITIAL);
            mInterstitialAfterPlayShown = true;
        } else if (Appodeal.isLoaded(Appodeal.INTERSTITIAL)) {
            Appodeal.show(mActivity, Appodeal.INTERSTITIAL);
            mInterstitialAfterPlayShown = true;
        }
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
