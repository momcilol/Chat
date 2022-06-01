package server;

import client.ChatListener;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class Listeners {

    private List<ChatListener> chatListeners;

    public Listeners() {
        this.chatListeners = new ArrayList<>();
    }

    public void addChatListener(ChatListener chatListener) {
        this.chatListeners.add(chatListener);
    }

    public void removeChatListener(ChatListener chatListener) {
        this.chatListeners.remove(chatListener);
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
     * @param name
     *      Name of sender
     * @param message
     *      Message from sender
     */
    public void notifyListeners(String name, String message) {
        for (ChatListener cl : chatListeners) {
            try {
                cl.receiveMessage(name, message);
            } catch (RemoteException re) {
                System.err.println("Chat.server.Listeners.notifyListeners(): " + re);
                re.printStackTrace();
            }
        }
    }


}
