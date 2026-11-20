package com.uniajc.controlador;

import java.util.List;
import java.util.Optional;

import com.uniajc.dao.MateriaDao;
import com.uniajc.modelo.Materia;
import com.uniajc.servicios.MateriaService;

public class ControladorMateria {

    private final MateriaService servicio;

    public ControladorMateria() {
        this(new MateriaService(new MateriaDao()));
    }

    public ControladorMateria(MateriaService servicio) {
        this.servicio = servicio;
    }

    public void registrar(String nombre, int creditos) {
        servicio.registrarMateria(new Materia(nombre, creditos));
    }

    public List<Materia> listarTodas() {
        return servicio.obtenerTodasLasMaterias();
    }

    public Optional<Materia> buscarPorId(int id) {
        return servicio.buscarMateriaPorId(id);
    }

    public void actualizar(int id, String nombre, int creditos) {
        servicio.actualizarMateria(new Materia(id, nombre, creditos));
    }

    public void eliminar(int id) {
        servicio.eliminarMateria(id);
    }
}
