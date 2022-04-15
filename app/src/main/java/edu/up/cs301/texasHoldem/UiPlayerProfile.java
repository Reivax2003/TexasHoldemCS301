package edu.up.cs301.texasHoldem;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import edu.up.cs301.game.GameFramework.animation.AnimationSurface;
import edu.up.cs301.game.R;

/**
 * UiPlayerProfile
 * Creates a fragment that contains the opponent's information which includes their Image, Player name and Cards.
 * Cards will remain hidden until the game is over
 * @Author Kevin Nguyen
 * @Version 3.31.2022
 */

public class UiPlayerProfile extends Fragment {

    ImageView playerPic;
    TextView playerName;
    TextView actionText;
    AnimationSurface playerAS;
    Bitmap picture;
    CardAnimator playerAnimation;

    public UiPlayerProfile(String name) {
        super(R.layout.fragment_ui_player_profile);
        //playerName.setText(name);
        //TODO:Remove this code and replace it with the player's bitmap once it becomes implemented
        //playerPic.setImageResource(R.drawable.cheems_avatar);
    }

    /**
     * onCreateView - creates the view of the fragment. The layout is the player profile:
     * @R.layout.fragment_ui_player_profile
     * Majority of the params is unknown to me :P More research is probably best for me
     * @return the created view of the fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Gets the fragment UI
        View profileView = inflater.inflate(R.layout.fragment_ui_player_profile, container, false);

        playerPic = (ImageView) profileView.findViewById(R.id.oppImg1);
        playerName = (TextView) profileView.findViewById(R.id.oppID1);
        actionText = (TextView) profileView.findViewById(R.id.oppAct1);
        playerAS = (AnimationSurface) profileView.findViewById(R.id.oppHands1);

        // Inflate the layout for this fragment
        return profileView;
    }

    /**
     * External Citation
     * 3.30.2022
     * Problem: be able to change the contents of the view inside the fragment
     * Credit: https://stackoverflow.com/questions/24188050/how-to-access-fragments-child-views-inside-fragments-parent-activity
     * Solution: Create methods that can be called to change information of the fragment
     */

    /**
     * setCardImg - sets the cards of the player's hand
     * TODO: make sure that the game knows when the cards should be hidden or not when game ends
     *
     */
    public void setCardImg(Card[] hand){
        if(playerAnimation == null){
            //playerAnimation = new CardAnimator(hand,"player",0xFFFFFFFF,playerAS);
            //playerAS.setAnimator(playerAnimation);
        }
        playerAnimation.setCards(hand);
    }

    /**
     * setActionText - Will change the textView to display the action of the player
     */
    public void setActionText(String action){
        actionText.setText(action);
    }
}