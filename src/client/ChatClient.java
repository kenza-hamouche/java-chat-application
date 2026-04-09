package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {

    private static final String HOST = "localhost";
    private static final int PORT = 1234;

    public void startClient() {

        try {
            // 🔹 Connexion au serveur
            Socket socket = new Socket(HOST, PORT);

            System.out.println("Connecté au serveur");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            PrintWriter writer = new PrintWriter(
                    socket.getOutputStream(), true
            );

            BufferedReader console = new BufferedReader(
                    new InputStreamReader(System.in)
            );

            // 🔹 Thread pour recevoir les messages
            new Thread(() -> {
                try {
                    String msg;
                    while ((msg = reader.readLine()) != null) {
                        System.out.println(msg);
                    }
                } catch (IOException e) {
                    System.out.println("Connexion perdue");
                }
            }).start();

            // 🔹 Envoyer les messages
            String input;

            while ((input = console.readLine()) != null) {
                writer.println(input);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}