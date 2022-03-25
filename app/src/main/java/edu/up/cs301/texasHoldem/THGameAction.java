package edu.up.cs301.texasHoldem;

public class THGameAction {

    // the player who generated the request
    private Player player;


    public THGameAction(Player p) {
        this.player = p;
    }

    //return player's action.
    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player p) {
        this.player = p;
    }

}
