package edu.up.cs301.texasHoldem;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

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

    /**
     * A basic implementation of PokerHumanPlayer derived from GameFrameWork Diagram. Many things
     * are still incomplete and need further implementations.
     */
    private Button bet;
    private Button fold;
    private SeekBar valueSB;
    private TextView qualityTV;
    private TextView valueTV;
    private TextView balanceTV;
    private TextView potTV;
    private TextView usernameTV;
    private TextView timerTV;
    private ProgressBar timePB;
    private AnimationSurface dealerAS;
    private AnimationSurface handAS;
    private LinearLayout[] oppProfiles = new LinearLayout[3];

    private ArrayList<Player> playerList = new ArrayList<>();
    private ArrayList<CardAnimator> hands = new ArrayList<>();

    private int valueInt;
    private Player me; //this will make things much easier
    private CardAnimator handAnimator;
    private CardAnimator dealerAnimator;

    private THState gameState;
    private RankHand handRanker;
    private long time;
    private CountDownTimer timer;


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
        playerList = gameState.getPlayers();




        //if this is the first time we're receiving gamestate
        //it's also possible for a gamestate to not have a handranker
        if (handRanker == null && gameState.getHandRanker() != null) {
            handRanker = gameState.getHandRanker();
        }

        //this actually needs to get updated every time we get new info
        me = gameState.getPlayers().get(playerNum);

        if (handAnimator == null || dealerAnimator == null) {
            handAnimator = new CardAnimator(me.getHand(), 0xFFFFFFFF, handAS);
            handAnimator.initImages(myActivity);
            hands.add(playerNum,handAnimator);
            dealerAnimator = new CardAnimator(gameState.getDealerHandAsArray(),
                    backgroundColor, dealerAS);
            dealerAnimator.initImages(myActivity);
            handAS.setAnimator(handAnimator);
            dealerAS.setAnimator(dealerAnimator);
        }
        handAnimator.setCards(me.getHand()); //make sure we're rendering the current game state
        dealerAnimator.setCards(gameState.getDealerHandAsArray());


        //Sets up the other players Information
        int idx = 0;
        for(int i = 0; i < playerList.size(); i++){
            Player player = gameState.getPlayers().get(i);
            LinearLayout layout = null;
            //To separate which one is the player and which one is the opponent
            if(i == playerNum){
                //skip the player bc they'll be updated in UpdateUI
                continue;
            }
            else{
                layout = oppProfiles[idx];
                idx++;
            }

            if(player != null) { //Makes sure that the player exist (should not trigger)
                /**
                 * External citation - Feb 13, 2022
                 * problem: getting the children of the liner layout
                 * link: https://stackoverflow.com/questions/6615723/getting-child-elements-from-linearlayout
                 * solution: do a for loop to access all the views inside the layout
                 */
                int count = layout.getChildCount();
                for (int j = 0; j < count; j++) {
                    View id = layout.getChildAt(j);

                    if(id instanceof ImageView){
                        ((ImageView) id).setImageResource(R.drawable.cheems_avatar);
                    }
                    else if(id instanceof LinearLayout){
                        //Since one of the child is also a linear layout, I have to do the same damned for loop
                        //But this time with tag! (yay....)
                        LinearLayout childLayout = ((LinearLayout) id);
                        int countId = childLayout.getChildCount();
                        for(int c = 0; c < countId; c++){
                            View childView = childLayout.getChildAt(c);
                            if(childView instanceof TextView) {
                                String tag = (String) childView.getTag();
                                if (tag.equals("name")) {
                                    ((TextView)childView).setText(player.getName());
                                } else if (tag.equals("action")) {
                                    //TODO: Get the action from each player from somewhere
                                    ((TextView)childView).setText(player.getAction());
                                } else if (tag.equals("money")) {
                                    ((TextView)childView).setText("$ " + player.getBalance());
                                }
                            }
                            else if(childView instanceof AnimationSurface){
                                //creates the hand if it hasn't before using playerlist as size reference
                                //TODO: Find a way to hide the opponent's cards until the game completes
                                if(hands.size() < playerList.size()){
                                    hands.add(i , new CardAnimator(player.getHand(),0xFFFFFF,(AnimationSurface) childView));
                                    ((AnimationSurface) childView).setAnimator(hands.get(i));
                                }
                                ((AnimationSurface) childView).setAnimator(hands.get(i));
                            }
                        }
                    }
                }
            }
        }
/*
        manager = new ProfileManager(gameState.getProfiles(),R.id.profileList);
        for(int  i = 0; i < gameState.getPlayers().size(); i++){
            if(playerNum != i) {
                manager.addProfiles(i);
            }
        }
*/

        /**
         * External Citation - April 20, 2022
         * Problem: Trying to setup a timer in java
         * link: https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/TimeUnit.html
         * Solution: access methods contained in TimeUnit
         *
         * */
        time = TimeUnit.SECONDS.toMillis(gameState.getTimer());

        cancelTimer();

        startTimer();


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
        this.qualityTV = activity.findViewById(R.id.handQuality);
        this.timerTV = activity.findViewById(R.id.textView);
        this.timePB = activity.findViewById(R.id.userPlayTimer);

        this.oppProfiles[0] = activity.findViewById(R.id.oppProfile1);
        this.oppProfiles[1] = activity.findViewById(R.id.oppProfile2);
        this.oppProfiles[2] = activity.findViewById(R.id.oppProfile3);

        //Listen for button presses
        bet.setOnClickListener(this);
        fold.setOnClickListener(this);
        valueSB.setOnSeekBarChangeListener(this);
    }

    /**
     * Method to update all the text in the UI and the cards shown
     * We need to call this once whenever we receive info
     */
    public void updateUI() {


        //set our balance and the pool (probably just 0)
        balanceTV.setText(me.getBalance()+"$");
        potTV.setText("Pot: "+gameState.getPool()+"$\nBet: "+me.getBet());

        //set our username in the bottom left
        usernameTV.setText(me.getName());

        //(current min bet - our current bet) + ( our balance * ( seekbar value / seekbar max ) )
        valueTV.setText(""+getSliderBet());



        //show hand quality in post-flop rounds
        if (gameState.getRound() > 0) {
            //get all cards
            Card[] dHand = gameState.getDealerHandAsArray();
            Card[] allCards = new Card[dHand.length + 2];
            allCards[0] = me.getHand()[0];
            allCards[1] = me.getHand()[1];

            //combine into one list
            for (int i = 0; i < dHand.length; i++) {
                allCards[i + 2] = dHand[i];
            }

            int quality = (int) (handRanker.getHandRankFloat(allCards)*100);
            qualityTV.setText(quality+"%");
        }

        //change bet TV
        int betAmount = gameState.getCurrentBet()-me.getBet();
        if (betAmount == getSliderBet()){
            bet.setText("Check");
        }
        else bet.setText("Bet");
    }

    public void onClick(View view) {
        //checks if id equals the bet or fold action.
        if (view.getId() == bet.getId()) {
            //recalculate this here. feels flimsy to just use whatever text is on the screen
            int betAmount = (int) ((gameState.getCurrentBet()-me.getBet())
                    +(me.getBalance()*((float) valueSB.getProgress()/valueSB.getMax())));
            Bet betAction = new Bet(this, betAmount);
            game.sendAction((GameAction) (THGameAction) betAction);
            cancelTimer();

        }
        if (view.getId() == fold.getId()) {
            Fold foldAction = new Fold(this);
            game.sendAction(foldAction);
            cancelTimer();
        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        //change bet TV
        updateUI();
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
        int minBet = gameState.getCurrentBet()-me.getBet();
        float sliderProgress = (float) valueSB.getProgress()/valueSB.getMax();
        int value = (int) (minBet+((me.getBalance()-minBet)*sliderProgress));
        return value;
    }

    /**
     * External Citation - April 20, 2022
     * Problem: Trying to setup a timer in java without data leaks
     * link: https://stackoverflow.com/questions/10032003/how-to-make-a-countdown-timer-in-android
     * Solution: create void methods
     *
     * */
    private void startTimer() {
        Fold foldactionTime = new Fold(this);
        timer = new CountDownTimer(time, 1000) {
            public void onTick(long millisUntilFinished) {
                timerTV.setText("Time: " + millisUntilFinished / 1000);
                timePB.setProgress((int) millisUntilFinished / 1000);
                int w = (int) millisUntilFinished / 1000;
                Log.i("Timer Count", "" + w);
            }
            public void onFinish() {
                game.sendAction(foldactionTime);

            }
        };
        timer.start();

    }


    //cancel timer
    private void cancelTimer() {
        if(timer!=null)
            timer.cancel();
    }
}
