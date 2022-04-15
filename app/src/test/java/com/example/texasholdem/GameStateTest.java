package com.example.texasholdem;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import edu.up.cs301.texasHoldem.Card;
import edu.up.cs301.texasHoldem.EvaluateHand;
import edu.up.cs301.texasHoldem.Player;
import edu.up.cs301.texasHoldem.THState;

public class GameStateTest {
    //TODO: test that current bet is correct

    @Test
    public void testFold() {
        ArrayList<Player> players = new ArrayList<Player>();

        //set up players
        Player p1 = new Player("Joe", 1000);
        players.add(p1);
        Player p2 = new Player("Mary", 2000);
        players.add(p2);
        Player p3 = new Player("Bill", 3000);
        players.add(p3);

        THState gState = new THState(players, 60, 100);

        //player 2 shouldn't be able to fold while it's not their turn
        assertFalse(gState.fold(1)); //gamestate should not take the action
        assertFalse(p2.isFolded()); //player's state should n ot be updated

        //it is player 1's turn by default so they should be able to fold
        assertTrue(gState.fold(0)); //gamestate thinks action was successful
        assertTrue(p1.isFolded()); //check that action was successful
    }

    @Test
    public void testGetPool() {
        ArrayList<Player> players = new ArrayList<Player>();

        //set up players
        Player p1 = new Player("Joe", 1000);
        players.add(p1);
        Player p2 = new Player("Mary", 2000);
        players.add(p2);
        Player p3 = new Player("Bill", 3000);
        players.add(p3);

        THState gState = new THState(players, 60, 100);

        assertEquals(0, gState.getPool()); //should start at 0 since no bets

        p1.addBet(100);
        p2.addBet(300);
        p3.addBet(500);

        assertEquals(900, gState.getPool()); //should return 900, sum of bets

        gState.fold(0);
        assertEquals(900, gState.getPool()); //should still return 900 even though someone folded
    }

    @Test
    public void toStringTest() {
        ArrayList<Player> players = new ArrayList<Player>();

        //set up players
        Player p1 = new Player("Joe", 1000);
        players.add(p1);
        Player p2 = new Player("Mary", 2000);
        players.add(p2);
        Player p3 = new Player("Bill", 3000);
        players.add(p3);

        THState gState = new THState(players, 60, 100);

        p1.setHand(new Card[]{new Card("AH"), new Card("2S")});
        p2.setHand(new Card[]{new Card("KC"), new Card("TS")});
        p3.setHand(new Card[]{new Card("6S"), new Card("8D")});

        p2.setFold(true);

        p1.addBet(100);
        p3.addBet(200);

        gState.setCurrentBet(200);
        gState.setTimer(20);

        ArrayList<Card> dealerHand = new ArrayList<Card>();
        dealerHand.add(new Card("4D"));
        dealerHand.add(new Card("3C"));
        dealerHand.add(new Card("AH"));
        gState.setDealerHand(dealerHand);

        gState.setRound(1);

        String message =
                "Rules:\n" +
                        "- Timer Length: 60\n" +
                        "- Blind Bet: 100\n" +
                        "\n" +
                        "Current Game:\n" +
                        "- Round: Post-Flop 1\n" +
                        "- Pool: 300\n" +
                        "- Turn: Joe\n" +
                        "- Required Bet: 200\n" +
                        "- Remaining Time: 20\n" +
                        "\n" +
                        "Dealer's Hand:\n" +
                        "- 4 of Diamonds\n" +
                        "- 3 of Clubs\n" +
                        "- Ace of Hearts\n" +
                        "\n" +
                        "Players:\n" +
                        "- Name: Joe, balance: 1000, bet: 100, folded: false, hand: Ace of Hearts, 2 of Spades\n" +
                        "- Name: Mary, balance: 2000, bet: 0, folded: true, hand: King of Clubs, 10 of Spades\n" +
                        "- Name: Bill, balance: 3000, bet: 200, folded: false, hand: 6 of Spades, 8 of Diamonds\n";
        //the last \n is supposed to be there, it makes the code easier

        assertEquals(message, gState.toString());
    }

    @Test
    public void BetBalanceTest() {
        //this will test the functionality of the bet action when it is called by
        //players, and it will also check whether bet gets removed from user's balance.
        //In this example we will be ignoring card hands and just create a bet action.

        ArrayList<Player> players = new ArrayList<Player>();

        //set up players
        Player p1 = new Player("Joe", 1000);
        players.add(p1);
        Player p2 = new Player("Mary", 2000);
        players.add(p2);
        Player p3 = new Player("Bill", 3000);
        players.add(p3);

        THState gState = new THState(players, 60, 0);

        //make players take action
        assertTrue(gState.bet(0, 100));
        assertTrue(gState.bet(1, 400)); //this one has to be bigger

        //should return 600 given that the first user made a bet of 400.
        assertEquals(900, p1.getBalance());

        //should return 1900 given that the first user made a bet of 100.
        assertEquals(1600, p2.getBalance());

    }

    @Test
    public void evaluationTest() {
        Card[] cards = new Card[7];
        /*
        cards[0] = (new Card("2H"));
        cards[1] = (new Card("3H"));
        cards[2] = (new Card("4H"));
        cards[3] = (new Card("5H"));
        cards[4] = (new Card("6H"));

         */

        /*
        cards[0] = (new Card(10, 'h'));
        cards[1] = (new Card(4, 'c'));
        cards[2] = (new Card(4, 'h'));
        cards[3] = (new Card(3, 'c'));
        cards[4] = (new Card(2, 'd'));
        cards[5] = (new Card(3, 'd'));
        cards[6] = (new Card("TS"));


         */




        cards[0] = (new Card("AH"));
        cards[1] = (new Card("TH"));
        cards[2] = (new Card("JH"));
        cards[3] = (new Card("QH"));
        cards[4] = (new Card("KH"));
        cards[5] = (new Card(3, 'h'));
        cards[6] = (new Card(4, 's'));



        //EvaluateHand test = new EvaluateHand(cards);


        //assertEquals(true, test.checkFlush());

/*
        for (int i = 0; i < cards.length; i++) {
            System.out.println(cards[i].toString());
        }

 */

        //boolean flush = true;
        /*
        for (int i = 0; i < 4; i++) {
            //ISSUE: getter is getting suit, but for some reason it is returning longName
            if (cards[i].getSuit() != cards[i+1].getSuit());
            {
                flush = false;
            }
        }
        */

        /*
        System.out.println(cards[0].getSuit());

        for (int i = 0; i < 4; i++) {
            if (cards[i].getSuit() != cards[i+1].getSuit())
            {
                flush = false;
            }
        }
        //tests whether the card array is true/false
        System.out.println(flush);
         */
        /*
        boolean straight = false;
        for (int i = 0; i < 1; i++) {

            if (cards[i].getValue() == cards[i].getValue() && cards[i+1].getValue() == cards[i].getValue()+1 &&
                    cards[i+2].getValue() == cards[i].getValue()+2 && cards[i+3].getValue() == cards[i].getValue()+3 && cards[i+4].getValue() ==
                    cards[i].getValue()+4) {
                straight = true;
            }
        }
        System.out.println(straight);


         */
        /**
         * Straight works, flush works, sorting values work. Sorting suit works.
         * */

        /*
        for (int x = 0; x < 5; x++) {
            for (int i = 0; i < cards.length-1; i++) {
                Card[] temp = new Card[cards.length];
                if (cards[i].getValue() > cards[i+1].getValue()) {
                    temp[i] = cards[i];
                    cards[i] = cards[i + 1];
                    cards[i + 1] = temp[i];
                }
            }
        }

        */
        /*
        for (int x = 0; x < 5; x++) {
            for (int i = 0; i < cards.length-1; i++) {
                Card[] temp = new Card[cards.length];
                if (cards[i].getSuitAsInt() > cards[i+1].getSuitAsInt()) {
                    temp[i] = cards[i];
                    cards[i] = cards[i+1];
                    cards[i+1] = temp[i];

                }
            }
        }


         */

        EvaluateHand test = new EvaluateHand(cards);

        //System.out.println(cards[cards.length-1].getValue());
       // System.out.println(test.checkPair());
       // System.out.println(test.checkXKinds());
       // System.out.println(test.checkFullHouse());
       // System.out.println(test.highHand());
       // System.out.println(test.checkFullHouse());

        System.out.println(test.checkStraight());
        System.out.println(test.highHand().getValue());






        /*
        for (int i = 0; i < cards.length; i++) {
            System.out.println(cards[i].toString());
        }
        */

    }

    @Test
    public void dealerDecktest() {
        ArrayList<Player> players = new ArrayList<Player>();

        Player p1 = new Player("Joe", 1000);
        players.add(p1);
        Player p2 = new Player("Mary", 2000);
        players.add(p2);
        Player p3 = new Player("Bill", 3000);
        players.add(p3);

        THState gState = new THState(players, 60, 100);
        ArrayList<Card> hand = gState.getDealerHand();

        for (int i = 0; i < hand.size(); i++) {
            System.out.println(hand.get(i).toString());
        }
    }
}
