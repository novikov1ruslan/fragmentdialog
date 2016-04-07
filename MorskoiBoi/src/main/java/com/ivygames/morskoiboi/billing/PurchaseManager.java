package com.ivygames.morskoiboi.billing;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.ivygames.billing.IabHelper;
import com.ivygames.billing.IabResult;
import com.ivygames.billing.Purchase;
import com.ivygames.morskoiboi.BattleshipActivity;

import org.acra.ACRA;
import org.commons.logger.Ln;

public class PurchaseManager {

    // TODO:
     /*
      * base64EncodedPublicKey should be YOUR APPLICATION'S PUBLIC KEY (that you got from the Google Play developer console). This is not your developer public
 	 * key, it's the *app-specific* public key.
-	 *
+	 *
 	 * Instead of just storing the entire literal string here embedded in the program, construct the key at runtime from pieces or use bit manipulation (for
 	 * example, XOR with some other string) to hide the actual key. The key itself is not secret information, but we don't want to make it easy for an attacker
 	 * to replace the public key with one of their own and then fake messages from the server.
 	 */
    private static final String BASE64_ENCODED_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsZ8ufj+4+R1sqPrTudIeXZBD6NUtKo8fWLpbQHp9ib9jtIv3PVOzVuNKIsG7eXqn0U+vWX8WYtoPGmogYr4GDJqdzOQb2xq5ZEsAzXoE+Yeiqpp/ASUs1IU2Tw+cu30rKStgktnFeIfcFowPyHeSgSQlqBFUrL0A8oipc5oesao7OiGGCwpUf6OJuvyK0DmdhdYUMPRxTgp0v5+JnXhNEqgiU00W468vf4rfUGqQWUNN902fphf8oADJT5FdlculaQva5t+55RdpqtP8UAficOUXh1xyAn1KQ0APKOPU5x7wAe/z3bLdjE1Ik4g4KXyHLGfP5PMjkfqvgNeU2WsN4QIDAQAB";

    static final String SKU_NO_ADS = "no_ads";

    private IabHelper mHelper;

    @NonNull
    private final Activity mActivity;

    public PurchaseManager(@NonNull BattleshipActivity activity) {
        mActivity = activity;
    }

    public void query(final @NonNull HasNoAdsListener listener) {
        destroy();

        // Create the helper, passing it our context and the public key to verify signatures with
        Ln.d("Creating IAB helper.");
        mHelper = new IabHelper(mActivity, BASE64_ENCODED_PUBLIC_KEY);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Ln.d("Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {

            @Override
            public void onIabSetupFinished(IabResult result) {
                if (mHelper == null) {
                    PurchaseUtils.alreadyDestroyedWarning();
                    return;
                }

                try {
                    if (!result.isSuccess()) {
                        Ln.w("Problem setting up in-app billing: " + result);
                        return;
                    }

                    // IAB is fully set up. Now, let's get an inventory of stuff we own.
                    Ln.d("Setup successful. Querying inventory.");
                    mHelper.queryInventoryAsync(new QueryInventoryFinishedImpl(listener));
                } catch (Exception e) {
                    ACRA.getErrorReporter().handleException(e);
                }
            }
        });
    }

    public void destroy() {
        if (mHelper == null) {
            PurchaseUtils.alreadyDestroyedWarning();
            return;
        }

        Ln.d("Destroying helper.");
        try {
            mHelper.dispose();
        } catch (Exception e) {
            ACRA.getErrorReporter().handleException(e);
        }
        mHelper = null;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mHelper == null) {
            PurchaseUtils.alreadyDestroyedWarning();
            return;
        }
        Ln.v("request code = " + requestCode + "; response code = " + resultCode);

        try {
            // Pass on the activity result to the helper for handling
            boolean handled = mHelper.handleActivityResult(requestCode, resultCode, data);
            if (handled) {
                Ln.d("onActivityResult handled by IABUtil.");
            }
        } catch (Exception e) {
            ACRA.getErrorReporter().handleException(e);
        }
    }

    public void purchase(int requestCode, final @NonNull PurchaseStatusListener listener) {
        if (mHelper == null) {
            PurchaseUtils.alreadyDestroyedWarning();
            return;
        }

        /*
         * TODO: for security, generate your payload here for verification. See the comments on verifyDeveloperPayload() for more info. Since this is a SAMPLE,
		 * we just use an empty string, but on a production app you should carefully generate this.
		 */
        String payload = "";

        if (mHelper.isAsyncInProgress()) {
            Ln.e("no_ads in progress");
            return;
        }

        IabHelper.OnIabPurchaseFinishedListener purchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
            @Override
            public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                Ln.d("Purchase finished: " + result + ", purchase: " + purchase);
                if (result.isFailure()) {
                    Ln.w("Error purchasing: " + result);
                    listener.onPurchaseFailed();
                    return;
                }
                Ln.d("Purchase successful.");

                if (purchase.getSku().equals(SKU_NO_ADS)) {
                    listener.onHasNoAds();
                }
            }
        };

        mHelper.launchPurchaseFlow(mActivity, SKU_NO_ADS, requestCode, purchaseFinishedListener, payload);
    }

}