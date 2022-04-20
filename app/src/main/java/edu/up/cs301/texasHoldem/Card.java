package edu.up.cs301.texasHoldem;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import edu.up.cs301.game.R;

/**
 * Card class containing a value and suit for a card
 * has compressed card names, ex: 4H or AC
 * has full card names, ex: 4 of hearts or Ace of Spades
 *
 * Credit to Steven R. Vegdahl for his Card class in SlapJack
 *
 * @author Xavier Santiago
 * @version 2.22.2022
 */
public class Card implements Serializable {
    private char suit;
    private int value;
    private String shortName;
    private String longName;
    //this array lets us quickly convert between integer value and string value
    //assume aces are high (doesn't really matter)
    private ArrayList<String> values = new ArrayList<String>
            (Arrays.asList(null,null,"2","3","4","5","6","7","8","9","T","J","Q","K","A"));

    // ex: Card(4, 'S') for 4 of spades
    public Card(int value, char suit) {
        this.suit = Character.toUpperCase(suit); //standardize suit as upper case
        this.value = value;
        this.shortName = values.get(value)+suit;
        longName = getLongName();
    }

    // ex: Card("4S") for 4 of spades
    public Card(String shortName) {
        this.shortName = shortName;
        /**
         * Citation (Xavier, 2.22.2022)
         * Had to check whether substring was inclusive or exclusive on bounds
         * https://www.javatpoint.com/java-string-substring
         */
        value = values.indexOf(shortName.substring(0,1));
        this.suit = Character.toUpperCase(shortName.toCharArray()[1]);
        longName = getLongName();
    }

    // copy constructor, no variables require deep copies since we just have primitives and strings
    public Card(Card orig) {
        suit = orig.suit;
        value = orig.value;
        shortName = orig.shortName;
        longName = orig.longName;
    }

    // already stored as longName
    @Override
    public String toString() {
        return longName;
    }

    // this needs to be run when the card is created and any time something is changed
    public String getLongName() {
        String name = "";

        if (value <= 10) { //if it's a number card we can just use it's value as a string
            name += value;
        } else { //otherwise, we can check which face card it is based on value
            name += Arrays.asList("Jack", "Queen", "King", "Ace").get(value-11);
        }

        name += " of ";

        //check for suit name
        if (suit == 'H') {name += "Hearts";}
        else if (suit == 'D') {name += "Diamonds";}
        else if (suit == 'S') {name += "Spades";}
        else if (suit == 'C') {name += "Clubs";}
        else {name += "Invalid Suit";}

        return name;
    }
    public String getShortName() {
        return shortName;
    }

    /**
     * gets the short name of next card in the same suit, useful for calculating a best hand
     * @return card of same suit with 1 greater rank. returns 2 if ace
     */
    public String nextCard() {
        //value%14 will return value for everything but ace, where it returns 1
        String card = new Card((value%14)+1, suit).getShortName();
        return card;
    }

    /**
     * @return value of card as an int, aces high
     */
    public int getValue() {
        return value;
    }

    /**
     * @return value of the card as a single character
     */
    public String getValueChar() {
        return values.get(value);
    }

    /**
     * @return Suit of card as a char: H, D, S, or C
     */
    public char getSuit() {
        return suit;
    }

    /**
     * Gets the suit of a card as a character, suits arranged alphabetically
     * 0 = clubs
     * 1 = diamonds
     * 2 = hearts
     * 3 = spades
     * returns -1 if suit is not C, D, H, or S
     * @return integer representing the suit of this card
     */
    public int getSuitAsInt() {
        if (suit == 'C') {
            return 0;
        } else if (suit == 'D') {
            return 1;
        } else if (suit == 'H') {
            return 2;
        } else if (suit == 'S') {
            return 3;
        } else {
            return -1;
        }
    }


    /**
     * Compares two cards to see if they are the same
     * @param other: the card to compare
     * @return boolean, whether or not they are equivalent
     */
    public boolean equals(Card other) {
        if (other.getValue() == value && other.getSuit() == suit) {
            return true;
        }
        return false;
    }

    /**
     * Gets the representation of a card in binary form
     * Originally from:
     *
     * Static class that handles cards. We represent cards as 32-bit integers, so
     * there is no object instantiation - they are just ints. Most of the bits are
     * used, and have a specific meaning. See below:
     *
     *                                 Card:
     *
     *                       bitrank     suit rank   prime
     *                 +--------+--------+--------+--------+
     *                 |xxxbbbbb|bbbbbbbb|cdhsrrrr|xxpppppp|
     *                 +--------+--------+--------+--------+
     *
     *     1) p = prime number of rank (deuce=2,trey=3,four=5,...,ace=41)
     *     2) r = rank of card (deuce=0,trey=1,four=2,five=3,...,ace=12)
     *     3) cdhs = suit of card (bit turned on based on suit of card)
     *     4) b = bit turned on depending on rank of card
     *     5) x = unused
     *
     * This representation will allow us to do very important things like:
     * - Make a unique prime prodcut for each hand
     * - Detect flushes
     * - Detect straights
     *
     * and is also quite performant.
     *
     * @return integer form of binary described above
     */
    public int getCardBinary() {
        String STR_RANKS = "23456789TJQKA";
        int[] INT_RANKS = {0,1,2,3,4,5,6,7,8,9,10,11,12};
        int[] PRIMES = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41};

        String[] CHAR_RANK_TO_INT_RANK = {"2","3","4","5","6","7","8","9","T","J","Q","K","A"};
        /**
         * how to initialize an arraylist with variables
         * https://stackoverflow.com/questions/16194921/initializing-arraylist-with-some-predefined-values
         * Xavier Santiago 4.12.22
         */
        ArrayList<String> CHAR_SUIT_TO_INT_SUIT = new ArrayList<>(
                Arrays.asList(null, "S", "H", null, "D", null, null, null, "C"));

        String INT_SUIT_TO_CHAR_SUIT = "xshxdxxxc";

        int rank_int = value-2;
        int suit_int = CHAR_SUIT_TO_INT_SUIT.indexOf(String.valueOf(suit));
        int rank_prime = PRIMES[rank_int];

        int bitrank = 1 << rank_int << 16;
        int bitsuit = suit_int << 12;
        int rank = rank_int << 8;

        int result = bitrank | bitsuit | rank | rank_prime;

        //Log.i("card", ""+result);
        return result;
    }
}
