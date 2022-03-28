package edu.up.cs301.texasHoldem;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

public class Deck {
    //use an arraylist so we can vary the size
    private ArrayList<Card> cards;

    public Deck() {
        cards = new ArrayList<Card>(){};
        char[] suits = new char[]{'H','D','S','C'};
        for (int suit = 0; suit < 4; suit++) { //iterate through suits list
            for (int value = 2; value < 15; value++) { //2 being 2 and 14 being ace
                cards.add(new Card(value, suits[suit]));
            }
        }
    }
    public Deck(Deck orig) {
        cards = (ArrayList<Card>) orig.cards.clone();
    }
    public Card deal() {
        Random r = new Random();
        int index = r.nextInt(cards.size());
        Card card = cards.get(index); //specified value exclusive so use length of list
        cards.remove(index); //remove the card we drew
        //shouldn't matter if we make a copy or not so do it just to be safe
        return new Card(card);
    }

    public int getDeck() {
        return cards.size();
    }
}
