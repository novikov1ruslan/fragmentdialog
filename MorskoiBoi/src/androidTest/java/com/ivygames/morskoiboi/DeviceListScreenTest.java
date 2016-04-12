package com.ivygames.morskoiboi;

import android.bluetooth.BluetoothAdapter;

import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.bluetooth.BluetoothScreen;
import com.ivygames.morskoiboi.screen.devicelist.DeviceListScreen;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;


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
