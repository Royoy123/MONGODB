package org.example;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CambioContraseñaInterfaz extends JFrame {
    private final String username;
    private final MongoDBConnection mongoDBConnection;

    private JTextField nuevaContraseñaField;
    private JPasswordField confirmarContraseñaField;

    public CambioContraseñaInterfaz(String username, MongoDBConnection mongoDBConnection) {
        this.username = username;
        this.mongoDBConnection = mongoDBConnection;

        setTitle("Cambio de Contraseña");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        nuevaContraseñaField = new JTextField();
        confirmarContraseñaField = new JPasswordField();

        JButton cambiarContraseñaButton = new JButton("Cambiar Contraseña");
        cambiarContraseñaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cambiarContraseña();
            }
        });

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Nueva Contraseña:"));
        panel.add(nuevaContraseñaField);
        panel.add(new JLabel("Confirmar Contraseña:"));
        panel.add(confirmarContraseñaField);
        panel.add(new JLabel()); // Espacio en blanco
        panel.add(cambiarContraseñaButton);

        add(panel);
    }

    private void cambiarContraseña() {
        String nuevaContraseña = nuevaContraseñaField.getText();
        String confirmarContraseña = new String(confirmarContraseñaField.getPassword());

        if (nuevaContraseña.isEmpty() || confirmarContraseña.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingresa la nueva contraseña y confírmala.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!nuevaContraseña.equals(confirmarContraseña)) {
            JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden. Vuelve a intentarlo.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            MongoCollection<Document> userCollection = mongoDBConnection.getUserCollection();

            // Aplicar hash a la nueva contraseña
            String hashedPassword = hashPassword(nuevaContraseña);

            userCollection.updateOne(
                    new Document("username", username),
                    new Document("$set", new Document("password", hashedPassword))
            );

            JOptionPane.showMessageDialog(this, "Contraseña cambiada exitosamente.");
            dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al intentar cambiar la contraseña.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Función para hashear la contraseña
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
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CambioContraseñaInterfaz("john", MongoDBConnection.getInstance()).setVisible(true);
        });
    }
}
