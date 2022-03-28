package edu.up.cs301.texasHoldem;

public class EvaluateHand {

    private Card[] cards;
    private boolean flush;

    public EvaluateHand(Card[] game) {
         //ignore for now
        cards = new Card[game.length];
        for (int i = 0; i < game.length; i++) {
            cards[i] = game[i];
        }
    }

    public boolean checkFlush(){
        flush = true;
        for (int i = 0; i < 4; i++) {
            if (cards[i].getValue() != cards[i+1].getValue());
            {
                flush = false;
            }
        }


        return flush;


        //if matches, return a specific number and use if statement to decide
    }

    public boolean checkStraight() {
        boolean straight = false;
        for (int i = 2; i < 9; i++) {

            if (cards[i].getValue() == cards[i].getValue() && cards[i+1].getValue() == cards[i].getValue()+1 &&
            cards[i+2].getValue() == cards[i].getValue()+2 && cards[i+3].getValue() == cards[i].getValue()+3 && cards[i+4].getValue() ==
            cards[i].getValue()+4) {
                straight = true;
            }
        }
        return straight;
    }

    /*public boolean checkStraightFlush() {
        boolean straightFlush = false;
        for (int i = 2; i < 9; i++) {

            if (cards[i].getLongName() == cards[i].getValue() && cards[i+1].getValue() == cards[i].getValue()+1 &&
                    cards[i+2].getValue() == cards[i].getValue()+2 && cards[i+3].getValue() == cards[i].getValue()+3 && cards[i+4].getValue() ==
                    cards[i].getValue()+4) {
                straightFlush = true;
            }
        }
        return straightFlush;


    }

    */


}
