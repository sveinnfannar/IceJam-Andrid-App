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
 * Challenges activity of the game.
 *
 * @author Paolo Rovelli and Sveinn Fannar Kristjánsson.
 * @date 03/24/2013
 * @time 9:55AM
 */
public class ChallengesActivity extends ListActivity {
    private Parser parser = new Parser();
    private List<String> challengesNames = new ArrayList<String>();
    private List<String> challengesFiles = new ArrayList<String>();


    /**
     * Class Constructor.
     *
     * @param savedInstanceState
     */
    public void onCreate(Bundle savedInstanceState) {
        try {
            //Open the assets file:
            InputStream file = getAssets().open( "challengelist.xml" );

            //Parse the challenges:
            this.parser.parseChallenges(file, challengesNames, challengesFiles);
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        super.onCreate(savedInstanceState);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_list_item_1,  this.challengesNames);

        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView lv, View v, int position, long id) {
        Intent intent = new Intent(this, LevelsActivity.class);

        //Pass the name of the chosen challenge to the LevelsActivity through Preferences file:
        SharedPreferences preferences = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("ChallengeName",  this.challengesNames.get(position));
        editor.putString("ChallengeFile",  this.challengesFiles.get(position));
        editor.commit();

        startActivity(intent);  // start the game...
    }
}