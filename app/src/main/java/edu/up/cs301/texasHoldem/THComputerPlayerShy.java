package edu.up.cs301.texasHoldem;

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
public class THComputerPlayerShy extends GameComputerPlayer {

    private RankHand handRanker;
    private boolean fisrtTurn = true;
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
            {"AK"},
            // index 1, raise once then call
            {"AQ", "AJ", "KQ"},
            // index 2, always call
            {"AT", "JK", "JQ"},
            // index 3, call once then fold
            {"9A", "KT", "QT", "JT"},

            //next unsuited hands
            // index 4, always raise
            {},
            // index 5, raise once then call
            {},
            // index 6, always call
            {"AK", "AQ"},
            //index 7, call once then fold
            {"KQ"},

            //and finally pairs
            // index 8, always raise
            {"AA", "KK", "QQ"},
            // index 9, raise once then call
            {"JJ"},
            // index 10, always call
            {"TT", "99"},
            // index 11, call once then fold
            {"88", "77"}
    };

    /**
     * constructor
     * @param name the player's name (e.g., "John")
     */
    public THComputerPlayerShy(String name) {
        super(name);
    }

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

        if (handRanker == null && state.getHandRanker() != null) {
            handRanker = state.getHandRanker();
        } else if (state.getHandRanker() == null) {
            assert(false);
            fold();
            return;
        }

        if (state.getRound() != round) {
            fisrtTurn = true;
            round = state.getRound();
        } else {
            fisrtTurn = false;
        }

        int betNeeded = state.getCurrentBet()-self.getBet(); //amount needed to check
        int raiseAmount = (int) ((self.getBalance()-betNeeded)*0.06f);

        //randomize raise amount from 6% to 10% of remaining money
        Random r = new Random(); //to make things a bit more human-like
        raiseAmount += r.nextInt((int) ((self.getBalance()-betNeeded)*0.04f));
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
                        if (fisrtTurn) {
                            bet(betNeeded+raiseAmount);
                        } else {
                            bet(betNeeded);
                        }
                        break;
                    case 2: //always call
                        bet(betNeeded);
                        break;
                    case 3: //call once then fold
                        if (fisrtTurn) {
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
                        if (fisrtTurn) {
                            bet(betNeeded+raiseAmount);
                        } else {
                            bet(betNeeded);
                        }
                        break;
                    case 10: //always call
                        bet(betNeeded);
                        break;
                    case 11: //call once then fold
                        if (fisrtTurn) {
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
                        if (fisrtTurn) {
                            bet(betNeeded+raiseAmount);
                        } else {
                            bet(betNeeded);
                        }
                        break;
                    case 6: //always call
                        bet(betNeeded);
                        break;
                    case 7: //call once then fold
                        if (fisrtTurn) {
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
            float quality = handRanker.getHandRankFloat(allCards);
            if (quality > 0.9f) {
                bet(betNeeded+raiseAmount);
            } else if (quality > 0.75f) {
                if (fisrtTurn) {
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

    private void fold() {
        Fold action = new Fold(this);
        game.sendAction(action);
    }
    private void bet(int amount) {
        if (amount == 0) { //only in the case of the start of a round (so we'll bet less
            int raiseAmount = (int) (self.getBalance()*0.03f);
            //randomize raise amount from 6% to 10% of remaining money
            Random r = new Random(); //to make things a bit more human-like
            raiseAmount += r.nextInt((int) (self.getBalance()*0.02f));
            raiseAmount = (raiseAmount/5)*5; //intentional integer division (round to 5)
        }
        amount = Math.max(amount, state.getMinBet());
        // if the min bet is greater than our balance we still bet, I don't know if this is allowed
        // for now it'll stay in since going all in seems to be a unique case
        amount = Math.min(amount, self.getBalance());
        Bet action = new Bet(this, amount);
        game.sendAction(action);
    }
}
