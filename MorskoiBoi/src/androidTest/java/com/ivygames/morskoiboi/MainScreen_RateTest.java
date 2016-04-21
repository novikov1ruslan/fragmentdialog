package com.ivygames.morskoiboi;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import com.ivygames.common.PlayUtils;

import org.hamcrest.Matcher;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MainScreen_RateTest extends MainScreenTest {
    @NonNull
    protected static Matcher<View> rateDialog() {
        return withText(R.string.rate_request);
    }

    @Test
    public void RateDialogShown() {
        when(settings().shouldProposeRating()).thenReturn(true);
        setScreen(newScreen());
        checkDisplayed(rateDialog());
    }

    @Test
    public void WhenRateButtonPressed__RateIntentFiredAndChoiceSaved() {
        RateDialogShown();
        Intent intent = PlayUtils.rateIntent(activity().getPackageName());
        clickForIntent(withText(R.string.rate), fromIntent(intent));
        verify(settings(), times(1)).setRated();
        checkDoesNotExist(rateDialog());
    }

    @Test
    public void WhenLaterButtonPressed__ChoiceSaved() {
        RateDialogShown();
        onView(withText(R.string.later)).perform(click());
        verify(settings(), times(1)).rateLater();
        checkDoesNotExist(rateDialog());
    }

    @Test
    public void WhenBackPressedOnRateDialog__DialogDismissed() {
        RateDialogShown();
        pressBack();
        verify(settings(), never()).rateLater();
        verify(settings(), never()).setRated();
        checkDoesNotExist(rateDialog());
    }

    @Test
    public void RateDialogNotShown() {
        when(settings().shouldProposeRating()).thenReturn(false);
        setScreen(newScreen());
        checkDoesNotExist(rateDialog());
    }
}