package com.ivygames.morskoiboi;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.view.View;

import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.devicelist.DeviceListScreen;
import com.ivygames.morskoiboi.screen.selectgame.SelectGameScreen;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class DeviceListScreenTest extends ScreenTest {

    private BluetoothAdapter btAdapter;

    @Before
    public void setup() {
        super.setup();
    }

    @Override
    public BattleshipScreen newScreen() {
//        btAdapter = mock(BluetoothAdapter.class);
//        when(btAdapter.getBondedDevices()).thenReturn(new HashSet<BluetoothDevice>());
        return new DeviceListScreen(activity(), BluetoothAdapter.getDefaultAdapter());
    }

    @Test
    public void when_back_button_pressed__main_screen_opens() {
//        setScreen(newScreen());
//        pressBack();
//        onView(Matchers.<View>instanceOf(SelectGameScreen.class)).check(matches(isDisplayed()));
    }

}
