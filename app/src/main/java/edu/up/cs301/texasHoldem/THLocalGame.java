package edu.up.cs301.texasHoldem;

import android.util.Log;

import java.util.ArrayList;

import edu.up.cs301.game.GameFramework.players.GamePlayer;
import edu.up.cs301.game.GameFramework.LocalGame;
import edu.up.cs301.game.GameFramework.actionMessage.*;

/**
 * LocalGame for Texas Holdem, defines rules and holds the master state
 * 
 * Credit to Steven R. Vegdahl for his Slapjack code
 *
 * @author Xavier Santiago
 * @author Milton Nguy
 * @author Thomas Kone
 * @author Kevin Nguyen
 * @version 3.30.22
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

	@Override
	public void start(GamePlayer[] players) {
    	//couldn't find a better way to initialize players -Xavier

		super.start(players); //this should initialize the playerNames array

		ArrayList<Player> gamePlayers = new ArrayList<Player>();
		//for each player create a Player object
		for (int i = 0; i < players.length; i++) {
			Player player = new Player(playerNames[i], 1000);
			gamePlayers.add(player);
		}
		//use this to create a fresh game
		state = new THState(gamePlayers);
		state.dealPlayers(); //this is the only place this should happen
		state.placeBlindBets(); //we treat the first player as the dealer
		sendAllUpdatedState(); //need to call this to update everyone of blind bets
	}

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

    	if (staticState.getActivePlayers() == 1) { //if only one player is left
    		Player winner = staticState.getActivePlayersList().get(0);
    		return "Winner: "+winner.getName()+", everyone else folded\n";
		}
    	boolean allIn = false;
    	if (checkIfAllIn(staticState)) {
    		allIn = true;
    		while (state.getRound() < 3) {
				state.nextRound(); //if it's not the last round, proceed to the next round
				sendAllUpdatedState(); //update all players
			}
			staticState = new THState(state); //make sure we have the actual version of the game
		}

		if (checkIfRoundOver(staticState) || allIn) {//check if the current round is over
			if (staticState.getRound() == 3 || allIn) { //if the current round is the last then the game is over

				//for now just evaluate highest card as win
				int high = 0;
				Player winner = null;
				for (Player player : staticState.getPlayers()) {
					if (!player.isFolded()) { //only care about players who are still in
						ArrayList<Card> hand = staticState.getDealerHand(); //arraylist is just easier
						hand.add(player.getHand()[0]);
						hand.add(player.getHand()[1]);
						Card best = staticState.highHand(hand);

						//EvaluateHand eh = new EvaluateHand(hand);

						String bestStr = staticState.bestHand(hand);

						int value = -100;

						switch (bestStr) {
							case "royal flush":
								value = 100 + best.getValue();
								best.storeValue(value);
								break;
							case "straight flush":
								value = 80 + best.getValue();
								best.storeValue(value);
								break;
							case "four of a kind":
								value = 60 + best.getValue();
								best.storeValue(value);
								break;
							case "full house":
								value = 40 + best.getValue();
								best.storeValue(value);
								break;
							case "flush":
								value = 20 + best.getValue();
								best.storeValue(value);
								break;
							case "straight":
								value = 0 + best.getValue();
								best.storeValue(value);
								break;
							case "three of a kind":
								value = -20 + best.getValue();
								best.storeValue(value);
								break;
							case "two pairs":
								value = -40 + best.getValue();
								best.storeValue(value);
								break;
							case "one pair":
								value = -60 + best.getValue();
								best.storeValue(value);
								break;
							case "high hand":
								value = -80 + best.getValue();
								best.storeValue(value);
								break;
						}

						if (best.getEvalValue() > high) {
							high = best.getValue();
							winner = player;
						} else if (best.getEvalValue() == high) {
							winner = null;
						}
						/*if (best.getValue() > high) {
							high = best.getValue();
							winner = player;
						} else if (best.getValue() == high) {
							winner = null;
						}

						 */

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
			if (player.getBet() != state.getCurrentBet() && !(player.isFolded()) && !(player.isAllIn())) {
				return false;
			}
		}
		return true;
	}

	public boolean checkIfAllIn(THState state) {
		for (int i = 0; i < state.getPlayers().size(); i++) {
			Player player = state.getPlayers().get(i);
			if (!(player.isFolded()) && !(player.isAllIn())) { //if player is not folded and not all in
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
