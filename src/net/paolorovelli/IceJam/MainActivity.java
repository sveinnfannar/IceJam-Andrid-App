package net.paolorovelli.IceJam;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.Button;

/**
 * Main activity of the game.
 *
 * @author Paolo Rovelli and Sveinn Fannar Kristjánsson.
 * @date 03/24/2013
 * @time 9:55AM
 */
public class MainActivity extends Activity {
    AudioManager audioManager;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void buttonClick(View view) {
        int id = view.getId();
        Intent intent = null;

        //Play a sound:
        view.playSoundEffect( android.view.SoundEffectConstants.CLICK );

        //Debug:
        System.out.println("[MAIN] Sound: " + view.isSoundEffectsEnabled());

        switch( id ) {
            case R.id.buttonPlay:
                    intent = new Intent(this, PlayActivity.class);
                    startActivity( intent );
                break;

            case R.id.buttonChallenges:
                intent = new Intent(this, ChallengesActivity.class);
                startActivity( intent );
                break;

            case R.id.buttonSettings:
                    intent = new Intent(this, SettingsActivity.class);
                    startActivity( intent );
                break;

        }
    }
}
