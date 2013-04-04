package net.paolorovelli.IceJam;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
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
    private DatabaseHelper db = new DatabaseHelper(this);  // SQLite DB object
    AudioManager audioManager;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button button = (Button) findViewById(R.id.buttonPlay);
        //button.setBackgroundColor(Color.TRANSPARENT);

        //Instantiate the SQLite DB object:
        if( db.isEmpty() ) {  // the database is empty!
            Button continueButton = (Button) findViewById(R.id.buttonPlay);
            continueButton.setEnabled(false);
        }
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
