package server;

import client.ChatListener;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class ChatServerImpl extends UnicastRemoteObject implements ChatServer {

    private Listeners listeners;

    public ChatServerImpl() throws RemoteException {
        this.listeners = new Listeners();
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
