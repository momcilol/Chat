package server.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordsDOM {

    private Document document;

    public WordsDOM(String filename) {
        this.document = loadDocument(filename);
    }

    private Document loadDocument(String filename) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(filename);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Reads from XML document, and returns map of all words in the document,
     * where key is nickname of client that has added that word.
     *
     * @return Map<String, List<String>>
     * */

    public Map<String, List<String>> getWords() {
        Map<String, List<String>> wordMap = new HashMap<>();

        Element root = document.getDocumentElement();
        NodeList words = root.getElementsByTagName("word");

        for (int i = 0; i < words.getLength(); i++) {

            Element word = (Element) words.item(i);
            String nickname = word.getAttribute("nickname");

            if (!wordMap.containsKey(word.getAttribute(nickname)))
                wordMap.put(nickname, new ArrayList<>());

            wordMap.get(nickname).add(word.getTextContent());
        }

        return wordMap;
    }


}
