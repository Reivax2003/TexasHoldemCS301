package edu.up.cs301.texasHoldem;

import android.util.Log;

import java.util.ArrayList;

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
        Log.i("THLocalGame", "creating game");
        // create the state for the beginning of the game
        this.state = new THState();
        super.state = this.state;
    }

	public THLocalGame(THState initState) {
		Log.i("THLocalGame", "creating game");
		// create the state for the beginning of the game
		this.state = initState;
		super.state = initState;
	}

	//Gutted all the functions so we can add our own, original can still be accessed since it's on moodle
	//TODO: All functions below

	@Override
	protected void sendUpdatedStateTo(GamePlayer p) {
    	p.sendInfo(new THState(state));
	}

	@Override
	protected boolean canMove(int playerIdx) {
		return playerIdx == state.playerTurn;
	}

	@Override
    protected String checkIfGameOver() {
    	//make a copy state that won't change while we evaluate
    	THState staticState = new THState(state);
		if (checkIfRoundOver(staticState)) {//check if the current round is over
			if (staticState.getRound() == 3) { //if the current round is the last then the game is over

				//for now just evaluate highest card as win
				int high = 0;
				Player winner = null;
				for (Player player : staticState.getPlayers()) {
					ArrayList<Card> hand = staticState.getDealerHand(); //arraylist is just easier
					hand.add(player.getHand()[0]);
					hand.add(player.getHand()[1]);
					Card best = staticState.bestHand(hand);
					if (best.getValue() > high) {
						high = best.getValue();
						winner = player;
					} else if (best.getValue() == high) {
						winner = null;
					}
				}
				if (winner == null) {
					return "Game resulted in a tie";
				} else {
					// \n makes the message look nicer
					return "Winner: "+winner.getName()+", Highest valued card: "+high+"\n";
				}
			} else {
				//this one is the real state because we're making changes we want to apply
				state.nextRound(); //if it's not the last round, proceed to the next round
				sendAllUpdatedState();
			}
		}
		return null;
    }

    public boolean checkIfRoundOver(THState state) {
    	//iterate through all players
		for (int i = 0; i < state.getPlayers().size(); i++) {
			Player player = state.getPlayers().get(i);
			//a round is over if all players have bet the same amount except those who folded
			//this also works to check if all but one player folds
			if (player.getBet() != state.getCurrentBet() && !(player.isFolded())) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected boolean makeMove(GameAction action) {
    	boolean succeeded = false;
		if (action instanceof Bet) {
			Bet bet = (Bet) action;
			succeeded = state.bet(getPlayerIdx(bet.getPlayer()), bet.getAmount());
		} else if (action instanceof Fold) {
			succeeded = state.fold(getPlayerIdx(action.getPlayer()));
		}

		return succeeded;
	}
}
