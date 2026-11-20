package com.uniajc.vista;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class VistaPrincipalSwing extends JFrame {

    private static final Color COLOR_PRIMARIO = new Color(41, 128, 185);
    private static final Color COLOR_SECUNDARIO = new Color(236, 240, 241);
    private static final Color COLOR_TEXTO = new Color(44, 62, 80);

    private final CardLayout disposicion = new CardLayout();
    private final JPanel contenido = new JPanel(disposicion);
    private final Map<String, Supplier<PanelCrudSwing<?>>> fabricas = new LinkedHashMap<>();
    private final Map<String, PanelCrudSwing<?>> paneles = new HashMap<>();

    public VistaPrincipalSwing() {
        super("Sistema de Gestión Académica UNIAJC");
        fabricas.put("Estudiantes", VistaEstudianteSwing::new);
        fabricas.put("Docentes", VistaDocenteSwing::new);
        fabricas.put("Materias", VistaMateriaSwing::new);
        fabricas.put("Grupos", VistaGrupoSwing::new);
        fabricas.put("Inscripciones", VistaInscripcionSwing::new);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 620);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(crearMenu(), BorderLayout.WEST);
        contenido.setBackground(COLOR_SECUNDARIO);
        contenido.add(crearBienvenida(), "inicio");
        add(contenido, BorderLayout.CENTER);
    }

    private JPanel crearMenu() {
        JPanel menu = new JPanel(new GridLayout(0, 1, 0, 8));
        menu.setBackground(COLOR_PRIMARIO);
        menu.setBorder(BorderFactory.createEmptyBorder(15, 12, 15, 12));
        menu.setPreferredSize(new Dimension(190, 0));

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(COLOR_PRIMARIO);
        JLabel titulo = new JLabel("UNIAJC", JLabel.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(Color.WHITE);
        titulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        contenedor.add(titulo, BorderLayout.NORTH);

        for (String nombre : fabricas.keySet()) {
            JButton boton = new JButton(nombre);
            boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
            boton.setBackground(Color.WHITE);
            boton.setForeground(COLOR_PRIMARIO);
            boton.setFocusPainted(false);
            boton.addActionListener(e -> mostrar(nombre));
            menu.add(boton);
        }
        contenedor.add(menu, BorderLayout.CENTER);
        contenedor.setPreferredSize(new Dimension(190, 0));
        return contenedor;
    }

    private JPanel crearBienvenida() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_SECUNDARIO);
        JLabel mensaje = new JLabel("Seleccione un módulo del menú", JLabel.CENTER);
        mensaje.setFont(new Font("Segoe UI", Font.BOLD, 20));
        mensaje.setForeground(COLOR_TEXTO);
        panel.add(mensaje, BorderLayout.CENTER);
        return panel;
    }

    private void mostrar(String nombre) {
        PanelCrudSwing<?> panel = paneles.get(nombre);
        if (panel == null) {
            panel = fabricas.get(nombre).get();
            paneles.put(nombre, panel);
            contenido.add(panel, nombre);
        } else {
            panel.cargar();
        }
        disposicion.show(contenido, nombre);
    }
}
