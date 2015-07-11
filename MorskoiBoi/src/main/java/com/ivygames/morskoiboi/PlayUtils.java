package com.ivygames.morskoiboi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.google.android.gms.plus.PlusShare;

public class PlayUtils {

    private PlayUtils() {
    }

    public static void rateApp(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getPlayUrl(context)));
        context.startActivity(intent);
    }

    public static String getPlayUrl(Context context) {
        return "https://play.google.com/store/apps/details?id=" + context.getPackageName();
    }

    public static void foo(Activity activity) {
        PlusShare.Builder builder = new PlusShare.Builder(activity);

        // Set call-to-action metadata.
        builder.addCallToAction(
                "CREATE_ITEM", /** call-to-action button label */
                Uri.parse("http://plus.google.com/pages/create"), /** call-to-action url (for desktop use) */
                "/pages/create" /** call to action deep-link ID (for mobile use), 512 characters or fewer */);

        // Set the content url (for desktop use).
        builder.setContentUrl(Uri.parse("https://plus.google.com/pages/"));

        // Set the target deep-link ID (for mobile use).
        builder.setContentDeepLinkId("/pages/",
                null, null, null);

        // Set the share text.
        builder.setText("Create your Google+ Page too!");

        activity.startActivityForResult(builder.getIntent(), 0);
    }


}
