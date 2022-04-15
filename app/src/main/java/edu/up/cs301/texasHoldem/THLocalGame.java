package edu.up.cs301.texasHoldem;

import android.content.Context;
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
    Context context;
    RankHand handRanker;

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

	public THLocalGame(THState initState, Context context) {
		Log.i("THLocalGame", "creating game");
		// create the state for the beginning of the game
		this.state = initState;
		super.state = initState;
		this.context = context;
		handRanker = new RankHand(context);
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

	//Gutted all the functions so we can add our own, original can still be accessed since it's on moodle
	//TODO: All functions below

	@Override
	protected void sendUpdatedStateTo(GamePlayer p) {
    	THState copy = new THState(state);
    	copy.redactFor(getPlayerIdx(p)); //remove other player's cards
		copy.setHandRanker(handRanker);
    	p.sendInfo(copy);
	}

	@Override
	protected boolean canMove(int playerIdx) {
		return playerIdx == state.playerTurn;
	}

	@Override
    protected String checkIfGameOver() {
    	if (state.getActivePlayers() == 1) { //if only one player is left
    		Player winner = state.getActivePlayersList().get(0);
    		return "Winner: "+winner.getName()+", everyone else folded\n";
		}
    	boolean allIn = false;
    	if (checkIfAllIn(state)) {
    		allIn = true;
    		while (state.getRound() < 3) { //TODO
				state.nextRound(); //if it's not the last round, proceed to the next round
				sendAllUpdatedState(); //update all players
			}
		}

		if (checkIfRoundOver(state) || allIn) {//check if the current round is over
			if (state.getRound() == 3 || allIn) { //if the current round is the last then the game is over
				//need to keep track if players tied, but only if they have the highest cards
				ArrayList<Player> winners = new ArrayList<>();
				int bestHand = 9999; //lower is better, this is higher than any
				for (Player player : state.getPlayers()) { //loop through all players
					if (!player.isFolded()) { //only care if player is in
						//grab player and dealer cards
						Card[] dHand = state.getDealerHandAsArray();
						Card[] allCards = new Card[dHand.length + 2];
						allCards[0] = player.getHand()[0];
						allCards[1] = player.getHand()[1];

						//combine into one list
						for (int i = 0; i < dHand.length; i++) {
							allCards[i + 2] = dHand[i];
						}

						//get rank of hand
						int handValue = handRanker.getHandRank(allCards);
						//lower means better, 1 is a royal flush
						if (handValue < bestHand) {
							bestHand = handValue;
							winners.clear();
							winners.add(player);
						}
						//it's possible for multiple hands to have the same rank
						//for example, you can have two full houses of equal value
						else if (handValue == bestHand) {
							winners.add(player);
						}
					}
				}
				if (winners.size() == 0) {
					return "something went wrong\nwinner could not be evaluated";
				} else if (winners.size() == 1) {
					Log.i("Winning hand rank", ""+bestHand); //for debugging
					//this is usually going to happen, just print the winner
					return winners.get(0).getName()+" wins with a "
							+handRanker.getRankText(bestHand)+"\n";
				} else if (winners.size() == 2) {
					Log.i("Winning hand rank", ""+bestHand); //for debugging
					//have to handle a 2 player tie separately so the message looks right
					String message = winners.get(0).getName()+" and "+winners.get(0).getName()
							+" tied with a "+handRanker.getRankText(bestHand)+"\n";
					return message;
				}
				else {
					Log.i("Winning hand rank", ""+bestHand); //for debugging
					String message = "";
					//iterate through and add players to message
					for (int i = 0; i < winners.size()-1; i++) {
						message = message+winners.get(i).getName()+", ";
					}
					message = message+"and "+winners.get(winners.size()-1).getName()+" tied with a "
							+handRanker.getRankText(bestHand)+"\n";
					return message;
				}

				//Below is the brute force approach to hand evaluation
				/**
				//for now just evaluate highest card as win
				int high = -200;
				Player winner = null;
				for (Player player : state.getPlayers()) { //TODO check dealerhand
					ArrayList<Card> hand = state.getDealerHand(); //arraylist is just easier
					hand.add(player.getHand()[0]);
					hand.add(player.getHand()[1]);
					Card best = state.highHand(hand);

					//Log.d("hands: ", hand.toString());
					//EvaluateHand eh = new EvaluateHand(hand);

					String bestStr = state.bestHand(hand);

					int value = -100;

					switch(bestStr) {
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
						case "straight": //Not performing straight correctly when theres two pairs
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
						case "one pair": //TODO fix getvalue of besthand as it should be getting combo
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

				/**
				}
				if (winner == null) {
					return "Game resulted in a tie";
				} else {
					// \n makes the message look nicer
					return "Winner: "+winner.getName()+", Highest valued card: "+high+"\n";
				}*/
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
