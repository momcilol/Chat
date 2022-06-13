package server;

import client.ChatListener;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatServer extends Remote {

    void sendMessage(String name, String message) throws RemoteException;

    void addListener(String name, ChatListener chatListener) throws RemoteException;

    void removeListener(String name) throws RemoteException;


}
