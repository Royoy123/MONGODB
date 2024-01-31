package org.example;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

import javax.swing.*;
import java.awt.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegistroInterfaz extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;

    public RegistroInterfaz() {
        setTitle("Registro");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        emailField = new JTextField(20);

        JButton registerButton = new JButton("Registrar");
        registerButton.addActionListener(e -> realizarRegistro());

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Nombre de usuario:"));
        panel.add(usernameField);
        panel.add(new JLabel("Correo electrónico:"));
        panel.add(emailField);
        panel.add(new JLabel("Contraseña:"));
        panel.add(passwordField);
        panel.add(new JLabel(""));
        panel.add(registerButton);

        add(panel);
    }

    private void realizarRegistro() {
        String username = usernameField.getText();
        String email = emailField.getText();
        char[] passwordChars = passwordField.getPassword();
        String password = new String(passwordChars);

        try {
            MongoDBConnection mongoDBConnection = MongoDBConnection.getInstance();
            MongoCollection<Document> userCollection = mongoDBConnection.getUserCollection();

            Document existingUser = userCollection.find(new Document("username", username)).first();
            if (existingUser != null) {
                JOptionPane.showMessageDialog(this, "Usuario ya registrado. Por favor, elige otro nombre de usuario.");
                return;
            }

            Document newUser = new Document("username", username)
                    .append("email", email)
                    .append("password", hashPassword(password));

            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    userCollection.insertOne(newUser);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        dispose();
                        SwingUtilities.invokeLater(() -> new MainInterfaz().setVisible(true));
                        JOptionPane.showMessageDialog(null, "Registro exitoso. La aplicación se cerrará.");
                        System.exit(0);
                    } catch (Exception ex) {
                        handleException(ex, "Error al realizar acciones posteriores al registro.");
                    }
                }
            }.execute();
        } catch (Exception ex) {
            handleException(ex, "Error al registrar usuario. Por favor, inténtalo de nuevo.");
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());

            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : hashedBytes) {
                stringBuilder.append(String.format("%02x", b));
            }

            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            handleException(e,"Error al hashear la contraseña.");
            return null;
        }
    }

    private void handleException(Exception ex, String errorMessage) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegistroInterfaz().setVisible(true));
    }
}
