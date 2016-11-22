package com.ivygames.battleship.ship;

import com.ivygames.battleship.board.BoardTestUtils;
import com.ivygames.battleship.board.LocatedShip;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class ShipSerializationTest {

    @Test
    public void testJson() {
        Ship ship = new Ship(3, Ship.Orientation.VERTICAL);
        JSONObject shipJson = ShipSerialization.toJson(new LocatedShip(ship, 5, 7));

        Ship ship2 = ShipSerialization.fromJson(shipJson).ship;
        assertThat(BoardTestUtils.similar(ship, ship2), is(true));
    }
}