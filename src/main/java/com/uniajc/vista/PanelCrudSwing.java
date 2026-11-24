package com.uniajc.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public abstract class PanelCrudSwing<T> extends JPanel {

    protected static final Color COLOR_PRIMARIO = new Color(41, 128, 185);
    protected static final Color COLOR_SECUNDARIO = new Color(236, 240, 241);
    protected static final Color COLOR_EXITO = new Color(39, 174, 96);
    protected static final Color COLOR_PELIGRO = new Color(231, 76, 60);
    protected static final Color COLOR_NEUTRO = new Color(75, 85, 99);

    public interface Formulario {
        Map<String, JComponent> campos();

        void guardar();
    }

    private final DefaultTableModel modeloTabla;
    private final JTable tabla;
    private final JTextField textoBusqueda = new JTextField(25);
    private List<T> visibles = new ArrayList<>();

    protected PanelCrudSwing(String titulo, String[] columnas) {
        setLayout(new BorderLayout(15, 15));
        setBackground(COLOR_SECUNDARIO);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel etiquetaTitulo = new JLabel(titulo);
        etiquetaTitulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        etiquetaTitulo.setForeground(COLOR_PRIMARIO);

        JButton botonBuscar = crearBoton("Buscar", COLOR_PRIMARIO);
        botonBuscar.addActionListener(e -> buscar());
        textoBusqueda.addActionListener(e -> buscar());

        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelBusqueda.setBackground(COLOR_SECUNDARIO);
        panelBusqueda.add(new JLabel("Buscar:"));
        panelBusqueda.add(textoBusqueda);
        panelBusqueda.add(botonBuscar);

        JPanel panelNorte = new JPanel(new BorderLayout(10, 10));
        panelNorte.setBackground(COLOR_SECUNDARIO);
        panelNorte.add(etiquetaTitulo, BorderLayout.NORTH);
        panelNorte.add(panelBusqueda, BorderLayout.SOUTH);
        add(panelNorte, BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setRowHeight(25);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        DefaultTableCellRenderer renderEncabezado = new DefaultTableCellRenderer();
        renderEncabezado.setFont(new Font("Segoe UI", Font.BOLD, 13));
        renderEncabezado.setBackground(COLOR_PRIMARIO);
        renderEncabezado.setForeground(Color.WHITE);
        renderEncabezado.setHorizontalAlignment(JLabel.CENTER);
        renderEncabezado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        tabla.getTableHeader().setDefaultRenderer(renderEncabezado);
        JScrollPane desplazamiento = new JScrollPane(tabla);
        desplazamiento.setBorder(BorderFactory.createLineBorder(COLOR_PRIMARIO, 2));
        add(desplazamiento, BorderLayout.CENTER);

        JButton botonRegistrar = crearBoton("Registrar", COLOR_EXITO);
        botonRegistrar.addActionListener(e -> abrirFormulario(null));
        JButton botonEditar = crearBoton("Editar", COLOR_PRIMARIO);
        botonEditar.addActionListener(e -> editarSeleccionado());
        JButton botonEliminar = crearBoton("Eliminar", COLOR_PELIGRO);
        botonEliminar.addActionListener(e -> eliminarSeleccionado());
        JButton botonRecargar = crearBoton("Recargar", COLOR_NEUTRO);
        botonRecargar.addActionListener(e -> cargar());

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotones.setBackground(COLOR_SECUNDARIO);
        panelBotones.add(botonRegistrar);
        panelBotones.add(botonEditar);
        panelBotones.add(botonEliminar);
        panelBotones.add(botonRecargar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    protected abstract List<T> obtenerTodos();

    protected abstract Object[] fila(T registro);

    protected abstract int idDe(T registro);

    protected abstract Formulario crearFormulario(T seleccionado);

    protected abstract void eliminar(int id);

    protected final void iniciar() {
        cargar();
    }

    public final void cargar() {
        try {
            textoBusqueda.setText("");
            mostrar(obtenerTodos());
        } catch (RuntimeException ex) {
            mostrarError("Error al cargar los datos: " + ex.getMessage());
        }
    }

    protected JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setBackground(Color.WHITE);
        boton.setForeground(color);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(6, 16, 6, 16)));
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return boton;
    }

    private void mostrar(List<T> registros) {
        visibles = new ArrayList<>(registros);
        modeloTabla.setRowCount(0);
        for (T registro : visibles) {
            modeloTabla.addRow(fila(registro));
        }
    }

    private void buscar() {
        String consulta = textoBusqueda.getText().trim().toLowerCase();
        try {
            List<T> todos = obtenerTodos();
            if (consulta.isEmpty()) {
                mostrar(todos);
                return;
            }
            mostrar(todos.stream()
                    .filter(r -> Arrays.stream(fila(r))
                            .map(String::valueOf)
                            .collect(Collectors.joining(" "))
                            .toLowerCase()
                            .contains(consulta))
                    .collect(Collectors.toList()));
        } catch (RuntimeException ex) {
            mostrarError("Error en la búsqueda: " + ex.getMessage());
        }
    }

    private T seleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un registro de la tabla.", "Atención",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return visibles.get(fila);
    }

    private void editarSeleccionado() {
        T registro = seleccionado();
        if (registro != null) {
            abrirFormulario(registro);
        }
    }

    private void eliminarSeleccionado() {
        T registro = seleccionado();
        if (registro == null) {
            return;
        }
        int id = idDe(registro);
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Seguro que desea eliminar el registro con ID " + id + "?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            eliminar(id);
            JOptionPane.showMessageDialog(this, "Registro eliminado correctamente.", "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            cargar();
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void abrirFormulario(T registro) {
        Formulario formulario;
        try {
            formulario = crearFormulario(registro);
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
            return;
        }
        Window ventana = SwingUtilities.getWindowAncestor(this);
        JDialog dialogo = new JDialog(ventana,
                registro == null ? "Registrar" : "Editar", Dialog.ModalityType.APPLICATION_MODAL);

        JPanel campos = new JPanel(new GridLayout(0, 2, 10, 10));
        campos.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        formulario.campos().forEach((etiqueta, componente) -> {
            campos.add(new JLabel(etiqueta + ":"));
            campos.add(componente);
        });

        JButton botonGuardar = crearBoton("Guardar", COLOR_EXITO);
        JButton botonCancelar = crearBoton("Cancelar", COLOR_NEUTRO);
        botonGuardar.addActionListener(e -> {
            try {
                formulario.guardar();
                dialogo.dispose();
                cargar();
                JOptionPane.showMessageDialog(this, "Registro guardado correctamente.", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(dialogo, ex.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
            }
        });
        botonCancelar.addActionListener(e -> dialogo.dispose());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        botones.add(botonGuardar);
        botones.add(botonCancelar);

        dialogo.setLayout(new BorderLayout());
        dialogo.add(campos, BorderLayout.CENTER);
        dialogo.add(botones, BorderLayout.SOUTH);
        dialogo.pack();
        dialogo.setMinimumSize(new java.awt.Dimension(420, dialogo.getHeight()));
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
