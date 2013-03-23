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
public class LevelsActivity extends ListActivity {         //TODO: change in ListView????
    class ColorPair {
        private int mColorFirst;
        private int mColorSecond;

        ColorPair(int colorFirst, int colorSecond) {
            mColorFirst = colorFirst;
            mColorSecond = colorSecond;
        }

        public int getColorFirst() {
            return mColorFirst;
        }

        public int getColorSecond() {
            return mColorSecond;
        }

        public String toString() {
            return "" + colorName(mColorFirst) + " - " + colorName(mColorSecond);
        }

        private String colorName( int color ) {
            switch( color ) {
                case Color.GREEN:
                    return "GREEN";
                case Color.RED:
                    return "RED";
                case Color.BLUE:
                    return "BLUE";
                case Color.GRAY:
                    return "GRAY";
                case Color.YELLOW:
                    return "YELLOW";
                case Color.CYAN:
                    return "CYAN";
            }

            return "UNKNOWN";
        }
    }

    private List<ColorPair> mColorPair = new ArrayList<ColorPair>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mColorPair.add( new ColorPair(Color.RED,  Color.BLUE) );
        mColorPair.add( new ColorPair(Color.GREEN,  Color.YELLOW) );
        mColorPair.add( new ColorPair(Color.CYAN,  Color.GRAY) );

        ArrayAdapter<ColorPair> adapter = new ArrayAdapter<ColorPair>(this, R.layout.simple_list_item_1, mColorPair);

        setListAdapter( adapter );
    }

    @Override
    protected void onListItemClick(ListView lv, View v, int position, long id) {
        System.out.println("F: " + position );

        Intent intent = new Intent(this, PlayActivity.class);

        //Pass parameters to the next Activity through Intent:
        //intent.putExtra("ColorFirst", mColorPair.get(position).getColorFirst());
        //intent.putExtra("ColorSecond", mColorPair.get(position).getColorSecond());

        //Pass parameters to the next Activity through Preferences file:
        SharedPreferences preferences = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("ColorFirst", mColorPair.get(position).getColorFirst());
        editor.putInt("ColorSecond", mColorPair.get(position).getColorSecond());
        editor.commit();

        startActivity(intent);  // start the game...
    }
}