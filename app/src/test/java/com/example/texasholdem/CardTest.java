package com.example.texasholdem;

import org.junit.Test;

import static org.junit.Assert.*;

import edu.up.cs301.texasHoldem.Card;

public class CardTest {

    /**
     * There were supposed to be many tests here about evaluating cards
     * Unfortunately, due to the fact that rankHand requires a context to read from a file, there
     * aren't very many tests I can do here
     */
    @Test
    public void testBinary() {
        Card card = new Card("2S");
        assertEquals(69634, card.getCardBinary());
        card = new Card("TH");
        assertEquals(16787479, card.getCardBinary());
        card = new Card("5C");
        assertEquals(557831, card.getCardBinary());
        card = new Card("AD");
        assertEquals(268454953, card.getCardBinary());
        card = new Card("9H");
        assertEquals(8398611, card.getCardBinary());
    }

    @Test
    public void testCardVars() {
        Card card = new Card("5H");
        assertEquals("5 of Hearts", card.getLongName());
        assertEquals(5, card.getValue());
        assertEquals('H', card.getSuit());
        assertEquals("6H", card.nextCard().getShortName());
        Card aceCard = new Card("AS");
        assertEquals("2S", aceCard.nextCard().getShortName());

    }
}