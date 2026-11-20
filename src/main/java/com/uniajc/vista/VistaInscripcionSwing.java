package com.uniajc.vista;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.uniajc.controlador.ControladorInscripcion;
import com.uniajc.modelo.Inscripcion;
import com.uniajc.modelo.Opcion;

public class VistaInscripcionSwing extends PanelCrudSwing<Inscripcion> {

    private final ControladorInscripcion controlador;

    public VistaInscripcionSwing() {
        this(new ControladorInscripcion());
    }

    public VistaInscripcionSwing(ControladorInscripcion controlador) {
        super("Gestión de Inscripciones", new String[] {"ID", "Estudiante", "Grupo", "Nota final", "Estado"});
        this.controlador = controlador;
        iniciar();
    }

    @Override
    protected List<Inscripcion> obtenerTodos() {
        return controlador.listarTodas();
    }

    @Override
    protected Object[] fila(Inscripcion i) {
        return new Object[] {i.getId(), i.getEstudianteNombre(), i.getGrupoDescripcion(),
                i.getFinalGrade() == null ? "-" : String.format("%.2f", i.getFinalGrade()), i.getStatus()};
    }

    @Override
    protected int idDe(Inscripcion i) {
        return i.getId();
    }

    @Override
    protected void eliminar(int id) {
        controlador.eliminar(id);
    }

    @Override
    protected Formulario crearFormulario(Inscripcion actual) {
        return actual == null ? formularioNuevo() : formularioEdicion(actual);
    }

    private Formulario formularioNuevo() {
        JComboBox<Opcion> estudiantes = new JComboBox<>(controlador.listarEstudiantes().toArray(new Opcion[0]));
        JComboBox<Opcion> grupos = new JComboBox<>(controlador.listarGrupos().toArray(new Opcion[0]));

        Map<String, JComponent> campos = new LinkedHashMap<>();
        campos.put("Estudiante", estudiantes);
        campos.put("Grupo", grupos);

        return new Formulario() {
            @Override
            public Map<String, JComponent> campos() {
                return campos;
            }

            @Override
            public void guardar() {
                Opcion estudiante = (Opcion) estudiantes.getSelectedItem();
                Opcion grupo = (Opcion) grupos.getSelectedItem();
                if (estudiante == null || grupo == null) {
                    throw new IllegalArgumentException("Debe existir al menos un estudiante y un grupo registrados.");
                }
                controlador.registrar(estudiante.id(), grupo.id());
            }
        };
    }

    private Formulario formularioEdicion(Inscripcion actual) {
        JTextField nota = new JTextField(actual.getFinalGrade() == null ? "" : String.valueOf(actual.getFinalGrade()));
        JComboBox<String> estado = new JComboBox<>(Inscripcion.ESTADOS.toArray(new String[0]));
        estado.setSelectedItem(actual.getStatus());

        Map<String, JComponent> campos = new LinkedHashMap<>();
        campos.put("Estudiante", new JLabel(actual.getEstudianteNombre()));
        campos.put("Grupo", new JLabel(actual.getGrupoDescripcion()));
        campos.put("Nota final", nota);
        campos.put("Estado", estado);

        return new Formulario() {
            @Override
            public Map<String, JComponent> campos() {
                return campos;
            }

            @Override
            public void guardar() {
                controlador.actualizar(actual.getId(), interpretarNota(nota.getText()), (String) estado.getSelectedItem());
            }
        };
    }

    private Double interpretarNota(String texto) {
        String limpio = texto.trim();
        if (limpio.isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(limpio.replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("La nota debe ser un número.");
        }
    }
}
