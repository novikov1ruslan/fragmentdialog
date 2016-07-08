package com.ivygames.common.billing;

import android.support.annotation.NonNull;

import com.ivygames.billing.IabHelper;
import com.ivygames.billing.IabResult;
import com.ivygames.billing.Inventory;
import com.ivygames.billing.Purchase;

import org.commons.logger.Ln;

class QueryInventoryFinishedImpl implements IabHelper.QueryInventoryFinishedListener {

    @NonNull
    private final HasNoAdsListener mHasNoAdsListener;
    @NonNull
    private final String mSku;

    QueryInventoryFinishedImpl(@NonNull String sku, @NonNull HasNoAdsListener listener) {
        mSku = sku;
        mHasNoAdsListener = listener;
    }

    @Override
    public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
        if (result.isFailure()) {
            Ln.w("Failed to query inventory: " + result);
            return;
        }
        Ln.d("Query inventory was successful.");

        Purchase noAdsPurchase = inventory.getPurchase(mSku);
        if (noAdsPurchase != null) {
            mHasNoAdsListener.onHasNoAds();
        }
    }
}
