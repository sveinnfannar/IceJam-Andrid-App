package net.paolorovelli.IceJam;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.paolorovelli.IceJam.GameLogic;


public class TrafficJam {
    private static Random m_rand = new Random();

    public static void main(String[] args) {
        System.out.println( "Welcome to TrafficJam!");

        List<String> puzzles = new ArrayList<String>();
        readChallenge( "./challenge.xml", puzzles );

        GameLogic game = new GameLogic();

        for( String puzzle : puzzles ) {
            boolean ok = game.setup( puzzle );
            System.out.println("OK = " + ok);
            if( ok ) {
                System.out.print( game.toString() );
                List<GameLogic.Action> actions = game.getActions();
                for( GameLogic.Action action : actions ) {
                    System.out.print( " " + action.toString() );
                }
                System.out.println();
                if( !actions.isEmpty() ) {
                    GameLogic.Action action = actions.get( m_rand.nextInt( actions.size() ) );
                    game.makeAction( action );
                    System.out.println( "Made action " + action.toString() );
                    System.out.print( game.toString() );
                }
            }
        }
    }


    private static void readChallenge( String filename, List<String> puzzles ) {
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
