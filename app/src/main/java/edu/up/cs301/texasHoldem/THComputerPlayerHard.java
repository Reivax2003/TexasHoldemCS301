package edu.up.cs301.texasHoldem;

import android.util.Log;

import java.util.Arrays;
import java.util.Random;

import edu.up.cs301.game.GameFramework.infoMessage.GameInfo;
import edu.up.cs301.game.GameFramework.players.GameComputerPlayer;

/**
 * Simple computer player, folds 2% of the time, raises 25% of the time, and checks otherwise
 *
 * @author Xavier Santiago
 * @author Milton Nguy
 * @author Thomas Kone
 * @author Kevin Nguyen
 * @version 3.30.22
 */
public class THComputerPlayerHard extends GameComputerPlayer {

    private int turn = 0;
    private int round = -1;
    private Player self;
    private THState state;

    /**
     * some lists so we know what to do preflop
     * though the link technically isn't telling us to use this strategy, implementing the strategy
     * shown would be much longer. Although we are misusing their chart, it is still telling us
     * which hands are good and which are bad. I originally used this with a more conservative
     * chart and the AI did practically nothing so this feels better (old is not the shy AI)
     *
     * https://matchpoker.com/learn/strategy-guides/pre-flop-ranges-6-max
     */
    private String[][] preFlop = {
            // pairs of cards are searched both ways so we don't need both "AK" and "KA"

            // start with the suited hands
            // index 0, always raise
            {"AK", "AQ", "AJ", "AT", "A9", "A8", "A7", "A6", "A5", "A4", "A3", "A2", "KQ", "KJ",
                    "KT", "QJ", "QT", "JT", "T9", "65"},
            // index 1, raise once then call
            {"K9", "K8", "Q9", "J9", "98", "87", "76", "54"},
            // index 2, always call
            {"K7", "K6", "K5", "Q8", "J8", "T8", "97", "86", "75"},
            // index 3, call once then fold
            {"K4", "K3", "K2", "Q7", "Q6", "Q5", "J7", "T7", "T6", "96", "85", "64", "53", "43"},

            //next unsuited hands
            // index 4, always raise
            {"AK", "AQ", "KQ", "AJ", "AT"},
            // index 5, raise once then call
            {"KJ", "QJ"},
            // index 6, always call
            {"KT", "QT", "JT"},
            //index 7, call once then fold
            {"A9", "K9", "Q9", "J9", "T9", "A8", "98", "A7", "A6", "A5", "A4"},

            //and finally pairs
            // index 8, always raise
            {"AA", "KK", "QQ", "JJ", "TT", "99", "88", "77", "66", "55"},
            // index 9, raise once then call
            {"44"},
            // index 10, always call
            {"33", "22"},
            // index 11, call once then fold
            {}
    };

    /**
     * constructor
     * @param name the player's name (e.g., "John")
     */
    public THComputerPlayerHard(String name) {
        super(name);
    }

    /**
     * the AI uses multiple lookup tables to evaluate the strength of its hand and how it should act
     * actions are sorted into four groups: 1 - fold, 2 - check once then fold, 3 - always check
     * 4 - raise once then check, and 5 - always raise (there's a failsafe to stop a raise loop
     * between multiple AI after 3 round)
     * @param info the received info (we only care if it's a GameState)
     */
    @Override
    protected void receiveInfo(GameInfo info) {
        try {
            int sleepTime = (int) (Math.random()*1500)+500; //sleep somewhere between 0.5-2 secs
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (! (info instanceof THState)) {
            return;
        }
        state = new THState((THState) info); //deep copy of state (needed for online)
        if (state.getPlayerTurn() != playerNum) { //make sure it's our turn
            return;
        }
        self = state.getPlayers().get(playerNum); //the Player object associated with us

        if (state.getRound() != round) {
            turn = 0;
            round = state.getRound();
        } else {
            turn++;
        }

        int betNeeded = state.getCurrentBet()-self.getBet(); //amount needed to check
        int raiseAmount = (int) ((self.getBalance()-betNeeded)*0.06f);

        //randomize raise amount from 6% to 10% of remaining money
        Random r = new Random(); //to make things a bit more human-like
        raiseAmount += r.nextInt((int) (1 + (self.getBalance()-betNeeded)*0.04f));
        raiseAmount = (raiseAmount/5)*5; //intentional integer division (round to 5)


        /** PRE-FLOP EVALUATION */
        if (state.getRound() == 0) { //if we're in the pre-flop
            Card[] hand = self.getHand();
            String values = hand[0].getValueChar()+""+hand[1].getValueChar();
            String values2 = hand[1].getValueChar()+""+hand[0].getValueChar(); //check opposite too
            if(hand[0].getSuit() == hand[1].getSuit()) { //if we have a suited hand
                int action = -1;
                for (int i = 0; i < 4; i++) { //search suited values
                    if (Arrays.asList(preFlop[i]).contains(values)
                            || Arrays.asList(preFlop[i]).contains(values2)) {
                        action = i;
                        break;
                    }
                }
                switch (action) {
                    case -1: //if this wasn't on a list then it's a hand we should fold on
                        fold();
                        break;
                    case 0: //always raise
                        bet(betNeeded+raiseAmount);
                        break;
                    case 1: //raise once then call
                        if (turn == 0) {
                            bet(betNeeded+raiseAmount);
                        } else {
                            bet(betNeeded);
                        }
                        break;
                    case 2: //always call
                        bet(betNeeded);
                        break;
                    case 3: //call once then fold
                        if (turn == 0) {
                            bet(betNeeded);
                        } else {
                            fold();
                        }
                        break;
                }
            } else if (hand[0].getValue() == hand[1].getValue()) { //if we have a pair
                int action = -1;
                for (int i = 8; i < 12; i++) { //search pair values
                    if (Arrays.asList(preFlop[i]).contains(values)
                            || Arrays.asList(preFlop[i]).contains(values2)) {
                        action = i;
                        break;
                    }
                }
                switch (action) {
                    case -1: //if this wasn't on a list then it's a hand we should fold on
                        fold();
                        break;
                    case 8: //always raise
                        bet(betNeeded+raiseAmount);
                        break;
                    case 9: //raise once then call
                        if (turn == 0) {
                            bet(betNeeded+raiseAmount);
                        } else {
                            bet(betNeeded);
                        }
                        break;
                    case 10: //always call
                        bet(betNeeded);
                        break;
                    case 11: //call once then fold
                        if (turn == 0) {
                            bet(betNeeded);
                        } else {
                            fold();
                        }
                        break;
                }
            } else { //unsuited and no pair
                int action = -1;
                for (int i = 4; i < 8; i++) { //search suited values
                    if (Arrays.asList(preFlop[i]).contains(values)
                            || Arrays.asList(preFlop[i]).contains(values2)) {
                        action = i;
                        break;
                    }
                }
                switch (action) {
                    case -1: //if this wasn't on a list then it's a hand we should fold on
                        fold();
                        break;
                    case 4: //always raise
                        bet(betNeeded+raiseAmount);
                        break;
                    case 5: //raise once then call
                        if (turn == 0) {
                            bet(betNeeded+raiseAmount);
                        } else {
                            bet(betNeeded);
                        }
                        break;
                    case 6: //always call
                        bet(betNeeded);
                        break;
                    case 7: //call once then fold
                        if (turn == 0) {
                            bet(betNeeded);
                        } else {
                            fold();
                        }
                        break;
                }
            }
        }
        /** POST-FLOP EVALUATION */
        else {
            /**
             * Basic strategy post-flop
             * 1 - evaluate hand ranking among all hands
             * 2 - if above 50% call
             * 3 - if above 25% raise once
             * 4 - if above 10% always raise
             */
            Card[] dHand = state.getDealerHandAsArray();
            Card[] allCards = new Card[dHand.length+2];
            allCards[0] = self.getHand()[0];
            allCards[1] = self.getHand()[1];
            for (int i = 0; i < dHand.length; i++) {
                allCards[i+2] = dHand[i];
            }
            //get hand quality as a float between 0 and 1
            float quality = 1-(self.getHandValue()-1)/7461f;
            if (quality > 0.9f) {
                bet(betNeeded+raiseAmount);
            } else if (quality > 0.75f) {
                if (turn == 0) {
                    bet(betNeeded+raiseAmount);
                } else {
                    bet(betNeeded);
                }
            } else if (quality > 0.5f) {
                if (r.nextFloat() < .05f) { // 5% chance to bluff
                    if (r.nextFloat() < .5f) { // 20% (1% cumulatively) chance to wildly bluff
                        bet(betNeeded+raiseAmount);
                        return;
                    }
                    bet(betNeeded);
                    return;
                }
                bet(betNeeded);
            } else {
                if (r.nextFloat() < .05f) { // 5% chance to completely bluff
                    if (r.nextFloat() < .2f) { // 20% (1% cumulatively) chance to wildly bluff
                        bet(betNeeded+raiseAmount);
                        return;
                    }
                    bet(betNeeded);
                    return;
                }
                fold();
            }
        }
    }

    /**
     * abstracts the fold action to match the bet action
     */
    private void fold() {
        Fold action = new Fold(state.getPlayerID(self));
        game.sendAction(action);
    }

    /**
     * Bets the specified amount, bounding it between the minimum bet and the AIs remaining money.
     * Prioritizes remaining money over minimum bet, but we'll have to check the rules on that
     * if the AI has already raised 3 times, it will always check (failsafe to prevent going all in
     * on the first round)
     * @param amount the initial amount the AI wants to bet
     */
    private void bet(int amount) {
        int betNeeded = state.getCurrentBet()-self.getBet();
        if (amount == 0) { //only in the case of the start of a round (so we'll bet less
            int raiseAmount = (int) (self.getBalance()*0.03f);
            //randomize raise amount from 6% to 10% of remaining money
            Random r = new Random(); //to make things a bit more human-like
            raiseAmount += r.nextInt((int) (self.getBalance()*0.02f));
            raiseAmount = (raiseAmount/5)*5; //intentional integer division (round to 5)
            amount = raiseAmount;
        }

        if (turn >= 3) {
            amount = betNeeded;
        }
        amount = Math.max(amount, betNeeded); //just in case, this should never be needed
        // if the min bet is greater than our balance we still bet, I don't know if this is allowed
        // for now it'll stay in since going all in seems to be a unique case
        amount = Math.min(amount, self.getBalance());
        if (amount < 0) {
            amount *= -1;
        }
        Bet action = new Bet(state.getPlayerID(self), amount);
        game.sendAction(action);
    }
}
