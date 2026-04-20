package client.ui;

import client.SocketClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;


public class FenetreLogin extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;
    private JButton loginBtn;
    private JButton registerBtn;

    public FenetreLogin() {
        setTitle("ChatApp - Connexion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        faireUI();
        setMinimumSize(new Dimension(520, 420));
        setSize(new Dimension(560, 460));
        setLocationRelativeTo(null);
        setResizable(true);
    }

    private void faireUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(245, 245, 250));

        JPanel main = new JPanel(new BorderLayout(0, 16));
        main.setBackground(new Color(245, 245, 250));
        main.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JLabel header = new JLabel("ChatApp", SwingConstants.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 28));
        header.setForeground(new Color(60, 80, 180));
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        main.add(header, BorderLayout.NORTH);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setMaximumSize(new Dimension(420, 260));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        usernameField = new JTextField(24);
        passwordField = new JPasswordField(24);
        usernameField.setPreferredSize(new Dimension(260, 34));
        passwordField.setPreferredSize(new Dimension(260, 34));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        form.add(new JLabel("Nom d'utilisateur :"), gbc);

        gbc.gridy = 1;
        form.add(usernameField, gbc);

        gbc.gridy = 2;
        form.add(new JLabel("Mot de passe :"), gbc);

        gbc.gridy = 3;
        form.add(passwordField, gbc);

        loginBtn = new JButton("Se connecter");
        loginBtn.setFocusPainted(false);
        loginBtn.setPreferredSize(new Dimension(170, 40));
        loginBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                essaiConnexion();
            }
        });

        registerBtn = new JButton("S'inscrire");
        registerBtn.setFocusPainted(false);
        registerBtn.setPreferredSize(new Dimension(170, 40));
        registerBtn.addActionListener(e -> essaiInscription());

        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        gbc.gridx = 0;
        form.add(loginBtn, gbc);

        gbc.gridx = 1;
        form.add(registerBtn, gbc);

        centerWrapper.add(form);
        main.add(centerWrapper, BorderLayout.CENTER);

        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setForeground(new Color(180, 40, 40));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        statusLabel.setPreferredSize(new Dimension(100, 28));
        main.add(statusLabel, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(main);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        scrollPane.getViewport().setBackground(new Color(245, 245, 250));

        root.add(scrollPane, BorderLayout.CENTER);
        setContentPane(root);
    }

    private void essaiConnexion() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        statusLabel.setText("Connexion en cours...");
        changerChamps(false);

        new Thread(() -> {
            try {
                SocketClient[] socketHolder = new SocketClient[1];

                SocketClient.MessageListener loginListener = message -> {
                    if (message.startsWith("OK|LOGIN")) {
                        String[] parts = message.split("\\|");
                        int userId = Integer.parseInt(parts[2]);
                        String uname = parts[3];
                        SwingUtilities.invokeLater(() -> {
                            FenetreChat frame = new FenetreChat(socketHolder[0], userId, uname);
                            frame.setVisible(true);
                            dispose();
                        });
                    } else if (message.startsWith("ERROR")) {
                        String[] parts = message.split("\\|", 2);
                        String errMsg = parts.length > 1 ? parts[1] : "Erreur";
                        SwingUtilities.invokeLater(() -> {
                            statusLabel.setText(errMsg);
                            changerChamps(true);
                        });
                    }
                };

                socketHolder[0] = new SocketClient(loginListener);
                Thread socketThread = new Thread(socketHolder[0]);
                socketThread.setDaemon(true);
                socketThread.start();
                socketHolder[0].send("LOGIN|" + username + "|" + password);
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Impossible de joindre le serveur.");
                    changerChamps(true);
                });
            }
        }).start();
    }

    private void essaiInscription() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Créer le compte '" + username + "' ?",
            "Confirmation d'inscription",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        statusLabel.setText("Inscription en cours...");
        changerChamps(false);

        new Thread(() -> {
            try {
                SocketClient.MessageListener regListener = message -> {
                    if (message.startsWith("OK|REGISTERED")) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(
                                this,
                                "Compte créé. Vous pouvez maintenant vous connecter.",
                                "Succès",
                                JOptionPane.INFORMATION_MESSAGE
                            );
                            statusLabel.setText(" ");
                            changerChamps(true);
                        });
                    } else if (message.startsWith("ERROR")) {
                        String[] parts = message.split("\\|", 2);
                        String errMsg = parts.length > 1 ? parts[1] : "Erreur";
                        SwingUtilities.invokeLater(() -> {
                            statusLabel.setText(errMsg);
                            changerChamps(true);
                        });
                    }
                };

                SocketClient tempSocket = new SocketClient(regListener);
                Thread t = new Thread(tempSocket);
                t.setDaemon(true);
                t.start();
                tempSocket.send("REGISTER|" + username + "|" + password);
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Impossible de joindre le serveur.");
                    changerChamps(true);
                });
            }
        }).start();
    }

    private void changerChamps(boolean enabled) {
        SwingUtilities.invokeLater(() -> {
            usernameField.setEnabled(enabled);
            passwordField.setEnabled(enabled);
            loginBtn.setEnabled(enabled);
            registerBtn.setEnabled(enabled);
        });
    }
}
