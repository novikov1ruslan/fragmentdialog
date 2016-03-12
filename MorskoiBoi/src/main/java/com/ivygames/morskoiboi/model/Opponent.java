package com.ivygames.morskoiboi.model;

public interface Opponent {

    int CURRENT_VERSION = 3;

    /**
     * This opponent is being shot at given coordinate. <br>
     * This call will trigger this opponent to call {@link #onShotResult(PokeResult)} on its opponent. <br>
     * If the result of the shot is {@link Cell#isHit()}, {@link #go()} method is called afterwards.
     */
    void onShotAt(Vector2 aim);

    /**
     * This opponent received result of his/her shot (Called on me)
     */
    void onShotResult(PokeResult pokeResult);

    void go();

    // TODO: remove it from the interface
    void setOpponent(Opponent opponent);

    void bid(int bid);

    String getName();

    void opponentLost(Board board);

    void setOpponentVersion(int ver);

    void onNewMessage(String text);
}
