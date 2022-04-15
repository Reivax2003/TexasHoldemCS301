package com.example.texasholdem;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import edu.up.cs301.texasHoldem.Card;
import edu.up.cs301.texasHoldem.EvaluateHand;
import edu.up.cs301.texasHoldem.RankHand;

@RunWith(JUnit4.class)
public class CardTest {
    private Context context;

    /**
     * how to get context in tests
     * https://stackoverflow.com/questions/8605611/get-context-of-test-project-in-android-junit-test-case
     * Xavier Santiago 4.14.22
     */
    @Before
    public void initialize() {
        context = InstrumentationRegistry.getInstrumentation().getContext();
    }
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
    public void testPrimeProduct() {
        Card[] hand = {
                new Card("2H"),
                new Card("3H"),
                new Card("TS"),
                new Card("AC"),
                new Card("5D")
        };
        RankHand rh = new RankHand(context);
        assertEquals(967846, rh.prime_product_from_rankbits(0b0001101000010001));
    }
}
