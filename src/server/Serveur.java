package server;

import dao.Base;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;


public class Serveur {

    private static final int PORT = 12345;

    public static void main(String[] args) {
        try {
            Base.initBase();
        } catch (SQLException e) {
            System.err.println("[Serveur] Impossible d'initialiser la BDD : " + e.getMessage());
            return;
        }

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("[Serveur] Démarré sur le port " + PORT);
            System.out.println("[Serveur] En attente de connexions...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                Thread clientThread = new Thread(new ClientServeur(clientSocket));
                clientThread.setDaemon(true);
                clientThread.start();
            }
        } catch (IOException e) {
            System.err.println("[Serveur] Erreur : " + e.getMessage());
        }
    }
}
