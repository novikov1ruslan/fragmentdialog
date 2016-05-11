package com.ivygames.morskoiboi.bluetooth;

import android.support.annotation.NonNull;

interface MessageReceiver {
    void onMessageReceived(@NonNull String message);
}
