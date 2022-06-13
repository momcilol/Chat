package server;

import client.ChatListener;
import server.xml.WordsDOM;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

public class ChatServerImpl extends UnicastRemoteObject implements ChatServer {

    private Listeners listeners;

    private WordsDOM badWordsRepo;

    public ChatServerImpl() throws RemoteException {
        this.listeners = new Listeners();
        this.badWordsRepo = new WordsDOM();
    }

    /**
     * Notifies to listeners if someone sent message
     *
     * @param name
     *      Name of sender
     * @param message
     *      Message from sender
     */
    @Override
    public synchronized void sendMessage(String name, String message) throws RemoteException {
        if (message.startsWith("/+/"))
            badWordsRepo.addWord(name, message.split(" ")[0].replace("/+/", ""));
        else if (message.startsWith("/-/"))
            badWordsRepo.removeWord(name, message.split(" ")[0].replace("/-/", ""));

        this.listeners.notifyListeners(name, message);
    }

    @Override
    public void addListener(ChatListener chatListener) throws RemoteException {
        this.listeners.addChatListener(chatListener);
    }

    @Override
    public void removeListener(ChatListener chatListener) throws RemoteException {
        this.listeners.removeChatListener(chatListener);
    }

    public static void main(String[] args) {

        String name = "chat";


        try {
            LocateRegistry.createRegistry(1099);
            ChatServerImpl chatServer = new ChatServerImpl();
            Naming.rebind(name,chatServer);
        } catch (RemoteException | MalformedURLException e) {
            e.printStackTrace();
        }

        System.out.println("Server working...");
    }
}
