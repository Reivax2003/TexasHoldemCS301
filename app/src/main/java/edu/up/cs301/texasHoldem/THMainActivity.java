package edu.up.cs301.texasHoldem;

import java.util.ArrayList;

import edu.up.cs301.game.GameFramework.GameMainActivity;
import edu.up.cs301.game.GameFramework.infoMessage.GameState;
import edu.up.cs301.game.GameFramework.LocalGame;
import edu.up.cs301.game.GameFramework.gameConfiguration.*;
import edu.up.cs301.game.GameFramework.players.GamePlayer;

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
