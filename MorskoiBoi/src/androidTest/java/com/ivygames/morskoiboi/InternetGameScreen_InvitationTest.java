package com.ivygames.morskoiboi;

import com.ivygames.morskoiboi.screen.view.InvitationButton;

import org.junit.After;
import org.junit.Test;

import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static org.hamcrest.Matchers.is;

public class InternetGameScreen_InvitationTest extends InternetGameScreen_ {

    private InvitationProxy invitations = new InvitationProxy();

    @After
    public void teardown() {
        super.teardown();

        invitations.destroy();
    }

    @Test
    public void WhenThereIsInvitation__EnvelopeIsShown() {
        invitations.setInvitations(INVITATIONS);
        showScreen();
        checkHasInvitation(true);
    }

    @Test
    public void WhenThereAreNoInvitations__EnvelopeIsHidden() {
        invitations.setInvitations(NO_INVITATIONS);
        showScreen();
        checkHasInvitation(false);
    }

    @Test
    public void WhenInvitationArrives__EnvelopeIsShown() {
        invitations.setInvitations(NO_INVITATIONS);
        showScreen();

        invitations.sendInvitation("Sagi", "test_id");

        checkHasInvitation(true);
    }

    private void checkHasInvitation(boolean hasInvitation) {
        InvitationButton button = (InvitationButton) viewById(R.id.view_invitations_button);
        assertThat(button.hasInvitation(), is(hasInvitation));
    }

}
