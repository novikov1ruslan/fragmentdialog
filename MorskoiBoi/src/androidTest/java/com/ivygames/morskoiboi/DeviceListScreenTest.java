package com.ivygames.morskoiboi;

import android.view.View;

import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.devicelist.DeviceListScreen;
import com.ivygames.morskoiboi.screen.help.HelpScreen;
import com.ivygames.morskoiboi.screen.main.MainScreenLayout;
import com.ivygames.morskoiboi.screen.selectgame.SelectGameScreen;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;


public class DeviceListScreenTest extends ScreenTest {

    @Before
    public void setup() {
        super.setup();
    }

    @Override
    public BattleshipScreen newScreen() {
        return new DeviceListScreen(activity());
    }

    @Test
    public void when_back_button_pressed__main_screen_opens() {
        setScreen(newScreen());
        pressBack();
        onView(Matchers.<View>instanceOf(SelectGameScreen.class)).check(matches(isDisplayed()));
    }

}
