package com.uniajc.vista;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JTextField;

import com.uniajc.controlador.ControladorEstudiante;
import com.uniajc.modelo.Estudiante;

public class VistaEstudianteSwing extends PanelCrudSwing<Estudiante> {

    private final ControladorEstudiante controlador;

    public VistaEstudianteSwing() {
        this(new ControladorEstudiante());
    }

    public VistaEstudianteSwing(ControladorEstudiante controlador) {
        super("Gestión de Estudiantes", new String[] {"ID", "Nombre", "Apellido", "Correo electrónico"});
        this.controlador = controlador;
        iniciar();
    }

    @Override
    protected List<Estudiante> obtenerTodos() {
        return controlador.listarTodos();
    }

    @Override
    protected Object[] fila(Estudiante e) {
        return new Object[] {e.getId(), e.getName(), e.getLastname(), e.getEmail()};
    }

    @Override
    protected int idDe(Estudiante e) {
        return e.getId();
    }

    @Override
    protected void eliminar(int id) {
        controlador.eliminar(id);
    }

    @Override
    protected Formulario crearFormulario(Estudiante actual) {
        JTextField nombre = new JTextField(actual == null ? "" : actual.getName());
        JTextField apellido = new JTextField(actual == null ? "" : actual.getLastname());
        JTextField email = new JTextField(actual == null ? "" : actual.getEmail());

        Map<String, JComponent> campos = new LinkedHashMap<>();
        campos.put("Nombre", nombre);
        campos.put("Apellido", apellido);
        campos.put("Correo electrónico", email);

        return new Formulario() {
            @Override
            public Map<String, JComponent> campos() {
                return campos;
            }

            @Override
            public void guardar() {
                if (actual == null) {
                    controlador.registrar(nombre.getText(), apellido.getText(), email.getText());
                } else {
                    controlador.actualizar(actual.getId(), nombre.getText(), apellido.getText(), email.getText());
                }
            }
        };
    }
}
