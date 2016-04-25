package com.ivygames.morskoiboi;

import android.support.annotation.NonNull;
import android.view.View;

import com.ivygames.morskoiboi.model.Game;

import org.hamcrest.Matcher;
import org.junit.Test;

import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class GameplayScreenTest extends GameplayScreen_ {

    @Test
    public void WhenBackPressed__DialogDisplayed() {
//        showScreen();
//        pressBack();
//        checkDisplayed(MAIN_LAYOUT);
    }

    @Test
    public void ForAndroidGame__ChatButtonHidden() {
        setGameType(Game.Type.VS_ANDROID);
        showScreen();
        checkNotDisplayed(chat());
    }

    @Test
    public void ForBluetoothGame__ChatButtonHidden() {
        setGameType(Game.Type.BLUETOOTH);
        showScreen();
        checkNotDisplayed(chat());
    }

    @Test
    public void ForInternetGame__ChatButtonVisible() {
        setGameType(Game.Type.INTERNET);
        showScreen();
        checkDisplayed(chat());
    }

    @NonNull
    protected final Matcher<View> chat() {
        return withId(R.id.chat_button);
    }

}
