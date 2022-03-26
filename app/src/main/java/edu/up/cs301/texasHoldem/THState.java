package edu.up.cs301.texasHoldem;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import edu.up.cs301.game.GameFramework.infoMessage.GameState;
import edu.up.cs301.game.GameFramework.players.GameComputerPlayer;
import edu.up.cs301.game.GameFramework.players.GameHumanPlayer;
import edu.up.cs301.game.GameFramework.players.GamePlayer;

/**
 * Game State for our game, contains a list of players and other info
 * @author Xavier Santiago
 * @author Milton Nguy
 * @version 2.22.2022
 */
public class THState extends GameState {
    ArrayList<Player> players;
    ArrayList<Card> dealerHand = new ArrayList<Card>();
    int timer;
    int round; //pre-flop, post-flop 1, post-flop 2, and final round
    int MAX_TIMER;
    int playerTurn;
    int blindBet;
    int currentBet; //easier to keep track of this than iterate through players every time we need it

    //just an empty constructor, this'll never be used but it's useful for now
    public THState() {
        players = new ArrayList<Player>();
        blindBet = 20; //arbitrary value
        MAX_TIMER = 60; //arbitrary value
        timer = MAX_TIMER;
        playerTurn = 0;
        round = 0;
        currentBet = 0;
    }

    //constructor with just players
    public THState(ArrayList<Player> players) {
        this.players = players;
        blindBet = 20; //arbitrary value
        MAX_TIMER = 60; //arbitrary value
        timer = MAX_TIMER;
        playerTurn = 0;
        round = 0;
        currentBet = 0;
    }

    public THState(ArrayList<Player> players, int maxTimer, int blindBet) {
        this.players = (ArrayList<Player>) players.clone();
        this.blindBet = blindBet;
        MAX_TIMER = maxTimer; //TODO: change timer conditions in the future
        timer = MAX_TIMER;
        playerTurn = 0;
        round = 0;
        currentBet = 0;
    }

    //deep copy constructor, need to iterate through lists to make copies of all objects
    public THState(THState orig) {
        this.timer = orig.timer;
        this.round = orig.round;
        this.MAX_TIMER = orig.MAX_TIMER;
        this.playerTurn = orig.playerTurn;
        this.blindBet = orig.blindBet;
        this.currentBet = orig.currentBet;

        //the arraylists need deep copies so they don't contain references
        this.players = new ArrayList<Player>();
        for (Player each : orig.players) {
            players.add(new Player(each));
        }

        this.dealerHand = new ArrayList<Card>();
        for (Card each : orig.dealerHand) {
            dealerHand.add(new Card(each));
        }
    }

    // pass the player who is betting and the amount they want to bet
    // bet should be amount to add, not their total bet
    public boolean bet(int playerID, int amount) {
        Player currentPlayer = players.get(playerTurn);

        //separated fail conditions for better readability
        if (playerTurn != playerID) {
            return false;
        }
        //in theory this should never happen, since the minimum the game will let you bet is enough to stay in
        if (currentPlayer.getBet() + amount < currentBet) {
            return false;
        }
        //prevents player from accidentally passing their turn at the beginning
        if (amount == 0) {
            return false;
        }

        currentPlayer.addBet(amount);
        currentPlayer.removeBalance(amount);

        //amount was just added so we just compare with current bet
        if (currentPlayer.getBet() > currentBet) { //should always happen
            currentBet = currentPlayer.getBet();
        }

        nextTurn(); //taking any action ends your turn

        return true;
    }

    //only requirement to fold is it needs to be your turn
    public boolean fold(int playerID) {
        Player currentPlayer = players.get(playerTurn);

        //needs to be your turn
        if (playerTurn != playerID) {
            return false;
        }

        currentPlayer.setFold(true);
        nextTurn(); //taking any action ends your turn
        return true;
    }

    //just sum up all bets
    public int getPool() {
        int pool = 0;
        for (Player each : players) {
            pool += each.getBet();
        }
        return pool;
    }

    /**
     * Function to update turn to next player
     * Should be called in THLocalGame any time an action is successful
     */
    public void nextTurn() {
        playerTurn++;
        //if same as length reset to 0
        if (playerTurn >= players.size()) {
            playerTurn = 0;
        }
    }

    public int getTimer() {return timer;}
    public int getRound() {return round;}
    public int getMAX_TIMER() {return MAX_TIMER;}
    public int getPlayerTurn() {return playerTurn;}
    public int getBlindBet() {return blindBet;}
    public int getCurrentBet() {return currentBet;}
    public ArrayList<Card> getDealerHand() {return dealerHand;}
    public ArrayList<Player> getPlayers() {return players;}

    //set functions for use in unit tests
    public void setCurrentBet(int bet) {
        currentBet = bet;
    }
    public void setTimer(int time) {
        timer = time;
    }
    public void setDealerHand(ArrayList<Card> hand) {
        dealerHand = hand;
    }
    public void setRound(int round) {
        this.round = round;
    }

    @Override
    public String toString() {
        /*
        example output:

        Rules:
        - Timer Length: 30
        - Blind Bet: 100

        Current Game:
        - Round: Post-Flop 1
        - Pool: 500$
        - Turn: Jerry
        - Required bet: 200
        - Remaining time: 22

        Dealer's Hand:
        - Ace of Hearts
        - 3 of Spades
        - 10 of Hearts

        Players:
        - Name: Jerry, balance: 2600, bet: 100, folded: false, hand: King of Hearts, 9 of Diamonds
        - Name: Paul, balance: 800, bet: 150, folded: false, hand: 4 of clubs, 2 of Diamonds
        - Name: Anna, balance: 1200, bet: 0, folded: true, hand: 7 of Hearts, 9 of Clubs
        - Name: Mary, balance: 4400, bet: 200, folded: false, hand: Jack of Clubs, Ace of Diamonds
         */

        String message = "Rules:" +
                "\n- Timer Length: "+MAX_TIMER+
                "\n- Blind Bet: "+blindBet+
                "\n" +
                "\nCurrent Game:" +
                "\n- Round: ";
        message += Arrays.asList("Pre-Flop", "Post-Flop 1", "Post-Flop 2", "Final Round").get(round);

        message += "\n- Pool: "+getPool()+
                "\n- Turn: "+players.get(playerTurn).getName()+
                "\n- Required Bet: "+currentBet+
                "\n- Remaining Time: "+timer+
                "\n"+
                "\nDealer's Hand:\n";

        for (Card each : dealerHand) {
            message += "- "+each+"\n";
        }

        message += "\nPlayers:\n";

        for (Player each : players) {
            message += "- "+each+"\n";
        }

        return message;
    }
}
