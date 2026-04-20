package client;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.BorderFactory;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;


public class NotifSwing {

    public static void montrerPopup(Component parent, String title, String message) {
        SwingUtilities.invokeLater(() -> {
            JWindow popup = new JWindow(SwingUtilities.getWindowAncestor(parent));
            popup.setLayout(new BorderLayout());
            popup.setBackground(new Color(50, 50, 50, 220));

            JPanel panel = new JPanel(new BorderLayout(8, 4));
            panel.setBackground(new Color(50, 50, 50));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

            JLabel titleLabel = new JLabel(title);
            titleLabel.setForeground(Color.WHITE);
            titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 13f));

            JLabel msgLabel = new JLabel(message);
            msgLabel.setForeground(new Color(220, 220, 220));
            msgLabel.setFont(msgLabel.getFont().deriveFont(12f));

            panel.add(titleLabel, BorderLayout.NORTH);
            panel.add(msgLabel, BorderLayout.CENTER);
            popup.add(panel);
            popup.pack();

            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            popup.setLocation(screen.width - popup.getWidth() - 20, screen.height - popup.getHeight() - 60);
            popup.setVisible(true);

            Timer timer = new Timer(3000, e -> popup.dispose());
            timer.setRepeats(false);
            timer.start();
        });
    }

    public static void majBadge(JLabel badge, int count) {
        SwingUtilities.invokeLater(() -> {
            if (count > 0) {
                badge.setText(String.valueOf(count));
                badge.setVisible(true);
            } else {
                badge.setVisible(false);
            }
        });
    }

    public static void ajouterMessage(JTextArea area, String message) {
        SwingUtilities.invokeLater(() -> {
            area.append(message + "\n");
            area.setCaretPosition(area.getDocument().getLength());
        });
    }
}
