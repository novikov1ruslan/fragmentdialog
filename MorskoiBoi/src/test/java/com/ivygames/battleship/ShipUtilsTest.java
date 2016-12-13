package com.ivygames.battleship;

import com.ivygames.battleship.board.Vector;
import com.ivygames.battleship.ship.LocatedShip;
import com.ivygames.battleship.ship.Ship;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ShipUtilsTest {

    @Test
    public void testOnlyHorizontalShips() {
        Collection<Ship> ships = new ArrayList<>();

        boolean onlyHorizontalShips = ShipUtils.onlyHorizontalShips(ships);
        assertThat(onlyHorizontalShips, is(true));

        Ship vertical1 = new Ship(1, Ship.Orientation.VERTICAL);
        Ship horizontal2 = new Ship(2, Ship.Orientation.HORIZONTAL);
        Ship vertical2 = new Ship(2, Ship.Orientation.VERTICAL);

        ships = Arrays.asList(vertical1);
        onlyHorizontalShips = ShipUtils.onlyHorizontalShips(ships);
        assertThat(onlyHorizontalShips, is(true));

        ships = Arrays.asList(horizontal2);
        onlyHorizontalShips = ShipUtils.onlyHorizontalShips(ships);
        assertThat(onlyHorizontalShips, is(true));

        ships = Arrays.asList(vertical2);
        onlyHorizontalShips = ShipUtils.onlyHorizontalShips(ships);
        assertThat(onlyHorizontalShips, is(false));

        ships = Arrays.asList(vertical2, horizontal2);
        onlyHorizontalShips = ShipUtils.onlyHorizontalShips(ships);
        assertThat(onlyHorizontalShips, is(false));

        ships = Arrays.asList(vertical2, horizontal2, vertical1);
        onlyHorizontalShips = ShipUtils.onlyHorizontalShips(ships);
        assertThat(onlyHorizontalShips, is(false));
    }

    @Test
    public void NeighbouringCells8() {
        Collection<Vector> coordinates = getNeighboringCoordinates(1, 1);

        assertThat(coordinates, containsInAnyOrder(
                Vector.get(0, 0),
                Vector.get(1, 0),
                Vector.get(2, 0),

                Vector.get(0, 1),
                Vector.get(2, 1),

                Vector.get(0, 2),
                Vector.get(1, 2),
                Vector.get(2, 2)));
    }

    @Test
    public void NeighbouringCellsCorner() {
        Collection<Vector> coordinates = getNeighboringCoordinates(0, 0);

        assertThat(coordinates, containsInAnyOrder(
                Vector.get(0, 1),
                Vector.get(1, 1),
                Vector.get(1, 0)));
    }

    @Test
    public void NeighbouringCellsWall() {
        Collection<Vector> coordinates = getNeighboringCoordinates(0, 1);

        assertThat(coordinates, containsInAnyOrder(
                Vector.get(0, 0),
                Vector.get(0, 2),
                Vector.get(1, 0),
                Vector.get(1, 1),
                Vector.get(1, 2)));
    }

    @Test
    public void coordinateIsInHorizontalShip() {
        Vector coordinate = Vector.get(3, 4);
        LocatedShip locatedShip = new LocatedShip(new Ship(2, Ship.Orientation.HORIZONTAL), 2, 4);

        boolean inShip = ShipUtils.isInShip(coordinate, locatedShip);

        assertThat(inShip, is(true));
    }

    @Test
    public void coordinateIsInVerticalShip() {
        Vector coordinate = Vector.get(3, 4);
        LocatedShip locatedShip = new LocatedShip(new Ship(2, Ship.Orientation.VERTICAL), 3, 3);

        boolean inShip = ShipUtils.isInShip(coordinate, locatedShip);

        assertThat(inShip, is(true));
    }

    @Test
    public void coordinateIsNotInShip() {
        Vector coordinate = Vector.get(3, 4);
        LocatedShip locatedShip = new LocatedShip(new Ship(2, Ship.Orientation.VERTICAL), 2, 4);

        boolean inShip = ShipUtils.isInShip(coordinate, locatedShip);

        assertThat(inShip, is(false));
    }

    @Test
    public void getHorizontalShipCoordinates() {
        Ship ship = new Ship(3, Ship.Orientation.HORIZONTAL);
        Collection<Vector> coordinates = ShipUtils.getShipCoordinates(ship, Vector.get(3, 4));

        assertThat(coordinates, containsInAnyOrder(
                Vector.get(3, 4),
                Vector.get(4, 4),
                Vector.get(5, 4)));
    }

    @Test
    public void getVerticalShipCoordinates() {
        Ship ship = new Ship(3, Ship.Orientation.VERTICAL);
        Collection<Vector> coordinates = ShipUtils.getShipCoordinates(ship, Vector.get(2, 1));

        assertThat(coordinates, containsInAnyOrder(
                Vector.get(2, 1),
                Vector.get(2, 2),
                Vector.get(2, 3)));
    }

    private Collection<Vector> getNeighboringCoordinates(int i, int j) {
        return ShipUtils.getCoordinates(locatedShip(i, j), CoordinateType.NEAR_SHIP);
    }

    private LocatedShip locatedShip(int i, int j) {
        return new LocatedShip(new Ship(1), i, j);
    }
}