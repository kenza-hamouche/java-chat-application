package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginView extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel registerLabel; // texte avec "inscrivez-vous" cliquable

    public LoginView() {
        setTitle("Connexion");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel principal
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Nom d'utilisateur :"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        panel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Mot de passe :"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);

        // Bouton Se connecter (centré)
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginButton = new JButton("Se connecter");
        panel.add(loginButton, gbc);

        // Phrase "Si vous n’avez pas de compte, inscrivez-vous"
        gbc.gridy = 3;
        String text = "<html>Si vous n'avez pas de compte, " +
                "<a href=''>inscrivez-vous</a></html>";
        registerLabel = new JLabel(text);
        registerLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.add(registerLabel, gbc);

        // Action clic sur "inscrivez-vous" (pour tester ouverture fenêtre inscription)
        registerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Ici on peut ouvrir une future fenêtre RegisterView
                JOptionPane.showMessageDialog(null, "Page d'inscription (à implémenter)");
            }
        });

        add(panel);
    }

    // Getters pour controller futur
    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public JButton getLoginButton() {
        return loginButton;
    }

    public JLabel getRegisterLabel() {
        return registerLabel;
    }
}