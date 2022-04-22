package edu.up.cs301.game.GameFramework.actionMessage;


import java.io.Serializable;
import edu.up.cs301.game.GameFramework.players.GamePlayer;

/**
 * An action for a generic game.  A game action is something that a player
 * tells the game that it wants to do (e.g., put an 'X' on the top-left
 * tic-tac-toe square).  The game will then decide whether the player is
 * allowed to perform that action before effecting the action on the
 * players behalf.  Most real games will subclass GameAction to define
 * actions that are relevant to the particular game.  A GameAction contains
 * the player as part of its state; this way the game always knows what
 * player sent it the action.
 * <P>
 * Several "generic" of GameAction classes are already defined.  These
 * include MyNameIsAction and GameOverAckAction.
 *
 * @author Steven R. Vegdahl
 * @author Andrew M. Nuxoll
 * @version July 2013
 */
public abstract class GameAction implements Serializable {
    //Tag for logging
    private static final String TAG = "GameAction";
    // to support the Serializable interface
    private static final long serialVersionUID = 30672013L;
    private int playerID; //id of player

    /**
     * constructor for GameAction
     *
     * @param playerID
     * 		the player who created the action
     */
    public GameAction(int playerID) {
        this.playerID = playerID;
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }
}