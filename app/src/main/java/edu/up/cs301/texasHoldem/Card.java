package edu.up.cs301.texasHoldem;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Card class containing a value and suit for a card
 * has compressed card names, ex: 4H or AC
 * has full card names, ex: 4 of hearts or Ace of Spades
 *
 * @author Xavier Santiago
 * @version 2.22.2022
 */
public class Card {
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
    public int getSuit() {
        return value;
    }

    public char getSuitChar() {
        return suit;
    }

}
