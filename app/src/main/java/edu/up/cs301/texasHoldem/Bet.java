package edu.up.cs301.texasHoldem;

import edu.up.cs301.game.GameFramework.players.GamePlayer;

/**
 * Bet action
 * @author Xavier Santiago
 * @author Milton Nguy
 * @author Thomas Kone
 * @author Kevin Nguyen
 * @version 3.30.22
 */
public class Bet extends THGameAction {
    private int amount;

    public Bet(GamePlayer x, int amount) {
        super(x);
        this.amount = amount;
    }

    /**
     * @return the value associated with this bet action
     */
    public int getAmount() {
        return amount;
    }
}
