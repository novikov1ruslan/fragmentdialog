package com.ivygames.morskoiboi.internet;

import android.app.Activity;
import android.support.test.espresso.matcher.ViewMatchers;

import com.ivygames.morskoiboi.BattleshipActivity;
import com.ivygames.morskoiboi.R;

import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.ivygames.morskoiboi.ScreenUtils.checkDisplayed;
import static com.ivygames.morskoiboi.ScreenUtils.clickOn;
import static org.mockito.Mockito.when;

public class InternetGameScreenTest extends InternetGameScreen_ {

    @Test
    public void WhenBackPressed__SelectGameScreenOpens() {
        showScreen();

        pressBack();

        checkDisplayed(SELECT_GAME_LAYOUT);
    }

    @Test
    public void WhenScreenIsDisplayed__PlayerNameIsShown() {
        when(settings().getPlayerName()).thenReturn("Sagi");

        showScreen();

        onView(ViewMatchers.withId(R.id.player_name)).check(matches(withText("Sagi")));
    }

    @Test
    public void WhenInvitePlayerPressed__WaitDialogIsDisplayed_And_InviteScreenShown() {
        showScreen();

        clickOn(invite());

        checkDisplayed(waitDialog());
//        verify(multiplayer, times(1)).invitePlayers(anyInt(), any(RoomListener.class));
    }

    @Test
    public void WhenViewInvitationsPressed__WaitDialogIsDisplayed_And_InvitationsShown() {
        showScreen();

        clickOn(viewInvitations());

        checkDisplayed(waitDialog());
//        verify(multiplayer, times(1)).showInvitations(anyInt(), any(RoomListener.class));
    }

    @Test
    public void WhenQuickGamePressed__WaitDialogIsDisplayed() {
        showScreen();

        clickOn(quickGame());

        checkDisplayed(waitDialog());
    }

    @Test
    public void WhenWaitDialogIsDisplayed__PressingBackHasNoEffect() {
        WhenInvitePlayerPressed__WaitDialogIsDisplayed_And_InviteScreenShown();

        pressBack();

        checkDisplayed(waitDialog());
    }

    @Test
    public void WhenWaitDialogIsDisplayed__CancellingTheInvitationRemovesDialog() {
        WhenInvitePlayerPressed__WaitDialogIsDisplayed_And_InviteScreenShown();

        onActivityResult(BattleshipActivity.RC_SELECT_PLAYERS, Activity.RESULT_CANCELED, null);


        checkDoesNotExist(waitDialog());
    }

    @Test
    public void WhenWaitDialogIsDisplayed__CancellingWaitingRoomRemovesDialog() {
        WhenInvitePlayerPressed__WaitDialogIsDisplayed_And_InviteScreenShown();

        onActivityResult(BattleshipActivity.RC_SELECT_PLAYERS, Activity.RESULT_CANCELED, null);
        onActivityResult(BattleshipActivity.RC_SELECT_PLAYERS, Activity.RESULT_OK, null);

        checkDoesNotExist(waitDialog());
    }

}