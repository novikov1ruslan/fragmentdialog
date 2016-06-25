package com.ivygames.morskoiboi.model;

import com.ivygames.morskoiboi.player.PlayerOpponent;

public class Model {
    private Model() {

    }

    public static PlayerOpponent player;
    public static Opponent opponent;

    public static void setOpponents(PlayerOpponent player, Opponent opponent) {
        Model.player = player;
        Model.opponent = opponent;
        Model.player.setOpponent(opponent);
        Model.opponent.setOpponent(player);
    }
}
