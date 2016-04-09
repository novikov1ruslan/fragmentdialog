package com.ivygames.common;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class PlayUtils {

    private PlayUtils() {
        // utils
    }

    public static Intent rateIntent(String packageName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getPlayUrl(packageName)));
        return intent;
    }

    public static String getPlayUrl(String packageName) {
        return "https://play.google.com/store/apps/details?id=" + packageName;
    }

}
