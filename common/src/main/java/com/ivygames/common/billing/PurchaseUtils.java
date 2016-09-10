package com.ivygames.common.billing;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.annotation.NonNull;

import com.example.android.trivialdrivesample.util.IabHelper;
import com.example.android.trivialdrivesample.util.Purchase;

import org.commons.logger.Ln;

import java.util.List;

import static com.ivygames.common.analytics.ExceptionHandler.reportException;

public class PurchaseUtils {
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

    public static boolean isBillingAvailable(PackageManager pm) {
        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        List<ResolveInfo> intentServices = pm.queryIntentServices(serviceIntent, 0);
        return intentServices != null && !intentServices.isEmpty();
    }

    static void dispose(@NonNull IabHelper helper) {
        try {
            helper.dispose();
        } catch (Exception e) {
            reportException(e);
        }
    }

    static void handleActivityResult(@NonNull IabHelper helper,
                                     int requestCode, int resultCode, Intent data) {
        try {
            // Pass on the activity result to the helper for handling
            boolean handled = helper.handleActivityResult(requestCode, resultCode, data);
            if (handled) {
                Ln.d("onActivityResult handled by IABUtil.");
            }
        } catch (Exception e) {
            reportException(e);
        }
    }

    static void purchase(@NonNull IabHelper helper,
                         @NonNull Activity activity,
                         @NonNull String sku,
                         int requestCode,
                         @NonNull PurchaseStatusListener listener,
                         @NonNull String payload) {
        try {
            helper.launchPurchaseFlow(activity, sku, requestCode,
                    new OnIabPurchaseFinishedImpl(listener, sku), payload);
        } catch (Exception e) {
            reportException(e);
        }
    }

    static void query(@NonNull IabHelper helper,
                      @NonNull String sku,
                      @NonNull HasNoAdsListener listener) {
        try {
            // IAB is fully set up. Now, let's get an inventory of stuff we own.
            Ln.d("Setup successful. Querying inventory.");
            helper.queryInventoryAsync(new QueryInventoryFinishedImpl(sku, listener));
        } catch (Exception e) {
            reportException(e);
        }
    }

}
