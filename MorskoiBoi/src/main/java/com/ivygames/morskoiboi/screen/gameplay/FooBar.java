package com.ivygames.morskoiboi.screen.gameplay;

import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.utils.GameUtils;

import java.util.Collection;
import java.util.LinkedList;

public class FooBar {
    public Collection<Ship> getWorkingEnemyShips() {
        return null;
    }

    public boolean isEnemyBoardDefeated() {
//        mRules.isItDefeatedBoard(mEnemyPublicBoard);
        return false;
    }

    public LinkedList<Ship> getWorkingPlayerShips() {
//        return GameUtils.getWorkingShips(mPlayerPrivateBoard.getShips());
        return null;
    }

    public boolean isPlayerBoardDefeated() {
//        return mRules.isItDefeatedBoard(mPlayerPrivateBoard);
        return false;
    }

    public boolean versionSupportsBoardReveal() {
//        return mPlayer.getOpponentVersion() >= GameUtils.PROTOCOL_VERSION_SUPPORTS_BOARD_REVEAL;
        return false;
    }

    public PokeResult getLastShotResult() {
//        mPlayer.onShotAtForResult(aim)
        return null;
    }

    public boolean isOpponentTurn() {
//        mPlayer.isOpponentTurn();
        return false;
    }
}
