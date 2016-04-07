package com.ivygames.morskoiboi.billing;

public interface PurchaseStatusListener extends HasNoAdsListener {
    void onPurchaseFailed();
}
