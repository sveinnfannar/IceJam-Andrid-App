package net.paolorovelli.IceJam;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

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
public class LevelsActivity extends Activity {
    private static DatabaseHelper db;
    private Parser parser = new Parser();
    private String challengeName = new String();
    private String challengeFile = new String();
    private List<String> levelsID = new ArrayList<String>();
    private List<String> levelsSetups = new ArrayList<String>();


    /**
     * CellAdapter.
     *
     * @author Paolo Rovelli and Sveinn Fannar Kristjánsson.
     * @date 03/24/2013
     * @time 9:55AM
     */
    public static class CellAdapter extends BaseAdapter {
        private Context context;
        private String challenge;
        private List<String> cells;

        //Image references:
        private Integer thumbIds = R.drawable.level;
        private Integer thumbSolvedIds = R.drawable.levelsolved;

        public CellAdapter(Context context, String challenge, List<String> cells) {
            this.context = context;
            this.challenge = challenge;
            this.cells = cells;

            db = new DatabaseHelper(context);
        }

        public int getCount() {
            return cells.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        /**
         * Create a new TextView for each item referenced by the Adapter.
         *
         * @param position
         * @param view
         * @param parent
         * @return
         */
        public View getView(int position, View view, ViewGroup parent) {
            TextView newView;  // TextView
            if ( view == null ) {  // if it's not recycled, initialize some attributes
                newView = new TextView( this.context );  // TextView
            } else {
                newView = (TextView) view;  // TextView
            }

            //Set the ID:
            newView.setId( Integer.parseInt( this.cells.get(position) ) );  // android:id

            //Set the text:
            newView.setText( this.cells.get(position) );  // android:text
            newView.setTextColor( Color.BLACK );  // android:textColor
            newView.setTextSize(15);  // android:textSize
            newView.setTypeface( Typeface.DEFAULT_BOLD );  // android:textStyle
            newView.setGravity( Gravity.CENTER );  // android:gravity
            //newView.setPadding(0, 0, 0, 0);

            //Check if the level has been already solved:
            if( db.isSolved(this.challenge, this.cells.get(position)) ) {  // already solved...
                //Set background image:
                newView.setBackgroundResource( this.thumbSolvedIds );  // android:background
            }
            else {  // !db.isSolved(this.challenge, this.cells.get(position))  // NOT solved yet!
                //Set background image:
                newView.setBackgroundResource( this.thumbIds );  // android:background
            }

            //Debug:
            //System.out.println("[DB] Solved: " + db.isSolved(this.challenge, this.cells.get(position)));

            //Set background image size:
            newView.setWidth(64);
            newView.setHeight(128);

            return newView;
        }
    }


    /**
     * On create.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.levels);

        //Find the GridView resource:
        GridView gridview = (GridView) findViewById(R.id.levelsView);
        //gridview.setBackgroundResource(R.drawable.box);

        //Read the name and the file of the chosen challenge from Preferences file:
        SharedPreferences preferences = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        this.challengeName = preferences.getString("ChallengeName", "");
        this.challengeFile = preferences.getString("ChallengeFile", "");

        //Debug:
        //System.out.println("[LEVELS] Challenge name: " + this.challengeName);
        //System.out.println("[LEVELS] Challenge file: " + this.challengeFile);

        //Read the levels file:
        try {
            //Open the assets file:
            InputStream file = getAssets().open( this.challengeFile );

            //Parse the challenges:
            this.parser.parseLevels(file, this.levelsID, this.levelsSetups);
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        //Set the custom ArrayAdapter to the grid:
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, this.levelsID);
        //gridview.setAdapter( adapter );
        gridview.setAdapter( new CellAdapter(this, this.challengeName, this.levelsID) );

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(LevelsActivity.this, PlayActivity.class);

                //Pass parameters to the next Activity through Intent:
                //intent.putExtra("level", levels.get(position));

                //Send the level ID and setup to the PlayActivity through Preferences file:
                SharedPreferences preferences = getSharedPreferences("GamePrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("LevelID", LevelsActivity.this.levelsID.get(position));
                editor.putString("LevelSetup", LevelsActivity.this.levelsSetups.get(position));
                editor.commit();

                startActivity(intent);  // start the game...
            }
        });
    }
}