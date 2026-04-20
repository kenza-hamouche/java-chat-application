package client;

import client.ui.FenetreLogin;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;


public class Client {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new FenetreLogin().setVisible(true);
        });
    }
}
