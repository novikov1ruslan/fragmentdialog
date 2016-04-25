package com.ivygames.morskoiboi.screen.gameplay;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;

import java.util.Collection;

public interface GameplayLayoutInterface extends TimeConsumer {

    void setSound(boolean soundOn);

    void setPlayerBoard(@NonNull Board board);

    void setEnemyBoard(@NonNull Board board);

    /**
     * @param millis time in milliseconds
     */
    void setAlarmTime(int millis);

    void lock();

    void setPlayerName(@NonNull CharSequence name);

    void setEnemyName(@NonNull CharSequence name);

    void setShotListener(@NonNull ShotListener listener);

    boolean isLocked();

    void unLock();

    void showOpponentSettingBoardNotification(@NonNull String message);

    void setAim(@NonNull Vector2 aim);

    void playerTurn();

    void enemyTurn();

    void hideOpponentSettingBoardNotification();

    void removeAim();

    void setShotResult(@NonNull PokeResult result);

    void invalidateEnemyBoard();

    void shakeEnemyBoard();

    void win();

    long getUnlockedTime();

    void shakePlayerBoard();

    void invalidatePlayerBoard();

    void lost();

    void updateMyWorkingShips(@NonNull Collection<Ship> workingShips);

    void updateEnemyWorkingShips(@NonNull Collection<Ship> workingShips);

    void hideChatButton();

    void setListener(@NonNull GameplayLayoutListener listener);

    void setShipsSizes(@NonNull int[] shipsSizes);
}
