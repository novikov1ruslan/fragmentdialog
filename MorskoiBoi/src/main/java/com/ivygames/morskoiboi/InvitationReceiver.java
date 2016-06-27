package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.rt.InvitationEvent;

public interface InvitationReceiver {
    void onEventMainThread(@NonNull InvitationEvent event);
}
