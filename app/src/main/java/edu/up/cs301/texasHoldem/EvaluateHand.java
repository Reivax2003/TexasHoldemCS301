package edu.up.cs301.texasHoldem;

import android.content.Context;
import android.content.res.Resources;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

import edu.up.cs301.game.R;

/**
 * Helper Class to evaluate a given set of cards for the best poker hand. This is outdated.
 * Please refer to RankHand.java
 *
 * @author Xavier Santiago
 * @author Milton Nguy
 * @author Thomas Kone
 * @author Kevin Nguyen
 * @version 3.30.22
 */
public class EvaluateHand {

    /**
     * I'm not gonna put comments here since I don't know what everything is for, Milton will need
     * to get it up to the code standard
     */
    private Card[] cards;
    private ArrayList<Card> cardsAL;

    public EvaluateHand(Card[] game) {
         cards = new Card[game.length];
         for (int i = 0; i < game.length; i++) {
             cards[i] = game[i];
         }
    }

    public EvaluateHand(ArrayList<Card> game) {
        cards = new Card[game.size()];
        for (int i = 0; i < game.size(); i++) {
            cards[i] = game.get(i);
        }
    }

    public boolean checkFlush()
    {
        this.sortSuit();
        boolean flush = true;
        for (int i = 0; i < cards.length-1; i++) {
            if (cards[i].getSuit() != cards[i+1].getSuit())
            {
                flush = false;
            }
        }
        return flush;


        //if matches, return a specific number and use if statement to decide
    }

    public boolean checkStraight() {
        this.sortValue();
        boolean straight = false;
        for (int i = 0; i < cards.length-4; i++) { //MAY VARY BY HAND COUNT

            if (cards[i].getValue() == cards[i].getValue() && cards[i+1].getValue() == cards[i].getValue()+1 &&
                    cards[i+2].getValue() == cards[i].getValue()+2 && cards[i+3].getValue() == cards[i].getValue()+3 && cards[i+4].getValue() ==
                    cards[i].getValue()+4) {
                straight = true;
            }
        }
        return straight;
    }

    //^ if statement checkStraight() and checkFlush() at the same time for straight flush

    /*
    public boolean checkStraightFlush() {
        boolean straightFlush = false;
        for (int i = 0; i < 1; i++) {

            if (cards[i].getValue() == cards[i].getValue() && cards[i+1].getValue() == cards[i].getValue()+1 &&
                    cards[i+2].getValue() == cards[i].getValue()+2 && cards[i+3].getValue() == cards[i].getValue()+3 && cards[i+4].getValue() ==
                    cards[i].getValue()+4) {
                straightFlush = true;
            }
        }
        return straightFlush;


    }
     */


    //3, 2, 5, 10, 3
    public void sortValue() {
        for (int x = 0; x < 7; x++) { //ITERATIONS MAY VARY, CHECK WITH DECK
            for (int i = 0; i < cards.length-1; i++) {
                Card[] temp = new Card[cards.length];
                if (cards[i].getValue() > cards[i+1].getValue()) {
                    temp[i] = cards[i];
                    cards[i] = cards[i + 1];
                    cards[i + 1] = temp[i];
                }
            }
        }
    }

    //TODO: organize array from least to greatest possible errors: dealers hand is split amongst players, therefore shuffling may differ

    // H, H, S, C, H
    // 2, 2, 3, 0, 2
    public void sortSuit() {
       for (int x = 0; x < 7; x++) {
           for (int i = 0; i < cards.length-1; i++) {
               Card[] temp = new Card[cards.length];
               if (cards[i].getSuitAsInt() > cards[i+1].getSuitAsInt()) {
                   temp[i] = cards[i];
                   cards[i] = cards[i+1];
                   cards[i+1] = temp[i];

               }
            }
        }
    }

    //2, 2, 5, 5, 9
    public int checkPair() {
        ArrayList<Card> temp = new ArrayList<>();
        Card[] copy = new Card[cards.length];
        for(int i = 0; i < cards.length; ++i) {
            copy[i] = cards[i];
        }
        int count = 0;
        this.sortValue();
        for (int i = 0; i < copy.length-1; i++) {

            if (cards[i].getValue() == cards[i+1].getValue()) {
                temp.add(copy[i]);
                copy[i] = null;
            }
        }

       /* if (temp.size() > 5) {
            temp.remove(0);
        }
        */

        for (int i = 0; i < temp.size()-1; i++) {
            if (temp.get(i).getValue() == temp.get(i+1).getValue()) {
                temp.remove(i);
            }
        }

        if (temp.size() > 2) {
            temp.remove(0);
        }
        for (Card cards : temp) {
            System.out.println(cards);
            count++;
        }


        /**
         * scans through array and checks if there is multiple pairs. in the game, it's impossible to have "three pairs" as the system
         * checks which pair is greater than the other.
         */
        if (count == 1) {
            return 1;
        }
        else if (count == 2) {
            return 2;
        }
        else {
            return 0;
        }

    }
    public boolean checkFullHouse() {
        boolean fullHouse = false;
        if (this.checkXKinds() == 3 && this.checkPair() >= 0) {
            fullHouse = true;
        }
        return fullHouse;
    }

    public int checkXKinds() {
        boolean FourOfAKind = false;
        boolean ThreeOfAKind = false;
        this.sortValue();
        for (int i = 0; i < cards.length-3; i++) {
            if (cards[i].getValue() == cards[i+1].getValue() && cards[i].getValue() == cards[i+2].getValue() &&
                    cards[i].getValue() == cards[i+3].getValue()) {
                FourOfAKind = true;
            }
        }

        for (int i = 0; i < cards.length-2; i++) {
            if (cards[i].getValue() == cards[i+1].getValue() && cards[i].getValue() == cards[i+2].getValue()) {
                ThreeOfAKind = true;
            }
        }

        if (FourOfAKind == true) {
            return 4;
        }

        if (ThreeOfAKind == true) {
            return 3;
        }

        else {
            return 0;
        }
    }

    public Card highHand() {
        this.sortValue();
        Card winCard = cards[cards.length-1];
        return winCard;
    }

    public int highHandInt() {
        return highHand().getValue();
    }

    public String toString() {
        String str = " ";
        for (int i = 0; i < cards.length; i++) {
            System.out.println(cards[i].getLongName());
        }
        return str;
    }
}
