package com.ivygames.battleship.ship;

import com.ivygames.battleship.board.Vector;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ShipSerializationTest {
    private static final String VERTICAL_3_H3_5_7 = "{\"size\":3,\"is_horizontal\":false,\"x\":5,\"y\":7,\"health\":3}";

    @Test
    public void successfulSerialization() {
        Ship ship = new Ship(3, Ship.Orientation.VERTICAL);
        String shipJson = ShipSerialization.toJson(new LocatedShip(ship, 5, 7)).toString();

        assertThat(shipJson, is(VERTICAL_3_H3_5_7));
    }

    @Test
    public void successfulDeSerialization() {
        LocatedShip locatedShip = ShipSerialization.fromJson(VERTICAL_3_H3_5_7);

        assertThat(locatedShip.ship.getHealth(), is(3));
        assertThat(locatedShip.ship.isHorizontal(), is(false));
        assertThat(locatedShip.ship.size, is(3));
        assertThat(locatedShip.coordinate, is(Vector.get(5, 7)));
    }
}