package com.ivygames.morskoiboi.billing;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.ivygames.billing.IabHelper;
import com.ivygames.billing.IabResult;
import com.ivygames.billing.Purchase;
import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.billing.PurchaseHelper;

import org.acra.ACRA;
import org.commons.logger.Ln;

public class PurchaseManager implements IabHelper.OnIabPurchaseFinishedListener {

    @NonNull
    private final BattleshipActivity mActivity;

    public interface PurchaseStatusListener {
        void onPurchaseFailed();

        void onPurchaseSucceeded();
    }

    private PurchaseHelper mPurchaseHelper;

    private PurchaseStatusListener mPurchaseStatusListener;

    public PurchaseManager(@NonNull BattleshipActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
        Ln.d("Purchase finished: " + result + ", purchase: " + purchase);

        // if we were disposed of in the meantime, quit.
        if (mPurchaseHelper == null) {
            return;
        }

        if (result.isFailure()) {
            Ln.w("Error purchasing: " + result);
            mPurchaseStatusListener.onPurchaseFailed();
            return;
        }

        Ln.d("Purchase successful.");

        if (purchase.getSku().equals(PurchaseHelper.SKU_NO_ADS)) {
            mPurchaseStatusListener.onPurchaseSucceeded();
        }
    }

    public void init(@NonNull PurchaseStatusListener listener) {
        mPurchaseStatusListener = listener;
        mPurchaseHelper = new PurchaseHelper(mActivity);
        try {
            mPurchaseHelper.query();
        } catch (Exception e) {
            ACRA.getErrorReporter().handleException(e);
        }
    }

    public void destroy() {
        if (mPurchaseHelper != null) {
            try {
                mPurchaseHelper.destroy();
            } catch (Exception e) {
                ACRA.getErrorReporter().handleException(e);
            }
            mPurchaseHelper = null;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mPurchaseHelper != null) {
            try {
                mPurchaseHelper.onActivityResult(requestCode, resultCode, data);
            } catch (Exception e) {
                ACRA.getErrorReporter().handleException(e);
            }
        }
    }

    public void purchase(int requestCode) {
        if (mPurchaseHelper != null) {
            try {
                Ln.d("No ads button clicked; launching purchase flow for upgrade.");
                mPurchaseHelper.purchase(requestCode, this);
            } catch (Exception e) {
                ACRA.getErrorReporter().handleException(e);
            }
        }
    }

}
