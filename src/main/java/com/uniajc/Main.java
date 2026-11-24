package com.uniajc;

import java.util.Scanner;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.uniajc.config.ConexionPostgresDatabase;
import com.uniajc.vista.MenuPrincipalConsola;
import com.uniajc.vista.VistaPrincipalSwing;

public class Main {

    public static void main(String[] args) {
        if (args.length > 0 && "consola".equalsIgnoreCase(args[0])) {
            new MenuPrincipalConsola(new Scanner(System.in)).iniciar();
            ConexionPostgresDatabase.closeConnection();
            return;
        }

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignorada) {
                System.err.println("Se usará el aspecto visual por defecto.");
            }
            VistaPrincipalSwing ventana = new VistaPrincipalSwing();
            ventana.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    ConexionPostgresDatabase.closeConnection();
                }
            });
            ventana.setVisible(true);
        });
    }
}
