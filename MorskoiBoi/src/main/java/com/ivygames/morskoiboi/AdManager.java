package com.ivygames.morskoiboi;

import org.commons.logger.Ln;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdRequest.Builder;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.plus.model.people.Person;
import com.jirbo.adcolony.AdColony;
import com.jirbo.adcolony.AdColonyAdapter;
import com.jirbo.adcolony.AdColonyBundleBuilder;

public class AdManager {
	private static final boolean SUPPORT_AD_COLONY = false;
	private static final String ADCOLONY_ZONE_ID = "vz37b387ad9fa84e87bf";

	public static final AdManager instance = new AdManager();

	private AdManager() {

	}

	private InterstitialAd mInterstitialAfterPlay;
	private InterstitialAd mInterstitialAfterExit;
	private boolean mInterstitialAfterPlayShown = true;
	private String mBirthday;
	private String mCurrentLocation;
	private int mGender;
	private Person mPerson;
	private AdView mBanner;

	public void needToShowInterstitialAfterPlay() {
		mInterstitialAfterPlayShown = false;
	}

	public void showInterstitialAfterPlay() {
		if (GameSettings.get().noAds()) {
			Ln.v("no ad game - skipping ads");
			return;
		}

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

	public void showInterstitialAfterExit(Activity activity) {
		if (GameSettings.get().noAds()) {
			Ln.v("no ad game - skipping ads");
			return;
		}

		if (mInterstitialAfterExit.isLoaded()) {
			mInterstitialAfterExit.show();
		} else {
			Ln.d("ad could not be loaded");
			activity.finish();
		}
	}

	public void initInterstitialAfterPlay(Context context, String adUnitId) {
		if (GameSettings.get().noAds()) {
			Ln.v("no ad game - skipping ads");
			return;
		}

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

	public void initInterstitialAfterExit(final Activity activity, String adUnitId) {
		if (GameSettings.get().noAds()) {
			Ln.v("no ad game - skipping ads");
			return;
		}

		mInterstitialAfterExit = new InterstitialAd(activity);
		mInterstitialAfterExit.setAdUnitId(adUnitId);
		Ln.v("interstitial after exit initialized for [" + adUnitId + "]");

		Builder builder = AdManager.createAdRequest(mPerson);
		if (SUPPORT_AD_COLONY) {
			AdColonyBundleBuilder.setZoneId(ADCOLONY_ZONE_ID);
			builder.addNetworkExtrasBundle(AdColonyAdapter.class, AdColonyBundleBuilder.build());
		}
		AdRequest adRequest = builder.build();
		mInterstitialAfterExit.loadAd(adRequest);
		mInterstitialAfterExit.setAdListener(new AdListener() {
			@Override
			public void onAdClosed() {
				Ln.d("interstitial closed - closing the game now");
				activity.finish();
			}
		});
	}

	public void setPerson(Person person) {
		if (person == null) {
			Ln.w("person is null");
			return;
		}

		mPerson = person;
		if (person.hasBirthday()) {
			mBirthday = person.getBirthday();
			Ln.v("birthday=" + mBirthday);
		} else {
			Ln.v("player has not specified birthday");
		}

		if (person.hasCurrentLocation()) {
			mCurrentLocation = person.getCurrentLocation();
			Ln.v("current location=" + mCurrentLocation);
		} else {
			Ln.v("player has not specified location");
		}

		if (person.hasGender()) {
			mGender = person.getGender();
			String gender = AdManager.genderToString(mGender);
			Ln.v("gender=" + gender);
		} else {
			Ln.v("player has not specified gender");
		}
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
	public AdRequest createAdRequest() {
		return AdManager.createAdRequest(mPerson).build();
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

	public void configure(Activity activity) {
		mBanner = (AdView) activity.findViewById(R.id.banner);
		if (GameSettings.get().noAds()) {
			mBanner.setVisibility(View.GONE);
		} else {
			if (SUPPORT_AD_COLONY) {
				AdColony.configure(activity, "version:" + activity.getString(R.string.versionName) + ",store:google", "app2c40a372149e43558c", ADCOLONY_ZONE_ID);
			}
			initInterstitialAfterExit(activity, activity.getString(R.string.admob_interstitial_after_exit_id));
			initInterstitialAfterPlay(activity, activity.getString(R.string.admob_interstitial_after_play_id));
			if (isSmallScreen(activity)) {
				// && isGamePlayScreenDislpayed()) {
				mBanner.setVisibility(View.GONE);
			} else {
				mBanner.setVisibility(View.VISIBLE);
			}
		}
	}

	private boolean isSmallScreen(Context context) {
		return context.getResources().getBoolean(R.bool.is_small_screen);
	}

	public void resume(Activity activity) {
		if (GameSettings.get().noAds()) {
			Ln.v("game resumed - resuming ad serving, volume stream saved");
		} else {
			Ln.v("game resumed - volume stream saved");
			mBanner.loadAd(AdManager.instance.createAdRequest());
			mBanner.resume();
			if (SUPPORT_AD_COLONY) {
				AdColony.resume(activity);
			}
		}
	}

	public void pause() {
		if (GameSettings.get().noAds()) {
			Ln.v("game paused - restored volume stream");
		} else {
			Ln.v("game paused - pausing ad serving, volume stream restored");
			mBanner.pause();
			if (SUPPORT_AD_COLONY) {
				AdColony.pause();
			}
		}
	}

	public void destroy() {
		if (mBanner != null) {
			mBanner.destroy();
		}
	}

}
