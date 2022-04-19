package edu.up.cs301.texasHoldem;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

import edu.up.cs301.game.GameFramework.animation.AnimationSurface;
import edu.up.cs301.game.GameFramework.animation.Animator;

/**
 * Animator to display cards on screen. Takes cards of a given size ratio and displays them evenly
 * in the center of whatever bounds the animator is given
 *
 * @author Xavier Santiago
 * @author Milton Nguy
 * @author Thomas Kone
 * @author Kevin Nguyen
 * @version 3.30.22
 */
public class CardAnimator implements Animator {
    private int backgroundColor;
    private Card[] cards;
    private float left;
    private float top;
    private float bottom;
    private float right;

    private float sizeRatio = 0.71f; // width/height, standard playing cards are ~0.7143
    private float cardWidth = 100;
    private float cardHeight = 100;
    private float cardLeft = 0;
    private float cardTop = 0;

    /**
     * constructor for cardAnimator
     * @param cards cards to render
     * @param bgColor color of the background
     * @param left left border of animation bounds
     * @param top top border of animation bounds
     * @param bottom bottom border of animation bounds
     * @param right right border of animation bounds
     */
    public CardAnimator(Card[] cards, int bgColor, float left, float top, float bottom, float right) {
        this.cards = cards;
        backgroundColor = bgColor;
        this.left = left;
        this.top = top;
        this.bottom = bottom;
        this.right = right;

        calcCardSize();
    }

    /**
     * constructor for cardAnimator
     * @param cards cards to render
     * @param bgColor color of the background
     * @param AS animation surface to get bounds from
     */
    public CardAnimator(Card[] cards, int bgColor, AnimationSurface AS) {
        this.cards = cards;
        backgroundColor = bgColor;
        this.left = AS.getLeft();
        this.top = AS.getTop();
        this.bottom = AS.getBottom();
        this.right = AS.getRight();

        calcCardSize();
    }

    /**
     * sets the list of cards we are rendering
     * @param cards cards to render
     */
    public void setCards(Card[] cards) {
        this.cards = cards;
        calcCardSize();
    }

    /**
     * Setter method for size ratio if cards width and height
     * @param ratio value to set ratio to
     */
    public void setSizeRatio(float ratio) {
        sizeRatio = ratio;
    }

    /**
     * given the size ratio for cards and the size of the region to display them on, calculate
     * how large the cards should be and where they should be (assuming they're displayed in one
     * row left to right)
     */
    public void calcCardSize() {
        float width = right-left;
        float height = bottom-top;

        float totalRatio = cards.length*sizeRatio;

        if (width/height > totalRatio) { //if area is wider than needed
            cardWidth = height*sizeRatio;
            cardHeight = height;
            cardTop = 0;
            cardLeft = (width-(cardWidth*cards.length))/2;
        } else { //if area is taller than necessary (or perfectly sized)
            cardHeight = (width/cards.length)/sizeRatio;
            cardWidth = width/cards.length;
            cardLeft = 0;
            cardTop = (height-cardHeight)/2;
        }
    }

    /**
     * @return
     * 		the amimation interval, in milliseconds
     */
    public int interval() {
        // 1/20 of a second
        return 50;
    }

    /**
     * @return
     * 		the background color
     */
    public int backgroundColor() {
        return backgroundColor;
    }

    /**
     * @return
     * 		whether the animation should be paused
     */
    public boolean doPause() {
        return false;
    }

    /**
     * @return
     * 		whether the animation should be terminated
     */
    public boolean doQuit() {
        return false;
    }

    /**
     * callback-method: we have gotten an animation "tick"; redraw the screen image:
     * - the middle deck, with the top card face-up, others face-down
     * - the two players' decks, with all cards face-down
     * - a red bar to indicate whose turn it is
     *
     * @param g
     * 		the canvas on which we are to draw
     */
    public void tick(Canvas g) {
        // ignore if we have no cards to render
        if (cards.length == 0) return;

        for (int i = 0; i < cards.length; i++) {
            Card card = cards[i];
            float left = cardLeft+cardWidth*i;
            float right = left+cardWidth;
            float bottom = cardTop+cardHeight;
            RectF rect = new RectF(left, cardTop, right, bottom);
            drawCard(g, rect, card);
        }
    }

    @Override
    public void onTouch(MotionEvent event) {
        //no need to do anything here since we don't move around cards in texas holdem
    }

    /**
     * Credit: Steven R. Vegdahl, July 2013
     *
     * draws a card on the canvas; if the card is null, draw a card-back
     *
     * @param g
     * 		the canvas object
     * @param rect
     * 		a rectangle defining the location to draw the card
     * @param c
     * 		the card to draw; if null, a card-back is drawn
     */
    private static void drawCard(Canvas g, RectF rect, Card c) {
        //leaving this in in case we ever want to render card backs later - Xavier
        if (c == null) {
            // null: draw a card-back, consisting of a blue card
            // with a white line near the border. We implement this
            // by drawing 3 concentric rectangles:
            // - blue, full-size
            // - white, slightly smaller
            // - blue, even slightly smaller
            Paint white = new Paint();
            white.setColor(Color.WHITE);
            Paint blue = new Paint();
            blue.setColor(Color.BLUE);
            RectF inner1 = scaledBy(rect, 0.96f); // scaled by 96%
            RectF inner2 = scaledBy(rect, 0.98f); // scaled by 98%
            g.drawRect(rect, blue); // outer rectangle: blue
            g.drawRect(inner2, white); // middle rectangle: white
            g.drawRect(inner1, blue); // inner rectangle: blue
        }
        else {
            // just draw the card
            c.drawOn(g, rect);
        }
    }
    /**
     * Credit: Steven R. Vegdahl, July 2013
     *
     * scales a rectangle, moving all edges with respect to its center
     *
     * @param rect
     * 		the original rectangle
     * @param factor
     * 		the scaling factor
     * @return
     * 		the scaled rectangle
     */
    private static RectF scaledBy(RectF rect, float factor) {
        // compute the edge locations of the original rectangle, but with
        // the middle of the rectangle moved to the origin
        float midX = (rect.left+rect.right)/2;
        float midY = (rect.top+rect.bottom)/2;
        float left = rect.left-midX;
        float right = rect.right-midX;
        float top = rect.top-midY;
        float bottom = rect.bottom-midY;

        // scale each side; move back so that center is in original location
        left = left*factor + midX;
        right = right*factor + midX;
        top = top*factor + midY;
        bottom = bottom*factor + midY;

        // create/return the new rectangle
        return new RectF(left, top, right, bottom);
    }
}
