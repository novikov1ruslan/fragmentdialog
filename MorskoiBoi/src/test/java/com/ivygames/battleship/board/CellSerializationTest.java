package com.ivygames.battleship.board;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CellSerializationTest {

    @Test
    public void testParse() {
        Cell cell = CellSerialization.parse(' ');
        assertThat(cell, is(Cell.EMPTY));

        cell = CellSerialization.parse('*');
        assertThat(cell, is(Cell.MISS));

        cell = CellSerialization.parse('X');
        assertThat(cell, is(Cell.HIT));

        cell = CellSerialization.parse('0');
        assertThat(cell, is(Cell.EMPTY));
    }

    @Test
    public void testToChar() {
        char cell = CellSerialization.toChar(Cell.EMPTY);
        assertThat(cell, is(' '));

        cell = CellSerialization.toChar(Cell.MISS);
        assertThat(cell, is('*'));

        cell = CellSerialization.toChar(Cell.HIT);
        assertThat(cell, is('X'));
    }
}
