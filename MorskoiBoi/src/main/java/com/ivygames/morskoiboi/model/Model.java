package com.ivygames.morskoiboi.model;

import com.ivygames.morskoiboi.PlayerOpponent;

public class Model {
	public static final Model instance = new Model();

	private Model() {

	}

	public PlayerOpponent player;
	public Opponent opponent;
	public Game game;

	public void setOpponents(PlayerOpponent player, Opponent opponent) {
		this.player = player;
		this.opponent = opponent;
		this.player.setOpponent(opponent);
		this.opponent.setOpponent(player);
	}
}
