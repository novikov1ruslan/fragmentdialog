package com.ivygames.morskoiboi.renderer;

import android.graphics.Point;
import android.graphics.Rect;

import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.screen.view.Aiming;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class BaseGeometryProcessorTest {

    private BaseGeometryProcessor mPresenter;
    private static final int V_OFFSET = 20;
    private static final int H_PADDING = 6;
    private static final int V_PADDING = 8;

    @Before
    public void setup() {
        mPresenter = new BaseGeometryProcessor(10, 2);
        mPresenter.measure(320, 480, H_PADDING, V_PADDING);
        mPresenter.setBoardVerticalOffset(V_OFFSET);
    }

    @Test
    public void aiming_produces_correct_output() {
        int i = 5;
        int j = 5;
        int width = 1;
        int height = 1;
        Aiming aiming = new Aiming();
        aiming.set(i, j, width, height);
        AimingG aimingG = mPresenter.getAimingG(aiming);

        AimingG expected = new AimingG(new Rect(160, 105, 191, 415), new Rect(5, 260, 315, 291));
        assertThat(aimingG, equalTo(expected));
    }

    @Test
    public void aiming_produces_correct_output_2() {
        int i = 6;
        int j = 6;
        int width = 1;
        int height = 4;
        AimingG aiming = mPresenter.getAimingG(Vector2.get(i, j), width, height);

        AimingG expected = new AimingG(new Rect(191, 105, 222, 415), new Rect(5, 291, 315, 415));
        assertThat(aiming, equalTo(expected));
    }

    @Test
    public void aiming_truncated_for_width() {
        int i = 9;
        int j = 9;
        int width = 4;
        int height = 1;
        AimingG aiming = mPresenter.getAimingG(Vector2.get(i, j), width, height);

        AimingG expected = new AimingG(new Rect(284, 105, 315, 415), new Rect(5, 384, 315, 415));
        assertThat(aiming, equalTo(expected));
    }

    @Test
    public void aiming_truncated_for_height() {
        int i = 9;
        int j = 9;
        int width = 1;
        int height = 4;
        AimingG aiming = mPresenter.getAimingG(Vector2.get(i, j), width, height);

        AimingG expected = new AimingG(new Rect(284, 105, 315, 415), new Rect(5, 384, 315, 415));
        assertThat(aiming, equalTo(expected));
    }

    @Test(expected=IllegalStateException.class)
    public void getMarkThrowsException() {
        mPresenter = new BaseGeometryProcessor(10, 2);
        mPresenter.getMark(0, 0);
    }

    @Test(expected=IllegalStateException.class)
    public void getRectForShipThrowsException() {
        mPresenter = new BaseGeometryProcessor(10, 2);
        mPresenter.getRectForShip(new Ship(1));
    }

    @Test(expected=IllegalArgumentException.class)
    public void getAimingThrowsException() {
        mPresenter = new BaseGeometryProcessor(10, 2);
        Aiming aiming = new Aiming();
        aiming.set(0, 0, 0, 1);
        mPresenter.getAimingG(aiming);
    }

    @Test
    public void getMark() {
        Mark mark = mPresenter.getMark(0, 0);
        Mark expected = new Mark();
        expected.centerX = 20;
        expected.centerY = 120;
        expected.outerRadius = 9.0f;
        expected.innerRadius = 4.0f;
        assertThat(mark, equalTo(expected));
    }

    @Test
    public void getMarkForLandscape() {
        mPresenter.measure(800, 480, H_PADDING, V_PADDING);
        mPresenter.setBoardVerticalOffset(V_OFFSET);

        Mark mark = mPresenter.getMark(0, 0);
        Mark expected = new Mark();
        expected.centerX = 188;
        expected.centerY = 48;
        expected.outerRadius = 14.0f;
        expected.innerRadius = 7.0f;

        assertThat(mark, equalTo(expected));
    }

    @Test
    public void getBoard() {
        BoardG board = mPresenter.getBoardG();
        float[][] lines={{5.0f, 105.0f, 5.0f, 415.0f},
                {36.0f, 105.0f, 36.0f, 415.0f},
                {67.0f, 105.0f, 67.0f, 415.0f},
                {98.0f, 105.0f, 98.0f, 415.0f},
                {129.0f, 105.0f, 129.0f, 415.0f},
                {160.0f, 105.0f, 160.0f, 415.0f},
                {191.0f, 105.0f, 191.0f, 415.0f},
                {222.0f, 105.0f, 222.0f, 415.0f},
                {253.0f, 105.0f, 253.0f, 415.0f},
                {284.0f, 105.0f, 284.0f, 415.0f},
                {5.0f, 105.0f, 315.0f, 105.0f},
                {5.0f, 136.0f, 315.0f, 136.0f},
                {5.0f, 167.0f, 315.0f, 167.0f},
                {5.0f, 198.0f, 315.0f, 198.0f},
                {5.0f, 229.0f, 315.0f, 229.0f},
                {5.0f, 260.0f, 315.0f, 260.0f},
                {5.0f, 291.0f, 315.0f, 291.0f},
                {5.0f, 322.0f, 315.0f, 322.0f},
                {5.0f, 353.0f, 315.0f, 353.0f},
                {5.0f, 384.0f, 315.0f, 384.0f},
                {5.0f, 415.0f, 315.0f, 415.0f},
                {0.0f, 0.0f, 0.0f, 0.0f}};

        BoardG expected = new BoardG();
        System.arraycopy( lines, 0, expected.lines, 0, lines.length );
        expected.frame=new Rect(4, 104, 316, 416);

        assertThat(board, equalTo(expected));
    }

    @Test
    public void getRectForShipHorizontal() {
        Rect rect = mPresenter.getRectForShip(new Ship(4, Ship.Orientation.HORIZONTAL));
        Rect expected = new Rect(5, 105, 129, 136);
        assertThat(rect, equalTo(expected));
    }

    @Test
    public void getRectForShipVertical() {
        Rect rect = mPresenter.getRectForShip(new Ship(4, Ship.Orientation.VERTICAL));
        Rect expected = new Rect(5, 105, 36, 229);
        assertThat(rect, equalTo(expected));
    }

    @Test
    public void getRectForShip2() {
        Rect rect = mPresenter.getRectForShip(new Ship(4), new Point(100, 100));
        Rect expected = new Rect(100, 100, 224, 131);
        assertThat(rect, equalTo(expected));
    }
}
