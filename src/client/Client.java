package client;

import server.ChatServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Client {

    private String nickname;
    private ChatServer chatServer;
    private ChatListenerImpl chatListener;

    public Client() {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try {
            System.out.println("Napisite svoje ime:");
            this.nickname = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        connectToServer();

        try {
            this.chatListener = new ChatListenerImpl(this, this.chatServer);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        run();

        System.exit(0);
    }

    private void run() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            String message;
            while (true) {
                message = br.readLine();
                if (message == null || message.equals("")) return;
                this.chatServer.sendMessage(nickname, message);
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                this.chatListener.terminate();
            } catch (RemoteException ex) {
                e.printStackTrace();
            }
            System.exit(-1);
        }
    }

    private void connectToServer() {
        String name = "//localhost:1099/chat";
        try {
            this.chatServer = (ChatServer) Naming.lookup(name);
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }
    }

    public void receiveMessage(String name, String message) {
        System.out.println(name + ": " + message + "\n>>");
    }

    public static void main(String[] args) {

        Client client = new Client();
        client.start();
    }
}
