package org.example;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList; // Agregado para importar ArrayList
import java.util.List;

public class AdminInterfaz extends JFrame {
    private final String username;
    private final JComboBox<String> usuariosComboBox;
    private MongoDBConnection mongoDBConnection;

    public AdminInterfaz(String username, MongoDBConnection mongoDBConnection) {
        this.username = username;
        this.mongoDBConnection = mongoDBConnection;

        setTitle("Panel de Administración");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        usuariosComboBox = new JComboBox<>();

        List<String> listaUsuarios = obtenerListaUsuarios();
        usuariosComboBox.setModel(new DefaultComboBoxModel<>(listaUsuarios.toArray(new String[0])));

        JButton cambiarContraseñaButton = new JButton("Cambiar Contraseña");
        cambiarContraseñaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cambiarContraseña();
            }
        });

        JButton borrarUsuarioButton = new JButton("Borrar Usuario");
        borrarUsuarioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                borrarUsuario();
            }
        });

        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(usuariosComboBox);
        panel.add(cambiarContraseñaButton);
        panel.add(borrarUsuarioButton);

        add(panel);
    }

    private List<String> obtenerListaUsuarios() {
        // Aquí obtienes la lista de usuarios desde la base de datos
        // Modifica esta lógica según la estructura de tu base de datos y tu implementación
        try {
            MongoCollection<Document> userCollection = mongoDBConnection.getUserCollection();
            List<String> usuarios = userCollection.distinct("username", String.class).into(new ArrayList<>());
            return usuarios;
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al obtener la lista de usuarios.", "Error", JOptionPane.ERROR_MESSAGE);
            return List.of();
        }
    }

    private void cambiarContraseña() {
        String selectedUser = (String) usuariosComboBox.getSelectedItem();
        if (selectedUser != null) {
            SwingUtilities.invokeLater(() -> {
                new CambioContraseñaInterfaz(selectedUser, mongoDBConnection).setVisible(true);
            });
        } else {
            JOptionPane.showMessageDialog(this, "Selecciona un usuario para cambiar la contraseña.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void borrarUsuario() {
        String selectedUser = (String) usuariosComboBox.getSelectedItem();
        if (selectedUser != null) {
            int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de que quieres borrar a " + selectedUser + "?", "Confirmar borrado", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    MongoCollection<Document> userCollection = mongoDBConnection.getUserCollection();
                    userCollection.deleteOne(new Document("username", selectedUser));
                    JOptionPane.showMessageDialog(this, "Usuario borrado: " + selectedUser);

                    // Actualiza la lista de usuarios después de borrar.
                    List<String> updatedUserList = obtenerListaUsuarios();
                    usuariosComboBox.setModel(new DefaultComboBoxModel<>(updatedUserList.toArray(new String[0])));
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error al borrar al usuario.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecciona un usuario para borrar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Ejemplo de uso: supongamos que el usuario "admin" ha iniciado sesión
            new AdminInterfaz("admin", MongoDBConnection.getInstance()).setVisible(true);
        });
    }
}
