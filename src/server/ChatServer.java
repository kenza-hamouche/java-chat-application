package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {

    // 🔹 Port du serveur
    private static final int PORT = 1234;

    // 🔹 Liste des clients connectés
    private List<ClientHandler> clients = new ArrayList<>();

    public void startServer() {

        System.out.println("Serveur en cours de démarrage...");

        try {
            // 🔹 Création du serveur (écoute sur un port)
            ServerSocket serverSocket = new ServerSocket(PORT);

            System.out.println("Serveur lancé sur le port " + PORT);

            // 🔹 Boucle infinie pour accepter plusieurs clients
            while (true) {

                // 🔹 Attendre un client
                Socket socket = serverSocket.accept();

                System.out.println("Nouveau client connecté");

                // 🔹 Créer un handler pour ce client
                ClientHandler clientHandler = new ClientHandler(socket, this);

                // 🔹 Ajouter à la liste
                clients.add(clientHandler);

                // 🔹 Lancer le thread (très important)
                new Thread(clientHandler).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 🔹 Envoyer un message à tous les clients
    public void broadcast(String message, ClientHandler sender) {

        for (ClientHandler client : clients) {

            // 🔸 On n’envoie pas au client qui a envoyé
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    // 🔹 Supprimer un client (déconnexion)
    public void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }
}