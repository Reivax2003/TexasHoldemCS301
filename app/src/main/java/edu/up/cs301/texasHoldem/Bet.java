package edu.up.cs301.texasHoldem;

import edu.up.cs301.game.GameFramework.players.GamePlayer;

public class Bet extends THGameAction {
    private int amount;

    public Bet(GamePlayer x, int amount) {
        super(x);
        this.amount = amount;
    }
    public int getAmount() {
        return amount;
    }
}
