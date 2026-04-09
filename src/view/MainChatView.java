package view;

import javax.swing.*;
import java.awt.*;

public class MainChatView extends JFrame {

    // Partie gauche : liste des conversations
    private JList<String> conversationList;

    // Partie droite : zone de discussion et ajout d'amis
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private JTextField addFriendField;
    private JButton addFriendButton;

    public MainChatView() {
        setTitle("Chat Application");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout principal
        JSplitPane splitPane = new JSplitPane();
        splitPane.setDividerLocation(250); // 1/3 gauche, 2/3 droite

        // Partie gauche : liste des conversations
        conversationList = new JList<>(new DefaultListModel<>());
        JScrollPane conversationScroll = new JScrollPane(conversationList);
        splitPane.setLeftComponent(conversationScroll);

        // Partie droite : chat et ajout d'amis
        JPanel chatPanel = new JPanel(new BorderLayout());

        // Zone chat
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatPanel.add(chatScroll, BorderLayout.CENTER);

        // Panel pour écrire le message + bouton envoyer
        JPanel messagePanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        sendButton = new JButton("Envoyer");
        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.EAST);
        chatPanel.add(messagePanel, BorderLayout.SOUTH);

        // Panel pour ajouter un ami
        JPanel addFriendPanel = new JPanel(new BorderLayout());
        addFriendField = new JTextField();
        addFriendButton = new JButton("Ajouter ami");
        addFriendPanel.add(addFriendField, BorderLayout.CENTER);
        addFriendPanel.add(addFriendButton, BorderLayout.EAST);
        chatPanel.add(addFriendPanel, BorderLayout.NORTH);

        splitPane.setRightComponent(chatPanel);
        add(splitPane);
    }

    // Getters pour controller futur
    public JList<String> getConversationList() {
        return conversationList;
    }

    public JTextArea getChatArea() {
        return chatArea;
    }

    public JTextField getMessageField() {
        return messageField;
    }

    public JButton getSendButton() {
        return sendButton;
    }

    public JTextField getAddFriendField() {
        return addFriendField;
    }

    public JButton getAddFriendButton() {
        return addFriendButton;
    }
}
