package client.ui;

import client.SocketClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;


public class PanelAmi extends JPanel {

    private final SocketClient socket;
    private final FenetreChat mainFrame;

    private DefaultListModel<String> friendListModel;
    private DefaultListModel<String> pendingListModel;
    private JList<String> friendList;
    private JList<String> pendingList;
    private final Map<String, Integer> pendingRequestIds = new HashMap<>();

    public PanelAmi(SocketClient socket, FenetreChat mainFrame) {
        this.socket = socket;
        this.mainFrame = mainFrame;
        faireUI();
    }

    private void faireUI() {
        setLayout(new BorderLayout(6, 6));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JLabel friendsTitle = new JLabel("Mes amis");
        friendsTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        friendsTitle.setForeground(new Color(60, 80, 180));

        friendListModel = new DefaultListModel<>();
        friendList = new JList<>(friendListModel);
        friendList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        friendList.setFont(new Font("SansSerif", Font.PLAIN, 12));
        friendList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String selected = friendList.getSelectedValue();
                    if (selected != null) {
                        String username = selected.replace("[En ligne] ", "")
                                                  .replace("[Hors ligne] ", "")
                                                  .trim();
                        mainFrame.ouvrirPrive(username);
                    }
                }
            }
        });

        JScrollPane friendScroll = new JScrollPane(friendList);
        friendScroll.setPreferredSize(new Dimension(0, 170));

        JButton addFriendBtn = new JButton("Ajouter un ami");
        addFriendBtn.addActionListener(e -> dialogAjouterAmi());

        JLabel pendingTitle = new JLabel("Demandes reçues");
        pendingTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        pendingTitle.setForeground(new Color(180, 80, 60));

        pendingListModel = new DefaultListModel<>();
        pendingList = new JList<>(pendingListModel);
        pendingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pendingList.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JScrollPane pendingScroll = new JScrollPane(pendingList);
        pendingScroll.setPreferredSize(new Dimension(0, 120));

        JPanel pendingButtons = new JPanel(new GridLayout(1, 2, 6, 0));
        JButton acceptBtn = new JButton("Accepter");
        JButton declineBtn = new JButton("Refuser");
        acceptBtn.addActionListener(e -> faireAcceptOuRefus(true));
        declineBtn.addActionListener(e -> faireAcceptOuRefus(false));
        pendingButtons.add(acceptBtn);
        pendingButtons.add(declineBtn);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.add(friendsTitle);
        content.add(Box.createVerticalStrut(4));
        content.add(friendScroll);
        content.add(Box.createVerticalStrut(6));
        content.add(addFriendBtn);
        content.add(Box.createVerticalStrut(14));
        content.add(pendingTitle);
        content.add(Box.createVerticalStrut(4));
        content.add(pendingScroll);
        content.add(Box.createVerticalStrut(6));
        content.add(pendingButtons);

        add(content, BorderLayout.NORTH);
    }

    public void majAmis(String[] parts) {
        friendListModel.clear();
        if (parts.length < 2 || parts[1].isEmpty()) return;
        for (String entry : parts[1].split(",")) {
            String[] fields = entry.split(":");
            if (fields.length >= 3) {
                String prefix = "online".equalsIgnoreCase(fields[2]) ? "[En ligne] " : "[Hors ligne] ";
                friendListModel.addElement(prefix + fields[1]);
            }
        }
    }

    public void majDemandes(String[] parts) {
        pendingListModel.clear();
        pendingRequestIds.clear();
        if (parts.length < 2 || parts[1].isEmpty()) return;
        for (String entry : parts[1].split(",")) {
            String[] fields = entry.split(":");
            if (fields.length >= 2) {
                int reqId = Integer.parseInt(fields[0]);
                String sender = fields[1];
                if (!pendingRequestIds.containsKey(sender)) {
                    pendingListModel.addElement(sender);
                }
                pendingRequestIds.put(sender, reqId);
            }
        }
    }

    public void receptionDemandeAmi(String[] parts) {
        if (parts.length < 3) return;
        int reqId = Integer.parseInt(parts[1]);
        String sender = parts[2];
        if (!pendingRequestIds.containsKey(sender)) {
            pendingListModel.addElement(sender);
        }
        pendingRequestIds.put(sender, reqId);
    }

    public void amiAccepte(String[] parts) {
        socket.send("LIST_FRIENDS");
        socket.send("LIST_PENDING_REQUESTS");
        if (parts.length >= 2) {
            JOptionPane.showMessageDialog(
                mainFrame,
                parts[1] + " a accepté votre demande d'amitié.",
                "Demande acceptée",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    public void majStatutUser(String[] parts) {
        socket.send("LIST_FRIENDS");
    }

    private void dialogAjouterAmi() {
        String target = JOptionPane.showInputDialog(
            mainFrame,
            "Entrez le nom d'utilisateur à ajouter :",
            "Envoyer une demande d'amitié",
            JOptionPane.QUESTION_MESSAGE
        );
        if (target != null && !target.trim().isEmpty()) {
            socket.send("FRIEND_REQUEST|" + target.trim());
        }
    }

    private void faireAcceptOuRefus(boolean accept) {
        String selected = pendingList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(mainFrame, "Sélectionnez une demande.");
            return;
        }
        Integer reqId = pendingRequestIds.get(selected);
        if (reqId == null) return;

        if (accept) {
            socket.send("FRIEND_ACCEPT|" + reqId);
        } else {
            int confirm = JOptionPane.showConfirmDialog(
                mainFrame,
                "Refuser la demande de " + selected + " ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm != JOptionPane.YES_OPTION) return;
            socket.send("FRIEND_DECLINE|" + reqId);
        }

        pendingListModel.removeElement(selected);
        pendingRequestIds.remove(selected);
    }
}
