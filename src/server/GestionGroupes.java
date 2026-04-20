package server;

import dao.Amis;
import dao.Groupes;
import dao.Users;

import java.io.PrintWriter;
import java.sql.SQLException;


public class GestionGroupes {

    private final Groupes groupDAO = new Groupes();
    private final Amis friendDAO = new Amis();
    private final Users userDAO = new Users();

    public void handleCreate(int creatorId, String creatorUsername,
                             String groupName, PrintWriter writer) {
        try {
            int groupId = groupDAO.creerGroupe(groupName, creatorId);
            if (groupId == -1) {
                writer.println("ERROR|Impossible de créer le groupe.");
                return;
            }
            writer.println("OK|GROUP_CREATED|" + groupId + "|" + groupName);
            System.out.println("[Groupe] " + creatorUsername + " a créé le groupe '" + groupName + "' (id=" + groupId + ")");
        } catch (SQLException e) {
            writer.println("ERROR|Erreur serveur : " + e.getMessage());
        }
    }

    public void handleAddMember(int requesterId, String requesterUsername,
                                int groupId, String targetUsername,
                                PrintWriter writer) {
        try {
            if (!groupDAO.estMembre(groupId, requesterId)) {
                writer.println("ERROR|Vous n'êtes pas membre de ce groupe.");
                return;
            }

            int targetId = userDAO.getIdParNom(targetUsername);
            if (targetId == -1) {
                writer.println("ERROR|Utilisateur '" + targetUsername + "' introuvable.");
                return;
            }

            if (!friendDAO.sontAmis(requesterId, targetId)) {
                writer.println("ERROR|NOT_FRIENDS|Vous devez être ami avec " + targetUsername + " pour l'ajouter au groupe.");
                return;
            }

            groupDAO.ajouterMembre(groupId, targetId);
            writer.println("OK|MEMBER_ADDED|" + groupId + "|" + targetUsername);

            NotifServeur.envoyerNotif(targetId, "GROUP_INVITE", requesterUsername + " vous a ajouté au groupe #" + groupId);
            NotifServeur.envoyerDirect(targetId, "GROUP_INVITE|" + groupId + "|" + requesterUsername);
        } catch (SQLException e) {
            writer.println("ERROR|Erreur serveur : " + e.getMessage());
        }
    }
}
