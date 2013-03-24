package net.paolorovelli.IceJam;

import android.content.res.AssetManager;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * XML Parser.
 *
 * @author Paolo Rovelli
 * @date 03/24/2013
 * @time 9:55AM
 */
public class Parser {
    String challenge;
    String level;
    String setup;

    /**
     * Class constructor.
     */
    public Parser() {
        this.challenge = new String();
        this.level = new String();
        this.setup = new String();
    }


    /**
     * Parse the challenges.
     *
     * @param is
     * @return
     * @throws IOException
     */
    public List<String> parseChallenges(InputStream is) throws IOException {
        List<String> challenges = new ArrayList<String>();

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            //Debug:
            //System.out.println( doc.getDocumentElement().getNodeName() );

            NodeList nList = doc.getElementsByTagName("challenge");
            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);

                if ( nNode.getNodeType() == Node.ELEMENT_NODE ) {
                    Element eElement = (Element) nNode;

                    //Read the elements:
                    String challengeName = eElement.getElementsByTagName("name").item(0).getTextContent();  // eElement.getAttribute("name")
                    String challengeFile = eElement.getElementsByTagName("puzzles").item(0).getTextContent();  // eElement.getAttribute("file")

                    //Debug:
                    System.out.println("[PARSER] Challenge name: " + challengeName);
                    System.out.println("[PARSER] Challenge file: " + challengeFile);

                    challenges.add( challengeName );
                }
            }

            return challenges;
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Parse the levels (puzzles).
     *
     * @param is
     * @return
     * @throws IOException
     */
    public List<String> parseLevels(InputStream is) throws IOException {
        List<String> levels = new ArrayList<String>();

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            //Debug:
            //System.out.println( doc.getDocumentElement().getNodeName() );

            NodeList nList = doc.getElementsByTagName("puzzle");
            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);

                if ( nNode.getNodeType() == Node.ELEMENT_NODE ) {
                    Element eElement = (Element) nNode;

                    //Read the elements:
                    String puzzleID = eElement.getAttribute("id");
                    String puzzleSetup = eElement.getElementsByTagName("setup").item(0).getTextContent();

                    //Debug:
                    System.out.println("[PARSER] Puzzle id: " + puzzleID);
                    //System.out.println("[PARSER] Puzzle setup: " + puzzleSetup);

                    levels.add( puzzleID );
                }
            }

            return levels;
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
