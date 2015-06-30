package com.ivygames.morskoiboi.billing;

import android.app.AlertDialog;

import com.ivygames.billing.IabHelper;
import com.ivygames.billing.IabResult;
import com.ivygames.billing.Inventory;
import com.ivygames.billing.Purchase;
import com.ivygames.morskoiboi.GameSettings;
import com.ivygames.morskoiboi.ui.BattleshipActivity;
import com.ruslan.fragmentdialog.FragmentAlertDialog;

import org.commons.logger.Ln;

public class InventoryHelper {
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
    static final String BASE64_ENCODED_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsZ8ufj+4+R1sqPrTudIeXZBD6NUtKo8fWLpbQHp9ib9jtIv3PVOzVuNKIsG7eXqn0U+vWX8WYtoPGmogYr4GDJqdzOQb2xq5ZEsAzXoE+Yeiqpp/ASUs1IU2Tw+cu30rKStgktnFeIfcFowPyHeSgSQlqBFUrL0A8oipc5oesao7OiGGCwpUf6OJuvyK0DmdhdYUMPRxTgp0v5+JnXhNEqgiU00W468vf4rfUGqQWUNN902fphf8oADJT5FdlculaQva5t+55RdpqtP8UAficOUXh1xyAn1KQ0APKOPU5x7wAe/z3bLdjE1Ik4g4KXyHLGfP5PMjkfqvgNeU2WsN4QIDAQAB";

    private IabHelper mHelper;
    private final BattleshipActivity mActivity;

    static final String SKU_NO_ADS = "no_ads";
    private static final String DIALOG = FragmentAlertDialog.TAG;

    public InventoryHelper(BattleshipActivity activity) {
        mActivity = activity;
    }

    public void onCreate() {
        // Create the helper, passing it our context and the public key to verify signatures with
        Ln.d("Creating IAB helper.");
        mHelper = new IabHelper(mActivity, BASE64_ENCODED_PUBLIC_KEY);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Ln.d("Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Ln.d("Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Ln.d("Setup successful. Querying inventory.");
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });

    }

    private void complain(String message) {
        Ln.e("**** TrivialDrive Error: " + message);
        alert("Error: " + message);
    }

    private void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(mActivity);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Ln.d("Showing alert dialog: " + message);
        bld.create().show();
    }

    // Listener that's called when we finish querying the items and subscriptions we own
    private IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Ln.d("Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }

            Ln.d("Query inventory was successful.");

			/*
             * Check for items we own. Notice that for each purchase, we check the developer payload to see if it's correct! See verifyDeveloperPayload().
			 */

            // Do we have the premium upgrade?
            Purchase noAdsPurchase = inventory.getPurchase(SKU_NO_ADS);
            boolean noAds = (noAdsPurchase != null && verifyDeveloperPayload(noAdsPurchase));
            if (noAds) {
                Ln.d("removing ads");
                GameSettings.get().setNoAds();
                mActivity.hideAds();
            }
        }
    };


    /**
     * Verifies the developer payload of a purchase.
     */
    static boolean verifyDeveloperPayload(Purchase p) {
        // String payload = p.getDeveloperPayload();

		/*
         * TODO: verify that the developer payload of the purchase is correct. It will be the same one that you sent when initiating the purchase.
		 *
		 * WARNING: Locally generating a random string when starting a purchase and verifying it here might seem like a good approach, but this will fail in the
		 * case where the user purchases an item on one device and then uses your app on a different device, because on the other device you will not have
		 * access to the random string you originally generated.
		 *
		 * So a good developer payload has these characteristics:
		 *
		 * 1. If two different users purchase an item, the payload is different between them, so that one user's purchase can't be replayed to another user.
		 *
		 * 2. The payload must be such that you can verify it even when the app wasn't the one who initiated the purchase flow (so that items purchased by the
		 * user on one device work on other devices owned by the user).
		 *
		 * Using your own server to store and verify developer payloads across app installations is recommended.
		 */

        return true;
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
