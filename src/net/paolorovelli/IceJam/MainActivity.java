package net.paolorovelli.IceJam;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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

        View rootView = findViewById(android.R.id.content);
        applyCustomFont((ViewGroup)rootView, Typeface.createFromAsset(getAssets(), "fonts/viking.ttf"));

        //Instantiate the SQLite DB object:
        if( db.isEmpty() ) {  // the database is empty!
            Button continueButton = (Button) findViewById(R.id.buttonPlay);
            continueButton.setEnabled(false);
            continueButton.setText("");
            continueButton.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    public static void applyCustomFont(ViewGroup list, Typeface customTypeface) {
        for (int i = 0; i < list.getChildCount(); i++) {
            View view = list.getChildAt(i);
            if (view instanceof ViewGroup) {
                applyCustomFont((ViewGroup) view, customTypeface);
            } else if (view instanceof TextView) {
                ((TextView) view).setTypeface(customTypeface);
            }
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
