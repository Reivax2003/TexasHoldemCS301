package edu.up.cs301.texasHoldem;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Scanner;

import edu.up.cs301.game.R;

/**
 * Class to evaluate a given set of cards for the best poker hand using binaries and hashtables.
 *
 * @author Xavier Santiago
 * @author Milton Nguy
 * @author Thomas Kone
 * @author Kevin Nguyen
 * @version 4.15.22
 */

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

            /**
             * We had a problem with line separators
             * https://stackoverflow.com/questions/9260126/what-are-the-differences-between-char-literals-n-and-r-in-java
             * Xavier Santiago 4.19.22
             */
            String[] lookup = result.split(System.lineSeparator()); //turn into list
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
            //repeat what we did for the flushes
            Scanner s = new Scanner(in).useDelimiter("\\A");
            String result = s.hasNext() ? s.next() : ""; //read all of file

            String[] lookup = result.split(System.lineSeparator()); //turn into list
            for (int i = 0; i < lookup.length; i++) {
                String[] temp = lookup[i].split(",");
                if(temp[1].contains("\r")){
                    temp[1] = temp[1].substring(0,temp[1].indexOf("\r"));
                }
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
            int bestRank = 9999;
            ArrayList<int[]> combos = getCombinationsOfSize(hand.length);
            for (int[] each : combos) {
                int[] subhand = new int[5];
                int index = 0;
                for (int i = 0; i < hand.length; i++) {
                    if (each[i] == 1) {
                        subhand[index] = hand[i].getCardBinary();
                        index++;
                    }
                }
                int rank = rank5(subhand);
                if (rank < bestRank) bestRank = rank;
            }
            return bestRank;
        }
    }

    /**
     * Return the % of cards this hand is better than
     *
     * @param hand the cards to be evaluated (must be at least 5)
     * @return rank of card as an int between 1 (royal flush) and 0 (unsuited 7-5-4-3-2)
     */
    public float getHandRankFloat(Card[] hand) {
        return 1-(getHandRank(hand)-1)/7461f;
    }

    private int rank5(int[] cards) {
        /**
         * Original python code for 5 cards
         *
         * # if flush
         * if cards[0] & cards[1] & cards[2] & cards[3] & cards[4] & 0xF000:
         *    handOR = (cards[0] | cards[1] | cards[2] | cards[3] | cards[4]) >> 16
         *    prime = Card.prime_product_from_rankbits(handOR)
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
    private ArrayList<int[]> getCombinationsOfSize(int size) {
        ArrayList<int[]> combinations = new ArrayList<>();
        for (int i = 0; i < Math.pow(2, size)-1; i++) {
            /**
             * how to convert an integer to a binary string
             * https://www.educative.io/edpresso/how-to-convert-an-integer-to-binary-in-java
             * Xavier Santiago 4.14.22
             */
            String bString = Integer.toBinaryString(i);
            String bString0s = bString.replace("0","");
            //if there are exactly 5 1s
            if (bString0s.length() == 5) {
                char[] chars = bString.toCharArray();
                int[] ints = new int[size];
                for (int j = 0; j < size; j++) {
                    //convert to integers
                    if (j < chars.length) ints[j] = chars[j] == '1' ? 1 : 0;
                    else ints[j] = 0;
                }
                combinations.add(ints);
            }
        }
        return combinations;
    }

    /**
     * returns the name of the given hand from its rank. for example passing in 1 would return
     * "royal flush" while 7462 would return "high card"
     *
     * @param rank the rank of hand to get the name of
     * @return the name of the type of hand the rank is
     */
    public String getRankText(int rank) {
        /**
         * Number of Distinct Hand Values:
         *
         *      Straight Flush   10
         *      Four of a Kind   156      [(13 choose 2) * (2 choose 1)]
         *      Full Houses      156      [(13 choose 2) * (2 choose 1)]
         *      Flush            1277     [(13 choose 5) - 10 straight flushes]
         *      Straight         10
         *      Three of a Kind  858      [(13 choose 3) * (3 choose 1)]
         *      Two Pair         858      [(13 choose 3) * (3 choose 2)]
         *      One Pair         2860     [(13 choose 4) * (4 choose 1)]
         *      High Card        1277     [(13 choose 5) - 10 straights]
         *      -------------------------
         *      TOTAL            7462
         *
         * credit: https://github.com/ihendley/treys
         */
        if (rank < 1) {
            return null; //just in case
        }
        if ( rank == 1) {
            return "Royal Flush";
        }
        if (rank <= 10) {
            return "Straight Flush";
        }
        if (rank <= 10+156) {
            return "Four of a Kind";
        }
        if (rank <= 10+156+156) {
            return "Full House";
        }
        if (rank <= 10+156+156+1277) {
            return "Flush";
        }
        if (rank <= 10+156+156+1277+10) {
            return "Straight";
        }
        if (rank <= 10+156+156+1277+10+858) {
            return "Three of a Kind";
        }
        if (rank <= 10+156+156+1277+10+858+858) {
            return "Two Pair";
        }
        if (rank <= 10+156+156+1277+10+858+858+2860) {
            return "Pair";
        }
        if (rank <= 10+156+156+1277+10+858+858+2860+1277) {
            return "Highest Card";
        }
        return null; //just in case
    }
}