package net.paolorovelli.IceJam;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Play activity of the game.
 *
 * @author Paolo Rovelli and Sveinn Fannar Kristjánsson.
 * @date 03/24/2013
 * @time 9:55AM
 */
public class PlayActivity extends Activity {
    private static DatabaseHelper db;  // SQLite DB object
    private static Parser parser = new Parser();  // XML parser object
    final Context context = this;

    private static String challengeName = new String();
    private static String challengeFile = new String();
    private static String levelID = new String();
    private static String levelSetup = new String();
    private static boolean chkNextLevel = false;

    private DrawView mDrawView;
    private TextView mLevelView;
    private TextView mMovesView;
    private TextView mBestView;
    private TextView mTimerView;
    private TextView mTimerBestView;
    private boolean mTimerStarted;

    private static Integer moves;
    private static Integer bestNumberOfMoves;
    private static String bestTime;

    //Timer:
    private Timer timer;
    private Integer timeSeconds = 0;
    private Integer timeMinutes = 0;
    private static String timestamp = "";
    private Runnable Timer_Tick = new Runnable() {
        public void run() {
            timestamp = "";

            if ( timeSeconds == 59 ) {
                timeSeconds = 0;

                if( timeMinutes == 59 ) {
                    timeMinutes = 0;
                }
                else {  // timeMinutes < 59
                    timeMinutes++;
                }
            }

            if ( timeMinutes < 10 ) {
                timestamp += "0";
            }
            timestamp += timeMinutes.toString();  // mm
            timestamp += ":";
            if ( timeSeconds < 10 ) {
                timestamp += "0";
            }
            timestamp += timeSeconds.toString();  // ss

            mTimerView.setText( timestamp );  // stamp the timestamp
            timeSeconds++;
        }
    };

    private boolean loadLevel(int offset) {
        Integer nextLevelIDInt = Integer.parseInt(levelID) + offset;
        String nextLevelID = nextLevelIDInt.toString();
        String nextLevelSetup = null;

        //Read the levels file:
        try {
            //Open the assets file:
            InputStream file = getAssets().open( challengeFile );

            //Parse the challenges:
            nextLevelSetup = parser.parseTheLevel(file, nextLevelID);
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        if( nextLevelSetup != null ) {  // it is NOT the last level of the challenge...
            //Send the level ID and setup to the PlayActivity through Preferences file:
            SharedPreferences preferences = getSharedPreferences("GamePrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("LevelID", nextLevelID);
            editor.putString("LevelSetup", nextLevelSetup);
            editor.commit();

            chkNextLevel = true;
        }

        return chkNextLevel;
    }

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
        mTimerView = (TextView) findViewById( R.id.timer );
        mTimerBestView = (TextView) findViewById( R.id.timerBest );

        //Timer:
        timer = new Timer();

        //Read parameters from the last Activity through Intent:
        //Intent intent = getIntent();
        //mColorFirst = intent.getIntExtra("ColorFirst", Color.RED);
        //mColorSecond = intent.getIntExtra("ColorSecond", Color.GREEN);

        //Read parameters from the last Activity through Preferences file:
        SharedPreferences preferences = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        this.challengeName = preferences.getString("ChallengeName", "");
        this.challengeFile = preferences.getString("ChallengeFile", "");
        this.levelID = preferences.getString("LevelID", "");
        this.levelSetup = preferences.getString("LevelSetup", "");

        //Update Level TextView:
        mLevelView.setText(this.levelID);

        //Instantiate the SQLite DB object:
        db = new DatabaseHelper( getApplicationContext() );
        bestNumberOfMoves = (Integer) db.isSolved(this.challengeName, this.levelID);  // the number of moves needed to solve the puzzle... (0 if never solved)
        if( bestNumberOfMoves > 0 ) {  // already solved...
            //Update the best number of moves TextView:
            mBestView.setText( bestNumberOfMoves.toString() );
        }
        else {  // moves == 0  // NOT solved yet!
            //Update the best number of moves TextView:
            mBestView.setText("-");
        }

        //Instantiate the SQLite DB object:
        bestTime = db.isSolvedInTime(this.challengeName, this.levelID);  // the number of moves needed to solve the puzzle... (0 if never solved)
        mTimerBestView.setText( bestTime );



        //Load level:
        final String puzzleStr = this.levelSetup;  // "(H 1 3 2), (V 0 0 2), (V 0 2 3)";

        mDrawView.post(new Runnable() {
            @Override
            public void run() {
                //Set the grid size:
                mDrawView.setGridSize(6, 6);

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
                //Stop the timer:
                timer.cancel();


                /* --- BEGIN Store in SQLite DB: --- */
                //Query the SQLite DB to check if the level has been already solved:
                db.solved(challengeName, levelID, moves, timestamp);
                /* --- END Store in SQLite DB. --- */


                /* --- BEGIN Pre-Load the next level (for the continue button in the MainActivity): --- */
                chkNextLevel = loadLevel(1);
                /* --- END Pre-Load the next level (for the continue button in the MainActivity). --- */


                /* --- BEGIN Dialog: --- */
                //Open custom dialog:
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog);
                dialog.setTitle("Level " + levelID + " solved!");

                //Set the dialog number of moves:
                TextView dgMoves = (TextView) dialog.findViewById(R.id.dialogMoves);
                dgMoves.setText(dgMoves.getText() + " " + moves.toString());

                //Set the dialog best number of moves:
                TextView dgBest = (TextView) dialog.findViewById(R.id.dialogBest);
                dgBest.setText(dgBest.getText() + " " + bestNumberOfMoves.toString());

                //Set the dialog current time:
                TextView dgTimer = (TextView) dialog.findViewById(R.id.dialogTime);
                dgTimer.setText(dgTimer.getText() + " " + timestamp);

                //Set the dialog previous best time:
                TextView dgTimerBest = (TextView) dialog.findViewById(R.id.dialogTimeBest);
                dgTimerBest.setText(dgTimerBest.getText() + " " + bestTime);

                //Set the dialog number of times solved:
                Integer numberOfTimesSolved = (Integer) db.nTimesSolved(challengeName, levelID);  // the number of times the level has been solved... (0 if never solved)
                TextView dgNTimes = (TextView) dialog.findViewById(R.id.dialogNTimes);
                dgNTimes.setText(dgNTimes.getText() + " " + numberOfTimesSolved.toString());

                //Set the dialog image:
                //ImageView dgImage = (ImageView) dialog.findViewById(R.id.dialogImage);
                //dgImage.setImageResource(R.drawable.levelsolved);

                //Set the dialog menu button:
                Button dialogButtonMenu = (Button) dialog.findViewById(R.id.dialogButtonMenu);
                dialogButtonMenu.setText("");
                dialogButtonMenu.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Close the dialog:
                        //dialog.dismiss();

                        Intent intent = new Intent(context, MainActivity.class);
                        startActivity(intent);  // start the home view...
                    }
                });

                //Set the dialog menu button:
                Button dialogButtonRetry = (Button) dialog.findViewById(R.id.dialogButtonRetry);
                dialogButtonRetry.setText("");
                dialogButtonRetry.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    //Close the dialog:
                    //dialog.dismiss();

                    //Send the level ID and setup to the PlayActivity through Preferences file:
                    SharedPreferences preferences = getSharedPreferences("GamePrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("LevelID", levelID);
                    editor.putString("LevelSetup", levelSetup);
                    editor.commit();

                    Intent intent = new Intent(context, PlayActivity.class);
                    startActivity(intent);  // start the home view...
                    }
                });

                //Set the dialog next button:
                Button dialogButtonNext = (Button) dialog.findViewById(R.id.dialogButtonNext);
                dialogButtonNext.setText("");
                dialogButtonNext.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Close the dialog:
                        //dialog.dismiss();

                        if( !chkNextLevel ) {  // it is the last level of the challenge!
                            //Start the challenges activity:
                            Intent intent = new Intent(context, ChallengesActivity.class);
                            startActivity(intent);  // start the challenges activity...
                        }
                        else {  // chkNextLevel == true  // it is NOT the last level of the challenge...
                            //Start the new level:
                            Intent intent = new Intent(context, PlayActivity.class);
                            startActivity(intent);  // start the game...
                        }
                    }
                });

                //Show the dialog:
                dialog.show();
                /* --- END Dialog. --- */
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();

        //Timer:
        if (!mTimerStarted) {
            timer.schedule(new TimerTask() {
            @Override
            public void run() {
                TimerMethod();
            }

        }, 0, 1000);
            mTimerStarted = true;
        }
    }

    public void buttonClick(View view) {
        int id = view.getId();
        boolean chkNextLevel = false;

        switch(id) {
            case R.id.buttonNext:
                chkNextLevel = loadLevel(1);
                break;

            case R.id.buttonPrevious:
                chkNextLevel = loadLevel(-1);
                break;
        }

        if( !chkNextLevel ) {  // it is the last level of the challenge!
            //Start the challenges activity:
            Intent intent = new Intent(context, ChallengesActivity.class);
            finish();
            startActivity(intent);  // start the challenges activity...
        }
        else {  // chkNextLevel == true  // it is NOT the last level of the challenge...
            //Start the new level:
            Intent intent = new Intent(context, PlayActivity.class);
            finish();
            startActivity(intent);  // start the game...
        }
    }

    /**
     * Timer method.
     */
    private void TimerMethod() {
        //This method is called directly by the timer
        //and runs in the same thread as the timer.

        //We call the method that will work with the UI
        //through the runOnUiThread method.
        this.runOnUiThread(Timer_Tick);
    }
}