package server.xml;

import java.io.IOException;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class WordsSAX extends DefaultHandler implements IWordsXML {

    private String filename;

    private Map<String, List<String>> wordMap = new HashMap<>();

    private Stack<String> path = new Stack<>();
    private String name;

    public WordsSAX() {
        this("res/bad_words.xml");
    }

    public WordsSAX(String filename) {
        this.filename = filename;
        parseDocument(filename);
    }

    public void parseDocument() {
        parseDocument(this.filename);
    }

    public void parseDocument(String filename) {

        // Get a factory
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {

            // Get a new instance of parser
            SAXParser parser = factory.newSAXParser();

            // Parse the file and also register this class for call backs
            parser.parse(filename, this);

            // Print any errors
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        // Add new note to the path
        path.push(qName);

        // Set movie fields
        if (qName.equals("movie")) {
            name = attributes.getValue("nickname");
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        path.pop();

        if (qName.equals("word")) {
            name = null;
        }

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

        // Get current node and text content
        String node = path.peek();
        String text = new String(ch, start, length);

        // Print title and year
        if (node.equals("word")) {
            if (!this.wordMap.containsKey(name))
                this.wordMap.put(name, new ArrayList<>());

            wordMap.get(name).add(text);
        }

    }

    public static void main(String[] args) {
        WordsSAX app = new WordsSAX();
        app.parseDocument();
    }

    @Override
    public boolean addWord(String nickname, String word) {
        return false;
    }

    @Override
    public boolean removeWord(String nickname, String word) {
        return false;
    }

    @Override
    public Map<String, List<String>> getWords() {
        return null;
    }
}