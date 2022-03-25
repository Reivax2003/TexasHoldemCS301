package edu.up.cs301.texasHoldem;

import android.util.Log;

import edu.up.cs301.game.GameFramework.players.GamePlayer;
import edu.up.cs301.game.GameFramework.LocalGame;
import edu.up.cs301.game.GameFramework.actionMessage.*;

/**
 * The LocalGame class for a slapjack game.  Defines and enforces
 * the game rules; handles interactions between players.
 * 
 * @author Steven R. Vegdahl 
 * @version July 2013
 */

public class THLocalGame extends LocalGame {

    // the game's state
    THState state;

    /**
     * Constructors for the SJLocalGame.
     */
    public THLocalGame() {
    	super();
        Log.i("SJLocalGame", "creating game");
        // create the state for the beginning of the game
        this.state = new THState();
        super.state = this.state;
    }

	public THLocalGame(THState initState) {
		Log.i("SJLocalGame", "creating game");
		// create the state for the beginning of the game
		this.state = initState;
		super.state = initState;
	}

	//Gutted all the functions so we can add our own, original can still be accessed since it's on moodle
	//TODO: All functions below

	@Override
	protected void sendUpdatedStateTo(GamePlayer p) {

	}

	@Override
	protected boolean canMove(int playerIdx) {
		return false;
	}

	@Override
    protected String checkIfGameOver() {
    	return null;
    }

	@Override
	protected boolean makeMove(GameAction action) {
		return false;
	}
}
