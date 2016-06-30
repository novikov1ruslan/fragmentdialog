package com.ivygames.morskoiboi.selectgame;

import com.ivygames.morskoiboi.InvitationReceiver;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.screen.view.InvitationButton;

import org.junit.Test;

import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static org.hamcrest.Matchers.is;

public class SelectGameScreen_InvitationTest extends SelectGameScreen_ {
    @Test
    public void WhenThereIsInvitation__EnvelopeIsShown() {
        setInvitation(INVITATIONS);
        showScreen();
        checkHasInvitation(true);
    }

    @Test
    public void WhenThereAreNoInvitations__EnvelopeIsHidden() {
        setInvitation(NO_INVITATIONS);
        showScreen();
        checkHasInvitation(false);
    }

    @Test
    public void WhenInvitationArrives__EnvelopeIsShown() {
        setInvitation(NO_INVITATIONS);
        showScreen();
        sendInvitation((InvitationReceiver) screen());
        checkHasInvitation(true);
    }

    protected void checkHasInvitation(boolean hasInvitation) {
        InvitationButton button = (InvitationButton) viewById(R.id.via_internet);
        assertThat(button.hasInvitation(), is(hasInvitation));
    }
}
