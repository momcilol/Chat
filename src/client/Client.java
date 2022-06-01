package client;

import server.ChatServer;
import server.ChatServerImpl;

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
    private ChatListener chatListener;

    public Client() {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try {
            this.nickname = br.readLine();
        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
        }
    }

    public void start() {
        connectToServer();

        try {
            this.chatListener = new ChatListenerImpl(this, this.chatServer);
        } catch (RemoteException e) {
            System.err.println(e.getLocalizedMessage());
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
            System.err.println(e.getLocalizedMessage());
            System.exit(-1);
        }
    }

    private void connectToServer() {
        String name = "//localhost:1099/chat";
        try {
            this.chatServer = (ChatServer) Naming.lookup(name);
        } catch (NotBoundException e) {
            System.err.println(e.getLocalizedMessage());
        } catch (MalformedURLException e) {
            System.err.println(e.getLocalizedMessage());
        } catch (RemoteException e) {
            System.err.println(e.getLocalizedMessage());
        }
    }

    public void receiveMessage(String name, String message) {
        System.out.println(name + ": " + message);
    }

    public static void main(String[] args) {

        Client client = new Client();
        client.start();
    }
}
