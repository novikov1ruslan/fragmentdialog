package com.ivygames.morskoiboi.ai;

import android.support.annotation.NonNull;

import com.ivygames.morskoiboi.AbstractOpponent;
import com.ivygames.morskoiboi.Bidder;
import com.ivygames.morskoiboi.Cancellable;
import com.ivygames.morskoiboi.CancellableOpponent;
import com.ivygames.morskoiboi.GameConstants;
import com.ivygames.morskoiboi.Rules;
import com.ivygames.morskoiboi.model.Board;
import com.ivygames.morskoiboi.model.Opponent;
import com.ivygames.morskoiboi.model.PokeResult;
import com.ivygames.morskoiboi.model.Ship;
import com.ivygames.morskoiboi.model.Vector2;
import com.ivygames.morskoiboi.utils.GameUtils;
import com.ivygames.morskoiboi.variant.RussianBot;

import org.commons.logger.Ln;

import java.util.Collection;
import java.util.Random;

import static com.ivygames.common.analytics.ExceptionHandler.reportException;

public class AndroidOpponent extends AbstractOpponent implements Cancellable {

    private volatile BotAlgorithm mBot;

    private final String mName;
    private final PlacementAlgorithm mPlacement;
    private final CancellableOpponent mDelegate;
    private final Rules mRules;
    private Opponent mOpponent;

    public AndroidOpponent(@NonNull String name,
                           @NonNull Board board,
                           @NonNull PlacementAlgorithm placement,
                           @NonNull Rules rules,
                           @NonNull CancellableOpponent delegate) {
        mName = name;
        mMyBoard = board;
        mPlacement = placement;
        mRules = rules;
        mDelegate = delegate;
        Ln.d(this + ": initializing boards and bids");
//        mEnemyBoard.clearBoard();
//        mMyBoard.clearBoard();
        mMyBid = new Bidder().newBid();
        mEnemyBid = OPPONENT_NOT_READY_BID;
        mBot = new RussianBot(new Random(System.currentTimeMillis()));//BotFactory.getAlgorithm(); // TODO: generalize FIXME
        Ln.v("new android opponent created");
    }

    @Override
    public void setOpponent(@NonNull Opponent opponent) {
        mOpponent = opponent;
        Ln.d(this + ": my opponent is " + opponent);
        mDelegate.setOpponent(opponent);
    }

    @Override
    protected final void reset(int myBid) {
        super.reset(myBid);
        mBot = new RussianBot(new Random(System.currentTimeMillis()));//BotFactory.getAlgorithm(); // TODO: generalize FIXME
    }

    private void placeShips() {
        mPlacement.populateBoardWithShips(mMyBoard, generateFullFleet());
        if (GameConstants.IS_TEST_MODE) {
            Ln.i(this + ": my board: " + mMyBoard);
        }
    }

    @NonNull
    private Collection<Ship> generateFullFleet() {
        return GameUtils.generateShipsForSizes(mRules.getAllShipsSizes());
    }

    @Override
    public void go() {
        if (!isOpponentReady()) {
            init();
            placeShips();
        }
        super.go();
        mDelegate.onShotAt(mBot.shoot(mEnemyBoard));
    }

    @Override
    public void onShotAt(@NonNull Vector2 aim) {
        PokeResult result = createResultForShootingAt(aim);
        mDelegate.onShotResult(result);
        boolean andGo = result.cell.isHit() && !mRules.isItDefeatedBoard(mMyBoard);
        if (andGo) {
            Ln.d("Android is hit, passing turn to " + mOpponent);
            mDelegate.go();
        }
    }

    @Override
    public void onShotResult(@NonNull PokeResult result) {
        mBot.setLastResult(result);
        updateEnemyBoard(result, mPlacement);
        Ln.v(result);

        if (result.cell.isMiss()) {
            Ln.d(this + ": I missed - passing the turn to " + mOpponent);
            mOpponent.go();
        } else if (result.ship != null) {
            if (mRules.isItDefeatedBoard(mEnemyBoard)) {
                Ln.d(this + ": I lost - notifying " + mOpponent);
                mOpponent.onLost(mMyBoard);
                reset(new Bidder().newBid());
            }
        }
    }

    @NonNull
    @Override
    public String getName() {
        return mName;
    }

    @Override
    public void onEnemyBid(int bid) {
        super.onEnemyBid(bid);
        init();
        placeShips();
        if (mEnemyBid == mMyBid) {
            reportException("stall");
            mMyBid = new Random(System.currentTimeMillis() + this.hashCode()).nextInt(Integer.MAX_VALUE);
        }
        mOpponent.setOpponentVersion(Opponent.CURRENT_VERSION);
        Ln.d("bidding against " + mOpponent + " with result " + isOpponentTurn());
        if (isOpponentTurn()) {
            mDelegate.go();
        } else {
            mDelegate.onEnemyBid(mMyBid);
        }
    }

    @Override
    public void setOpponentVersion(int ver) {
        Ln.d("player's protocol version = " + ver);
    }

    @Override
    public void onLost(Board board) {
        Ln.d("android lost - preparing for the next round");
        mDelegate.cancel();
        reset(new Bidder().newBid());
    }

    @Override
    public void onNewMessage(@NonNull String text) {
        // mirroring
        mOpponent.onNewMessage(text + "!!!");
    }

    @Override
    public void init() {
        mDelegate.init();
    }

    @Override
    public void cancel() {
        mDelegate.cancel();
    }

    @Override
    public String toString() {
        return mName;
    }

}
