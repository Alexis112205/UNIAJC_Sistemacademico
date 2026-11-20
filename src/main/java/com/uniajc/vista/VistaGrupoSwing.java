package com.uniajc.vista;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;

import com.uniajc.controlador.ControladorGrupo;
import com.uniajc.modelo.Grupo;
import com.uniajc.modelo.Opcion;

public class VistaGrupoSwing extends PanelCrudSwing<Grupo> {

    private final ControladorGrupo controlador;

    public VistaGrupoSwing() {
        this(new ControladorGrupo());
    }

    public VistaGrupoSwing(ControladorGrupo controlador) {
        super("Gestión de Grupos", new String[] {"ID", "Materia", "Docente", "Aula", "Horario"});
        this.controlador = controlador;
        iniciar();
    }

    @Override
    protected List<Grupo> obtenerTodos() {
        return controlador.listarTodos();
    }

    @Override
    protected Object[] fila(Grupo g) {
        return new Object[] {g.getId(), g.getMateriaNombre(), g.getDocenteNombre(), g.getClassroom(), g.getSchedule()};
    }

    @Override
    protected int idDe(Grupo g) {
        return g.getId();
    }

    @Override
    protected void eliminar(int id) {
        controlador.eliminar(id);
    }

    @Override
    protected Formulario crearFormulario(Grupo actual) {
        JComboBox<Opcion> materias = crearCombo(controlador.listarMaterias(), actual == null ? 0 : actual.getIdMateria());
        JComboBox<Opcion> docentes = crearCombo(controlador.listarDocentes(), actual == null ? 0 : actual.getIdDocente());
        JTextField aula = new JTextField(actual == null ? "" : actual.getClassroom());
        JTextField horario = new JTextField(actual == null ? "" : actual.getSchedule());

        Map<String, JComponent> campos = new LinkedHashMap<>();
        campos.put("Materia", materias);
        campos.put("Docente", docentes);
        campos.put("Aula", aula);
        campos.put("Horario", horario);

        return new Formulario() {
            @Override
            public Map<String, JComponent> campos() {
                return campos;
            }

            @Override
            public void guardar() {
                Opcion materia = (Opcion) materias.getSelectedItem();
                Opcion docente = (Opcion) docentes.getSelectedItem();
                if (materia == null || docente == null) {
                    throw new IllegalArgumentException("Debe existir al menos una materia y un docente registrados.");
                }
                if (actual == null) {
                    controlador.registrar(materia.id(), docente.id(), aula.getText(), horario.getText());
                } else {
                    controlador.actualizar(actual.getId(), materia.id(), docente.id(), aula.getText(), horario.getText());
                }
            }
        };
    }

    private JComboBox<Opcion> crearCombo(List<Opcion> opciones, int idSeleccionado) {
        JComboBox<Opcion> combo = new JComboBox<>();
        for (Opcion opcion : opciones) {
            combo.addItem(opcion);
            if (opcion.id() == idSeleccionado) {
                combo.setSelectedItem(opcion);
            }
        }
        return combo;
    }
}
