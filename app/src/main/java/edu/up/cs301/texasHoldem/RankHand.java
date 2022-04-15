package edu.up.cs301.texasHoldem;

import android.content.Context;
import android.content.res.Resources;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Scanner;

import edu.up.cs301.game.R;

public class RankHand implements Serializable {

    private Context context; //allows us to read from resources
    private Dictionary<Integer, Integer> flushes;
    private Dictionary<Integer, Integer> unsuited;

    public RankHand(Context context) {
        this.context = context;
        initialize();
    }

    private void initialize() {
        flushes = new Hashtable<>();
        unsuited = new Hashtable<>();

        Resources res = context.getResources();
        InputStream in = res.openRawResource(R.raw.flushes);
        try {
            /**
             * Checked how to read all of an input stream
             * https://stackoverflow.com/questions/309424/how-do-i-read-convert-an-inputstream-into-a-string-in-java
             * Xavier Santiago 4.14.22
             */
            Scanner s = new Scanner(in).useDelimiter("\\A");
            String result = s.hasNext() ? s.next() : ""; //read all of file

            String[] lookup = result.split("\n"); //turn into list
            for (int i = 0; i < lookup.length; i++) {
                String[] temp = lookup[i].split(",");
                flushes.put(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]));
            }
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        in = res.openRawResource(R.raw.unsuited);
        try {
            /**
             * Checked how to read all of an input stream
             * https://stackoverflow.com/questions/309424/how-do-i-read-convert-an-inputstream-into-a-string-in-java
             * Xavier Santiago 4.14.22
             */
            Scanner s = new Scanner(in).useDelimiter("\\A");
            String result = s.hasNext() ? s.next() : ""; //read all of file

            String[] lookup = result.split("\n"); //turn into list
            for (int i = 0; i < lookup.length; i++) {
                String[] temp = lookup[i].split(",");
                unsuited.put(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]));
            }
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Return the rank of best hand out of the given cards of the 7462 distinct
     * returns -1 if it receives less than 5 cards
     * poker hands using lookup tables gotten from:
     *
     * https://github.com/ihendley/treys
     *
     * @param hand the cards to be evaluated (must be at least 5)
     * @return rank of card as an int between 1 (royal flush) and 7462 (unsuited 7-5-4-3-2)
     */
    public int getHandRank(Card[] hand) {
        if (hand.length < 5) {
            return -1;
        } else if (hand.length == 5) {
            int[] cards = new int[hand.length];
            for (int i = 0; i < hand.length; i++) {
                cards[i] = hand[i].getCardBinary();
            }
            return rank5(cards);
        } else {
            return 0;
        }
    }

    private int rank5(int[] cards) {
        /**
         * Original python code for 5 cards
         *
         * # if flush
         * if cards[0] & cards[1] & cards[2] & cards[3] & cards[4] & 0xF000:
         *    handOR = (cards[0] | cards[1] | cards[2] | cards[3] | cards[4]) >> 16
         *    prime = Card.prime_product_from_rankbits(handOR) <- TODO
         *    return self.table.flush_lookup[prime]
         *
         * # otherwise
         * else:
         *    prime = Card.prime_product_from_hand(cards)
         *    return self.table.unsuited_lookup[prime]
         */


        // check if the suit bits are all the same
        // 61440 is so we only check the suit bits
        // & is bitwise and operator so all must have same suit
        if ((cards[0] & cards[1] & cards[2] & cards[3] & cards[4] & 61440) != 0){
            //bitshift right 16 places to just get bit ranks
            //this gets the bit ranks of all cards, i.e. tracks which values we have in our hand
            int handOR = (cards[0] | cards[1] | cards[2] | cards[3] | cards[4]) >> 16;
            int prime = prime_product_from_rankbits(handOR);
            return flushes.get(prime);
        } else {
            int prime = prime_product_from_hand(cards);
            return unsuited.get(prime);
        }
    }

    /**
     * returns a unique identifier for a given set of card ranks
     * card ranks are given in 16 bits, the first 3 of which are unused
     * ex: 0001101000010001 would be A,K,J,6,2
     * duplicates are discounted
     *
     * technically it would be possible to make a lookup table without this but since I'm
     * using someone else's table this is needed
     *
     * what we're essentially doing is separating each rank of flush including royal flushes
     *
     * Original code: https://github.com/ihendley/treys
     *
     * @param rankbits card ranks in format described above
     * @return unique integer representing rankbits (for use in lookup table)
     */
    public int prime_product_from_rankbits(int rankbits) {
        //list of prime numbers
        int[] primes = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41};
        int product = 1;
        //iterate through the range of card bits
        for (int i = 0; i < 13; i++) {
            //if the ith bit is set
            //take rankbits and compare to 1 shifted right i times, aka check if each bit is 1
            if ((rankbits & (1 << i)) != 0) {
                product *= primes[i];
            }
        }
        return product;
    }

    /**
     * create a unique identifier for a given set of cards, ignoring suit
     * unlike prime_product_from_rankbits, this cares about duplicates
     * @param hand the hand of cards in integer form
     * @return unique integer to represent this hand (for use in lookup table)
     */
    public int prime_product_from_hand(int[] hand) {
        int product = 1;
        //loop through all the cards
        for (int each : hand) {
            //multiply the product by the prime representation of each card (the first 8 bits)
            product *= (each & 0xFF);
        }
        return product;
    }

    /**
     * Gets all combinations of size 5 out of a list of objects
     * @param size size of list
     * @return list of lists of 1s and 0s, 1s meaning include, 0s meaning exclude
     */
    private int[][] getCombinationsofSize(int size) {
        ArrayList<Integer[]> combinations = new ArrayList<>();
        for (int i = 0; i < size; i++) {

        }
        return new int[0][0];
    }
}