package edu.up.cs301.texasHoldem;

import java.util.ArrayList;

import edu.up.cs301.game.GameFramework.GameMainActivity;
import edu.up.cs301.game.GameFramework.infoMessage.GameState;
import edu.up.cs301.game.GameFramework.LocalGame;
import edu.up.cs301.game.GameFramework.gameConfiguration.*;
import edu.up.cs301.game.GameFramework.players.GamePlayer;

/**
 * Final Release Report
 *
 * Project #j stays to follow the submission steps for project #i, so we assumed that the report
 * was included. Overall, our project is in a far better state than it was for the Beta release,
 * with all the requirements and features complete. I'll quickly go through each point on the
 * grading rubric and discuss how our game is doing.
 *
 * Rules of play - Fully complete, I believe we have every rule that is involved in texas holdem
 * including the more obscure ones that we didn't know about until recently like how to break a tie
 * or the fact that you are allowed to bet zero at the start of a round as a form of checking. the
 * only thing that could be seen as a deviation is the fact that you can't play multiple rounds,
 * however this technically isn't a rule in poker I don't think, since you can leave a game with
 * your winnings.
 *
 * Bugs - Currently there are no known bugs, all issues on github have been resolved.
 *
 * GUI - In my subjective opinion our GUI looks very nice. The only thing to say here is that player
 * profiles aren't as complete as they could be. It would be better if we could get a pool of random
 * images to use rather than blank profile icons, but we didn't have enough time. Either way, we've
 * got a pretty easy to use GUI, including things like indicating which players are folded and whose
 * turn it is by changing their name on the screen.
 *
 * AI - This is also complete. While it might be difficult to tell which AI is the "best" as a
 * player, the hard AI definitely thinks before it acts while the easy AI doesn't. We also have a
 * shy AI which is very similar to the hard AI but bets less often (see THComputerPlayerShy for a
 * full explanation).
 *
 * Network Play - Working! this was the largest problem with our Beta release, but we fixed it. We
 * did have to edit the gameFramework a bit to get it to work, but as far as we can tell from
 * testing no problems occurred as a result.
 *
 * Coding Standard - We've tried pretty hard to keep our code up to the standard. Pretty much all
 * methods should have descriptive headers and most code should be explained at least briefly. In
 * places we're probably edging on having useless comments, but overall this should be all good.
 *
 * Unit tests - We haven't added very many unit tests, but the ones we have cover a pretty good
 * variety of cases. No tests have been removed (as far as I can tell) and all pass.
 *
 * Extra Features - We don't have any of the examples listed, but here's the stuff we have that
 * isn't a required part of poker:
 * - opponent profile pictures
 * - a timer
 * - a hand quality meter
 * - an easter egg (there's a 1% chance for a players profile icon to be replaced with cheems)
 *
 * Overall our game is in a good state. Although we aren't supposed to keep working on it, there may
 * be a few sparse changes or additions over the weekend since I writing this know I don't have much
 * I need to study for for finals. Either way, I hope whoever is grading this enjoys our game and
 * thank you for reading our very long report on it.
 */

 /**
 * this is the primary activity for TexasHoldem game
 * 
 * Credit to Steven R. Vegdahl for his Slapjack code
 *
 * @author Xavier Santiago
 * @author Milton Nguy
 * @author Thomas Kone
 * @author Kevin Nguyen
 * @version 3.30.22
 */
public class THMainActivity extends GameMainActivity {
	
	public static final int PORT_NUMBER = 4752;

	/** a Texas Holdem game for 2-4 players, with the default of 1 human and 1 hard AI */
	@Override
	public GameConfig createDefaultConfig() {
		// Define the allowed player types
		ArrayList<GamePlayerType> playerTypes = new ArrayList<GamePlayerType>();

		playerTypes.add(new GamePlayerType("human player") {
			public GamePlayer createPlayer(String name) {
				return new THHumanPlayer(name);
			}});

		playerTypes.add(new GamePlayerType("computer player (easy)") {
			public GamePlayer createPlayer(String name) {
				return new THComputerPlayerEasy(name);
			}});

		playerTypes.add(new GamePlayerType("computer player (hard)") {
			public GamePlayer createPlayer(String name) {
				return new THComputerPlayerHard(name);
			}});

		playerTypes.add(new GamePlayerType("computer player (shy)") {
			public GamePlayer createPlayer(String name) {
				return new THComputerPlayerShy(name);
			}});

		// Create a game configuration class for SlapJack
		GameConfig defaultConfig = new GameConfig(playerTypes, 2, 4, "Texas Holdem", PORT_NUMBER);

		// Add the default players
		defaultConfig.addPlayer("Human", 0);
		defaultConfig.addPlayer("Computer", 2);
		
		// Set the initial information for the remote player
		defaultConfig.setRemoteData("Guest", "", 1);

		//done!
		return defaultConfig;
	}

	@Override
	public LocalGame createLocalGame(GameState gameState) {
		//just a blank game for now, this'll be handled in the start() function in THLocalGame
		if (gameState == null) { gameState = new THState(); }
		RankHand handRanker = new RankHand(getApplicationContext());
		//have to pass in context so that RankHand can access the lookup tables in resources
		return new THLocalGame((THState) gameState, getApplicationContext(), handRanker);
	}

}
