package net.paolorovelli.IceJam;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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


    private class CustomArrayAdapter extends ArrayAdapter<String> {

        public CustomArrayAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        public CustomArrayAdapter(Context context, int resource, int textViewResourceId) {
            super(context, resource, textViewResourceId);
        }

        public CustomArrayAdapter(Context context, int textViewResourceId, String[] objects) {
            super(context, textViewResourceId, objects);
        }

        public CustomArrayAdapter(Context context, int resource, int textViewResourceId, String[] objects) {
            super(context, resource, textViewResourceId, objects);
        }

        public CustomArrayAdapter(Context context, int textViewResourceId, List<String> objects) {
            super(context, textViewResourceId, objects);
        }

        public CustomArrayAdapter(Context context, int resource, int textViewResourceId, List<String> objects) {
            super(context, resource, textViewResourceId, objects);
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup)
        {
            View v = super.getView(position, view, viewGroup);
            ((TextView)v).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/viking.ttf"));
            return v;
        }
    }

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

        View rootView = findViewById(android.R.id.content);
        applyCustomFont((ViewGroup)rootView, Typeface.createFromAsset(getAssets(), "fonts/viking.ttf"));

        // Create ArrayAdapter using the challenges:
        CustomArrayAdapter adapter = new CustomArrayAdapter(this, android.R.layout.simple_list_item_1,  this.challengesNames);


        //Set the ArrayAdapter as the ListView's adapter:
        setListAdapter(adapter);
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