package net.paolorovelli.IceJam;

import android.app.Activity;
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
    private Parser parser = new Parser();
    private String levelID;
    private String levelSetup;
    DrawView mDrawView;
    TextView mMovesView;

    int mColorFirst;
    int mColorSecond;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.game );

        List<String> puzzles = new ArrayList<String>();

        try {
            InputStream file = getAssets().open( "challenge.xml" );
            //this.parser.parse( file );
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        GameLogic game = new GameLogic();

        for( String puzzle : puzzles ) {
            boolean ok = game.setup( puzzle );
            if( ok ) {
                System.out.print( game.toString() );
            }
        }

        mDrawView = (DrawView) findViewById( R.id.drawView );
        mMovesView = (TextView) findViewById( R.id.moves );

        //Read parameters from the last Activity through Intent:
        //Intent intent = getIntent();
        //mColorFirst = intent.getIntExtra("ColorFirst", Color.RED);
        //mColorSecond = intent.getIntExtra("ColorSecond", Color.GREEN);

        mColorFirst = Color.RED;
        mColorSecond = Color.GREEN;

        mDrawView.post(new Runnable() {
            @Override
            public void run() {
                mDrawView.addShape(mColorFirst);
                mDrawView.addShape(mColorSecond);
            }
        });

        mDrawView.setCustomEventHandler(new DrawEventHandler() {
            @Override
            public void onShapeMoved() {
                //TODO: ...
                Integer moves = Integer.parseInt( mMovesView.getText().toString() );
                moves++;
                mMovesView.setText( moves.toString() );
            }
        });
    }
}