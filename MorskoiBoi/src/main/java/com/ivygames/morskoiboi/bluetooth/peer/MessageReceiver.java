package com.ivygames.morskoiboi.bluetooth.peer;

import android.support.annotation.NonNull;

public interface MessageReceiver {
    void onMessageReceived(@NonNull String message);
}
