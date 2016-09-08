package com.ivygames.morskoiboi;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.plus.model.people.Person;
import com.ivygames.common.BuildConfig;
import com.ivygames.common.ads.AdProvider;

import org.commons.logger.Ln;

public class AdmobAdProvider implements AdProvider {

    @NonNull
    private final Builder mAdRequestBuilder;
    private InterstitialAd mInterstitialAfterPlay;
    private boolean mNeedToShowAfterPlayAd;
    private Person mPerson;
    private AdView mBanner;

    public AdmobAdProvider(Activity activity) {
        mAdRequestBuilder = createAdRequestBuilder(null);

        initInterstitialAfterPlay(activity, activity.getString(R.string.admob_interstitial_after_play_id));
        mBanner = (AdView) activity.findViewById(R.id.banner);
        if (isSmallScreen(activity)) {
            mBanner.setVisibility(View.GONE);
        } else {
            mBanner.setVisibility(View.VISIBLE);
        }
    }

    @NonNull
    private static Builder createAdRequestBuilder(Person person) {
        Builder builder = new AdRequest.Builder();
        if (BuildConfig.DEBUG) {
            /*
             * This call will add the emulator as a test device. To add a physical device for testing, pass in your hashed device ID, which can be found in the
			 * LogCat output when loading an ad on your device.
			 */
            builder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
            builder.addTestDevice("43548AA33072E95B1BDCE839CC2F99F2"); // LG g2
            builder.addTestDevice("D2E2CCE4C6E2B8BBAF4005B97CF6EB8C"); // LG L70
        }

        return builder;
    }

    @Override
    public void needToShowAfterPlayAd() {
        mNeedToShowAfterPlayAd = true;
    }

    @Override
    public void showAfterPlayAd() {
        if (mNeedToShowAfterPlayAd) {
            if (mInterstitialAfterPlay.isLoaded()) {
                mInterstitialAfterPlay.show();
                mNeedToShowAfterPlayAd = false;
            } else {
                Ln.d("after play ad not loaded");
            }
        } else {
            Ln.d("no need to show after play ad");
        }
    }

    private void initInterstitialAfterPlay(@NonNull Context context, @NonNull String adUnitId) {
        mInterstitialAfterPlay = new InterstitialAd(context);
        mInterstitialAfterPlay.setAdUnitId(adUnitId);
        Ln.v("interstitial after play initialized for [" + adUnitId + "]");

        final AdRequest adRequest = createAdRequest();
        mInterstitialAfterPlay.loadAd(adRequest);
        mInterstitialAfterPlay.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                mInterstitialAfterPlay.loadAd(adRequest);
            }
        });
    }

    @NonNull
    private static String genderToString(int gender) {
        switch (gender) {
            case Person.Gender.MALE:
                return "MALE";
            case Person.Gender.FEMALE:
                return "FEMALE";
            case Person.Gender.OTHER:
                return "OTHER";
            default:
                return "UNKNOWN(" + gender + ")";
        }
    }

    /**
     * Creates an ad request. It will be a test request if test mode is enabled.
     *
     * @return An AdRequest to use when loading an ad.
     */
    @NonNull
    private AdRequest createAdRequest() {
        return mAdRequestBuilder.build();
    }

    private boolean isSmallScreen(@NonNull Context context) {
        return context.getResources().getBoolean(R.bool.is_small_screen);
    }

    @Override
    public void resume(@NonNull Activity activity) {
        mBanner.loadAd(createAdRequest());
        mBanner.resume();
    }

    @Override
    public void pause() {
        Ln.v("pausing banner ad serving");
        mBanner.pause();
    }

    @Override
    public void destroy() {
        if (mBanner != null) {
            mBanner.destroy();
        }
    }

}
