package com.ivygames.morskoiboi;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.plus.model.people.Person;

import org.commons.logger.Ln;

public class AdmobAdProvider implements AdProvider {

    private InterstitialAd mInterstitialAfterPlay;
    private boolean mInterstitialAfterPlayShown = true;
    private Person mPerson;
    private AdView mBanner;

    public AdmobAdProvider(Activity activity) {
        initInterstitialAfterPlay(activity, activity.getString(R.string.admob_interstitial_after_play_id));
        if (isSmallScreen(activity)) {
            mBanner.setVisibility(View.GONE);
        } else {
            mBanner.setVisibility(View.VISIBLE);
        }
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

        if (mInterstitialAfterPlay.isLoaded()) {
            mInterstitialAfterPlay.show();
            mInterstitialAfterPlayShown = true;
        } else {
            Ln.d("ad could not be loaded");
        }
    }

    private void initInterstitialAfterPlay(Context context, String adUnitId) {
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

    @Override
    public void setPerson(Person person) {
        if (person == null) {
            Ln.w("person is null");
            return;
        }

        mPerson = person;
//        if (person.hasBirthday()) {
//            mBirthday = person.getBirthday();
//            Ln.v("birthday=" + mBirthday);
//        } else {
//            Ln.v("player has not specified birthday");
//        }
//
//        if (person.hasCurrentLocation()) {
//            mCurrentLocation = person.getCurrentLocation();
//            Ln.v("current location=" + mCurrentLocation);
//        } else {
//            Ln.v("player has not specified location");
//        }
//
//        if (person.hasGender()) {
//            mGender = person.getGender();
//            String gender = AdmobAdProvider.genderToString(mGender);
//            Ln.v("gender=" + gender);
//        } else {
//            Ln.v("player has not specified gender");
//        }
    }

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
    private AdRequest createAdRequest() {
        return AdmobAdProvider.createAdRequest(mPerson).build();
    }

    private static Builder createAdRequest(Person person) {
        Builder builder = new AdRequest.Builder();
        if (GameConstants.IS_TEST_MODE) {
            /*
             * This call will add the emulator as a test device. To add a physical device for testing, pass in your hashed device ID, which can be found in the
			 * LogCat output when loading an ad on your device.
			 */
            builder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
            builder.addTestDevice("43548AA33072E95B1BDCE839CC2F99F2"); // LG g2
            builder.addTestDevice("D2E2CCE4C6E2B8BBAF4005B97CF6EB8C"); // LG L70
            builder.addTestDevice("36C3996B19607DE01F507955C4DF1A2A"); // Nexus 6
            builder.addTestDevice("66785C8FFF22B62D4E8EA74ADD28FC11"); // Asus
        }

        // if (person != null && person.hasBirthday()) {
        // String birthday2 = person.getBirthday();
        // Date birthday = null;
        // builder.setBirthday(birthday);
        // }

        // AdRequest request = new AdRequest.Builder()
        // .setLocation(location)
        // .setGender(AdRequest.GENDER_FEMALE)
        // .setBirthday(new GregorianCalendar(1985, 1, 1).getTime())
        // // .tagForChildDirectedTreatment(true)
        // .build();

        return builder;
    }

    private boolean isSmallScreen(Context context) {
        return context.getResources().getBoolean(R.bool.is_small_screen);
    }

    @Override
    public void resume(Activity activity) {
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
