package com.ivygames.battleship.board;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class VectorSerializationTest {
    private static final String VECTOR_3_7 = "{\"X\":3,\"Y\":7}";

    @Test
    public void serializationSucceeds() {
        String string = VectorSerialization.toJson(Vector.get(3, 7)).toString();

        assertThat(string, is(VECTOR_3_7));
    }

    @Test
    public void deSerializationSucceeds() {
        Vector vector = VectorSerialization.fromJson(VECTOR_3_7);

        assertThat(vector, is(Vector.get(3, 7)));
    }
}