package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
    private static final int PORT = 1234;

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Serveur démarré sur le port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Nouveau client connecté !");

                ClientHandler clientHandler = new ClientHandler(socket);
                new Thread(clientHandler).start();
            }

        } catch (IOException e) {
            System.out.println("Erreur côté serveur !");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        server.startServer();
    }
}
