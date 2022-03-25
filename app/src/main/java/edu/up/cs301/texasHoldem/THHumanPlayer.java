package edu.up.cs301.texasHoldem;

import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import edu.up.cs301.game.GameFramework.GameMainActivity;
import edu.up.cs301.game.GameFramework.actionMessage.GameAction;
import edu.up.cs301.game.GameFramework.infoMessage.GameInfo;
import edu.up.cs301.game.GameFramework.players.GameHumanPlayer;
import edu.up.cs301.game.R;

public class THHumanPlayer extends GameHumanPlayer implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    //TODO: move View.OnClickListener to GameMainActivity

    /**
     * A basic implementation of PokerHumanPlayer derived from GameFrameWork Diagram. Many things
     * are still incomplete and need further implementations.
     */
    private Button bet;
    private Button fold;
    private Player user;
    private SeekBar valueSB;
    private TextView valueTV;
    private int valueInt;

    /**
     * constructor
     *
     * @param name the name of the player
     */
    public THHumanPlayer(String name) {
        super(name);
    }


    //TEMPORARY BLANK CLASSES
    @Override
    public View getTopView() {
        return null;
    }
    @Override
    public void receiveInfo(GameInfo info) { }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) { }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) { }
    //END TEMPORARY BLANK CLASSES

    @Override
    public void setAsGui(GameMainActivity activity) {
        // remember the activity
        myActivity = activity;

        // Load the layout resource for our GUI
        activity.setContentView(R.layout.th_human_player);

        //Initialize the widget reference member variables
        this.bet = activity.findViewById(R.id.buttonBet);
        this.valueSB = activity.findViewById(R.id.currBetSB);
        this.valueTV = activity.findViewById(R.id.currBetTV);

        //I don't understand what these lines do and they're crashing the app
        /** //Listen for button presses
        bet.setOnClickListener(this);
        fold.setOnClickListener(this);*/
    }

    /**
     *
     * Note from Xavier:
     * Had to edit this out temporarily as it was throwing errors
     */
    @Override
    public void onClick(View view) {
        //checks if id equals the bet action.
        Bet betAction = new Bet(this);
        Fold foldAction = new Fold(this);

        if (view.getId() == bet.getId()) {
            game.sendAction((GameAction) (THGameAction) betAction);

        }
        if (view.getId() == fold.getId()) {
            game.sendAction(foldAction);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (seekBar.getId() == R.id.currBetSB) {
            valueTV.setText(i + " $");
            valueInt = i;
        }

        //TODO: GameFramework & surfaceView to invalidate
        //.invalidate()
    }
}
