package edu.up.cs301.texasHoldem;

import edu.up.cs301.game.GameFramework.actionMessage.GameAction;
import edu.up.cs301.game.GameFramework.players.GamePlayer;

public class THGameAction extends GameAction {

    // the player who generated the request
    private GamePlayer player;


    public THGameAction(GamePlayer p) {
        super(p);
        this.player = p;
    }

    //return player's action.
    public GamePlayer getPlayer() {
        return player;
    }

    public void setPlayer(GamePlayer p) {
        this.player = p;
    }

}
