package com.uniajc.vista;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JTextField;

import com.uniajc.controlador.ControladorDocente;
import com.uniajc.modelo.Docente;

public class VistaDocenteSwing extends PanelCrudSwing<Docente> {

    private final ControladorDocente controlador;

    public VistaDocenteSwing() {
        this(new ControladorDocente());
    }

    public VistaDocenteSwing(ControladorDocente controlador) {
        super("Gestión de Docentes", new String[] {"ID", "Nombre", "Especialidad", "Correo electrónico"});
        this.controlador = controlador;
        iniciar();
    }

    @Override
    protected List<Docente> obtenerTodos() {
        return controlador.listarTodos();
    }

    @Override
    protected Object[] fila(Docente d) {
        return new Object[] {d.getId(), d.getName(), d.getSpecialty(), d.getEmail()};
    }

    @Override
    protected int idDe(Docente d) {
        return d.getId();
    }

    @Override
    protected void eliminar(int id) {
        controlador.eliminar(id);
    }

    @Override
    protected Formulario crearFormulario(Docente actual) {
        JTextField nombre = new JTextField(actual == null ? "" : actual.getName());
        JTextField especialidad = new JTextField(actual == null ? "" : actual.getSpecialty());
        JTextField email = new JTextField(actual == null ? "" : actual.getEmail());

        Map<String, JComponent> campos = new LinkedHashMap<>();
        campos.put("Nombre", nombre);
        campos.put("Especialidad", especialidad);
        campos.put("Correo electrónico", email);

        return new Formulario() {
            @Override
            public Map<String, JComponent> campos() {
                return campos;
            }

            @Override
            public void guardar() {
                if (actual == null) {
                    controlador.registrar(nombre.getText(), especialidad.getText(), email.getText());
                } else {
                    controlador.actualizar(actual.getId(), nombre.getText(), especialidad.getText(), email.getText());
                }
            }
        };
    }
}
