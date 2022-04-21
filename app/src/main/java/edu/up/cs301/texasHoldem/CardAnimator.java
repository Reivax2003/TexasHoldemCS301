package edu.up.cs301.texasHoldem;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

import edu.up.cs301.game.GameFramework.animation.AnimationSurface;
import edu.up.cs301.game.GameFramework.animation.Animator;
import edu.up.cs301.game.R;

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

    private static Bitmap[][] cardImages = null;

    /**
     * constructor for cardAnimator
     * @param cards cards to render
     * @param bgColor color of the background
     * @param left left border of animation bounds
     * @param top top border of animation bounds
     * @param bottom bottom border of animation bounds
     * @param right right border of animation bounds
     */
    public CardAnimator(Card[] cards, int bgColor, float left, float top, float bottom, float right,
                        Activity activity) {
        this.cards = cards;
        backgroundColor = bgColor;
        this.left = left;
        this.top = top;
        this.bottom = bottom;
        this.right = right;

        initImages(activity);

        calcCardSize();
    }

    /**
     * constructor for cardAnimator
     * @param cards cards to render
     * @param bgColor color of the background
     * @param AS animation surface to get bounds from
     */
    public CardAnimator(Card[] cards, int bgColor, AnimationSurface AS, Activity activity) {
        this.cards = cards;
        backgroundColor = bgColor;
        this.left = AS.getLeft();
        this.top = AS.getTop();
        this.bottom = AS.getBottom();
        this.right = AS.getRight();

        initImages(activity);

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
     * @return the amimation interval, in milliseconds
     */
    public int interval() {
        // 1/20 of a second
        return 50;
    }

    /**
     * @return the background color
     */
    public int backgroundColor() {
        return backgroundColor;
    }

    /**
     * @return whether the animation should be paused
     */
    public boolean doPause() {
        return false;
    }

    /**
     * @return whether the animation should be terminated
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

    /**
     * Overrides onTouch, this is required by implementing Animator but we don't use it
     * @param event a MotionEvent describing the touch
     */
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
            int suitInt = c.getSuitAsInt();
            int lookupValue = c.getValue()-2;
            // create the paint object
            Paint p = new Paint();
            p.setColor(Color.BLACK);

            // get the bitmap for the card
            Bitmap bitmap = cardImages[suitInt][lookupValue];

            // create the source rectangle
            Rect r = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

            // draw the bitmap into the target rectangle
            g.drawBitmap(bitmap, r, rect, p);
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
     * @param activity the current activity
     */
    private static void initImages(Activity activity) {
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
