package com.ivygames.common.billing;

import android.support.annotation.NonNull;

import com.example.android.trivialdrivesample.util.IabHelper;
import com.example.android.trivialdrivesample.util.IabResult;
import com.example.android.trivialdrivesample.util.Purchase;

import org.commons.logger.Ln;

class OnIabPurchaseFinishedImpl implements IabHelper.OnIabPurchaseFinishedListener {
    @NonNull
    private final PurchaseStatusListener mListener;
    @NonNull
    private final String mSku;

    public OnIabPurchaseFinishedImpl(@NonNull PurchaseStatusListener listener,
                                     @NonNull String sku) {
        mListener = listener;
        mSku = sku;
    }

    @Override
    public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
        Ln.d("Purchase finished: " + result + ", purchase: " + purchase);
        if (result.isFailure()) {
            Ln.w("Error purchasing: " + result);
            mListener.onPurchaseFailed();
            return;
        }
        Ln.d("Purchase successful.");

        if (purchase.getSku().equals(mSku)) {
            mListener.onHasNoAds();
        }
    }
}
