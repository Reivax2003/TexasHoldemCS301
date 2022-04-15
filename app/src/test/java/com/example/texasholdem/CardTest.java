package com.example.texasholdem;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import java.util.ArrayList;

import edu.up.cs301.texasHoldem.Card;
import edu.up.cs301.texasHoldem.EvaluateHand;
import edu.up.cs301.texasHoldem.RankHand;

public class CardTest {

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
}