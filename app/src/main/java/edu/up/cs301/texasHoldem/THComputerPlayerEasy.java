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
public class THComputerPlayerEasy extends GameComputerPlayer {
    /**
     * constructor
     * @param name the player's name (e.g., "John")
     */
    public THComputerPlayerEasy(String name) {
        super(name);
    }

    /**
     * When it becomes the AI's turn it has a 2% chance to fold, if it doesn't fold then it has a
     * 25% chance to raise, if neither happens then it checks
     * @param info the gamestate (usually) at the start of the AI's turn
     */
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

        int betNeeded = gameState.getCurrentBet()-self.getBet(); //check

        Random r = new Random();
         //1/50 chance to fold (might be too low)
        //also fold if you don't have enough to bet
        if (r.nextFloat() < 0.02 || betNeeded > self.getBalance()) {
            Fold action = new Fold(gameState.getPlayerID(self));
            game.sendAction(action);
        } else {
            if (r.nextFloat() < .25) { // 1/4 chance to raise
                //raise anywhere from 10 to 50 (in multiples of 10)
                betNeeded += (r.nextInt(4)+1)*10;
            }
            //make sure we don't bet above the amount we have
            Bet action = new Bet(gameState.getPlayerID(self),
                    Math.min(betNeeded, self.getBalance()));
            game.sendAction(action);
        }
    }
}
