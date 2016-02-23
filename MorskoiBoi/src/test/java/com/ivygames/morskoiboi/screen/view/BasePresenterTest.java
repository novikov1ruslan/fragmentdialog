package com.ivygames.morskoiboi.screen.view;

import android.graphics.Point;
import android.graphics.Rect;

import com.ivygames.morskoiboi.model.Ship;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.core.Is.*;
import static org.hamcrest.text.IsEqualIgnoringWhiteSpace.equalToIgnoringWhiteSpace;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class BasePresenterTest {

    private BasePresenter mPresenter;

    @Before
    public void setup() {
        mPresenter = new BasePresenter(10, 2);
        int hOffset = 10;
        int vOffset = 20;
        int hPadding = 6;
        int vPadding = 8;
        mPresenter.measure(320, 480, hOffset, vOffset, hPadding, vPadding);
    }

    @Test
    public void aiming_produces_correct_output() {
        int i = 5;
        int j = 5;
        int width = 1;
        int height = 1;
        Aiming aiming = mPresenter.getAiming(i, j, width, height);

        Aiming expected = new Aiming(new Rect(170, 105, 201, 415), new Rect(15, 260, 325, 291));
        assertThat(aiming.toString(), equalToIgnoringWhiteSpace(expected.toString()));
    }

    @Test
    public void aiming_produces_correct_output_2() {
        int i = 6;
        int j = 6;
        int width = 1;
        int height = 4;
        Aiming aiming = mPresenter.getAiming(i, j, width, height);

        Aiming expected = new Aiming(new Rect(201, 105, 232, 415), new Rect(15, 291, 325, 415));
        assertThat(aiming.toString(), equalToIgnoringWhiteSpace(expected.toString()));
    }

    @Test
    public void getMark() {
        Mark mark = mPresenter.getMark(0, 0);
        Mark expected = new Mark();
        expected.centerX = 30;
        expected.centerY = 120;
        expected.outerRadius = 9.0f;
        expected.innerRadius = 4.0f;
        assertThat(mark.toString(), equalToIgnoringWhiteSpace(expected.toString()));
    }

    @Test
    public void getBoard() {
        BoardG board = mPresenter.getBoard();
        float[][] lines={{15.0f, 105.0f, 15.0f, 415.0f},
        {46.0f, 105.0f, 46.0f, 415.0f},
        {77.0f, 105.0f, 77.0f, 415.0f},
        {108.0f, 105.0f, 108.0f, 415.0f},
        {139.0f, 105.0f, 139.0f, 415.0f},
        {170.0f, 105.0f, 170.0f, 415.0f},
        {201.0f, 105.0f, 201.0f, 415.0f},
        {232.0f, 105.0f, 232.0f, 415.0f},
        {263.0f, 105.0f, 263.0f, 415.0f},
        {294.0f, 105.0f, 294.0f, 415.0f},
        {15.0f, 105.0f, 325.0f, 105.0f},
        {15.0f, 136.0f, 325.0f, 136.0f},
        {15.0f, 167.0f, 325.0f, 167.0f},
        {15.0f, 198.0f, 325.0f, 198.0f},
        {15.0f, 229.0f, 325.0f, 229.0f},
        {15.0f, 260.0f, 325.0f, 260.0f},
        {15.0f, 291.0f, 325.0f, 291.0f},
        {15.0f, 322.0f, 325.0f, 322.0f},
        {15.0f, 353.0f, 325.0f, 353.0f},
        {15.0f, 384.0f, 325.0f, 384.0f},
        {15.0f, 415.0f, 325.0f, 415.0f},
        {0.0f, 0.0f, 0.0f, 0.0f}};

        BoardG expected = new BoardG();
        System.arraycopy( lines, 0, expected.lines, 0, lines.length );
        expected.frame=new Rect(14, 104, 326, 416);

        assertThat(board.toString(), equalToIgnoringWhiteSpace(expected.toString()));
    }

    @Test
    public void getRectForShip() {
        Rect rect = mPresenter.getRectForShip(new Ship(4));
        Rect expected = new Rect(15, 105, 139, 136);
        assertThat(rect.toString(), equalToIgnoringWhiteSpace(expected.toString()));
    }

    @Test
    public void getRectForShip2() {
        Rect rect = mPresenter.getRectForShip(new Ship(4), new Point(100, 100));
        Rect expected = new Rect(100, 100, 224, 131);
        assertThat(rect.toString(), equalToIgnoringWhiteSpace(expected.toString()));
    }

    @Test
    public void initially_turn_is_hidden() {
        assertThat(mPresenter.isTurn(), is(false));
    }

    @Test
    public void calling_showTurn_shows_turn() {
        mPresenter.showTurn();
        assertThat(mPresenter.isTurn(), is(true));
    }

    @Test
    public void calling_hideTurn_hides_turn() {
        mPresenter.showTurn();
        mPresenter.hideTurn();
        assertThat(mPresenter.isTurn(), is(false));
    }

    @Test
    public void foo() {
    }
}
