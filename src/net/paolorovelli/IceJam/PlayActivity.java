package net.paolorovelli.IceJam;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Play activity of the game.
 *
 * @author Paolo Rovelli and Sveinn Fannar Kristjánsson.
 * @date 03/24/2013
 * @time 9:55AM
 */
public class PlayActivity extends Activity {
    private static DatabaseHelper db;  // SQLite DB object
    private Parser parser = new Parser();  // XML parser object

    private static String challengeName = new String();
    private static String levelID = new String();
    private String levelSetup = new String();

    DrawView mDrawView;
    TextView mLevelView;
    TextView mMovesView;
    TextView mBestView;
    private static Integer moves;

    private int mColorFirst;
    private int mColorSecond;


    /**
     * On create method.
     *
     * @param savedInstanceState
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.game );

        //Find the View resource:
        mDrawView = (DrawView) findViewById( R.id.drawView );
        mLevelView = (TextView) findViewById( R.id.numberLevel );
        mMovesView = (TextView) findViewById( R.id.numberMoves );
        mBestView = (TextView) findViewById( R.id.numberBest );

        //Read parameters from the last Activity through Intent:
        //Intent intent = getIntent();
        //mColorFirst = intent.getIntExtra("ColorFirst", Color.RED);
        //mColorSecond = intent.getIntExtra("ColorSecond", Color.GREEN);

        //Read parameters from the last Activity through Preferences file:
        SharedPreferences preferences = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        this.challengeName = preferences.getString("ChallengeName", "");
        this.levelID = preferences.getString("LevelID", "");
        this.levelSetup = preferences.getString("LevelSetup", "");

        mColorFirst = Color.RED;
        mColorSecond = Color.BLUE;

        //Update Level TextView:
        mLevelView.setText( this.levelID );

        //Instantiate the SQLite DB object:
        db = new DatabaseHelper( getApplicationContext() );
        Integer bestNumberOfMoves = (Integer) db.isSolved(this.challengeName, this.levelID);  // the number of moves needed to solve the puzzle... (0 if never solved)
        if( bestNumberOfMoves > 0 ) {  // already solved...
            //Update the best number of moves TextView:
            mBestView.setText( bestNumberOfMoves.toString() );
        }
        else {  // moves == 0  // NOT solved yet!
            //Update the best number of moves TextView:
            mBestView.setText( "-" );
        }

        //Load level:
        final String puzzleStr = this.levelSetup;  // "(H 1 3 2), (V 0 0 2), (V 0 2 3)";

        mDrawView.post(new Runnable() {
            @Override
            public void run() {
                mDrawView.setGridSize(9, 9);

                //mDrawView.addShape(new Shape(Shape.Orientation.Vertical, 0, 0, 3));
                //mDrawView.addShape(new Shape(Shape.Orientation.Horizontal, 2, 2, 2));

                // Add the shapes defined in the xml file
                for (String shapeStr : puzzleStr.split(", ")) {
                    mDrawView.addShape(Shape.shapeFromString(shapeStr));
                }
            }
        });

        mDrawView.setCustomEventHandler(new DrawEventHandler() {
            @Override
            public void onShapeMoved() {
                //TODO: ...

                //Update the number of moves used until now:
                moves = Integer.parseInt( mMovesView.getText().toString() );
                moves++;
                mMovesView.setText( moves.toString() );
            }

            @Override
            public void onPuzzleSolved() {
                //Debug:
                System.out.println("[GAME] solved!");

                //Query the SQLite DB to check if the level has been already solved:
                db.solved(challengeName, levelID, moves);
            }
        });
    }
}