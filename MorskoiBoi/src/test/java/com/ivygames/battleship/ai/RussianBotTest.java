package com.ivygames.battleship.ai;

import com.ivygames.battleship.board.Board;
import com.ivygames.battleship.board.Cell;
import com.ivygames.battleship.board.Vector;

import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RussianBotTest {

    private RussianBot mBot;
    private Board mBoard = new Board();

    @Before
    public void setup() {
        mBot = newBot(0);
    }

    @Test
    public void shooting_on_empty_board() {
        assertShot(0, 0);
    }

    @Test
    public void shooting_when_already_missed() {
        missAt(0, 0);

        assertShot(0, 1);
    }

    @Test
    public void shooting_when_already_hit() {
        hitAt(5, 5);

        assertShot(4, 5);
    }

    @Test
    public void shooting_when_already_hit_twice() {
        hitAt(5, 5);
        hitAt(5, 4);

        assertShot(5, 3);
    }

    @Test
    public void shooting_when_already_hit_and_missed() {
        hitAt(5, 5);
        missAt(5, 4);

        assertShot(4, 5);
    }

    @Test
    public void shooting_horizontally_U() {
        hitAt(5, 5);

        assertMissAt(4, 5);
        assertMissAt(6, 5);

        assertHitAt(5, 4);
        assertShot(5, 3);
    }

    @Test
    public void shooting_horizontally_R_R() {
        hitAt(0, 0);

        assertHitAt(1, 0);
        assertShot(2, 0);
    }

    @Test
    public void shooting_horizontally_R() {
        hitAt(5, 5);

        assertMissAt(4, 5);

        assertHitAt(6, 5);
        assertShot(7, 5);
    }

    @Test
    public void shooting_vertically_L_L_R() {
        hitAt(5, 5);

        assertHitAt(4, 5);
        assertMissAt(3, 5);
        assertShot(6, 5);
    }

    @Test
    public void shooting_vertically_R_D() {
        missAt(0, 0);

        assertHitAt(0, 1);
        assertMissAt(1, 1);
        assertShot(0, 2);
    }

    @Test
    public void shooting_vertically_D() {
        hitAt(5, 5);

        assertMissAt(4, 5);
        assertMissAt(6, 5);
        assertMissAt(5, 4);

        assertHitAt(5, 6);
        assertShot(5, 7);
    }

    @Test
    public void hit_and_kill() {
        hitAt(5, 5);

        assertShot(4, 5);
    }

    private void assertHitAt(int x, int y) {
        Vector aim = mBot.shoot(mBoard);
        assertThat(aim, is(Vector.get(x, y)));
        hitAt(aim);
    }

    private void hitAt(int x, int y) {
        mBoard.setCell(Cell.HIT, Vector.get(x, y));
    }

    private void assertMissAt(int x, int y) {
        Vector aim = mBot.shoot(mBoard);
        assertThat(aim, is(Vector.get(x, y)));
        missAt(aim);
    }

    private void assertShot(int i, int j) {
        assertThat(mBot.shoot(mBoard), is(Vector.get(i, j)));
    }

    private void hitAt(Vector aim) {
        hitAt(aim.x, aim.y);
    }

    private void missAt(Vector aim) {
        missAt(aim.x, aim.y);
    }

    private void missAt(int x, int y) {
        mBoard.setCell(Cell.MISS, Vector.get(x, y));
    }

    private RussianBot newBot(int value) {
        Random random = mock(Random.class);
        when(random.nextInt()).thenReturn(value);
        return new RussianBot(random);
    }
}
