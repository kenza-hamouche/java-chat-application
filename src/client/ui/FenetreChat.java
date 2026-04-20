package client.ui;

import client.SocketClient;
import client.NotifSwing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FenetreChat extends JFrame {

    private final SocketClient socket;
    private final int currentUserId;
    private final String currentUsername;

    private JTextArea chatArea;
    private JTextField messageField;
    private JLabel statusLabel;

    private PanelAmi friendPanel;
    private PanelNotif notifPanel;
    private PanelGroupe groupPanel;

    private String currentChatTarget;
    private Integer currentGroupId;
    private String currentGroupName;

    private JLabel notifBadge;
    private int unreadCount = 0;

    private final Map<String, List<String>> privateHistories = new HashMap<>();
    private final Map<Integer, List<String>> groupHistories = new HashMap<>();

    public FenetreChat(SocketClient socket, int userId, String username) {
        this.socket = socket;
        this.currentUserId = userId;
        this.currentUsername = username;

        setTitle("ChatApp - " + username);
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        faireUI();
        brancherMessages();
        mettreFenetre();

        socket.send("LIST_FRIENDS");
        socket.send("LIST_PENDING_REQUESTS");
        socket.send("LIST_GROUPS");
    }

    private void faireUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 242, 248));

        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(new Color(60, 80, 180));
        titleBar.setBorder(new EmptyBorder(8, 14, 8, 14));

        JLabel titleLabel = new JLabel("ChatApp - " + currentUsername);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        titleBar.add(titleLabel, BorderLayout.WEST);

        notifBadge = new JLabel("0");
        notifBadge.setOpaque(true);
        notifBadge.setBackground(Color.RED);
        notifBadge.setForeground(Color.WHITE);
        notifBadge.setFont(new Font("SansSerif", Font.BOLD, 11));
        notifBadge.setBorder(new EmptyBorder(2, 6, 2, 6));
        notifBadge.setVisible(false);
        titleBar.add(notifBadge, BorderLayout.EAST);

        add(titleBar, BorderLayout.NORTH);

        JTabbedPane leftTabs = new JTabbedPane();
        leftTabs.setPreferredSize(new Dimension(250, 0));
        leftTabs.addChangeListener(e -> resetBadge());

        friendPanel = new PanelAmi(socket, this);
        notifPanel = new PanelNotif(this);
        groupPanel = new PanelGroupe(socket, this);

        leftTabs.addTab("Amis", friendPanel);
        leftTabs.addTab("Groupes", groupPanel);
        leftTabs.addTab("Notifications", notifPanel);

        add(leftTabs, BorderLayout.WEST);
        add(fairePanelChat(), BorderLayout.CENTER);
    }

    private JPanel fairePanelChat() {
        JPanel panel = new JPanel(new BorderLayout(4, 4));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        statusLabel = new JLabel("Sélectionnez un ami ou un groupe pour commencer à discuter.");
        statusLabel.setFont(new Font("SansSerif", Font.ITALIC, 13));
        statusLabel.setForeground(new Color(120, 120, 140));
        statusLabel.setBorder(new EmptyBorder(0, 0, 6, 0));
        panel.add(statusLabel, BorderLayout.NORTH);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        chatArea.setBackground(new Color(248, 249, 252));
        chatArea.setBorder(new EmptyBorder(6, 8, 6, 8));

        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 230)));
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout(6, 0));
        inputPanel.setOpaque(false);
        inputPanel.setBorder(new EmptyBorder(6, 0, 0, 0));

        messageField = new JTextField();
        messageField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        messageField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 190, 220)),
            new EmptyBorder(6, 10, 6, 10)
        ));

        JButton sendBtn = new JButton("Envoyer");
        sendBtn.addActionListener(e -> envoyerMessage());
        messageField.addActionListener(e -> envoyerMessage());

        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendBtn, BorderLayout.EAST);
        panel.add(inputPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void brancherMessages() {
        socket.setMessageListener(this::gererMsgServeur);
    }

    public void gererMsgServeur(String message) {
        String[] parts = message.split("\\|", -1);
        String type = parts[0];

        switch (type) {
            case "PRIVATE_MSG" -> gererPriveEntrant(parts);
            case "GROUP_MSG" -> gererGroupeEntrant(parts);
            case "NOTIFICATION" -> gererNotif(parts);
            case "FRIEND_LIST" -> SwingUtilities.invokeLater(() -> friendPanel.majAmis(parts));
            case "PENDING_REQUESTS" -> SwingUtilities.invokeLater(() -> friendPanel.majDemandes(parts));
            case "FRIEND_REQUEST_RECEIVED" -> SwingUtilities.invokeLater(() -> {
                friendPanel.receptionDemandeAmi(parts);
                socket.send("LIST_PENDING_REQUESTS");
            });
            case "FRIEND_ACCEPTED" -> SwingUtilities.invokeLater(() -> friendPanel.amiAccepte(parts));
            case "USER_STATUS" -> SwingUtilities.invokeLater(() -> friendPanel.majStatutUser(parts));
            case "GROUP_LIST" -> SwingUtilities.invokeLater(() -> groupPanel.majGroupes(parts));
            case "GROUP_INVITE" -> SwingUtilities.invokeLater(() -> {
                groupPanel.rafraichir();
                if (parts.length >= 3) {
                    notifPanel.ajouterNotif(parts[2] + " vous a ajouté à un groupe.");
                }
            });
            case "OK" -> gererOk(parts);
            case "ERROR" -> montrerErreur(parts);
            default -> {
            }
        }
    }

    private void gererPriveEntrant(String[] parts) {
        if (parts.length < 4) return;
        String sender = parts[1];
        String content = parts[2];
        String timestamp = parts[3];
        String formatted = sender + " [" + timestamp + "]\n" + content;
        privateHistories.computeIfAbsent(sender, k -> new ArrayList<>()).add(formatted);

        SwingUtilities.invokeLater(() -> {
            if (sender.equals(currentChatTarget) && currentGroupId == null) {
                ajouterDansChat(formatted);
            } else {
                plusBadge();
                notifPanel.ajouterNotif("Msg de " + sender + " : " + content);
                NotifSwing.montrerPopup(this, "Nouveau message de " + sender, content);
            }
        });
    }

    private void gererGroupeEntrant(String[] parts) {
        if (parts.length < 6) return;
        int groupId = Integer.parseInt(parts[1]);
        String groupName = parts[2];
        String sender = parts[3];
        String content = parts[4];
        String timestamp = parts[5];
        String formatted = "[" + groupName + "] " + sender + " [" + timestamp + "]\n" + content;
        groupHistories.computeIfAbsent(groupId, k -> new ArrayList<>()).add(formatted);

        SwingUtilities.invokeLater(() -> {
            if (currentGroupId != null && currentGroupId == groupId) {
                ajouterDansChat(formatted);
            } else {
                plusBadge();
                notifPanel.ajouterNotif("[" + groupName + "] " + sender + " : " + content);
                NotifSwing.montrerPopup(this, groupName + " - " + sender, content);
            }
        });
    }

    private void gererNotif(String[] parts) {
        if (parts.length < 3) return;
        String notifType = parts[1];
        String notifContent = parts[2];

        SwingUtilities.invokeLater(() -> {
            plusBadge();
            notifPanel.ajouterNotif("[" + notifType + "] " + notifContent);
            if ("FRIEND_REQUEST".equals(notifType) || "FRIEND_ACCEPTED".equals(notifType)) {
                socket.send("LIST_PENDING_REQUESTS");
                socket.send("LIST_FRIENDS");
            }
        });
    }

    private void gererOk(String[] parts) {
        if (parts.length < 2) return;
        switch (parts[1]) {
            case "GROUP_CREATED" -> {
                if (parts.length >= 4) {
                    SwingUtilities.invokeLater(() -> {
                        int groupId = Integer.parseInt(parts[2]);
                        String groupName = parts[3];
                        groupPanel.ajouterGroupe(groupId, groupName);
                        JOptionPane.showMessageDialog(this, "Groupe '" + groupName + "' créé.");
                    });
                }
            }
            case "MEMBER_ADDED" -> {
                if (parts.length >= 4) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, parts[3] + " a été ajouté au groupe."));
                }
            }
            case "FRIEND_REQUEST_SENT" -> {
                if (parts.length >= 3) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Demande envoyée à " + parts[2] + "."));
                }
            }
            case "MSG_SENT", "GROUP_MSG_SENT", "FRIEND_ACCEPTED", "FRIEND_DECLINED" -> {
            }
            default -> {
            }
        }
    }

    private void montrerErreur(String[] parts) {
        String msg = parts.length > 1 ? parts[parts.length - 1] : "Erreur inconnue.";
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, msg, "Erreur", JOptionPane.ERROR_MESSAGE));
    }

    private void envoyerMessage() {
        String content = messageField.getText().trim();
        if (content.isEmpty()) return;
        if (currentChatTarget == null && currentGroupId == null) {
            JOptionPane.showMessageDialog(this, "Sélectionnez d'abord un ami ou un groupe.");
            return;
        }

        if (currentGroupId != null) {
            socket.send("GROUP_MSG|" + currentGroupId + "|" + content);
            String formatted = "Moi [" + currentGroupName + "]\n" + content;
            groupHistories.computeIfAbsent(currentGroupId, k -> new ArrayList<>()).add(formatted);
            ajouterDansChat(formatted);
        } else {
            socket.send("PRIVATE_MSG|" + currentChatTarget + "|" + content);
            String formatted = "Moi -> " + currentChatTarget + "\n" + content;
            privateHistories.computeIfAbsent(currentChatTarget, k -> new ArrayList<>()).add(formatted);
            ajouterDansChat(formatted);
        }

        messageField.setText("");
    }

    public void ouvrirPrive(String friendUsername) {
        currentChatTarget = friendUsername;
        currentGroupId = null;
        currentGroupName = null;
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Conversation privée avec : " + friendUsername);
            displayPrivateHistory(friendUsername);
        });
    }

    public void ouvrirGroupe(int groupId, String groupName) {
        currentGroupId = groupId;
        currentGroupName = groupName;
        currentChatTarget = null;
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Conversation de groupe : " + groupName);
            displayGroupHistory(groupId);
        });
    }

    private void displayPrivateHistory(String username) {
        chatArea.setText("");
        List<String> history = privateHistories.get(username);
        if (history == null) return;
        for (String line : history) {
            ajouterDansChat(line);
        }
    }

    private void displayGroupHistory(int groupId) {
        chatArea.setText("");
        List<String> history = groupHistories.get(groupId);
        if (history == null) return;
        for (String line : history) {
            ajouterDansChat(line);
        }
    }

    private void ajouterDansChat(String message) {
        chatArea.append(message + "\n\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    private void plusBadge() {
        unreadCount++;
        NotifSwing.majBadge(notifBadge, unreadCount);
    }

    public void resetBadge() {
        unreadCount = 0;
        NotifSwing.majBadge(notifBadge, 0);
    }

    public SocketClient getSocket() { return socket; }
    public int getCurrentUserId() { return currentUserId; }
    public String getCurrentUsername() { return currentUsername; }

    private void mettreFenetre() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                    FenetreChat.this,
                    "Voulez-vous vous déconnecter et quitter ?",
                    "Quitter",
                    JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    socket.send("LOGOUT");
                    socket.close();
                    System.exit(0);
                }
            }
        });
    }
}
