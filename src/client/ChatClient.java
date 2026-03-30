package client;

import java.io.IOException;
import java.net.Socket;

public class ChatClient {
    private static final String HOST = "localhost";
    private static final int PORT = 1234;

    public void startClient() {
        try {
            Socket socket = new Socket(HOST, PORT);
            System.out.println("Connecté au serveur !");
        } catch (IOException e) {
            System.out.println("Erreur côté client !");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ChatClient client = new ChatClient();
        client.startClient();
    }
}