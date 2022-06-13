package server;

import client.ChatListener;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Listeners {

    private Map<String, ChatListener> chatListeners;

    public Listeners() {
        this.chatListeners = new HashMap<>();
    }

    public void addChatListener(String name, ChatListener chatListener) {
        this.chatListeners.put(name, chatListener);
    }

    public void removeChatListener(String name) {
        this.chatListeners.remove(name);
    }

    public void deleteChatListeners() {
        this.chatListeners.clear();
    }


    public int countChatListeners() {
        return this.chatListeners.size();
    }

    /**
     * Notifies to listeners if someone sent message
     *
     * @param name    Name of sender
     * @param message Message from sender
     */
    public void notifyListeners(String name, String message) {
        this.chatListeners.entrySet().stream()
                .filter(e -> !e.getKey().equals(name))
                .map(Map.Entry::getValue)
                .forEach(v -> {
                    try {
                        v.receiveMessage(name, message);
                    } catch (RemoteException re) {
                        System.err.println("Chat.server.Listeners.notifyListeners(): " + re);
                        re.printStackTrace();
                    }
                });
    }

    public void commandResponse(String name, String message) {
        try {
            this.chatListeners.get(name).receiveMessage(name, message);
        } catch (RemoteException re) {
            System.err.println("Chat.server.Listeners.notifyListeners(): " + re);
            re.printStackTrace();
        }
    }


}
