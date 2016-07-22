package com.ivygames.common.billing;

import android.support.annotation.NonNull;

import com.example.android.trivialdrivesample.util.IabHelper;
import com.example.android.trivialdrivesample.util.IabResult;
import com.example.android.trivialdrivesample.util.Inventory;
import com.example.android.trivialdrivesample.util.Purchase;

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
