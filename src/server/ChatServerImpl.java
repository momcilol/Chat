package server;

import client.ChatListener;
import org.jetbrains.annotations.NotNull;
import server.xml.IWordsXML;
import server.xml.WordsDOM;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChatServerImpl extends UnicastRemoteObject implements ChatServer {

    private Listeners listeners;

    private IWordsXML badWordsRepo;

    public ChatServerImpl(String xmlReader) throws RemoteException {
        this.listeners = new Listeners();
        this.badWordsRepo = new WordsDOM();
    }

    /**
     * Notifies to listeners if someone sent message, while hiding
     * bad words and/or adding/removing bad words in repository
     *
     * @param name    Name of sender
     * @param message Message from sender
     */
    @Override
    public void sendMessage(String name, String message) throws RemoteException {
        if (parseCommand(name, message).equals("")) return;

        String mess = hideBadWords(message);

        this.listeners.notifyListeners(name, message);
    }

    private String hideBadWords(String message) throws RemoteException {
        String regBad = badWordsRepo.getWords().values().stream()
                .flatMap(List::stream)
                .collect(Collectors.joining("|", "(", ")"));

        return message.replaceAll(regBad, "*****");
    }

    private String parseCommand(String name, @NotNull String message) throws RemoteException {
        String[] tokens = message.split(" ");

        if (tokens[0].startsWith("/+/")) {
            if (badWordsRepo.addWord(name, tokens[0] = tokens[0].replace("/+/", ""))) {
                this.listeners.commandResponse(name, "Uspesno dodata rec: " + tokens[0]);
            } else {
                this.listeners.commandResponse(name, "Neuspesno dodavanje reci: " + tokens[0]);
            }
            return "";
        } else if (tokens[0].startsWith("/-/")) {
            if (badWordsRepo.removeWord(name, tokens[0] = tokens[0].replace("/-/", ""))) {
                this.listeners.commandResponse(name, "Uspesno uklonjena rec " + tokens[0]);
            } else {
                this.listeners.commandResponse(name, "Neuspesno brisanje reci: " + tokens[0]);
            }
            return "";
        }

        return Arrays.stream(tokens).collect(Collectors.joining(" ", "", ""));
    }

    @Override
    public void addListener(String name, ChatListener chatListener) throws RemoteException {
        this.listeners.addChatListener(name, chatListener);
    }

    @Override
    public void removeListener(String name) throws RemoteException {
        this.listeners.removeChatListener(name);
    }

    public static void main(String[] args) {

        String name = "chat";

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try {
            LocateRegistry.createRegistry(1099);

            System.out.println("SAX[s] or DOM[otherwise]?");
            String xmlReader = br.readLine();

            ChatServerImpl chatServer = new ChatServerImpl(xmlReader);
            Naming.rebind(name, chatServer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Server working...");
    }
}
