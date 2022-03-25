package edu.up.cs301.texasHoldem;

import android.graphics.Color;

import java.util.ArrayList;

import edu.up.cs301.game.GameFramework.GameMainActivity;
import edu.up.cs301.game.GameFramework.infoMessage.GameState;
import edu.up.cs301.game.GameFramework.LocalGame;
import edu.up.cs301.game.GameFramework.gameConfiguration.*;
import edu.up.cs301.game.GameFramework.players.GamePlayer;

/**
 * this is the primary activity for Slapjack game
 * 
 * @author Steven R. Vegdahl
 * @version July 2013
 */
public class THMainActivity extends GameMainActivity {
	
	public static final int PORT_NUMBER = 4752;

	/** a slapjack game for two players. The default is human vs. computer */
	@Override

	public GameConfig createDefaultConfig() {

		//all this is useable code so i'm commenting it out instead of deleting

		// Define the allowed player types
		ArrayList<GamePlayerType> playerTypes = new ArrayList<GamePlayerType>();

		playerTypes.add(new GamePlayerType("human player") {
			public GamePlayer createPlayer(String name) {
				return new THHumanPlayer(name);
			}});

		playerTypes.add(new GamePlayerType("human player 2") {
			public GamePlayer createPlayer(String name) {
				return new THHumanPlayer(name);
			}});
		/**playerTypes.add(new GamePlayerType("human player (yellow)") {
			public GamePlayer createPlayer(String name) {
				return new SJHumanPlayer(name, Color.YELLOW);
			}
		});
		playerTypes.add(new GamePlayerType("computer player (normal)") {
			public GamePlayer createPlayer(String name) {
				return new SJComputerPlayer(name);
			}
		});
		playerTypes.add(new GamePlayerType("computer player (fast)") {
			public GamePlayer createPlayer(String name) {
				return new SJComputerPlayer(name, 0.3);
			}
		});
		playerTypes.add(new GamePlayerType("computer player (slow)") {
			public GamePlayer createPlayer(String name) {
				return new SJComputerPlayer(name, 1.0);
			}
		});
		playerTypes.add(new GamePlayerType("computer player (very fast)") {
			public GamePlayer createPlayer(String name) {
				return new SJComputerPlayer(name, 0.15);
			}
		});
		playerTypes.add(new GamePlayerType("computer player (very slow)") {
			public GamePlayer createPlayer(String name) {
				return new SJComputerPlayer(name, 3.5);
			}
		});
		 */

		// Create a game configuration class for SlapJack
		GameConfig defaultConfig = new GameConfig(playerTypes, 2, 6, "SlapJack", PORT_NUMBER);

		// Add the default players
		defaultConfig.addPlayer("Human", 0);
		defaultConfig.addPlayer("Human2", 1);
		
		// Set the initial information for the remote player
		defaultConfig.setRemoteData("Guest", "", 1);
		
		//done!
		return defaultConfig;
	}//createDefaultConfig

	@Override
	public LocalGame createLocalGame(GameState gameState) {
		if(gameState == null) {
			gameState = new THState();
		}

		return new THLocalGame((THState) gameState);
	}

}
