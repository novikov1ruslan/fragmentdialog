package com.ivygames.common.multiplayer;

/**
 * Called during game creation.
 */
public interface GameCreationListener {

    void gameAborted();

    /**
     * All participants are ready and RTMs are going to flow any moment.
     */
    void gameStarted();
}
