package com.ivygames.morskoiboi.main;

import com.ivygames.morskoiboi.InvitationProxy;
import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.screen.view.InvitationButton;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static org.hamcrest.Matchers.is;

public class MainScreen_InvitationTest extends MainScreen_ {

    private InvitationProxy invitations = new InvitationProxy();

    @After
    public void teardown() {
        super.teardown();

        invitations.destroy();
    }

    //@Test
    public void WhenThereIsInvitation__EnvelopeIsShown() {
        invitations.setInvitations(INVITATIONS);
        showScreen();
        checkHasInvitation(true);
    }

    //@Test
    public void WhenThereAreNoInvitations__EnvelopeIsHidden() {
        invitations.setInvitations(NO_INVITATIONS);
        showScreen();
        checkHasInvitation(false);
    }

    //@Test
    public void WhenInvitationArrives__EnvelopeIsShown() {
        invitations.setInvitations(NO_INVITATIONS);
        showScreen();
        invitations.sendInvitation("Sagi", "test_id");
        checkHasInvitation(true);
    }

    private void checkHasInvitation(boolean hasInvitation) {
        InvitationButton button = (InvitationButton) viewById(R.id.play);
        assertThat(button.hasInvitation(), is(hasInvitation));
    }
}
