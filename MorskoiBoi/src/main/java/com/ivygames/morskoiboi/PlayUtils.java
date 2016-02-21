package com.ivygames.morskoiboi;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class PlayUtils {

    private PlayUtils() {
        // utils
    }

    public static void rateApp(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getPlayUrl(context)));
        context.startActivity(intent);
    }

    public static String getPlayUrl(Context context) {
        return "https://play.google.com/store/apps/details?id=" + context.getPackageName();
    }

}
