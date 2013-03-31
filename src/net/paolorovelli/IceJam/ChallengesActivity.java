package net.paolorovelli.IceJam;

import android.app.Activity;
import android.app.ListActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.ArrayAdapter;

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
    private Parser parser = new Parser();  // XML parser object

    private List<String> challengesNames = new ArrayList<String>();
    private List<String> challengesFiles = new ArrayList<String>();


    /**
     * Class Constructor.
     *
     * @param savedInstanceState
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.challenges);

        //Find the ListView resource:
        //ListView view = (ListView) findViewById(R.id.listChallenges);
        //view.setBackgroundResource(R.drawable.icebergbackground);

        //Read the challenges file:
        try {
            //Open the assets file:
            InputStream file = getAssets().open( "challengelist.xml" );

            //Parse the challenges:
            this.parser.parseChallenges(file, challengesNames, challengesFiles);
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        // Create ArrayAdapter using the challenges:
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,  this.challengesNames);

        //Set the ArrayAdapter as the ListView's adapter:
        setListAdapter( adapter );
    }

    @Override
    protected void onListItemClick(ListView lv, View v, int position, long id) {
        Intent intent = new Intent(this, LevelsActivity.class);

        //Send the name of the chosen challenge to the LevelsActivity through Preferences file:
        SharedPreferences preferences = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("ChallengeName",  this.challengesNames.get(position));
        editor.putString("ChallengeFile",  this.challengesFiles.get(position));
        editor.commit();

        startActivity(intent);  // start the game...
    }
}