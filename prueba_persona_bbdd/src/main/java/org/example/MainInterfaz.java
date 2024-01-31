
package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainInterfaz extends JFrame {
    private final MongoDBConnection mongoDBConnection;

    public MainInterfaz() {
        setTitle("Bienvenido");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        mongoDBConnection = MongoDBConnection.getInstance();

        JButton registroButton = new JButton("Registro");
        JButton loginButton = new JButton("Login");

        registroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirRegistro();
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirLogin();
            }
        });

        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(registroButton);
        panel.add(loginButton);

        add(panel);
    }

    private void abrirRegistro() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RegistroInterfaz().setVisible(true);
                dispose();
            }
        });
    }

    private void abrirLogin() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginInterfaz(mongoDBConnection).setVisible(true);
                dispose();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainInterfaz mainInterfaz = new MainInterfaz();
                mainInterfaz.setVisible(true);

                mainInterfaz.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                        mainInterfaz.mongoDBConnection.closeConnection();
                        System.exit(0);
                    }
                });
            }
        });
    }
}
