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

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: paolo
 * Date: 03/23/2013
 * Time: 1:37PM
 * To change this template use File | Settings | File Templates.
 */
public class ChallengesActivity extends ListActivity {
    private List<String> challenges = new ArrayList<String>();


    /**
     * Class Constructor.
     *
     * @param savedInstanceState
     */
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        challenges.add("Basic");
        challenges.add("Medium");
        challenges.add("Advanced");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_list_item_1, challenges);

        setListAdapter( adapter );
    }

    @Override
    protected void onListItemClick(ListView lv, View v, int position, long id) {
        System.out.println("F: " + position );

        Intent intent = new Intent(this, LevelsActivity.class);

        startActivity(intent);  // start the game...
    }
}