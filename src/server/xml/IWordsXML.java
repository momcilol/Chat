package server.xml;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface IWordsXML {
    boolean addWord(String nickname, String word);

    boolean removeWord(String nickname, String word);

    Map<String, List<String>> getWords();
}
