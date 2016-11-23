package com.ivygames.battleship.ai;

import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.board.Cell;
import com.ivygames.battleship.board.Coordinate;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RussianBotTest {

    private RussianBot mBot;

    @Before
    public void setup() {
        Random random = mock(Random.class);
        when(random.nextInt()).thenReturn(0);
        mBot = new RussianBot(random);
    }

    @Test
    public void provided_random_seed_is_1_shooting_on_empty_board_returns_8_5() {
        Board board = new Board();
        assertThat(mBot.shoot(board), Is.is(Coordinate.get(0, 0)));
    }

    @Test
    public void shooting_when_already_missed() {
        Board board = new Board();
        missAt(board, 8, 5);
        assertThat(mBot.shoot(board), Is.is(Coordinate.get(0, 0)));
    }

    private void missAt(Board board, int x, int y) {
        Cell cell = Cell.MISS;
        Coordinate aim = Coordinate.get(x, y);
        board.setCell(cell, aim);
    }

    @Test
    public void shooting_when_already_hit() {
        Board board = new Board();
        hitAt(board, 5, 5);
        assertThat(mBot.shoot(board), Is.is(Coordinate.get(4, 5)));
    }

    @Test
    public void shooting_when_already_hit_twice() {
        Board board = new Board();
        hitAt(board, 5, 5);
        hitAt(board, 5, 4);
        assertThat(mBot.shoot(board), Is.is(Coordinate.get(5, 3)));
    }

    @Test
    public void shooting_when_already_hit_and_missed() {
        Board board = new Board();
        hitAt(board, 5, 5);
        missAt(board, 5, 4);
        assertThat(mBot.shoot(board), Is.is(Coordinate.get(4, 5)));
    }

    @Test
    public void shooting_horizontally_5_4_3() {
        Board board = new Board();
        hitAt(board, 5, 5);
        assertMissAt(board, 4, 5);
        assertMissAt(board, 6, 5);
        assertHitAt(board, 5, 4);
        assertHitAt(board, 5, 3);
    }

    @Test
    public void shooting_horizontally_5_6_7() {
        Board board = new Board();
        hitAt(board, 5, 5);
        assertMissAt(board, 4, 5);
        assertHitAt(board, 6, 5);
        assertHitAt(board, 7, 5);
        assertHitAt(board, 8, 5);
    }

    @Test
    public void shooting_vertically_5_4_6() {
        Board board = new Board();
        hitAt(board, 5, 5);
        assertHitAt(board, 4, 5);
        assertMissAt(board, 3, 5);
        assertHitAt(board, 6, 5);
    }

    @Test
    public void shooting_vertically_8_7_6_9() {
        Board board = new Board();
        assertMissAt(board, 0, 0);
        assertHitAt(board, 0, 1);
        assertMissAt(board, 1, 1);
        assertHitAt(board, 0, 2);
    }

    @Test
    public void shooting_vertically_5_6_7() {
        Board board = new Board();
        hitAt(board, 5, 5);
        assertMissAt(board, 4, 5);
        assertMissAt(board, 6, 5);
        assertMissAt(board, 5, 4);
        assertHitAt(board, 5, 6);
        assertHitAt(board, 5, 7);
    }

    @Test
    public void hit_and_kill() {
        Board board = new Board();
        Coordinate aim = Coordinate.get(5, 5);
        board.setCell(Cell.HIT, aim);

        Coordinate shoot = mBot.shoot(board);

        assertThat(shoot, Is.is(Coordinate.get(4, 5)));
    }

    private void assertHitAt(Board board, int x, int y) {
        Coordinate aim = mBot.shoot(board);
        assertThat(aim, Is.is(Coordinate.get(x, y)));
        hitAt(board, aim);
    }

    private void hitAt(Board board, int x, int y) {
        Cell cell = Cell.HIT;
        Coordinate aim = Coordinate.get(x, y);
        board.setCell(cell, aim);
    }

    private void assertMissAt(Board board, int x, int y) {
        Coordinate aim = mBot.shoot(board);
        assertThat(aim, Is.is(Coordinate.get(x, y)));
        missAt(board, aim);
    }

    private void hitAt(Board board, Coordinate aim) {
        hitAt(board, aim.i, aim.j);
    }

    private void missAt(Board board, Coordinate aim) {
        missAt(board, aim.i, aim.j);
    }

}
