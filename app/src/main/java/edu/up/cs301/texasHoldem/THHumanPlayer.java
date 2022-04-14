package edu.up.cs301.texasHoldem;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import edu.up.cs301.game.GameFramework.GameMainActivity;
import edu.up.cs301.game.GameFramework.actionMessage.GameAction;
import edu.up.cs301.game.GameFramework.animation.AnimationSurface;
import edu.up.cs301.game.GameFramework.animation.Animator;
import edu.up.cs301.game.GameFramework.infoMessage.GameInfo;
import edu.up.cs301.game.GameFramework.players.GameHumanPlayer;
import edu.up.cs301.game.R;

/**
 * Human player for Texas Holdem, manages the GUI the player sees
 *
 * @author Xavier Santiago
 * @author Milton Nguy
 * @author Thomas Kone
 * @author Kevin Nguyen
 * @version 3.30.22
 */
public class THHumanPlayer extends GameHumanPlayer implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    //TODO: move View.OnClickListener to GameMainActivity

    /**
     * A basic implementation of PokerHumanPlayer derived from GameFrameWork Diagram. Many things
     * are still incomplete and need further implementations.
     */
    private Player user;
    private Button bet;
    private Button fold;
    private SeekBar valueSB;
    private TextView valueTV;
    private TextView balanceTV;
    private TextView potTV;
    private TextView usernameTV;
    private AnimationSurface dealerAS;
    private AnimationSurface handAS;
    private int valueInt;
    private Player me; //this will make things much easier
    private CardAnimator handAnimator;
    private CardAnimator dealerAnimator;

    private THState gameState;

    private int backgroundColor = 0xFF35654D;

    /**
     * constructor
     *
     * @param name the name of the player
     */
    public THHumanPlayer(String name) {
        super(name);
    }


    //TEMPORARY BLANK CLASSES
    public View getTopView() {
        return null;
    }
    @Override
    public void receiveInfo(GameInfo info) {
        if (!(info instanceof THState)) {
            flash(Color.RED, 5);
            return;
        }
        gameState = (THState) info;

        //this actually needs to get updated every time we get new info
        me = gameState.getPlayers().get(playerNum);

        if (handAnimator == null || dealerAnimator == null) {
            handAnimator = new CardAnimator(me.getHand(), 0xFFFFFFFF, handAS);
            dealerAnimator = new CardAnimator(gameState.getDealerHandAsArray(),
                    backgroundColor, dealerAS);
            handAS.setAnimator(handAnimator);
            dealerAS.setAnimator(dealerAnimator);
        }
        handAnimator.setCards(me.getHand()); //make sure we're rendering the current game state
        dealerAnimator.setCards(gameState.getDealerHandAsArray());

        updateUI();
    }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) { }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) { }
    //END TEMPORARY BLANK CLASSES

    public void setAsGui(GameMainActivity activity) {
        // remember the activity
        myActivity = activity;

        // Load the layout resource for our GUI
        activity.setContentView(R.layout.th_human_player_2);

        //read in the card images
        Card.initImages(activity);

        //Initialize the widget reference member variables
        this.bet = activity.findViewById(R.id.buttonBet);
        this.fold = activity.findViewById(R.id.buttonFold);
        this.valueSB = activity.findViewById(R.id.currBetSB);
        this.valueTV = activity.findViewById(R.id.currBetTV);
        this.balanceTV = activity.findViewById(R.id.userPurse);
        this.potTV = activity.findViewById(R.id.gameSet);
        this.dealerAS = activity.findViewById(R.id.dealerHandAS);
        this.handAS = activity.findViewById(R.id.playerHandAS);
        this.usernameTV = activity.findViewById(R.id.userID);

        //Listen for button presses
        bet.setOnClickListener(this);
        fold.setOnClickListener(this);
        valueSB.setOnSeekBarChangeListener(this);
    }

    /**
     * Method to update all the text in the UI and (not currently implemented) the cards shown
     * We need to call this once whenever we receive info
     */
    public void updateUI() {
        //set up gui correctly
        THState gameState = (THState) game.getGameState();

        //set our balance and the pool (probably just 0)
        balanceTV.setText(me.getBalance()+"$");
        potTV.setText("Pot: "+gameState.getPool()+"$\nBet: "+me.getBet());

        //set our username in the bottom left
        usernameTV.setText(me.getName());

        //(current min bet - our current bet) + ( our balance * ( seekbar value / seekbar max ) )
        valueTV.setText(""+getSliderBet());

        //change bet TV
        int betAmount = gameState.getCurrentBet()-me.getBet();
        if (betAmount == getSliderBet()){
            bet.setText("Check");
        }
        else bet.setText("Bet");

        //TODO: animationSurfaces
    }

    public void onClick(View view) {
        //checks if id equals the bet or fold action.
        if (view.getId() == bet.getId()) {
            //recalculate this here. feels flimsy to just use whatever text is on the screen
            int betAmount = (int) ((gameState.getCurrentBet()-me.getBet())
                    +(me.getBalance()*((float) valueSB.getProgress()/valueSB.getMax())));
            Bet betAction = new Bet(this, betAmount);
            game.sendAction((GameAction) (THGameAction) betAction);
        }
        if (view.getId() == fold.getId()) {
            Fold foldAction = new Fold(this);
            game.sendAction(foldAction);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        updateUI();

        //TODO: GameFramework & surfaceView to invalidate
        //.invalidate()
    }

    public Player getPlayerObject() { //using "getPlayerObject" for clarity
        return me;
    }
    public void setPlayerObject(Player player) {
        me = player;
    }

    /**
     * Function that returns the bet we want to place based on the value of the slider
     * this is used in multiple places so this function is to keep things easy
     * @return value that should be bet if the bet action is taken
     */
    private int getSliderBet() {
        int minBet = Math.max(gameState.getCurrentBet()-me.getBet(), gameState.getMinBet());
        float sliderProgress = (float) valueSB.getProgress()/valueSB.getMax();
        int value = (int) (minBet+((me.getBalance()-minBet)*sliderProgress));
        return value;
    }
}
