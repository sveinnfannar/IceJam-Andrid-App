package net.paolorovelli.IceJam;

import android.*;
import android.R;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Levels activity of the game.
 *
 * @author Paolo Rovelli and Sveinn Fannar Kristjánsson.
 * @date 03/24/2013
 * @time 9:55AM
 */
public class LevelsActivity extends ListActivity {         //TODO: change in ListView????
    private Parser parser = new Parser();
    private String challenge = new String();
    private List<String> levels = new ArrayList<String>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        this.challenge = preferences.getString("Challenge", "");

        try {
            //Open the assets file:
            InputStream file = getAssets().open( "challenge.xml" );  //TODO: the file should be the one passed by ChallengesActivity....xml" );

            //Parse the challenges:
            this.levels =  this.parser.parseLevels( file );
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_list_item_1, levels);

        setListAdapter( adapter );
    }

    @Override
    protected void onListItemClick(ListView lv, View v, int position, long id) {
        Intent intent = new Intent(this, PlayActivity.class);

        //Pass parameters to the next Activity through Intent:
        //intent.putExtra("level", levels.get(position));

        //Pass parameters to the next Activity through Preferences file:
        SharedPreferences preferences = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Level", levels.get(position));
        editor.commit();

        //TODO: the activity should send to the PlayActivity the setup of the challenge...

        startActivity(intent);  // start the game...
    }
}