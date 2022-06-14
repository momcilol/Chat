package server.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class WordsSAX extends DefaultHandler implements IWordsXML {

    private final String filename;

    private final Map<String, List<String>> wordMap;

    private final Stack<String> path = new Stack<>();
    private String name;

    public WordsSAX() {
        this("res/bad_words.xml");
    }

    public WordsSAX(String filename) {
        this.filename = filename;
        this.wordMap = new HashMap<>();
        parseDocument(filename);
    }

    public static void main(String[] args) {
        WordsSAX app = new WordsSAX();
        app.parseDocument();
    }

    /**
     * Overloading method of {@link #parseDocument(String)}
     */
    public void parseDocument() {
        parseDocument(this.filename);
    }

    /**
     * Parse XML file from {@code filename}
     *
     * @param filename
     */
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

    /**
     * Overloading method of {@link #saveDocument(String)}
     */
    public void saveDocument() {
        saveDocument(filename);
    }

    /**
     * Saves XML document to {@code filename}
     */
    public void saveDocument(String filename) {
        try {
            String content = this.wordMap.entrySet().stream().map(e -> e.getValue().stream().collect(Collectors.joining("</word>\n\t<word nickname =\"" + e.getKey() + "\">", "<word nickname =\"" + e.getKey() + "\">", "</word>"))).collect(Collectors.joining("\n\t", """
                    <?xml version="1.0" encoding="UTF-16"?><!DOCTYPE list SYSTEM "bad_words.dtd">
                    <list>
                    \t""", "\n</list>"));

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(filename), StandardCharsets.UTF_16);
            outputStreamWriter.write(content);
            outputStreamWriter.close();

        } catch (ClassCastException | IOException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        // Add new note to the path
        path.push(qName);

        // Set movie fields
        if (qName.equals("word")) {
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
            this.wordMap.putIfAbsent(name, new ArrayList<>());
            this.wordMap.get(name).add(text);
        }
    }

    /**
     * Adds {@code word} in xml document and saves file immediately.
     *
     * @param word     Word that is being added
     * @param nickname Who adds {@code word}
     * @return boolean Successful
     */
    @Override
    public boolean addWord(String nickname, String word) {
        if (containsWord(word)) return false;

        this.wordMap.putIfAbsent(nickname, new ArrayList<>());
        this.wordMap.get(nickname).add(word);

        saveDocument();
        return true;
    }

    /**
     * If {@code word} is previously added by client with {@code nickname}, it will be removed.
     *
     * @param nickname Nickname of sender (in other parts of project referred as name)
     * @param word     Word that sender wants to remove
     * @return boolean Successful
     */
    @Override
    public boolean removeWord(String nickname, String word) {
        if (!containsWord(word)) return false;
        if (!wordWrittenBy(nickname, word)) return false;

        this.wordMap.get(nickname).remove(word);

        saveDocument();
        return true;
    }

    /**
     * Returns a map of all words in the document,
     * where key is nickname of client that have added that word.
     *
     * @return {@code Map<String, List<String>>}
     */
    @Override
    public Map<String, List<String>> getWords() {
        return this.wordMap;
    }

    /**
     * Checks if the {@code word} is written by {@code nickname}
     *
     * @param word
     * @param nickname
     */
    public boolean wordWrittenBy(String nickname, String word) {
        Map<String, List<String>> wordMap = getWords();
        return wordMap.containsKey(nickname) && wordMap.get(nickname).contains(word);
    }

    /**
     * Checks if the {@code word} is in Document
     *
     * @param word
     */
    public boolean containsWord(String word) {
        return this.wordMap.values().stream().flatMap(List::stream).anyMatch(s -> s.equalsIgnoreCase(word));
    }
}