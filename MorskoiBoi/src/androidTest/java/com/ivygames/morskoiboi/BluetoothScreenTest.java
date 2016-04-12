package com.ivygames.morskoiboi;

import com.ivygames.morskoiboi.bluetooth.BluetoothAdapterWrapper;
import com.ivygames.morskoiboi.screen.bluetooth.BluetoothScreen;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static android.support.test.espresso.Espresso.pressBack;


public class BluetoothScreenTest extends ScreenTest {

    @Before
    public void setup() {
        super.setup();
    }

    @Override
    public BluetoothScreen newScreen() {
        BluetoothAdapterWrapper adapter = Mockito.mock(BluetoothAdapterWrapper.class);
        return new BluetoothScreen(activity(), adapter);
    }

    @Test
    public void when_back_button_pressed__select_game_screen_opens() {
        setScreen(newScreen());
        pressBack();
        checkDisplayed(SELECT_GAME_LAYOUT);
    }

}
