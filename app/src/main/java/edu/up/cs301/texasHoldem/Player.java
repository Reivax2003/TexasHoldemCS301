package edu.up.cs301.texasHoldem;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.Serializable;

/**
 * Player class containing all variables we need
 * @author Xavier Santiago
 * @version 3.29.22
 */
public class Player implements Serializable {
    private String name;
    private int balance;
    private int bet = 0;
    private Bitmap picture;
    private Card[] hand = new Card[2];
    private boolean folded = false;
    private boolean allIn = false;
    private String action = "";
    //private UiPlayerProfile profile;

    // simple constructor
    public Player(String initName, int balance) {//, Bitmap picture) {
        this.name = initName;
        this.balance = balance;
        this.picture = picture;
        //profile = new UiPlayerProfile(initName);
    }

    // deep copy constructor
    public Player(Player orig) {
        name = orig.name;
        balance = orig.balance;
        picture = orig.picture;
        bet = orig.bet;
        folded = orig.folded;
        allIn = orig.allIn;
        //profile = orig.profile;

        //the array is the only thing we really need to do a deep copy of
        hand = orig.hand.clone();
    }

    public void redactCards() {
        hand[0] = null;
        hand[1] = null;
    }
    public String getName(){ return name;}

    public int getBet(){ return bet;}
    public void addBet(int newBet){
        bet += newBet;
        action = "Bet: $" + newBet;
        //profile.setActionText("Bet $" + newBet);
    }

    public void setFold(boolean foldStatus){
        folded = foldStatus;
        action = "fold";
        //profile.setActionText("Fold");
    }
    public boolean isFolded() {return folded; }

    public Card[] getHand() {return hand.clone();}
    public void setHand(Card[] hand) {
        this.hand = hand;
        //profile.setCardImg(hand);
    }
    public void giveCard(Card card, int index) { hand[index] = card;}

    public int getBalance() { return this.balance; }
    //changed name to "removebalance" for clarity
    public void removeBalance(int betAmount) {
        this.balance -= betAmount;
    }
    public void setBalance(int amount) {balance = amount;}

    public void goAllIn() {
        if (balance > 0) {
            return;
        }
        allIn = true;
        action = "All In";
        //profile.setActionText("All In");
    }
    public boolean isAllIn() {
        return allIn;
    }

    public String getAction(){return action;}
/*
    public UiPlayerProfile getProfile(){
        return profile;
    }
*/
    @Override
    public String toString() {
        return "Name: "+name+", balance: "+balance+", bet: "+bet+", folded: "+folded+", hand: "+hand[0]+", "+hand[1];
    }
}
