package edu.up.cs301.texasHoldem;

import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;

import edu.up.cs301.game.GameFramework.GameMainActivity;
import edu.up.cs301.game.GameFramework.infoMessage.GameState;
import edu.up.cs301.game.GameFramework.LocalGame;
import edu.up.cs301.game.GameFramework.gameConfiguration.*;
import edu.up.cs301.game.GameFramework.players.GamePlayer;

/**
 * Project #i Report
 *
 * In regards to overall completeness, our project is nowhere close to what is required for the beta
 * release. As I am writing this, it's 12:18 in the morning and I have been working since around
 * 1:30. I will continue to work on this tomorrow, however since the deadline was technically about
 * 20 minutes ago I am writing this to commit. I will put a footnote listing the changes I gave made
 * after submitting this.
 *
 * Beta Release Checklist:
 *
 * Rules of play: Our project is actually pretty complete in this regard. there are a few obscure
 * rules that remain unimplemented and a few bugs (see https://github.com/Reivax2003/TexasHoldemCS301/issues/8)
 * the main problem is that we currently only evaluate the highest card in a player's hand. this is
 * a leftover from the alpha release which has not been overwritten. We have the code to implement
 * the correct scoring, however it remains unimplemented. I plan on implementing it tomorrow morning.
 * (Note after submission: I am unsure whether the brute force implementation actually works, I am
 * using the evaluation made for the AI instead)
 *
 * GUI functionality: I'm not sure if extras count for this. For pure functionality We have
 * everything required for the game itself. However, UI elements like the timer and hand quality
 * meter remain unimplemented (these are extras, not required for poker).
 *
 * AIs: In this regard our project is complete, and it is also what I've spent the past 10 hours
 * working on. The Hard AI is nearly bug-free (see https://github.com/Reivax2003/TexasHoldemCS301/issues/9)
 * However I have been wholly unable to implement unit tests for that Ai in any way. This actually
 * isn't due to a lack of time, it's because I use a lookup table stored in resources which requires
 * a "context" to access, which I have been unable to get in the test. We also have a Shy AI, which
 * is a leftover from an older version of the pre-flop AI, which plays far more conservatively
 * (and probably better) but is less fun to play with. The shy AI is completely extra, but is
 * currently unimplemented (another thing to do tomorrow)
 *
 * Network Play: Currently untested. I was going to implement this, however something is wrong with
 * my tablet (it won't start and won't charge). We have serializable implemented most places that
 * it would be needed, however as we haven't tested it, I won't claim that it is everywhere it needs
 * to be. A known problem is that the player class uses a bitmap profile picture. I believe Nuxoll
 * mentioned in class that Bitmaps are non-serializable, so we will have to find a workaround or
 * alternate solution (it's unlikely we will even be able to use the profile picture in any useful
 * manner, so we will probably delete it or just use the auto-generated bitmaps so we don't have to
 * transmit them).
 *
 * Graphics Elements: Somewhat complete. Elements like the timer are unfinished because the code
 * behind them is unfinished. However we have the data needed for the hand quality because of how
 * the smart AI is implemented. The player profiles have been worked on by my team, but are not
 * entirely functional. However, The functional UI like the buttons and cards are complete and in
 * their final form. We have even made the bet button change to "check" when that is the appropriate
 * term for the action.
 *
 * Players: I have been unable to test this as of writing. Our team member working on player
 * profiles has said up to 4 should work, and prior to the profiles being implemented an unlimited
 * number of players would work. The alpha release had the max set to 6, however this was just a
 * placeholder since poker doesn't have a defined player limit.
 *
 * GUI effectiveness: Of the GUI which is complete, I think this is fulfilled. Nothing has really
 * changed from the alpha release, but I believe this is because it didn't need to. As I mentioned
 * before we have changed the text on the bet button when appropriate, and we have also changed the
 * minimum on the betting slider to 1 rather than 0 as you aren't allowed to bet 0 (there is still
 * a bug with this however: https://github.com/Reivax2003/TexasHoldemCS301/issues/7). All of our
 * UI should be relatively intuitive for actually playing the game. One thing that I would like to
 * add, which is not required, is a way to see whose turn it is, since tracking turns it a bit
 * difficult at the moment, just a forewarning.
 *
 * Bugs: We have a few. check the github issues page to see them. I have tried my best to document
 * new ones effectively and to clear old ones as they are fixed. The majority of bugs have known
 * solutions, but we have not had the time to implement them.
 *
 * Final Notes:
 *
 * We're drastically behind where we would like to be in the requirements for this project. As it is
 * suggested in the assignment, I will go over potential fixes for bugs, however I'll keep it short
 * since this comment is already too long.
 *
 * (numbers from gitHub)
 *
 * #3: Swapping Portrait and landscape is buggy. not exactly a problem with our code. Unable to fix
 * at this time
 *
 * #4: Deciding game winner. We have the code we need, we jut need to implement it. We have a brute
 * force method which didn't get finished in time and then the method that the AI uses to evaluate
 * its hand, which would also work.
 *
 * #6: First run on tablet is buggy. like #3, there's not much we can really do about this one, and
 * I'm not even sure it's something we need to be concerned with
 *
 * #7: Betting 1 doesn't work. I'll look into the code tomorrow, however this is probably a bug with
 * what value the player is sending vs what the screen says it is
 *
 * #8: folding at start of round. We need to track in the gameState if any bets have been placed
 * this round. I don't actually know the rule for this off the top of my head (whether it's a forced
 * bet or not) so what we do from there depends on the official rules.
 *
 * #9: Multiple Hard AI just bet forever. Not exactly a bug, just an unintended behavior. The AI
 * already has a counter for how many times it's bet in the current round, we jut need to tell it to
 * start checking instead of raising after a certain amount. (Fixed after submission)
 *
 * As for discussing how these bugs and problems with our code could have been prevented, it all
 * just comes down to time. We let the deadline sneak up on us and didn't have the time to finish.
 * Next time we'll have to make sure that we know exactly when everything is due and have a plan for
 * when each part needs to be finished to meet that deadline.
 *
 * Anyways, thank you for reading my way too long comment explaining the sorry state of our project.
 * I hope it isn't too bad.
 *
 * Changelog after submission
 * - Shy AI is now selectable
 * - Hard AI no longer bets infinitely
 * - Fixed a crash with Hard AI
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
	}//createDefaultConfig

	@Override
	public LocalGame createLocalGame(GameState gameState) {
		//just a blank game for now, this'll be handled in the start() function in THLocalGame
		if (gameState == null) { gameState = new THState(); }
		//have to pass in context so that RankHand can access the lookup tables in resources
		return new THLocalGame((THState) gameState, getApplicationContext());
	}

}
