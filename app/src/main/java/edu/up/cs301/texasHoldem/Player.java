package edu.up.cs301.texasHoldem;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.Serializable;
import java.util.Random;

/**
 * Player class containing all variables we need
 * @author Xavier Santiago
 * @author Milton Nguy
 * @author Thomas Kone
 * @author Kevin Nguyen
 * @version 3.29.22
 */
public class Player implements Serializable {
    private String name;
    private int balance;
    private int bet = 0;
    private Card[] hand = new Card[2];
    private boolean folded = false;
    private boolean allIn = false;
    private String action = "";
    private int handValue; //long story short, this is so that gamestate is serializable
    private boolean isCheems = false; //very important attribute

    // simple constructor
    public Player(String initName, int balance) {
        this.name = initName;
        this.balance = balance;

        Random r = new Random();
        if (r.nextFloat() < 0.01) { // 1/100 chance for profile picture to be cheems
            isCheems = true;
        }
    }

    // deep copy constructor
    public Player(Player orig) {
        name = orig.name;
        balance = orig.balance;
        bet = orig.bet;
        folded = orig.folded;
        allIn = orig.allIn;
        action = orig.action;
        isCheems = orig.isCheems;

        //the array is the only thing we really need to do a deep copy of
        hand = orig.hand.clone();
    }

    /**
     * removes the player's cards and hand value so that the gameState doesn't have any secret
     * info when sent to opponents
     */
    public void redactCards() {
        hand[0] = null;
        hand[1] = null;
        handValue = -1;
    }

    /**
     * increases player's bet. does NOT remove money
     * @param newBet amount to add to the bet
     */
    public void addBet(int newBet){
        bet += newBet;
        if(newBet == 0){
            action = "Check";
        }else {
            action = "Bet: $" + newBet;
        }
    }

    /**
     * Makes the player fold, sets the folded and action attributes
     */
    public void setFolded(){
        folded = true;
        action = "fold";
    }

    /**
     * getter and setter methods, not much to say here
     */
    public int getBalance() { return this.balance; }
    public void removeBalance(int betAmount) {
        this.balance -= betAmount;
    }
    public void setHandValue(int value) { handValue = value; }
    public int getHandValue() { return handValue; }
    public void setName(String name) { this.name = name; }
    public Card[] getHand() { return hand.clone(); }
    public void setHand(Card[] hand) { this.hand = hand; }
    public boolean isFolded() { return folded; }
    public String getName(){ return name; }
    public int getBet(){ return bet; }
    public boolean isCheems() {
        return isCheems;
    }

    /**
     * sets the player's allIn attribute to true. will only work if player has 0 balance
     */
    public void goAllIn() {
        if (balance > 0) {
            return;
        }
        allIn = true;
        action = "All In";
    }

    /**
     * @return whether or not the player has bet all their money
     */
    public boolean isAllIn() {
        return allIn;
    }

    /**
     * returns the last action the player took
     * @return string containing the last action the player took
     */
    public String getAction(){return action;}

    @Override
    public String toString() {
        return "Name: "+name+", balance: "+balance+", bet: "+bet+", folded: "+folded+", hand: "+hand[0]+", "+hand[1];
    }
}
