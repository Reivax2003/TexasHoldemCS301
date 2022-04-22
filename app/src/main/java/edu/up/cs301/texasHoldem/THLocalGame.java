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

	public THLocalGame(THState initState, Context context, RankHand handRanker) {
		Log.i("THLocalGame", "creating game");
		// create the state for the beginning of the game
		this.state = initState;
		super.state = initState;
		this.context = context;
		this.handRanker = handRanker;
	}

	/**
	 * the start method initializes an array of GamePlayers by inheriting from
	 * super class and creates the GameState with a set balance of $1000
	 * to give to all players that will be playing.
	 * Thus, it'll call actions to deal cards to players and place blindbets.
	 * */
	@Override
	public void start(GamePlayer[] players) {
		super.start(players);

		ArrayList<Player> gamePlayers = new ArrayList<Player>();
		//for each player create a Player object
		for (int i = 0; i < players.length; i++) {
			Player player = new Player(playerNames[i], 1000);
			gamePlayers.add(player);
		}
		//create a fresh game
		state = new THState(gamePlayers);
		state.dealPlayers();
		state.placeBlindBets();
		sendAllUpdatedState(); //need to call this to update everyone of blind bets
	}

	/**
	 * sends the updated state to a player, redacting information like opponent hands first
	 * @param p the player who the state should be sent to
	 */
	@Override
	protected void sendUpdatedStateTo(GamePlayer p) {
    	THState copy = new THState(state);
    	copy.redactFor(getPlayerIdx(p)); //remove other player's cards
		int value = handRanker.getHandRank(compileCards(getPlayerIdx(p)));
		copy.setPlayerHandValue(getPlayerIdx(p), value); //tell the player their hand rank
    	p.sendInfo(copy);
	}

	/**
	 * sends an un-redacted GameState to a player
	 * @param p the player to send the state to
	 */
	private void sendFinalStateTo(GamePlayer p) {
		THState copy = new THState(state);
		p.sendInfo(copy);
	}

	/**
	 * sends an un-redacted GameState to all the players
	 */
	private void sendFinalStateToAll() {
		for (GamePlayer p : players) {
			sendFinalStateTo(p);
		}
	}

	/**
	 * checks whether the player is allowed to move (if it is their turn)
	 * @param playerIdx the player's player-number (ID)
	 * @return whether or not the player can take an action right now
	 */
	@Override
	protected boolean canMove(int playerIdx) {
		return playerIdx == state.getPlayerTurn();
	}

	/**
	 * checks if the game has ended, by doing so also progresses the round of the game if needed
	 * @return a string containing the win message. if the game has not ended returns null
	 */
	@Override
    protected String checkIfGameOver() {
    	if (state.getActivePlayers() == 1) { //if only one player is left
    		Player winner = state.getActivePlayersList().get(0);
			sendFinalStateToAll();
    		return "Winner: "+winner.getName()+", everyone else folded\n";
		}
    	boolean allIn = false;
    	if (checkIfAllIn(state)) {
    		allIn = true;
    		while (state.getRound() < 3) {
				state.nextRound(); //if it's not the last round, proceed to the next round and update.
				sendAllUpdatedState();
			}
		}

    	//if current round is over, then game is over and track who has the best hand
		if (checkIfRoundOver(state) || allIn) {
			if (state.getRound() == 3 || allIn) {
				//need to keep track if players tied, but only if they have the highest cards
				ArrayList<Player> winners = new ArrayList<>();
				int bestHand = 9999; //lower is better, this is higher than any

				//loop through active players (not folded), and compare
				//player and dealer cards.
				for (Player player : state.getPlayers()) {
					if (!player.isFolded()) {
						Card[] allCards = compileCards(state.getPlayerID(player));

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
				//lines 144-165 are for debugging purposes
				if (winners.size() == 0) {
					sendFinalStateToAll();
					return "something went wrong\nwinner could not be evaluated";
				} else if (winners.size() == 1) {
					Log.i("Winning hand rank", ""+bestHand);
					sendFinalStateToAll();
					//this is usually going to happen, just print the winner
					return winners.get(0).getName()+" wins with a "
							+handRanker.getRankText(bestHand)+"\n";
				} else if (winners.size() == 2) {
					Log.i("Winning hand rank", ""+bestHand);
					sendFinalStateToAll();
					//have to handle a 2 player tie separately so the message looks right
					String message = winners.get(0).getName()+" and "+winners.get(0).getName()
							+" tied with a "+handRanker.getRankText(bestHand)+"\n";
					return message;
				}
				else {
					Log.i("Winning hand rank", ""+bestHand);
					String message = "";
					//iterate through and add players to message
					for (int i = 0; i < winners.size()-1; i++) {
						message = message+winners.get(i).getName()+", ";
					}
					message = message+"and "+winners.get(winners.size()-1).getName()+" tied with a "
							+handRanker.getRankText(bestHand)+"\n";
					sendFinalStateToAll();
					return message;
				}
			} else {
				//this one is the real state because we're making changes we want to apply
				state.nextRound(); //if it's not the last round, proceed to the next round
				sendAllUpdatedState();
			}
		}
		return null;
    }

	/**
	 * returns whether or not the round is over. for the round to be over everyone must have had a
	 * turn and must either have a matching bet or be folded
	 * @param state the GameState to evaluate
	 * @return whether or not the round is over
	 */
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
		if (!state.allChecked()) { //see if everyone has had a turn
			return false;
		}
		return true;
	}

	/**
	 * checks whether everyone in the game is "all in" (has bet all their money)
	 * @param state the state to evaluate
	 * @return whether or not every player is flagged as "all in"
	 */
	public boolean checkIfAllIn(THState state) {
		for (int i = 0; i < state.getPlayers().size(); i++) {
			Player player = state.getPlayers().get(i);
			if (!(player.isFolded()) && !(player.isAllIn())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * takes an action
	 * @param action The move that the player has sent to the game
	 * @return whether or not the action succeeded
	 */
	@Override
	protected boolean makeMove(GameAction action) {
    	boolean succeeded = false;
    	//send action
		if (action instanceof Bet) {
			Bet bet = (Bet) action; //have to cast to bet to get amount
			succeeded = state.bet(bet.getPlayerID(), bet.getAmount());
		} else if (action instanceof Fold) {
			succeeded = state.fold(action.getPlayerID());
		}

		return succeeded;
	}

	/**
	 * compiles the player's hand and the dealer's hand into one list of cards
	 * @param playerID the player whose hand to combine the dealer's hand with
	 * @return a list of cards containing both the player and dealer's cards
	 */
	public Card[] compileCards(int playerID) {
		Card[] dHand = state.getDealerHandAsArray();
		Card[] allCards = new Card[dHand.length + 2];
		allCards[0] = state.getPlayers().get(playerID).getHand()[0];
		allCards[1] = state.getPlayers().get(playerID).getHand()[1];

		//combine into one list
		for (int i = 0; i < dHand.length; i++) {
			allCards[i + 2] = dHand[i];
		}
		return allCards;
	}
}
