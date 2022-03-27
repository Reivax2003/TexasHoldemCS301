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

		// Create a game configuration class for SlapJack
		GameConfig defaultConfig = new GameConfig(playerTypes, 2, 6, "Texas Holdem", PORT_NUMBER);

		// Add the default players
		defaultConfig.addPlayer("Human", 0);
		defaultConfig.addPlayer("Computer", 1);
		
		// Set the initial information for the remote player
		defaultConfig.setRemoteData("Guest", "", 1);
		
		//done!
		return defaultConfig;
	}//createDefaultConfig

	@Override
	public LocalGame createLocalGame(GameState gameState) {
		//if gamestate is null, i.e. we aren't loading a game, create a new one with the players
		//TODO: other game initialization settings
		//TODO: change player arraylist to GamePlayer arraylist
		GameConfig config = getConfig();
		if (gameState == null) { //if this is a new game
			ArrayList<Player> players = new ArrayList<Player>();
			//for each player create a Player object
			for (int i = 0; i < config.getNumPlayers(); i++) {
				Player player = new Player(config.getSelName(i), 1000);
				players.add(player);
			}
			//use this to create a fresh game
			gameState = new THState(players);
			((THState) gameState).dealPlayers(); //this is the only place this should happen
		}

		return new THLocalGame((THState) gameState);
	}

}
