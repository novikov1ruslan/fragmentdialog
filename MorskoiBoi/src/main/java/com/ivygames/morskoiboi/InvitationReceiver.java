package com.ivygames.morskoiboi;

import com.ivygames.morskoiboi.rt.InvitationEvent;

public interface InvitationReceiver {
    void onEventMainThread(InvitationEvent event);
}
