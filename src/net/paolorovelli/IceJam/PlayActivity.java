package net.paolorovelli.IceJam;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: paolo
 * Date: 03/23/2013
 * Time: 9:57AM
 * To change this template use File | Settings | File Templates.
 */
public class PlayActivity extends Activity {
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
        mColorFirst = preferences.getInt("ColorFirst", Color.RED);
        mColorSecond = preferences.getInt("ColorSecond", Color.GREEN);

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
                System.out.println("MOVE +1");
                Integer moves = Integer.parseInt( mMovesView.getText().toString() );
                moves++;
                mMovesView.setText( moves.toString() );
            }
        });
    }


    private static void readPuzzleFromFile(String filename, List<String> puzzles) {
        try {
            File xmlFile = new File( filename );
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            System.out.println( doc.getDocumentElement().getNodeName() );
            NodeList nList = doc.getElementsByTagName("puzzle");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    System.out.println("Puzzle id   : " + eElement.getAttribute("id"));
                    String puzzleStr = eElement.getElementsByTagName("setup").item(0).getTextContent();
                    System.out.println("Puzzle setup: " + puzzleStr );
                    puzzles.add( puzzleStr );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}