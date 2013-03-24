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
    private String challengeName = new String();
    private String challengeFile = new String();
    private List<String> levelsID = new ArrayList<String>();
    private List<String> levelsSetups = new ArrayList<String>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        this.challengeName = preferences.getString("ChallengeName", "");
        this.challengeFile = preferences.getString("ChallengeFile", "");

        //Debug:
        //System.out.println("[LEVELS] Challenge name: " + this.challengeName);
        //System.out.println("[LEVELS] Challenge file: " + this.challengeFile);

        try {
            //Open the assets file:
            InputStream file = getAssets().open( this.challengeFile );

            //Parse the challenges:
            this.parser.parseLevels(file, this.levelsID, this.levelsSetups);
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_list_item_1, this.levelsID);

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
        editor.putString("LevelID", this.levelsID.get(position));
        editor.putString("LevelSetup", this.levelsSetups.get(position));
        editor.commit();

        startActivity(intent);  // start the game...
    }
}