package com.ivygames.common.billing;

public interface PurchaseStatusListener extends HasNoAdsListener {
    void onPurchaseFailed();
}
