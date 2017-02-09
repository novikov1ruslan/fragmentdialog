package com.ivygames.common.billing;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.example.android.trivialdrivesample.util.IabHelper;
import com.example.android.trivialdrivesample.util.IabResult;
import com.ivygames.common.BuildConfig;

import org.commons.logger.Ln;

public class PurchaseManager {

    @NonNull
    private final Activity mActivity;
    private final int mRequestCode;
    private IabHelper mHelper;
    private IabResult mResult;
    private Query mOngoingQuery;

    private static class Query {
        @NonNull
        final String sku;
        @NonNull
        final HasNoAdsListener listener;

        private Query(@NonNull String sku, @NonNull HasNoAdsListener listener) {
            this.sku = sku;
            this.listener = listener;
        }
    }

    public PurchaseManager(@NonNull Activity activity, int requestCode, @NonNull String key) {
        mActivity = activity;
        mRequestCode = requestCode;
        // Create the helper, passing it our context and the public key to verify signatures with
        Ln.d("Creating IAB helper.");
        mHelper = new IabHelper(activity, key);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(BuildConfig.DEBUG);
    }

    public void startSetup() {
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

                if (!result.isSuccess()) {
                    Ln.w("Problem setting up in-app billing: " + result);
                    return;
                }

                mResult = result;
                if (mOngoingQuery != null) {
                    Ln.d("ongoing query, executing.");
                    PurchaseUtils.query(mHelper, mOngoingQuery.sku, mOngoingQuery.listener);
                }
            }
        });
    }

    public void query(@NonNull String sku, @NonNull HasNoAdsListener listener) {
        if (mHelper == null) {
            alreadyDestroyedWarning();
            return;
        }

        if (mResult == null) {
            Ln.d("not yet initialized, queueing: " + sku);
            mOngoingQuery = new Query(sku, listener);
            return;
        }

        PurchaseUtils.query(mHelper, sku, listener);
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
        PurchaseUtils.handleActivityResult(mHelper, mRequestCode, resultCode, data);
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
            Ln.w("no_ads in progress");
            return;
        }

        PurchaseUtils.purchase(mHelper, mActivity, sku, mRequestCode, listener, payload);
    }

    private static void alreadyDestroyedWarning() {
        Ln.w("purchase helper has been already destroyed");
    }

}
