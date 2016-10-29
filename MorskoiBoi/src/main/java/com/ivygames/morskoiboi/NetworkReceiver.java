
package com.ivygames.morskoiboi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.commons.logger.Ln;

public final class NetworkReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            Ln.w("got wrong action: " + intent.getAction());
            return;
        }

        boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
        String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
        String info = intent.getStringExtra(ConnectivityManager.EXTRA_EXTRA_INFO);
        boolean isFailOver = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);

        // Since NetworkInfo can vary based on UID, applications should always obtain network information through getActiveNetworkInfo() or getAllNetworkInfo()
        NetworkInfo currentNetworkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
        NetworkInfo otherNetworkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

        Ln.d("noConnectivity=" + noConnectivity + "; reason=" + reason + "; info=" + info + "; isFailOver=" + isFailOver + "; currentNetworkInfo=" + currentNetworkInfo + "; otherNetworkInfo=" + otherNetworkInfo);
    }
}
