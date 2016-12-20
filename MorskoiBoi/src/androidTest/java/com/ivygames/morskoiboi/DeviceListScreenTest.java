package com.ivygames.morskoiboi;

import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.devicelist.DeviceListScreen;

import org.junit.Before;
import org.junit.Test;


public class DeviceListScreenTest extends ScreenTest {

    @Before
    public void setup() {
        super.setup();
    }

    @Override
    public BattleshipScreen newScreen() {
//        when(btAdapter.getBondedDevices()).thenReturn(new HashSet<BluetoothDevice>());
        return new DeviceListScreen(activity);
    }

    @Test
    public void when_back_button_pressed__main_screen_opens() {
//        setScreen(newScreen());
//        pressBack();
//        onView(Matchers.<View>instanceOf(SelectGameScreen.class)).check(matches(isDisplayed()));
    }

}
