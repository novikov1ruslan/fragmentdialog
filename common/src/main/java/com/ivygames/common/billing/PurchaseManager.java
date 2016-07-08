package com.ivygames.common.billing;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.ivygames.billing.IabHelper;
import com.ivygames.billing.IabResult;

import org.commons.logger.Ln;

public class PurchaseManager {
    private IabHelper mHelper;

    @NonNull
    private final Activity mActivity;
    private final int mRequestCode;

    public PurchaseManager(@NonNull Activity activity, int requestCode) {
        mActivity = activity;
        mRequestCode = requestCode;
    }

    public void query(final @NonNull String sku, final @NonNull HasNoAdsListener listener, @NonNull String key) {
        destroy();

        // Create the helper, passing it our context and the public key to verify signatures with
        Ln.d("Creating IAB helper.");
        mHelper = new IabHelper(mActivity, key);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Ln.d("Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {

            @Override
            public void onIabSetupFinished(IabResult result) {
                if (mHelper == null) {
                    alreadyDestroyedWarning();
                    return;
                }

                PurchaseUtils.query(sku, result, listener, mHelper);
            }
        });
    }

    public void destroy() {
        if (mHelper == null) {
            alreadyDestroyedWarning();
            return;
        }

        Ln.d("Destroying helper.");
        PurchaseUtils.dispose(mHelper);
        mHelper = null;
    }

    public void handleActivityResult(int resultCode, Intent data) {
        if (mHelper == null) {
            alreadyDestroyedWarning();
            return;
        }
        Ln.v("response code = " + resultCode);

        PurchaseUtils.handleActivityResult(mRequestCode, resultCode, data, mHelper);
    }

    public void purchase(@NonNull String sku, final @NonNull PurchaseStatusListener listener) {
        if (mHelper == null) {
            alreadyDestroyedWarning();
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

        PurchaseUtils.purchase(mActivity, sku, mRequestCode, listener, payload, mHelper);
    }

    private static void alreadyDestroyedWarning() {
        Ln.w("purchase helper has been already destroyed");
    }

}
