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
            System.out.print("Napisite svoje ime: ");
            this.nickname = br.readLine();
            System.out.print(">>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNickname() {
        return nickname;
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
            message = br.readLine();
            while (message != null && !message.equals("")) {
                System.out.print(">>");
                this.chatServer.sendMessage(nickname, message);
                message = br.readLine();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                this.chatListener.terminate();
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
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
        System.out.print(name + ": " + message + "\n>>");
    }

    public static void main(String[] args) {

        Client client = new Client();
        client.start();
    }
}
