package client.ui;

import client.SocketClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;


public class PanelGroupe extends JPanel {

    private final SocketClient socket;
    private final FenetreChat mainFrame;

    private DefaultListModel<String> groupModel;
    private JList<String> groupList;
    private final Map<String, Integer> groupIds = new HashMap<>();

    public PanelGroupe(SocketClient socket, FenetreChat mainFrame) {
        this.socket = socket;
        this.mainFrame = mainFrame;
        faireUI();
    }

    private void faireUI() {
        setLayout(new BorderLayout(6, 6));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JLabel title = new JLabel("Mes groupes");
        title.setFont(new Font("SansSerif", Font.BOLD, 13));
        title.setForeground(new Color(60, 80, 180));

        groupModel = new DefaultListModel<>();
        groupList = new JList<>(groupModel);
        groupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        groupList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String selected = groupList.getSelectedValue();
                    if (selected != null && groupIds.containsKey(selected)) {
                        mainFrame.ouvrirGroupe(groupIds.get(selected), selected);
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(groupList);

        JPanel buttons = new JPanel(new GridLayout(1, 2, 6, 0));
        JButton createBtn = new JButton("Créer");
        JButton addBtn = new JButton("Ajouter un ami");
        createBtn.addActionListener(e -> dialogCreerGroupe());
        addBtn.addActionListener(e -> dialogAjouterMembre());
        buttons.add(createBtn);
        buttons.add(addBtn);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.add(title);
        content.add(Box.createVerticalStrut(4));
        content.add(scroll);
        content.add(Box.createVerticalStrut(6));
        content.add(buttons);

        add(content, BorderLayout.NORTH);
    }

    private void dialogCreerGroupe() {
        String name = JOptionPane.showInputDialog(mainFrame, "Nom du groupe :", "Créer un groupe", JOptionPane.QUESTION_MESSAGE);
        if (name != null && !name.trim().isEmpty()) {
            socket.send("CREATE_GROUP|" + name.trim());
        }
    }

    private void dialogAjouterMembre() {
        String selected = groupList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(mainFrame, "Sélectionnez d'abord un groupe.");
            return;
        }
        Integer groupId = groupIds.get(selected);
        if (groupId == null) return;

        String username = JOptionPane.showInputDialog(mainFrame, "Nom d'utilisateur à ajouter (doit être votre ami) :", "Ajouter un membre", JOptionPane.QUESTION_MESSAGE);
        if (username != null && !username.trim().isEmpty()) {
            socket.send("ADD_TO_GROUP|" + groupId + "|" + username.trim());
        }
    }

    public void rafraichir() {
        socket.send("LIST_GROUPS");
    }

    public void ajouterGroupe(int id, String name) {
        if (!groupIds.containsKey(name)) {
            groupIds.put(name, id);
            groupModel.addElement(name);
        }
    }

    public void majGroupes(String[] parts) {
        groupModel.clear();
        groupIds.clear();
        if (parts.length < 2 || parts[1].isEmpty()) return;
        for (String entry : parts[1].split(",")) {
            String[] fields = entry.split(":", 2);
            if (fields.length == 2) {
                int id = Integer.parseInt(fields[0]);
                String name = fields[1];
                groupIds.put(name, id);
                groupModel.addElement(name);
            }
        }
    }
}
