package com.ivygames.morskoiboi.model;

import com.ivygames.morskoiboi.ShipUtils;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class ShipSerializationTest {

    @Test
    public void testJson() {
        Ship ship = new Ship(3, Ship.Orientation.VERTICAL);
        ship.setX(5);
        ship.setY(7);
        JSONObject shipJson = ShipSerialization.toJson(ship);

        Ship ship2 = ShipSerialization.fromJson(shipJson);
        assertThat(ShipUtils.similar(ship, ship2), is(true));
    }
}