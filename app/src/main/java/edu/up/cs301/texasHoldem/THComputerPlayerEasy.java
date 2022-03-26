package edu.up.cs301.texasHoldem;

import edu.up.cs301.game.GameFramework.infoMessage.GameInfo;
import edu.up.cs301.game.GameFramework.players.GameComputerPlayer;
import edu.up.cs301.game.GameFramework.players.GamePlayer;

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

        Bet betAction = new Bet(this, betNeeded);

        game.sendAction(betAction); //let's see if this works
    }
}
