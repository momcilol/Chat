package client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatListener extends Remote {
    void receiveMessage(String name, String message) throws RemoteException;
}
