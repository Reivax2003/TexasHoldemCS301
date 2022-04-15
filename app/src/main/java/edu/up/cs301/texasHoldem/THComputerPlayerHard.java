package edu.up.cs301.texasHoldem;

import java.util.Random;

import edu.up.cs301.game.GameFramework.actionMessage.GameAction;
import edu.up.cs301.game.GameFramework.infoMessage.GameInfo;
import edu.up.cs301.game.GameFramework.players.GameComputerPlayer;
import edu.up.cs301.game.GameFramework.players.GamePlayer;
import edu.up.cs301.game.R;

/**
 * Simple computer player, folds 2% of the time, raises 25% of the time, and checks otherwise
 *
 * @author Xavier Santiago
 * @author Milton Nguy
 * @author Thomas Kone
 * @author Kevin Nguyen
 * @version 3.30.22
 */
public class THComputerPlayerHard extends GameComputerPlayer {

    private RankHand handRanker;

    /**
     * constructor
     * @param name the player's name (e.g., "John")
     */
    public THComputerPlayerHard(String name) {
        super(name);
    }

    @Override
    protected void receiveInfo(GameInfo info) {
        try {
            int sleepTime = (int) (Math.random()*1500)+500; //sleep somewhere between 0.5-2 secs
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (! (info instanceof THState)) {
            return;
        }
        THState gameState = new THState((THState) info); //deep copy of gamestate (needed for online)
        if (gameState.getPlayerTurn() != playerNum) { //make sure it's our turn
            return;
        }
        Player self = gameState.getPlayers().get(playerNum); //the Player object associated with us

        if (handRanker == null) {
            handRanker = gameState.getHandRanker();
        }

        //for some reason we need this line to double check that a round isn't over
        if (gameState.getCurrentBet() == self.getBet()) {
            return;
        }

        int betNeeded = gameState.getCurrentBet()-self.getBet(); //check

        if (true) {
            Fold action = new Fold(this);
            game.sendAction(action);
        } else {
            //make sure we don't bet above the amount we have
            Bet action = new Bet(this, Math.min(betNeeded, self.getBalance()));
            game.sendAction(action);
        }
    }
}
