package com.uniajc.vista;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import com.uniajc.controlador.ControladorMateria;
import com.uniajc.modelo.Materia;

public class VistaMateriaSwing extends PanelCrudSwing<Materia> {

    private final ControladorMateria controlador;

    public VistaMateriaSwing() {
        this(new ControladorMateria());
    }

    public VistaMateriaSwing(ControladorMateria controlador) {
        super("Gestión de Materias", new String[] {"ID", "Nombre", "Créditos"});
        this.controlador = controlador;
        iniciar();
    }

    @Override
    protected List<Materia> obtenerTodos() {
        return controlador.listarTodas();
    }

    @Override
    protected Object[] fila(Materia m) {
        return new Object[] {m.getId(), m.getName(), m.getCredits()};
    }

    @Override
    protected int idDe(Materia m) {
        return m.getId();
    }

    @Override
    protected void eliminar(int id) {
        controlador.eliminar(id);
    }

    @Override
    protected Formulario crearFormulario(Materia actual) {
        JTextField nombre = new JTextField(actual == null ? "" : actual.getName());
        JSpinner creditos = new JSpinner(new SpinnerNumberModel(
                actual == null ? 3 : actual.getCredits(), Materia.CREDITOS_MINIMOS, Materia.CREDITOS_MAXIMOS, 1));

        Map<String, JComponent> campos = new LinkedHashMap<>();
        campos.put("Nombre", nombre);
        campos.put("Créditos", creditos);

        return new Formulario() {
            @Override
            public Map<String, JComponent> campos() {
                return campos;
            }

            @Override
            public void guardar() {
                int valorCreditos = (Integer) creditos.getValue();
                if (actual == null) {
                    controlador.registrar(nombre.getText(), valorCreditos);
                } else {
                    controlador.actualizar(actual.getId(), nombre.getText(), valorCreditos);
                }
            }
        };
    }
}
