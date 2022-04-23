package edu.up.cs301.texasHoldem;

import edu.up.cs301.game.GameFramework.actionMessage.GameAction;
import edu.up.cs301.game.GameFramework.players.GamePlayer;

/**
 * Game action off of which bet and fold are based
 *
 * @author Xavier Santiago
 * @author Milton Nguy
 * @author Thomas Kone
 * @author Kevin Nguyen
 * @version 3.30.22
 */
public class THGameAction extends GameAction {
    // parent class for bet and fold,
    // Not really needed but at this point it would take a while to change
    public THGameAction(int playerID) {
        super(playerID);
    }
}
