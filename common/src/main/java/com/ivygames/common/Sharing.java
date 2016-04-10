package com.ivygames.common;

import android.content.Intent;
import android.net.Uri;

public class Sharing {
    public static final String EMAIL = "ivy.games.studio@gmail.com";

    public static Intent createShareIntent(String packageName, String greeting) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, greeting + PlayUtils.getPlayUrl(packageName));
        return shareIntent;
    }

    public static Intent getEmailIntent(String chooserTitle, String subject, String version) {
        Uri uri = Uri.parse("mailto:" + EMAIL);
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(uri);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject + " (" + version + ")");
//        intent.putExtra(Intent.EXTRA_TEXT, "hi android jack!");
        return Intent.createChooser(intent, chooserTitle);
    }
}
