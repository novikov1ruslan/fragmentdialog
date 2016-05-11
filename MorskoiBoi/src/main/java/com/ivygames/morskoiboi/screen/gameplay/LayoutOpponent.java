package com.ivygames.morskoiboi.screen.gameplay;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.ChatMessage;
import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;

import org.commons.logger.Ln;

import java.util.LinkedList;

class LayoutOpponent implements Opponent {

    @NonNull
    private final Opponent mOpponent;
    @NonNull
    private final GameplayLayoutInterface mLayout;
    @NonNull
    private final ChatAdapter mChatAdapter;

    @NonNull
    private final FooBar mFoobar;

    public LayoutOpponent(@NonNull Opponent opponent,
                          @NonNull GameplayLayoutInterface layout,
                          @NonNull ChatAdapter chatAdapter,
                          @NonNull FooBar foobar) {
        mOpponent = opponent;
        mLayout = layout;
        mChatAdapter = chatAdapter;
        mFoobar = foobar;
    }

    @Override
    public void go() {
        mOpponent.go();
        mLayout.playerTurn();
        hideOpponentSettingBoardNotification();
    }

    private void hideOpponentSettingBoardNotification() {
        Ln.d("hiding \"opponent setting board\" notification");
        mLayout.hideOpponentSettingBoardNotification();
    }

    @Override
    public void onShotResult(@NonNull final PokeResult result) {
        mOpponent.onShotResult(result);

        mLayout.removeAim();
        mLayout.setShotResult(result);
        mLayout.invalidateEnemyBoard();

        // TODO: call this only if ship sank
        mLayout.updateEnemyWorkingShips(mFoobar.getWorkingEnemyShips());

        if (shipSank(result.ship)) {
            Ln.v("enemy ship is sunk!! - shake enemy board");
            mLayout.shakeEnemyBoard();

            if (mFoobar.isEnemyBoardDefeated()) {
                Ln.d("enemy has lost!!!");
                mLayout.win();
//                showWinScreenDelayed();
            }
        } else if (result.cell.isMiss()) {
            mLayout.enemyTurn();
        }
    }

    private boolean shipSank(Ship ship) {
        return ship != null;
    }

    @Override
    public void onShotAt(@NonNull Vector2 aim) {
        PokeResult result = mFoobar.getLastShotResult();

        LinkedList<Ship> workingShips = mFoobar.getWorkingPlayerShips();
        mLayout.updateMyWorkingShips(workingShips);

        if (shipSank(result.ship)) { // KILL
            mLayout.shakePlayerBoard();
        } else if (result.cell.isMiss()) { // MISS
        } else { // HIT
            mLayout.invalidatePlayerBoard();
        }

        // If the opponent's version does not support board reveal, just switch screen in 3 seconds.
        // In the later version of the protocol opponent notifies about players defeat sending his board along.
        if (!mFoobar.versionSupportsBoardReveal()) {
            if (mFoobar.isPlayerBoardDefeated()) {
                mLayout.lost();
            }
        }
    }

    @Override
    public void setOpponent(@NonNull Opponent opponent) {
        // do nothing
    }

    @Override
    public void onEnemyBid(final int bid) {
        Ln.d("opponent's bid received: " + bid);
        hideOpponentSettingBoardNotification();
        if (mFoobar.isOpponentTurn()) {
            mLayout.enemyTurn();
        }
    }

    @Override
    public String getName() {
        return mOpponent.getName();
    }

    @Override
    public void onLost(@NonNull Board board) {
        mLayout.updateEnemyWorkingShips(mFoobar.getWorkingEnemyShips());
        mLayout.setEnemyBoard(board);
        mLayout.lost();
    }

    @Override
    public void setOpponentVersion(int ver) {
        mOpponent.setOpponentVersion(ver);
    }

    @Override
    public void onNewMessage(@NonNull String text) {
        mChatAdapter.add(ChatMessage.newEnemyMessage(text));
        mOpponent.onNewMessage(text);
    }

    @Override
    public String toString() {
        return mOpponent.toString();
    }

}
