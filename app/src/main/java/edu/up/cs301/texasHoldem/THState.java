package edu.up.cs301.texasHoldem;

import android.util.Log;

import java.io.Serializable;
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
public class THState extends GameState implements Serializable {
    private ArrayList<Player> players;
    private ArrayList<Card> dealerHand = new ArrayList<Card>();
    private Deck deck;
    private int timer;
    private int round; //pre-flop, post-flop 1, post-flop 2, and final round
    private int roundTurns;
    private int MAX_TIMER;
    private int playerTurn;
    private int blindBet;
    private int currentBet; //easier to keep track of this than iterate through players every time we need it
    private boolean[] startOfRoundCheck;

    //just an empty constructor, this'll never be used but it's useful for now
    public THState() {
        players = new ArrayList<Player>();
        startOfRoundCheck = new boolean[0];
        blindBet = 20; //arbitrary value
        MAX_TIMER = 15; //arbitrary value
        timer = MAX_TIMER;
        playerTurn = 0;
        round = 0;
        roundTurns = 0;
        currentBet = 0;
        deck = new Deck();
    }

    //constructor with just players
    public THState(ArrayList<Player> players) {
        this.players = players;
        blindBet = 20; //arbitrary value
        MAX_TIMER = 15; //arbitrary value
        timer = MAX_TIMER;
        playerTurn = 0;
        round = 0;
        currentBet = 0;
        deck = new Deck();

        startOfRoundCheck = new boolean[players.size()];
        resetCheckCounter();
    }

    public THState(ArrayList<Player> players, int maxTimer, int blindBet) {
        this.players = (ArrayList<Player>) players.clone();
        this.blindBet = blindBet;
        MAX_TIMER = maxTimer;
        timer = MAX_TIMER;
        playerTurn = 0;
        round = 0;
        roundTurns = 0;
        currentBet = 0;
        deck = new Deck();

        startOfRoundCheck = new boolean[players.size()];
        resetCheckCounter();
    }

    //deep copy constructor, need to iterate through lists to make copies of all objects
    public THState(THState orig) {
        this.timer = orig.timer;
        this.round = orig.round;
        this.roundTurns = orig.roundTurns;
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
            this.dealerHand.add(new Card(each));
        }

        this.deck = new Deck(orig.deck); //so we know which cards are already in play

        this.startOfRoundCheck = orig.startOfRoundCheck.clone();
    }

    public void resetCheckCounter() {
        for (int i = 0; i < players.size(); i++) {
            startOfRoundCheck[i] = false;
        }
    }

    /**
     * redacts the cards of all opponents. this should only ever be called on a copy of the state
     * @param playerNum the player who will receive this redacted version of the state
     */
    public void redactFor(int playerNum) {
        for (int i = 0; i < players.size(); i++) {
            if (i != playerNum) {
                players.get(i).redactCards();
            }
        }
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

    /**
     * forces the first two players to place a small and large bet starting with player 0
     */
    public void placeBlindBets() {
        bet(0, blindBet/2);
        bet(1, blindBet);
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
        /**
         *
         //prevents player from skipping their turn
        if (amount == 0 && roundTurns > 0) {
            return false;
        }
        if (amount == 0){
            nextTurn();
            roundTurns = 0;
            return true;
        }*/

        if (amount > currentPlayer.getBalance()) {
            amount = currentPlayer.getBalance(); //this shouldn't happen but it is for some reason
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

        startOfRoundCheck[playerID] = true;
        nextTurn(); //taking any action ends your turn

        Log.i("GameState", "player "+playerID+" bets "+amount);

        return true;
    }

    //only requirement to fold is it needs to be your turn
    public boolean fold(int playerID) {
        Player currentPlayer = players.get(playerTurn);

        //needs to be your turn
        if (playerTurn != playerID) {
            return false;
        }

        Log.i("GameState", "player "+playerID+" folds");

        startOfRoundCheck[playerID] = true;
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
        roundTurns++;
        if (playerTurn >= players.size()) {
            playerTurn = 0;
        }
        int backupTurn = playerTurn;
        Player player = players.get(playerTurn);

        while (player.isAllIn() || player.isFolded()) {
            playerTurn++;
            //if same as length reset to 0
            if (playerTurn >= players.size()) {
                playerTurn = 0;
            }
            player = players.get(playerTurn);
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
        //always starts with the first player. technically between games this should rotate
        playerTurn = players.size()-1;
        nextTurn(); //easier than rewriting all that code

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
            case 1:
                //other 2 rounds just deal 1

            case 2:
                dealerHand.add(deck.deal());
                break;
            //if round is 3 we don't need to do anything since the game's over
        }
        roundTurns = 0;
        round++;

        resetCheckCounter();
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).isFolded()) {
                startOfRoundCheck[i] = true;
            }
        }

        Log.i("round",""+round);
        Log.i("player turn", ""+playerTurn);
    }

    /**
     * calculate highest value set of 5 cards given a hand of arbitrary size (not implemented)
     * @param hand: the hand do be evaluated
     * @return highest card
     */
    public String bestHand(ArrayList<Card> hand) {

        String str = " ";

        EvaluateHand eh = new EvaluateHand(hand);

        if (eh.checkFlush() == true && eh.checkStraight() == true && eh.highHand().getValue() == 14) {
            str = "royal flush";
        } else if (eh.checkFlush() == true && eh.checkStraight() == true) {
            str = "straight flush";
        } else if (eh.checkXKinds() == 4) {
            str = "four of a kind";
        } else if (eh.checkFullHouse() == true) {
            str = "full house";
        } else if (eh.checkFlush() == true) {
            str = "flush";
        } else if (eh.checkStraight() == true) {
            str = "straight";
        } else if (eh.checkXKinds() == 3) {
            str = "three of a kind";
        } else if (eh.checkPair() == 2) {
            str = "two pairs";
        } else if (eh.checkPair() == 1) {
            str = "one pair";
        } else {
            str = "high hand";
        }
        return str;

    }

    public Card highHand(ArrayList<Card> hand) {


        int high = 0;
        Card winCard = null;
        for (Card card : hand) {
            if (card.getValue() > high) {
                high = card.getValue();
                winCard = new Card(card);
            }
        }
        return winCard;
    }

    /**
     * These are all getters and setters, I don't think there's any reason to explain them
     * Everything involving lists clones so we don't pass the master copy accidentally
     */
    public int getTimer() {return timer;}
    public int getRound() {return round;}
    public int getMAX_TIMER() {return MAX_TIMER;}
    public int getPlayerTurn() {return playerTurn;}
    public int getBlindBet() {return blindBet;}
    public int getCurrentBet() {return currentBet;}
    public void setCurrentBet(int bet) { currentBet = bet; } //for use in unit tests
    public void setTimer(int time) { timer = time; } //for use in unit tests
    public void setDealerHand(ArrayList<Card> hand) { dealerHand = (ArrayList<Card>) hand.clone(); }
    public void setRound(int round) { this.round = round; }
    public ArrayList<Player> getPlayers() { return (ArrayList<Player>) players.clone();}
    public ArrayList<Card> getDealerHand() {return (ArrayList<Card>) dealerHand.clone();}
    public Card[] getDealerHandAsArray() {
        return getDealerHand().toArray(new Card[getDealerHand().size()]).clone();
    }

    /**
     * returns the ID of the given player
     * @param player the player whose ID you want
     * @return the ID of that player
     */
    public int getPlayerID(Player player) {
        for (int i = 0; i < players.size(); i++) {
            if (player.getName() == players.get(i).getName()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * sets the hand value of the player object. this is used to set the win likelihood of
     * remote players
     * @param playerID the player whose value is to be set
     * @param value the value to set it to
     */
    public void setPlayerHandValue(int playerID, int value) {
        players.get(playerID).setHandValue(value);
    }

    /**
     * for use at the start of a round, if all the players check 0 the round is over
     * @return whether or not all players have checked this round
     */
    public boolean allChecked() {
        for (int i = 0; i < startOfRoundCheck.length; i++) {
            if (!startOfRoundCheck[i]) {
                return false;
            }
        }
        return true;
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
