package server;

import client.ChatListener;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatServer extends Remote {

    void sendMessage(String name, String message) throws RemoteException;

    void addListener(ChatListener chatListener) throws RemoteException;

    void removeListener(ChatListener chatListener) throws RemoteException;


}
