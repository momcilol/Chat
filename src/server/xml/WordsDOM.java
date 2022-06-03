package server.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordsDOM {

    public final String filename;
    private Document document;

    private Map<String, List<String>> wordMap;

    public WordsDOM() {
        this.filename = "res/bad_words.xml";
        loadDocument();
        this.wordMap = getWords();
    }

    public WordsDOM(String filename) {
        this.filename = filename;
        loadDocument(filename);
    }

    public static void main(String[] args) {
        WordsDOM app = new WordsDOM();
        app.loadDocument();
        app.printWords();
        app.addWord("Milos", "drug");
        app.saveDocument();
    }

    /**
     * Overloading method of {@link #loadDocument(String)}
     */
    public void loadDocument() {
        loadDocument(filename);
    }

    /**
     * Loads XML document from {@code filename}
     *
     */
    public void loadDocument(String filename) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            this.document = builder.parse(filename);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
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
     *
     */
    public void saveDocument(String filename) {
        try {
            DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();

            DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
            LSSerializer writer = impl.createLSSerializer();

//            System.out.println(writer.writeToString(document));

            String content = writer.writeToString(document);

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(filename), StandardCharsets.UTF_16);
            outputStreamWriter.write(content);
            outputStreamWriter.close();

        } catch (ClassCastException | ClassNotFoundException | InstantiationException
                 | IllegalAccessException | IOException ex) {
            ex.printStackTrace();
        }

    }

    /**
     * Returns a map of all words in the document,
     * where key is nickname of client that have added that word.
     *
     * @return {@code Map<String, List<String>>}
     */
    public Map<String, List<String>> getWords() {
        if (this.wordMap != null) return this.wordMap;

        this.wordMap = new HashMap<>();
        updateMap();

        return this.wordMap;
    }

    private void updateMap() {
        Element root = document.getDocumentElement();
        NodeList words = root.getElementsByTagName("word");

        for (int i = 0; i < words.getLength(); i++) {

            Element word = (Element) words.item(i);
            String nickname = word.getAttribute("nickname");

            if (!this.wordMap.containsKey(word.getAttribute(nickname)))
                this.wordMap.put(nickname, new ArrayList<>());

            wordMap.get(nickname).add(word.getTextContent());
        }
    }

    /**
     * If {@code word} is previously added by client with {@code nickname}, it will be removed.
     *
     * @param nickname Nickname of sender (in other parts of project referred as name)
     * @param word     Word that sender wants to remove
     */
    public synchronized void removeWord(String nickname, String word) {
        Element root = document.getDocumentElement();

        NodeList words = root.getElementsByTagName("word");

        for (int i = 0; i < words.getLength(); i++) {
            Element wordElem = (Element) words.item(i);
            if (wordElem.getAttribute("nickname").equals(nickname) && wordElem.getTextContent().equals(word)) {
                root.removeChild(words.item(i));
                break;
            }
        }

        saveDocument();
        updateMap();
    }

    /**
     * Adds {@code word} in xml document and saves file immediately.
     *
     * @param word
     * @param nickname
     */
    public void addWord(String nickname, String word) {
        if (containsWord(word)) return;

        Element root = document.getDocumentElement();

        Element wordElem = document.createElement("word");
        wordElem.setAttribute("nickname", nickname);
        wordElem.appendChild(document.createTextNode("\n\t\t" + word + "\n\t"));


        root.appendChild(document.createTextNode("\n\t"));
        root.appendChild(wordElem);
        root.appendChild(document.createTextNode("\n"));

        saveDocument();
        updateMap();
    }

    public boolean wordWrittenBy(String nickname, String word) {
        Map<String, List<String>> wordMap = getWords();
        return wordMap.containsKey(nickname) && wordMap.get(nickname).contains(word);
    }

    public boolean containsWord(String word) {
        return this.wordMap.values().stream().flatMap(list -> list.stream())
                .anyMatch(s -> s.equalsIgnoreCase(word));
    }

    private void printWords() {

        Element root = document.getDocumentElement();

        NodeList nl = root.getElementsByTagName("word");
        for (int i = 0; i < nl.getLength(); i++) {
            Element word = (Element) nl.item(i);
            System.out.println(word.getAttribute("nickname") + ": " + word.getTextContent().trim());
        }

    }
}
