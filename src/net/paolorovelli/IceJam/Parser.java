package net.paolorovelli.IceJam;

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
 * @author Paolo Rovelli and Sveinn Fannar Kristjánsson.
 * @date 03/24/2013
 * @time 9:55AM
 */
public class Parser {
    /**
     * Class constructor.
     */
    public Parser() {}


    /**
     * Parse the challenges.
     *
     * @param is
     * @throws IOException
     */
    public void parseChallenges(InputStream is, List<String> names, List<String> files) throws IOException {
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
                    names.add( eElement.getElementsByTagName("name").item(0).getTextContent() );  // eElement.getAttribute("name")
                    files.add( eElement.getElementsByTagName("puzzles").item(0).getTextContent() );  // eElement.getAttribute("file")

                    //Debug:
                    //System.out.println("[PARSER] Challenge name: " + eElement.getElementsByTagName("name").item(0).getTextContent());
                    //System.out.println("[PARSER] Challenge file: " + eElement.getElementsByTagName("puzzles").item(0).getTextContent());
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Parse the levels (puzzles).
     *
     * @param is
     * @param names
     * @param setups
     * @throws IOException
     */
    public void parseLevels(InputStream is, List<String> names, List<String> setups, List<Integer> sizes) throws IOException {
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
                    names.add(eElement.getAttribute("id"));
                    setups.add( eElement.getElementsByTagName("setup").item(0).getTextContent() );
                    sizes.add( Integer.parseInt( eElement.getAttribute("size") ) );

                    //Debug:
                    //System.out.println("[PARSER] Puzzle level: " + eElement.getAttribute("id"));
                    //System.out.println("[PARSER] Puzzle setup: " + eElement.getElementsByTagName("setup").item(0).getTextContent());
                    //System.out.println("[PARSER] Puzzle size: " + eElement.getAttribute("size"));
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Parse a custom level (puzzle).
     *
     * @param is
     * @param level
     * @return the setup.
     * @throws IOException
     */
    public String parseTheLevel(InputStream is, String level, List<Integer> sizes) throws IOException {
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

                    if( eElement.getAttribute("id").equals( level ) ) {
                        //Debug:
                        //System.out.println("[PARSER] Puzzle level: " + eElement.getAttribute("id"));
                        //System.out.println("[PARSER] Puzzle setup: " + eElement.getElementsByTagName("setup").item(0).getTextContent());
                        //System.out.println("[PARSER] Puzzle size: " + eElement.getAttribute("size"));

                        sizes.add( Integer.parseInt( eElement.getAttribute("size") ) );

                        return eElement.getElementsByTagName("setup").item(0).getTextContent();
                    }
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
