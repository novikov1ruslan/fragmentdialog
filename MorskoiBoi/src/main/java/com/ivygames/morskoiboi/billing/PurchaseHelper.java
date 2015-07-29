package com.ivygames.morskoiboi.billing;

import android.app.Activity;
import android.content.Intent;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.ivygames.billing.IabHelper;
import com.ivygames.billing.IabResult;
import com.ivygames.billing.Purchase;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.ui.BattleshipActivity;

import org.apache.commons.lang3.Validate;
import org.commons.logger.Ln;

public class PurchaseHelper {

    private IabHelper mHelper;
    private Activity mActivity;
    // (arbitrary) request code for the purchase flow

    public void onCreate(Activity activity) {
        mActivity = Validate.notNull(activity);

        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(mActivity) != ConnectionResult.SUCCESS) {
            Ln.e("services not available");
            return;
        }

        // Create the helper, passing it our context and the public key to verify signatures with
        Ln.d("Creating IAB helper.");
        mHelper = new IabHelper(mActivity, InventoryHelper.BASE64_ENCODED_PUBLIC_KEY);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);
    }

    public void purchase(int requestCode, IabHelper.OnIabPurchaseFinishedListener listener) {
        // TODO: move to UI
//        mGaTracker.send(new UiEvent("no_ads").build());
        Ln.d("Upgrade button clicked; launching purchase flow for upgrade.");

		/*
         * TODO: for security, generate your payload here for verification. See the comments on verifyDeveloperPayload() for more info. Since this is a SAMPLE,
		 * we just use an empty string, but on a production app you should carefully generate this.
		 */
        String payload = "";

        if (mHelper == null) {
            Ln.e("no_ads not created");
            return;
        }

//        if (!mHelper.isSetupDone()) {
//            Ln.e("no_ads not setup", 1);
//            return;
//        }

        if (mHelper.isAsyncInProgress()) {
            Ln.e("no_ads in progress");
            return;
        }

//        showWaitingScreen();
        mHelper.launchPurchaseFlow(mActivity, InventoryHelper.SKU_NO_ADS, requestCode, listener, payload);
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        Ln.v("request code = " + requestCode + "; response code = " + resultCode);
        if (mHelper == null) {
            return false;
        }

        // Pass on the activity result to the helper for handling
        boolean handled = mHelper.handleActivityResult(requestCode, resultCode, data);
        if (handled) {
            Ln.d("onActivityResult handled by IABUtil.");
        }

        return handled;
    }

    public void onDestroy() {
        // very important:
        Ln.d("Destroying helper.");
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
    }
}
