package edu.up.cs301.texasHoldem;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import edu.up.cs301.game.GameFramework.infoMessage.GameState;

/**
 * Game State for our game, contains a list of players and other info
 *
 * @author Xavier Santiago
 * @author Milton Nguy
 * @author Thomas Kone
 * @author Kevin Nguyen
 * @version 2.22.2022
 */
public class THState extends GameState {
    private ArrayList<Player> players;
    private ArrayList<Card> dealerHand = new ArrayList<Card>();
    private Deck deck;
    private int timer;
    private int round; //pre-flop, post-flop 1, post-flop 2, and final round
    private int MAX_TIMER;
    int playerTurn;
    private int blindBet;
    private int currentBet; //easier to keep track of this than iterate through players every time we need it

    //just an empty constructor, this'll never be used but it's useful for now
    public THState() {
        players = new ArrayList<Player>();
        blindBet = 20; //arbitrary value
        MAX_TIMER = 60; //arbitrary value
        timer = MAX_TIMER;
        playerTurn = 0;
        round = 0;
        currentBet = 0;
        deck = new Deck();
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
        deck = new Deck();
    }

    public THState(ArrayList<Player> players, int maxTimer, int blindBet) {
        this.players = (ArrayList<Player>) players.clone();
        this.blindBet = blindBet;
        MAX_TIMER = maxTimer; //TODO: change timer conditions in the future
        timer = MAX_TIMER;
        playerTurn = 0;
        round = 0;
        currentBet = 0;
        deck = new Deck();
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

        this.deck = new Deck(orig.deck); //so we know which cards are already in play
    }

    /**
     * function to deal all players 2 new cards (overwrites old hand)
     * should only be called once (in createLocalGame in MainActivity)
     */
    public void dealPlayers() {
        for (Player player : players) {
            Card[] hand = new Card[]{deck.deal(), deck.deal()}; //deal 2 cards
            player.setHand(hand); //set their new hand
        }
    }

    // pass the player who is betting and the amount they want to bet
    // bet should be amount to add, not their total bet
    public boolean bet(int playerID, int amount) {
        //get a reference to the current player
        Player currentPlayer = players.get(playerTurn);

        //separated fail conditions for better readability
        if (playerTurn != playerID) {
            return false;
        }
        //in theory this should never happen, since the minimum the game will let you bet is enough to stay in
        if (currentPlayer.getBet() + amount < currentBet) {
            return false;
        }
        //prevents player from skipping their turn
        if (amount == 0) {
            return false;
        }

        currentPlayer.addBet(amount);
        currentPlayer.removeBalance(amount);

        //amount was just added so we just compare with current bet
        if (currentPlayer.getBet() > currentBet) { //should always happen
            currentBet = currentPlayer.getBet();
        }

        //if the player has no money left they are all in
        if (currentPlayer.getBalance() == 0) {
            currentPlayer.goAllIn();
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
        int backupTurn = playerTurn;
        if (playerTurn >= players.size()) {
            playerTurn = 0;
        }
        Player player = players.get(playerTurn);

        while (player.isAllIn() || player.isFolded()) {
            //if same as length reset to 0
            if (playerTurn >= players.size()) {
                playerTurn = 0;
            }
            player = players.get(playerTurn);
            playerTurn++;
            if (playerTurn == backupTurn) {
                break; //in case everyone goes all in, game should proceed to end immediately
            }
        }
    }

    /**
     * Function to change round number
     * called in local game while evaluating if the game is over
     */
    public void nextRound() {
        playerTurn = 0; //always starts with the first player. technically between games this should rotate
        Player player = players.get(playerTurn);
        while (player.isAllIn() || player.isFolded()) {
            player = players.get(playerTurn);
            playerTurn++;
            if (playerTurn == players.size()) { //if everyone is all in or folded
                playerTurn = 0;
                break;
            }
        }
        /**
         * Citation: looked up how switch/case works
         * https://www.w3schools.com/java/java_switch.asp
         * Xavier Santiago (3.26.2022)
         */
        switch (round) {
            case 0: //if pre-flop, then deal the flop
                dealerHand.add(deck.deal());
                dealerHand.add(deck.deal());
                dealerHand.add(deck.deal());
                break;
            case 1: //other 2 rounds just deal 1
            case 2:
                dealerHand.add(deck.deal());
                break;
            //if round is 3 we don't need to do anything since the game's over
        }
        round++;
        Log.i("round",""+round);
    }

    /**
     * calculate highest value set of 5 cards given a hand of arbitrary size (not implemented)
     * TODO: a unit test for this might be a good idea
     * @param hand: the hand do be evaluated
     * @return highest card
     */
    public Card bestHand(ArrayList<Card> hand) {

        int high = 0;
        Card winCard = null;
        for (Card card : hand) {
            if (card.getValue() > high) {
                high = card.getValue();
                winCard = new Card(card);
            }
        }
        return winCard;


        //if ()

        //may return number instead...

        /**
         * this is a work in progress, for now this function jut returns the highest card
         *
         //arraylist is just nicer to work with for this

        //for now, just check for each hand individually
        int[] suits = new int[4]; //keep track of how many of each suit (Flushes) [H, D, S, C]
        int[] values = new int[14]; //keep track of how many of each value
        Card flushStart = null; //if we have a flush, this hold the start of the highest one
        boolean isStraight = false; //true if we have a straight flush
        //if we have a straight flush, flushStart instead tracks the highest straight flush
        for (Card card : hand) {
            String shortCard = card.getShortName();

            //update lists
            switch (shortCard.substring(1,2)){
                case "H":
                    suits[0]++;
                    break;
                case "D":
                    suits[1]++;
                    break;
                case "S":
                    suits[2]++;
                    break;
                case "C":
                    suits[3]++;
                    break;
            }
            values[card.getValue()]++;

            //check for straight flush
            Card lastCard = new Card(card);
            boolean sFlush = true;
            for (int i = 0; i < 5; i++) {
                /**
                 * Citation: Checked to see if .contains works on objects
                 * https://stackoverflow.com/questions/2642589/how-does-a-arraylists-contains-method-evaluate-objects
                 * Xavier Santiago (3.26.2022)
                 */
                /**Card nextCard = new Card(lastCard.nextCard());
                if (hand.contains(nextCard)) {
                    lastCard = nextCard; //shouldn't matter that we pass the reference
                } else {
                    sFlush = false;
                    break;
                }
            }
            if (sFlush) {
                flushStart = new Card(card);
                isStraight = true;
            }
        }


        return new Card[5];*/
    }

    public int getTimer() {return timer;}
    public int getRound() {return round;}
    public int getMAX_TIMER() {return MAX_TIMER;}
    public int getPlayerTurn() {return playerTurn;}
    public int getBlindBet() {return blindBet;}
    public int getCurrentBet() {return currentBet;}
    //clone just to be safe
    public ArrayList<Card> getDealerHand() {return (ArrayList<Card>) dealerHand.clone();}
    public Card[] getDealerHandAsArray() {
        Card[] dealerHand = getDealerHand().toArray(new Card[getDealerHand().size()]);
        return dealerHand;
    }
    public ArrayList<Player> getPlayers() { return (ArrayList<Player>) players.clone();}

    //set functions for use in unit tests
    public void setCurrentBet(int bet) {
        currentBet = bet;
    }
    public void setTimer(int time) {
        timer = time;
    }
    //make sure we clone just to be safe
    public void setDealerHand(ArrayList<Card> hand) {
        dealerHand = (ArrayList<Card>) hand.clone();
    }
    public void setRound(int round) {
        this.round = round;
    }

    /**
     * @return number of players who aren't folded
     */
    public int getActivePlayers() {
        int count = 0;
        for (int i = 0; i < players.size(); i++) {
            if (!players.get(i).isFolded()) {
                count++;
            }
        }
        return count;
    }
    public ArrayList<Player> getActivePlayersList() {
        ArrayList<Player> active = new ArrayList<Player>();
        for (int i = 0; i < players.size(); i++) {
            if (!players.get(i).isFolded()) {
                active.add(new Player(players.get(i))); //make sure to create new player instances
            }
        }
        return active;
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
