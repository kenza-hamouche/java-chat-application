package client.ui;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;


public class PanelNotif extends JPanel {

    private final FenetreChat mainFrame;
    private DefaultListModel<String> notifModel;
    private JList<String> notifList;

    public PanelNotif(FenetreChat mainFrame) {
        this.mainFrame = mainFrame;
        faireUI();
    }

    private void faireUI() {
        setLayout(new BorderLayout(4, 4));
        setBackground(Color.WHITE);
        setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JLabel title = new JLabel("Notifications");
        title.setFont(new Font("SansSerif", Font.BOLD, 13));
        title.setForeground(new Color(60, 80, 180));

        notifModel = new DefaultListModel<>();
        notifList = new JList<>(notifModel);
        notifList.setFont(new Font("SansSerif", Font.PLAIN, 12));
        notifList.setFixedCellHeight(30);

        JScrollPane scroll = new JScrollPane(notifList);
        scroll.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(210, 220, 240)));

        JButton clearBtn = new JButton("Tout marquer comme lu");
        clearBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        clearBtn.addActionListener(e -> {
            notifModel.clear();
            mainFrame.resetBadge();
        });

        add(title, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(clearBtn, BorderLayout.SOUTH);
    }

    public void ajouterNotif(String message) {
        String time = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date());
        notifModel.addElement("[" + time + "] " + message);
        notifList.ensureIndexIsVisible(notifModel.size() - 1);
    }
}
