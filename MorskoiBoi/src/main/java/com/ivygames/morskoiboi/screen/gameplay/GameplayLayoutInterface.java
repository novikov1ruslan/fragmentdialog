package com.ivygames.morskoiboi.screen.gameplay;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;

import java.util.Collection;

public interface GameplayLayoutInterface extends TimeConsumer {
    void setSound(boolean soundOn);

//    void setVibration(boolean vibrationOn);

//    void hideVibrationSetting();

    void setTotalTime(int turnTimeout);

    void setPlayerBoard(Board mPlayerPrivateBoard);

    void setEnemyBoard(Board mEnemyPublicBoard);

    /**
     * @param millis time in milliseconds
     */
    void setAlarmTime(int millis);

    void lock();

    void setPlayerName(CharSequence name);

    void setEnemyName(CharSequence name);

    void setShotListener(ShotListener boardShotListener);

    boolean isLocked();

    void unLock();

    void showOpponentSettingBoardNotification(String message);

    void setAim(Vector2 aim);

    void playerTurn();

    void hideOpponentSettingBoardNotification();

    void removeAim();

    void setShotResult(PokeResult result);

    void invalidateEnemyBoard();

    void shakeEnemyBoard();

    void win();

    long getUnlockedTime();

    void shakePlayerBoard();

    void invalidatePlayerBoard();

    void lost();

    void setEnemyShips(Collection<Ship> fullFleet);

    void hideChatButton();

    void setListener(GameplayLayoutListener gameplayLayoutListener);

    void enemyTurn();

    void setMyShips(Collection<Ship> ships);

    void setShipsSizes(@NonNull int[] shipsSizes);
}
