package com.ivygames.morskoiboi.billing;

import com.ivygames.billing.IabHelper;
import com.ivygames.billing.IabResult;
import com.ivygames.billing.Purchase;

import org.commons.logger.Ln;

class OnIabPurchaseFinishedImpl implements IabHelper.OnIabPurchaseFinishedListener {
    private final PurchaseStatusListener mListener;

    public OnIabPurchaseFinishedImpl(PurchaseStatusListener listener) {
        mListener = listener;
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

        if (purchase.getSku().equals(PurchaseManager.SKU_NO_ADS)) {
            mListener.onHasNoAds();
        }
    }
}
