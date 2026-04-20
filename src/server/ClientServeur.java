package server;

import dao.Amis;
import dao.Groupes;
import dao.Messages;
import dao.Users;
import model.Groupe;
import model.Utilisateur;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;


public class ClientServeur implements Runnable {

    private final Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Utilisateur currentUser;

    private final Users userDAO = new Users();
    private final Amis friendDAO = new Amis();
    private final Messages messageDAO = new Messages();
    private final GestionAmis friendHandler = new GestionAmis();
    private final GestionGroupes groupHandler = new GestionGroupes();
    private final Groupes groupDAO = new Groupes();

    public ClientServeur(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("[Serveur] Nouveau client connecté : " + socket.getInetAddress());

            String line;
            while ((line = in.readLine()) != null) {
                gererCommande(line.trim());
            }
        } catch (IOException e) {
            System.out.println("[Serveur] Client déconnecté : " + e.getMessage());
        } finally {
            disconnect();
        }
    }

    private void gererCommande(String line) {
        if (line.isEmpty()) return;

        String[] parts = line.split("\\|", -1);
        String command = parts[0].toUpperCase();

        switch (command) {
            case "LOGIN" -> gererConnexion(parts);
            case "REGISTER" -> gererInscription(parts);
            case "LOGOUT" -> disconnect();
            case "LIST_USERS" -> handleListUsers();
            case "LIST_FRIENDS" -> faireListeAmis();
            case "LIST_PENDING_REQUESTS" -> faireListeAttente();
            case "FRIEND_REQUEST" -> gererDemandeAmi(parts);
            case "FRIEND_ACCEPT" -> gererAcceptAmi(parts);
            case "FRIEND_DECLINE" -> gererRefusAmi(parts);
            case "PRIVATE_MSG" -> gererPrive(parts);
            case "CREATE_GROUP" -> gererCreationGroupe(parts);
            case "ADD_TO_GROUP" -> gererAjoutGroupe(parts);
            case "GROUP_MSG" -> gererGroupe(parts);
            case "LIST_GROUPS" -> handleListGroups();
            default -> out.println("ERROR|Commande inconnue : " + command);
        }
    }

    private void gererConnexion(String[] parts) {
        if (parts.length < 3) { out.println("ERROR|Paramètres manquants."); return; }
        try {
            Utilisateur user = userDAO.connecter(parts[1], parts[2]);
            if (user == null) {
                out.println("ERROR|Identifiants incorrects.");
                return;
            }
            currentUser = user;
            userDAO.setStatut(user.getId(), "online");
            currentUser.setStatut("online");
            NotifServeur.ajouterClient(user.getId(), out);

            out.println("OK|LOGIN|" + user.getId() + "|" + user.getUsername());
            NotifServeur.livrerNotifAttente(user.getId(), out);
            broadcastStatusToFriends("online");
            System.out.println("[Auth] Connecté : " + user.getUsername());
        } catch (SQLException e) {
            out.println("ERROR|Erreur serveur.");
        }
    }

    private void gererInscription(String[] parts) {
        if (parts.length < 3) { out.println("ERROR|Paramètres manquants."); return; }
        try {
            boolean ok = userDAO.inscrire(parts[1], parts[2]);
            if (ok) out.println("OK|REGISTERED");
            else out.println("ERROR|Ce nom d'utilisateur est déjà pris.");
        } catch (SQLException e) {
            out.println("ERROR|Erreur serveur.");
        }
    }

    private void gererPrive(String[] parts) {
        if (!isAuthenticated()) return;
        if (parts.length < 3) { out.println("ERROR|Paramètres manquants."); return; }

        String targetUsername = parts[1];
        String content = parts[2];

        try {
            int targetId = userDAO.getIdParNom(targetUsername);
            if (targetId == -1) {
                out.println("ERROR|Utilisateur '" + targetUsername + "' introuvable.");
                return;
            }

            if (!friendDAO.sontAmis(currentUser.getId(), targetId)) {
                out.println("ERROR|NOT_FRIENDS|Vous devez être ami avec " + targetUsername + " pour lui envoyer un message.");
                return;
            }

            String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date());
            messageDAO.sauverPrive(currentUser.getId(), targetId, content);

            String msgLine = "PRIVATE_MSG|" + currentUser.getUsername() + "|" + content + "|" + timestamp;
            boolean delivered = NotifServeur.envoyerDirect(targetId, msgLine);
            if (!delivered) {
                NotifServeur.envoyerNotif(targetId, "NEW_MESSAGE", currentUser.getUsername() + " vous a envoyé un message.");
            }
            out.println("OK|MSG_SENT|" + targetUsername + "|" + timestamp);
        } catch (SQLException e) {
            out.println("ERROR|Erreur serveur.");
        }
    }

    private void gererGroupe(String[] parts) {
        if (!isAuthenticated()) return;
        if (parts.length < 3) { out.println("ERROR|Paramètres manquants."); return; }

        try {
            int groupId = Integer.parseInt(parts[1]);
            String content = parts[2];

            if (!groupDAO.estMembre(groupId, currentUser.getId())) {
                out.println("ERROR|Vous n'êtes pas membre de ce groupe.");
                return;
            }

            String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new java.util.Date());
            messageDAO.sauverGroupe(currentUser.getId(), groupId, content);

            String groupName = findGroupName(groupId);
            String msgLine = "GROUP_MSG|" + groupId + "|" + groupName + "|" + currentUser.getUsername() + "|" + content + "|" + timestamp;

            List<Utilisateur> members = groupDAO.getMembres(groupId);
            for (Utilisateur member : members) {
                if (member.getId() != currentUser.getId()) {
                    boolean delivered = NotifServeur.envoyerDirect(member.getId(), msgLine);
                    if (!delivered) {
                        NotifServeur.envoyerNotif(member.getId(), "NEW_GROUP_MESSAGE", currentUser.getUsername() + " a envoyé un message dans le groupe " + groupName);
                    }
                }
            }

            out.println("OK|GROUP_MSG_SENT|" + groupId + "|" + timestamp);
        } catch (NumberFormatException e) {
            out.println("ERROR|ID de groupe invalide.");
        } catch (SQLException e) {
            out.println("ERROR|Erreur serveur.");
        }
    }

    private void gererDemandeAmi(String[] parts) {
        if (!isAuthenticated()) return;
        if (parts.length < 2) { out.println("ERROR|Paramètres manquants."); return; }
        friendHandler.faireDemande(currentUser.getId(), currentUser.getUsername(), parts[1], out);
    }

    private void gererAcceptAmi(String[] parts) {
        if (!isAuthenticated()) return;
        if (parts.length < 2) { out.println("ERROR|Paramètres manquants."); return; }
        try {
            int requestId = Integer.parseInt(parts[1]);
            friendHandler.faireAccept(currentUser.getId(), currentUser.getUsername(), requestId, out);
        } catch (NumberFormatException e) {
            out.println("ERROR|ID de demande invalide.");
        }
    }

    private void gererRefusAmi(String[] parts) {
        if (!isAuthenticated()) return;
        if (parts.length < 2) { out.println("ERROR|Paramètres manquants."); return; }
        try {
            int requestId = Integer.parseInt(parts[1]);
            friendHandler.faireRefus(currentUser.getId(), currentUser.getUsername(), requestId, out);
        } catch (NumberFormatException e) {
            out.println("ERROR|ID de demande invalide.");
        }
    }

    private void faireListeAttente() {
        if (!isAuthenticated()) return;
        friendHandler.faireListeAttente(currentUser.getId(), out);
    }

    private void faireListeAmis() {
        if (!isAuthenticated()) return;
        friendHandler.faireListeAmis(currentUser.getId(), out);
    }

    private void gererCreationGroupe(String[] parts) {
        if (!isAuthenticated()) return;
        if (parts.length < 2) { out.println("ERROR|Paramètres manquants."); return; }
        groupHandler.handleCreate(currentUser.getId(), currentUser.getUsername(), parts[1], out);
    }

    private void gererAjoutGroupe(String[] parts) {
        if (!isAuthenticated()) return;
        if (parts.length < 3) { out.println("ERROR|Paramètres manquants."); return; }
        try {
            int groupId = Integer.parseInt(parts[1]);
            groupHandler.handleAddMember(currentUser.getId(), currentUser.getUsername(), groupId, parts[2], out);
        } catch (NumberFormatException e) {
            out.println("ERROR|ID de groupe invalide.");
        }
    }

    private void handleListUsers() {
        if (!isAuthenticated()) return;
        try {
            List<Utilisateur> users = userDAO.getTous();
            StringBuilder sb = new StringBuilder("USER_LIST|");
            for (Utilisateur u : users) {
                if (u.getId() != currentUser.getId()) {
                    sb.append(u.getUsername()).append(":").append(u.getStatus()).append(",");
                }
            }
            if (sb.charAt(sb.length() - 1) == ',') sb.setLength(sb.length() - 1);
            out.println(sb.toString());
        } catch (SQLException e) {
            out.println("ERROR|Erreur serveur.");
        }
    }

    private void handleListGroups() {
        if (!isAuthenticated()) return;
        try {
            List<Groupe> groups = groupDAO.getGroupesUser(currentUser.getId());
            StringBuilder sb = new StringBuilder("GROUP_LIST|");
            for (Groupe g : groups) {
                sb.append(g.getId()).append(":").append(g.getName()).append(",");
            }
            if (sb.charAt(sb.length() - 1) == ',') sb.setLength(sb.length() - 1);
            out.println(sb.toString());
        } catch (SQLException e) {
            out.println("ERROR|Erreur serveur.");
        }
    }

    private String findGroupName(int groupId) throws SQLException {
        for (Groupe g : groupDAO.getGroupesUser(currentUser.getId())) {
            if (g.getId() == groupId) return g.getName();
        }
        return "Groupe #" + groupId;
    }

    private boolean isAuthenticated() {
        if (currentUser == null) {
            out.println("ERROR|Vous devez être connecté pour effectuer cette action.");
            return false;
        }
        return true;
    }

    private void broadcastStatusToFriends(String status) {
        if (currentUser == null) return;
        try {
            List<Utilisateur> friends = friendDAO.getAmis(currentUser.getId());
            String statusMsg = "USER_STATUS|" + currentUser.getUsername() + "|" + status;
            for (Utilisateur friend : friends) {
                NotifServeur.envoyerDirect(friend.getId(), statusMsg);
            }
        } catch (SQLException e) {
            System.err.println("[ClientServeur] Erreur broadcast status : " + e.getMessage());
        }
    }

    private void disconnect() {
        if (currentUser != null) {
            String username = currentUser.getUsername();
            int userId = currentUser.getId();
            try {
                userDAO.setStatut(userId, "offline");
            } catch (SQLException ignored) {}
            NotifServeur.retirerClient(userId);
            broadcastStatusToFriends("offline");
            System.out.println("[Auth] Déconnecté : " + username);
            currentUser = null;
        }
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (!socket.isClosed()) socket.close();
        } catch (IOException ignored) {}
    }
}
