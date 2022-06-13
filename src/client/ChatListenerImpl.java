package client;

import server.ChatServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ChatListenerImpl extends UnicastRemoteObject implements ChatListener {

    private ChatServer chatServer;
    private Client client;

    public ChatListenerImpl(Client client, ChatServer chatServer) throws RemoteException {
        super();
        this.client = client;
        this.chatServer = chatServer;
        this.chatServer.addListener(this.client.getNickname() ,this);
    }

    public void terminate() throws RemoteException {
        this.chatServer.removeListener(this.client.getNickname());
    }

    @Override
    public void receiveMessage(String name, String message) throws RemoteException {
        this.client.receiveMessage(name, message);
    }
}
