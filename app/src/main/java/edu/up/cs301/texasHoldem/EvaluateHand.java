package edu.up.cs301.texasHoldem;

import java.util.Collections;

/**
 * Class to evaluate a given set of cards for the best poker hand
 *
 * @author Xavier Santiago
 * @author Milton Nguy
 * @author Thomas Kone
 * @author Kevin Nguyen
 * @version 3.30.22
 */
public class EvaluateHand {

    private Card[] cards;
    private boolean flush;

    public EvaluateHand(Card[] game) {
         cards = new Card[game.length];
         for (int i = 0; i < game.length; i++) {
             cards[i] = game[i];
         }

    }

    public boolean checkFlush()
    {
        boolean flush = true;
        for (int i = 0; i < 4; i++) {
            //ISSUE: getter is getting suit, but for some reason it is returning longName
            if (cards[i].getSuit() != cards[i+1].getSuit())
            {
                flush = false;
            }
        }
        return flush;


        //if matches, return a specific number and use if statement to decide
    }

    public boolean checkStraight() {
        boolean straight = false;
        for (int i = 0; i < 1; i++) {

            if (cards[i].getValue() == cards[i].getValue() && cards[i+1].getValue() == cards[i].getValue()+1 &&
                    cards[i+2].getValue() == cards[i].getValue()+2 && cards[i+3].getValue() == cards[i].getValue()+3 && cards[i+4].getValue() ==
                    cards[i].getValue()+4) {
                straight = true;
            }
        }
        return straight;
    }

    //^ if statement checkStraight() and checkFlush() at the same time for straight flush

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


    //3, 2, 5, 10, 3
    public void sortValue() {
        for (int x = 0; x < 5; x++) { //INTERATIONS MAY VARY, CHECK WITH DECK
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
       for (int x = 0; x < 5; x++) {
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
        Card[] temp = new Card[cards.length];
        int count = 0;
        for (int i = 0; i < cards.length; i++) {

            if (cards[i].getValue() == cards[i+1].getValue()) {
                temp[i] = cards[i];
                temp[i+1] = cards[i+1];
                cards[i] = null;
                cards[i+1] = null;
                count++;
            }
        }

        /**
         * scans through array and checks if there is multiple pairs. in the game, it's impossible to have "three pairs" as the system
         * checks which pair is greater than the other.
         */
        if (count == 1) {
            return 0;
        }
        else if (count == 2) {
            return 1;
        }
        else {
            return -1;
        }
        //TODO: Check which pair is greater
    }
    public void checkFullHouse(Card[] cards) {
        boolean fullHouse = true;
        //for (int i )
    }

    public String toString() {
        String str = " ";
        for (int i = 0; i < cards.length; i++) {
            System.out.println(cards[i].getLongName());
        }
        return str;
    }

}
