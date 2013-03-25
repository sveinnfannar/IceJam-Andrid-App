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
        private List<String> cells;

        //Image references:
        private Integer thumbIds = R.drawable.box;

        public CellAdapter(Context context, List<String> cells) {
            this.context = context;
            this.cells = cells;
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
            TextView textView;
            if ( view == null ) {  // if it's not recycled, initialize some attributes
                textView = new TextView( this.context );
            } else {
                textView = (TextView) view;
            }

            //Set the ID:
            textView.setId( Integer.parseInt( this.cells.get(position) ) );  // android:id

            //Set the text:
            textView.setText( this.cells.get(position) );  // android:text
            textView.setTextColor( Color.BLACK );  // android:textColor
            textView.setTextSize(25);  // android:textSize
            textView.setTypeface( Typeface.DEFAULT_BOLD );  // android:textStyle
            textView.setGravity( Gravity.CENTER );  // android:gravity
            textView.setPadding(0, 30, 0, 0);

            //Set backgorund image:
            textView.setBackgroundResource( this.thumbIds );  // android:background
            textView.setWidth(64);
            textView.setHeight(64);

            return textView;
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

        GridView gridview = (GridView) findViewById(R.id.levelsView);
        //gridview.setBackgroundResource(R.drawable.box);

        //Read the name and the file of the chosen challenge from Preferences file:
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

        //Set the custom ArrayAdapter to the grid:
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, this.levelsID);
        //gridview.setAdapter( adapter );
        gridview.setAdapter( new CellAdapter(this, this.levelsID) );

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