package org.example;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class LoginInterfaz extends JFrame {
    private JTextField usernameOrEmailField;
    private JPasswordField passwordField;
    private final MongoDBConnection mongoDBConnection;

    public LoginInterfaz(MongoDBConnection mongoDBConnection) {
        setTitle("Login");
        setSize(400, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        usernameOrEmailField = new JTextField(20);
        passwordField = new JPasswordField(20);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realizarLogin();
            }
        });

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Nombre de usuario o Correo electrónico:"));
        panel.add(usernameOrEmailField);
        panel.add(new JLabel("Contraseña:"));
        panel.add(passwordField);
        panel.add(new JLabel(""));
        panel.add(loginButton);

        add(panel);

        // Pasar la conexión a MongoDB desde el constructor
        this.mongoDBConnection = mongoDBConnection;
    }

    private void realizarLogin() {
        String usernameOrEmail = usernameOrEmailField.getText();
        char[] passwordChars = passwordField.getPassword();
        String password = new String(passwordChars);

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                MongoCollection<Document> userCollection = mongoDBConnection.getUserCollection();

                // Verificar si el usuario y la contraseña existen en la base de datos
                Document query = new Document("$and",
                        Arrays.asList(
                                new Document("username", usernameOrEmail),
                                new Document("password", hashPassword(password))
                        )
                );
                Document userDocument = userCollection.find(query).first();

                if (userDocument != null) {
                    // Usuario autenticado correctamente
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(LoginInterfaz.this, "Login exitoso");
                        new AdminInterfaz(usernameOrEmail, mongoDBConnection).setVisible(true);
                        dispose();
                    });
                } else {
                    // Usuario no encontrado o contraseña incorrecta
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(LoginInterfaz.this, "Error de login. Verifica tus credenciales.", "Error", JOptionPane.ERROR_MESSAGE);
                    });
                }

                return null;
            }
        }.execute();
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
            handleException(e, "Error al hashear la contraseña.");
            return null;
        }
    }

    private void handleException(Exception ex, String errorMessage) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginInterfaz(MongoDBConnection.getInstance()).setVisible(true);
        });
    }
}
