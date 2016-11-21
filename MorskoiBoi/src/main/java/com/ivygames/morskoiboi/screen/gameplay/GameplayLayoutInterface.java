package com.ivygames.morskoiboi.screen.gameplay;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.ShotResult;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;

import java.util.Collection;

public interface GameplayLayoutInterface {

    void setSound(boolean soundOn);

    void setPlayerBoard(@NonNull Board board);

    void setEnemyBoard(@NonNull Board board);

    void allowAdjacentShips();

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

    void showOpponentSettingBoardNote(@NonNull String message);

    void setAim(@NonNull Vector2 aim);

    /**
     * unlocks and sets border
     */
    void playerTurn();

    /**
     * locks and sets border
     */
    void enemyTurn();

    void hideOpponentSettingBoardNotification();

    void removeAim();

    void setShotResult(@NonNull ShotResult result);

    void shakeEnemyBoard();

    void win();

    void shakePlayerBoard();

    void invalidate();

    void lost();

    void updateMyWorkingShips(@NonNull Collection<Ship> workingShips);

    void updateEnemyWorkingShips(@NonNull Collection<Ship> workingShips);

    void hideChatButton();

    void setLayoutListener(@NonNull GameplayLayoutListener listener);

    void setShipsSizes(@NonNull int[] shipsSizes);

    void setCurrentTime(int time);
}
