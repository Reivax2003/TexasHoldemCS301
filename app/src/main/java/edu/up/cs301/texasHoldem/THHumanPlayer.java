package edu.up.cs301.texasHoldem;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import edu.up.cs301.game.GameFramework.GameMainActivity;
import edu.up.cs301.game.GameFramework.actionMessage.GameAction;
import edu.up.cs301.game.GameFramework.animation.AnimationSurface;
import edu.up.cs301.game.GameFramework.infoMessage.GameInfo;
import edu.up.cs301.game.GameFramework.players.GameHumanPlayer;
import edu.up.cs301.game.R;

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

    private THState gameState;

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
        String value = ""+(int) ((gameState.getCurrentBet()-me.getBet())
                +(me.getBalance()*((float) valueSB.getProgress()/valueSB.getMax())));
        valueTV.setText(value);

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
        //change bet TV
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
}
