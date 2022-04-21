package edu.up.cs301.texasHoldem;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * Class that stores a deck of cards and deals cards to players.
 *
 * @author Xavier Santiago
 * @author Milton Nguy
 * @author Thomas Kone
 * @author Kevin Nguyen
 * @version 3.15.22
 */
public class Deck implements Serializable {
    //use an arraylist so we can vary the size
    private ArrayList<Card> cards;

    //default constructor, creates all cards in a standard 52 card deck
    public Deck() {
        cards = new ArrayList<Card>(){};
        char[] suits = new char[]{'H','D','S','C'};
        for (int suit = 0; suit < 4; suit++) { //iterate through suits list
            for (int value = 2; value < 15; value++) { //2 being 2 and 14 being ace
                cards.add(new Card(value, suits[suit]));
            }
        }
    }

    //copy constructor
    public Deck(Deck orig) {
        cards = (ArrayList<Card>) orig.cards.clone();
    }

    /**
     * Removes one card from the deck and returns it
     * @return a random card that exists in the deck
     */
    public Card deal() {
        Random r = new Random();
        int index = r.nextInt(cards.size());
        Card card = cards.get(index); //specified value exclusive so use length of list
        cards.remove(index); //remove the card we drew
        //shouldn't matter if we make a copy or not so do it just to be safe
        return new Card(card);
    }
}
