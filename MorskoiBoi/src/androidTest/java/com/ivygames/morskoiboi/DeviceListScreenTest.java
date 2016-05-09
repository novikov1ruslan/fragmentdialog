package com.ivygames.morskoiboi;

import com.ivygames.morskoiboi.bluetooth.BluetoothAdapterWrapper;
import com.ivygames.morskoiboi.screen.BattleshipScreen;
import com.ivygames.morskoiboi.screen.devicelist.DeviceListScreen;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;


public class DeviceListScreenTest extends ScreenTest {

    @Mock
    private BluetoothAdapterWrapper btAdapter;

    @Before
    public void setup() {
        btAdapter = mock(BluetoothAdapterWrapper.class);
        super.setup();
    }

    @Override
    public BattleshipScreen newScreen() {
//        when(btAdapter.getBondedDevices()).thenReturn(new HashSet<BluetoothDevice>());
        return new DeviceListScreen(activity, btAdapter);
    }

    @Test
    public void when_back_button_pressed__main_screen_opens() {
//        setScreen(newScreen());
//        pressBack();
//        onView(Matchers.<View>instanceOf(SelectGameScreen.class)).check(matches(isDisplayed()));
    }

}
