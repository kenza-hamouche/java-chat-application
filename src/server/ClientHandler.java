package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Socket socket;
    private ChatServer server;

    private BufferedReader reader;
    private PrintWriter writer;

    private String username;

    public ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {

        try {
            // 🔹 Initialisation des flux
            reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );

            writer = new PrintWriter(socket.getOutputStream(), true);

            // 🔹 Demander le pseudo
            writer.println("Entrez votre pseudo :");

            username = reader.readLine();

            System.out.println(username + " connecté");

            // 🔹 Lire les messages en boucle
            String message;

            while ((message = reader.readLine()) != null) {

                System.out.println(username + " : " + message);

                // 🔹 Envoyer aux autres
                server.broadcast(username + " : " + message, this);
            }

        } catch (IOException e) {
            System.out.println("Client déconnecté");
        } finally {
            closeConnection();
        }
    }

    // 🔹 Envoyer message au client
    public void sendMessage(String message) {
        writer.println(message);
    }

    // 🔹 Fermer connexion
    private void closeConnection() {
        try {
            server.removeClient(this);

            if (socket != null) socket.close();
            if (reader != null) reader.close();
            if (writer != null) writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}