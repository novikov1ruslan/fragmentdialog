package com.ivygames.common;

import android.content.Context;
import android.content.Intent;

public class Sharing {
    public static Intent createShareIntent(Context context, String greeting) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, greeting + PlayUtils.getPlayUrl(context));
        return shareIntent;
    }
}
