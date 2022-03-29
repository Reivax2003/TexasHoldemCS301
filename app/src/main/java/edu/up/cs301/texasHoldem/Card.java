package edu.up.cs301.texasHoldem;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

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
    private static Bitmap[][] cardImages = null;

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
     * Credit: Steven R. Vegdahl, July 2013
     *
     * Draws the card on a Graphics object.  The card is drawn as a
     * white card with a black border.  If the card's rank is numerih, the
     * appropriate number of spots is drawn.  Otherwise the appropriate
     * picture (e.g., of a queen) is included in the card's drawing.
     *
     * @param g  the graphics object on which to draw
     * @param where  a rectangle that tells where the card should be drawn
     */
    public void drawOn(Canvas g, RectF where) {
        // create the paint object
        Paint p = new Paint();
        p.setColor(Color.BLACK);

        // get the bitmap for the card
        Bitmap bitmap = cardImages[this.getSuitAsInt()][this.getValue()-2];

        // create the source rectangle
        Rect r = new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());

        // draw the bitmap into the target rectangle
        g.drawBitmap(bitmap, r, where, p);
    }

    // array that contains the android resource indices for the 52 card
    // images
    private static int[][] resIdx = {
        {
            R.drawable.card_2c, R.drawable.card_3c,
            R.drawable.card_4c, R.drawable.card_5c, R.drawable.card_6c,
            R.drawable.card_7c, R.drawable.card_8c, R.drawable.card_9c,
            R.drawable.card_tc, R.drawable.card_jc, R.drawable.card_qc,
            R.drawable.card_kc, R.drawable.card_ac,
        },
        {
            R.drawable.card_2d, R.drawable.card_3d,
            R.drawable.card_4d, R.drawable.card_5d, R.drawable.card_6d,
            R.drawable.card_7d, R.drawable.card_8d, R.drawable.card_9d,
            R.drawable.card_td, R.drawable.card_jd, R.drawable.card_qd,
            R.drawable.card_kd, R.drawable.card_ad,
        },
        {
            R.drawable.card_2h, R.drawable.card_3h,
            R.drawable.card_4h, R.drawable.card_5h, R.drawable.card_6h,
            R.drawable.card_7h, R.drawable.card_8h, R.drawable.card_9h,
            R.drawable.card_th, R.drawable.card_jh, R.drawable.card_qh,
            R.drawable.card_kh, R.drawable.card_ah,
        },
        {
            R.drawable.card_2s, R.drawable.card_3s,
            R.drawable.card_4s, R.drawable.card_5s, R.drawable.card_6s,
            R.drawable.card_7s, R.drawable.card_8s, R.drawable.card_9s,
            R.drawable.card_ts, R.drawable.card_js, R.drawable.card_qs,
            R.drawable.card_ks, R.drawable.card_as,
        },
    };

    /**
     * Credit: Steven R. Vegdahl, July 2013
     *
     * initializes the card images
     *
     * @param activity
     * 		the current activity
     */
    public static void initImages(Activity activity) {
        // if it's already initialized, then ignore
        if (cardImages != null) return;

        // create the outer array
        cardImages = new Bitmap[resIdx.length][];

        // loop through the resource-index array, creating a
        // "parallel" array with the images themselves
        for (int i = 0; i < resIdx.length; i++) {
            // create an inner array
            cardImages[i] = new Bitmap[resIdx[i].length];
            for (int j = 0; j < resIdx[i].length; j++) {
                // create the bitmap from the corresponding image
                // resource, and set the corresponding array element
                cardImages[i][j] =
                        BitmapFactory.decodeResource(
                                activity.getResources(),
                                resIdx[i][j]);
            }
        }
    }
}
