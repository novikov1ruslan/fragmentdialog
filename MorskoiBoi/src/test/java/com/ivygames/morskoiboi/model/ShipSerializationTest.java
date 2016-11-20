package com.ivygames.morskoiboi.model;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class ShipSerializationTest {

    public static boolean similar(Ship ship1, Ship ship2) {
        if (ship1.isDead() && !ship1.isDead()) {
            return false;
        }
        if (ship1.isHorizontal() && !ship2.isHorizontal()) {
            return false;
        }
        if (ship1.getHealth() != ship2.getHealth()) {
            return false;
        }

        return true;
    }

    @Test
    public void testJson() {
        Ship ship = new Ship(3, Ship.Orientation.VERTICAL);
        ship.setX(5);
        ship.setY(7);
        JSONObject shipJson = ShipSerialization.toJson(ship);

        Ship ship2 = ShipSerialization.fromJson(shipJson).ship;
        assertThat(similar(ship, ship2), is(true));
    }
}