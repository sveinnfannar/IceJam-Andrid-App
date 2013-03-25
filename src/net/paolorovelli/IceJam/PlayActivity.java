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
    DrawView mDrawView;
    TextView mMovesView;

    private int mColorFirst;
    private int mColorSecond;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.game );

        mDrawView = (DrawView) findViewById( R.id.drawView );
        mMovesView = (TextView) findViewById( R.id.moves );

        //Read parameters from the last Activity through Intent:
        //Intent intent = getIntent();
        //mColorFirst = intent.getIntExtra("ColorFirst", Color.RED);
        //mColorSecond = intent.getIntExtra("ColorSecond", Color.GREEN);

        //Read parameters from the last Activity through Preferences file:
        SharedPreferences preferences = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        mColorFirst = Color.RED;
        mColorSecond = Color.BLUE;

        mDrawView.post(new Runnable() {
            @Override
            public void run() {
                // TODO: Use CarFromString method from GameLogic
                mDrawView.setGameLogic(new GameLogic());
                mDrawView.addShape(new Shape(Shape.Orientation.Vertical, 0, 0, 3));
                mDrawView.addShape(new Shape(Shape.Orientation.Horizontal, 2, 2, 2));
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