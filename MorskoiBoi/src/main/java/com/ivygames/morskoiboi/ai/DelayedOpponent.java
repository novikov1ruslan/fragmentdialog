package com.ivygames.morskoiboi.ai;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.commons.logger.Ln;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Vector2;

public class DelayedOpponent implements Opponent {
	private static final int WHISTLE_SOUND_DELAY = 1300;

	private final Opponent mDelegate;
	private final ExecutorService mExecutor;

	public DelayedOpponent(Opponent opponent, ExecutorService executor) {
		mDelegate = opponent;
		mExecutor = executor;
	}

	@Override
	public void onShotResult(PokeResult pokeResult) {
		Ln.e("onShotResult");
		mExecutor.execute(new DelayCommand(WHISTLE_SOUND_DELAY));
		mExecutor.execute(new OnShotResultCommand(mDelegate, pokeResult));
	}

	@Override
	public void onShotAt(Vector2 aim) {

	}

	@Override
	public void go() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setOpponent(Opponent opponent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bid(int bid) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		return mDelegate.getName();
	}

	@Override
	public void onLost(Board board) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setOpponentVersion(int ver) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNewMessage(String text) {
		// TODO Auto-generated method stub

	}

	public void shotDown() {
		List<Runnable> scheduledTasks = mExecutor.shutdownNow();
		try {
			mExecutor.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Ln.w("termitated while waiting");
			Thread.currentThread().interrupt();
		}
	}

	private static class OnShotResultCommand implements Runnable {

		private final PokeResult mPokeResult;
		private final Opponent mOpponent;

		private OnShotResultCommand(Opponent opponent, PokeResult pokeResult) {
			mOpponent = opponent;
			mPokeResult = pokeResult;
		}

		@Override
		public void run() {
			mOpponent.onShotResult(mPokeResult);
		}
	}

	private class DelayCommand implements Runnable {

		private final long mTimeout;

		DelayCommand(long timeout) {
			mTimeout = timeout;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(mTimeout);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

	}

}
