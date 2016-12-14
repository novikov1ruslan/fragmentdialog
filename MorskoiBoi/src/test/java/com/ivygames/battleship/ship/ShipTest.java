package com.ivygames.battleship.ship;

import com.ivygames.battleship.ShipUtils;
import com.ivygames.battleship.board.Vector;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ShipTest {

    @Test
    public void testSize() {
        Ship ship = new Ship(1);
        assertEquals(1, ship.size);
        ship = new Ship(2);
        assertEquals(2, ship.size);
        ship = new Ship(3);
        assertEquals(3, ship.size);
        ship = new Ship(4);
        assertEquals(4, ship.size);
    }

    @Test
    public void testConstructors() {
        Ship ship = new Ship(1);

        assertTrue(ship.isHorizontal());
        assertEquals(1, ship.size);

        ship = new Ship(4);
        assertEquals(4, ship.size);

        ship = new Ship(3, Ship.Orientation.VERTICAL);
        assertEquals(3, ship.size);
        assertFalse(ship.isHorizontal());

        ship = new Ship(2, Ship.Orientation.HORIZONTAL);
        assertEquals(2, ship.size);
        assertTrue(ship.isHorizontal());
    }

    @Test
    public void isHorizontal() {
        Ship ship = new Ship(2, Ship.Orientation.HORIZONTAL);
        assertTrue(ship.isHorizontal());

        ship = new Ship(2, Ship.Orientation.VERTICAL);
        assertFalse(ship.isHorizontal());
    }

    @Test
    public void testRotate() {
        Ship ship = new Ship(4, Ship.Orientation.HORIZONTAL);
        ship.rotate();
        assertFalse(ship.isHorizontal());
        ship.rotate();
        assertTrue(ship.isHorizontal());
    }

    @Test
    public void testIsDead() {
        // for (int i = 0; i < 5; i++) {
        Ship ship = new Ship(3);
        assertFalse(ship.isDead());
        ship.shoot();
        ship.shoot();
        ship.shoot();
        assertTrue(ship.isDead());
        // }
    }

    @Test
    public void testShoot() {
        Ship ship = new Ship(2);
        ship.shoot();
        assertFalse(ship.isDead());
        ship.shoot();
        assertTrue(ship.isDead());
    }

    @Test
    public void testIsInShip() {
        Ship ship1 = new Ship(2, Ship.Orientation.HORIZONTAL);
        Ship ship2 = new Ship(2, Ship.Orientation.VERTICAL);
        Ship ship3 = new Ship(3, Ship.Orientation.VERTICAL);

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Vector v = Vector.get(i, j);
                if ((i == 5 || i == 6) && j == 5) {
                    assertTrue(i + "," + j, ShipUtils.isInShip(v, new LocatedShip(ship1, 5, 5)));
                } else {
                    assertFalse(i + "," + j, ShipUtils.isInShip(v, new LocatedShip(ship1, 5, 5)));
                }

                if (i == 5 && (j == 5 || j == 6)) {
                    assertTrue(i + "," + j, ShipUtils.isInShip(v, new LocatedShip(ship2, 5, 5)));
                } else {
                    assertFalse(i + "," + j, ShipUtils.isInShip(v, new LocatedShip(ship2, 5, 5)));
                }

                if (i == 4 && (j >= 3 && j <= 5)) {
                    assertTrue(i + "," + j, ShipUtils.isInShip(v, new LocatedShip(ship3, 4, 3)));
                } else {
                    assertFalse(i + "," + j, ShipUtils.isInShip(v, new LocatedShip(ship3, 4, 3)));
                }
            }
        }
    }

}