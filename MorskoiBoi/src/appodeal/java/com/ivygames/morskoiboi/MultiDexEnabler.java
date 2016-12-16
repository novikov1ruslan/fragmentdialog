package com.ivygames.morskoiboi;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;

class MultiDexEnabler {
    static void enable(@NonNull Context context) {
        MultiDex.install(context);
    }
}
