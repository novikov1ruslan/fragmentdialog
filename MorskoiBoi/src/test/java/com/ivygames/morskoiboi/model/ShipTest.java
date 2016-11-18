package com.ivygames.morskoiboi.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class ShipTest {

    @Test
    public void testSize() {
        Ship ship = new Ship(1);
        assertEquals(1, ship.getSize());
        ship = new Ship(2);
        assertEquals(2, ship.getSize());
        ship = new Ship(3);
        assertEquals(3, ship.getSize());
        ship = new Ship(4);
        assertEquals(4, ship.getSize());
    }

    @Test
    public void testConstructors() {
        Ship ship = new Ship(1);

        assertTrue(ship.isHorizontal());
        assertEquals(1, ship.getSize());

        ship = new Ship(4);
        assertEquals(4, ship.getSize());

        ship = new Ship(3, Ship.Orientation.VERTICAL);
        assertEquals(3, ship.getSize());
        assertFalse(ship.isHorizontal());

        ship = new Ship(2, Ship.Orientation.HORIZONTAL);
        assertEquals(2, ship.getSize());
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
    public void testX() {
        Ship ship = new Ship(1);
        assertEquals(0, ship.getX());

        ship.setX(5);
        assertEquals(5, ship.getX());
    }

    @Test
    public void testY() {
        Ship ship = new Ship(1);
        assertEquals(0, ship.getY());

        ship.setY(5);
        assertEquals(5, ship.getY());
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
        ship1.setX(5);
        ship1.setY(5);
        // TODO: position is changed together - make it a vector
        Ship ship2 = new Ship(2, Ship.Orientation.VERTICAL);
        ship2.setX(5);
        ship2.setY(5);

        Ship ship3 = new Ship(3, Ship.Orientation.VERTICAL);
        ship3.setX(4);
        ship3.setY(3);

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Vector2 v = Vector2.get(i, j);
                if ((i == 5 || i == 6) && j == 5) {
                    assertTrue(i + "," + j, Ship.isInShip(ship1, v));
                } else {
                    assertFalse(i + "," + j, Ship.isInShip(ship1, v));
                }

                if (i == 5 && (j == 5 || j == 6)) {
                    assertTrue(i + "," + j, Ship.isInShip(ship2, v));
                } else {
                    assertFalse(i + "," + j, Ship.isInShip(ship2, v));
                }

                if (i == 4 && (j >= 3 && j <= 5)) {
                    assertTrue(i + "," + j, Ship.isInShip(ship3, v));
                } else {
                    assertFalse(i + "," + j, Ship.isInShip(ship3, v));
                }
            }
        }
    }

}