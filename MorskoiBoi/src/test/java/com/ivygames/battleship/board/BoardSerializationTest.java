package com.ivygames.battleship.board;

import android.support.annotation.NonNull;

import com.ivygames.battleship.ship.Ship;

import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.either;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class BoardSerializationTest {
    public static final String EMPTY_BOARD = "{\"ships\":[],\"cells\":\"                                                                                                    \"}";
    public static final String BOARD_WITH_SHIP_x1_5_5 = "{\"ships\":[{\"size\":1,\"is_horizontal\":true,\"x\":5,\"y\":5,\"health\":1}]," +
            "\"cells\":\"                                                                                                    \"}";
    public static final String BOARD_WITH_SHIP_x1_5_5_x2_9_8 = "{\"ships\":[" +
            "{\"size\":1,\"is_horizontal\":true,\"x\":5,\"y\":5,\"health\":1}," +
            "{\"size\":2,\"is_horizontal\":false,\"x\":9,\"y\":8,\"health\":2}" +
            "]," +
            "\"cells\":\"                                                                                                    \"}";

    public static final String BOARD_WITH_SHIP_x1_9_8_x2_5_5 = "{\"ships\":[" +
            "{\"size\":2,\"is_horizontal\":false,\"x\":9,\"y\":8,\"health\":2}" +
            "{\"size\":1,\"is_horizontal\":true,\"x\":5,\"y\":5,\"health\":1}," +
            "]," +
            "\"cells\":\"                                                                                                    \"}";

    private static final String LEGACY_BOARD_WITH_SHIP = "{\"ships\":[{\"size\":1,\"is_horizontal\":true,\"x\":5,\"y\":5,\"health\":1}]," +
            "\"cells\":\"                                            000       000       000                                 \"}";

    private Board mBoard = new Board();

    @Test
    public void serializationSucceeds() {
        Board board = new Board();
        board.addShip(new Ship(1), 5, 5);
        board.addShip(new Ship(2, Ship.Orientation.VERTICAL), 9, 8);

        String json = BoardSerialization.toJson(board).toString();

        assertThat(json, either(is(BOARD_WITH_SHIP_x1_5_5_x2_9_8)).or(is(BOARD_WITH_SHIP_x1_9_8_x2_5_5)));
    }

    @Test
    public void EmptyBoardSuccessfullyRecreated() {
        String json = BoardSerialization.toJson(mBoard).toString();
        Board board = BoardSerialization.fromJson(json);

        assertThat(BoardTestUtils.similar(mBoard, board), is(true));
    }

    @Test
    public void ParsingEmptyBoard() {
        Board board = BoardSerialization.fromJson(EMPTY_BOARD);

        assertBoardIsEmpty(board);
    }

    @Test
    public void ParsingBoardWithShip() {
        Board board2 = new Board();
        board2.addShip(new Ship(1), 5, 5);

        Board board1 = BoardSerialization.fromJson(BOARD_WITH_SHIP_x1_5_5);

        assertThat(BoardTestUtils.similar(board1, board2), is(true));
    }

    @Test
    public void ParsingLegacyBoardWithShip() {
        Board board2 = new Board();
        board2.addShip(new Ship(1), 5, 5);

        Board board1 = BoardSerialization.fromJson(LEGACY_BOARD_WITH_SHIP);

        assertThat(BoardTestUtils.similar(board1, board2), is(true));
    }

    @Test
    public void BoardWithShipSuccessfullySerializedAndParsed() {
        mBoard.addShip(new Ship(1), 5, 5);

        String json = BoardSerialization.toJson(mBoard).toString();
        Board board = BoardSerialization.fromJson(json);

        assertThat(BoardTestUtils.similar(mBoard, board), is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void ShouldThrowIllegalArgumentExceptionOnIllegalString() {
        BoardSerialization.fromJson("just some garbage");
    }

    @Test
    public void CopyOfABoard__IsIdenticalToOriginal() {
        Board board = BoardSerialization.fromJson(BOARD_WITH_SHIP_x1_5_5);

        Board copy = copy(board);

        assertThat(BoardTestUtils.similar(copy, board), is(true));
    }

    @NonNull
    private static Board copy(@NonNull Board board) {
        return BoardSerialization.fromJson(BoardSerialization.toJson(board));
    }

    private static void assertBoardIsEmpty(Board board) {
        for (int i = 0; i < Board.DIMENSION; i++) {
            for (int j = 0; j < Board.DIMENSION; j++) {
                assertTrue(board.getCell(i, j) == Cell.EMPTY);
            }
        }
    }

}