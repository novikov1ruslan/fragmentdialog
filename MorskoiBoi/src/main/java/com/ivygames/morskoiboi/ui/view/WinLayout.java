package com.ivygames.morskoiboi.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.ivygames.morskoiboi.R;
import com.ivygames.morskoiboi.model.Ship;

import org.commons.logger.Ln;

import java.util.Collection;

public class WinLayout extends WinLayoutSmall {

	private static final int[] SHIP1_IDS = { R.id.ship1_1, R.id.ship1_2, R.id.ship1_3, R.id.ship1_4 };

	private static final int[] SHIP2_IDS = { R.id.ship2_1, R.id.ship2_2, R.id.ship2_3 };

	private static final int[] SHIP3_IDS = { R.id.ship3_1, R.id.ship3_2 };

	public WinLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void setShips(Collection<Ship> ships) {
		ImageView shipView;
		int i1 = 0;
		int i2 = 0;
		int i3 = 0;
		for (Ship ship : ships) {
			if (ship.isDead()) {
				continue;
			}
			switch (ship.getSize()) {
			case 4:
				shipView = (ImageView) findViewById(R.id.ship4);
				shipView.setImageResource(R.drawable.aircraft_carrier);
				break;
			case 3:
				shipView = (ImageView) findViewById(SHIP3_IDS[i3]);
				shipView.setImageResource(R.drawable.battleship);
				i3++;
				break;
			case 2:
				shipView = (ImageView) findViewById(SHIP2_IDS[i2]);
				shipView.setImageResource(R.drawable.frigate);
				i2++;
				break;
			case 1:
				shipView = (ImageView) findViewById(SHIP1_IDS[i1]);
				shipView.setImageResource(R.drawable.gunboat);
				i1++;
				break;

			default:
				Ln.w("wrong ship size: " + ship.getSize());
				break;
			}
		}
		invalidate();
	}

}
