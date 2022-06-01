package server;

import client.ChatListener;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class ChatServerImpl implements ChatServer {

    private Listeners listeners;

    public ChatServerImpl() {
        this.listeners = new Listeners();
    }

    /**
     *
     * */
    @Override
    public void sendMessage(String name, String message) throws RemoteException {
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
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
