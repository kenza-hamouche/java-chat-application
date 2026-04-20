package server;

import dao.Amis;
import dao.Users;
import model.DemandeAmi;
import model.Utilisateur;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;


public class GestionAmis {

    private final Amis friendDAO = new Amis();
    private final Users userDAO = new Users();

    public void faireDemande(int senderId, String senderUsername,
                                  String targetUsername, PrintWriter senderWriter) {
        try {
            int targetId = userDAO.getIdParNom(targetUsername);
            if (targetId == -1) {
                senderWriter.println("ERROR|Utilisateur '" + targetUsername + "' introuvable.");
                return;
            }
            if (targetId == senderId) {
                senderWriter.println("ERROR|Vous ne pouvez pas vous ajouter vous-même.");
                return;
            }

            int requestId = friendDAO.envoyerDemande(senderId, targetId);
            if (requestId == -1) {
                senderWriter.println("ERROR|Une relation ou une demande existe déjà.");
                return;
            }

            senderWriter.println("OK|FRIEND_REQUEST_SENT|" + targetUsername);
            NotifServeur.envoyerDirect(targetId, "FRIEND_REQUEST_RECEIVED|" + requestId + "|" + senderUsername);
            NotifServeur.envoyerNotif(targetId, "FRIEND_REQUEST", senderUsername + " vous a envoyé une demande d'amitié.");
        } catch (SQLException e) {
            senderWriter.println("ERROR|Erreur serveur : " + e.getMessage());
        }
    }

    public void faireAccept(int receiverId, String receiverUsername,
                                    int requestId, PrintWriter receiverWriter) {
        try {
            int senderId = friendDAO.getIdEnvoyeur(requestId);
            if (senderId == -1) {
                receiverWriter.println("ERROR|Demande introuvable.");
                return;
            }

            boolean ok = friendDAO.accepterDemande(requestId, receiverId);
            if (!ok) {
                receiverWriter.println("ERROR|Impossible d'accepter cette demande.");
                return;
            }

            receiverWriter.println("OK|FRIEND_ACCEPTED|" + requestId);
            NotifServeur.envoyerDirect(senderId, "FRIEND_ACCEPTED|" + receiverUsername);
            NotifServeur.envoyerNotif(senderId, "FRIEND_ACCEPTED", receiverUsername + " a accepté votre demande d'amitié.");
            envoyerListesAmis(receiverId, senderId);
        } catch (SQLException e) {
            receiverWriter.println("ERROR|Erreur serveur : " + e.getMessage());
        }
    }

    public void faireRefus(int receiverId, String receiverUsername,
                                     int requestId, PrintWriter receiverWriter) {
        try {
            int senderId = friendDAO.getIdEnvoyeur(requestId);
            boolean ok = friendDAO.refuserDemande(requestId, receiverId);
            if (!ok) {
                receiverWriter.println("ERROR|Impossible de refuser cette demande.");
                return;
            }

            receiverWriter.println("OK|FRIEND_DECLINED|" + requestId);
            if (senderId != -1) {
                NotifServeur.envoyerNotif(senderId, "FRIEND_DECLINED", receiverUsername + " a refusé votre demande.");
            }
        } catch (SQLException e) {
            receiverWriter.println("ERROR|Erreur serveur : " + e.getMessage());
        }
    }

    public void faireListeAttente(int userId, PrintWriter writer) {
        try {
            List<DemandeAmi> pending = friendDAO.getDemandes(userId);
            if (pending.isEmpty()) {
                writer.println("PENDING_REQUESTS|");
                return;
            }
            StringBuilder sb = new StringBuilder("PENDING_REQUESTS|");
            for (DemandeAmi req : pending) {
                sb.append(req.getId()).append(":").append(req.getSenderUsername()).append(",");
            }
            sb.setLength(sb.length() - 1);
            writer.println(sb.toString());
        } catch (SQLException e) {
            writer.println("ERROR|Erreur serveur : " + e.getMessage());
        }
    }

    public void faireListeAmis(int userId, PrintWriter writer) {
        try {
            List<Utilisateur> friends = friendDAO.getAmis(userId);
            if (friends.isEmpty()) {
                writer.println("FRIEND_LIST|");
                return;
            }
            StringBuilder sb = new StringBuilder("FRIEND_LIST|");
            for (Utilisateur f : friends) {
                sb.append(f.getId()).append(":").append(f.getUsername()).append(":").append(f.getStatus()).append(",");
            }
            sb.setLength(sb.length() - 1);
            writer.println(sb.toString());
        } catch (SQLException e) {
            writer.println("ERROR|Erreur serveur : " + e.getMessage());
        }
    }

    private void envoyerListesAmis(int userId1, int userId2) {
        NotifServeur.envoyerAUser(userId1, writer -> faireListeAmis(userId1, writer));
        NotifServeur.envoyerAUser(userId2, writer -> faireListeAmis(userId2, writer));
    }
}
