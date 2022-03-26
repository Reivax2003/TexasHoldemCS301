package edu.up.cs301.texasHoldem;

import java.util.Random;

import edu.up.cs301.game.GameFramework.actionMessage.GameAction;
import edu.up.cs301.game.GameFramework.infoMessage.GameInfo;
import edu.up.cs301.game.GameFramework.players.GameComputerPlayer;
import edu.up.cs301.game.GameFramework.players.GamePlayer;
import edu.up.cs301.game.R;

public class THComputerPlayerEasy extends GameComputerPlayer {
    /**
     * constructor
     * @param name the player's name (e.g., "John")
     */
    public THComputerPlayerEasy(String name) {
        super(name);
    }

    @Override
    protected void receiveInfo(GameInfo info) {
        if (! (info instanceof THState)) {
            return;
        }
        THState gameState = new THState((THState) info); //deep copy of gamestate (needed for online)
        if (gameState.getPlayerTurn() != playerNum) { //make sure it's out turn
            return;
        }
        Player self = gameState.getPlayers().get(playerNum); //the Player object associated with us

        //I made this check instead of fold so that the player doesn't immediately win
        int betNeeded = gameState.getCurrentBet()-self.getBet(); //check

        Random r = new Random();
        if (r.nextFloat() < .25) { // 1/4 chance to raise
            betNeeded += r.nextInt(5)*10; //raise anywhere from 10 to 50 (in multiples of 10)
        }

        if (r.nextFloat() < .02) { // 1/50 chance to fold (might be too low)
            Fold action = new Fold(this);
            game.sendAction(action);
        } else {
            Bet action = new Bet(this, betNeeded);
            game.sendAction(action);
        }
    }
}
