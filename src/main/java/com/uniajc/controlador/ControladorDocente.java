package com.uniajc.controlador;

import java.util.List;
import java.util.Optional;

import com.uniajc.dao.DocenteDao;
import com.uniajc.modelo.Docente;
import com.uniajc.servicios.DocenteService;

public class ControladorDocente {

    private final DocenteService servicio;

    public ControladorDocente() {
        this(new DocenteService(new DocenteDao()));
    }

    public ControladorDocente(DocenteService servicio) {
        this.servicio = servicio;
    }

    public void registrar(String nombre, String especialidad, String email) {
        servicio.registrarDocente(new Docente(nombre, especialidad, email));
    }

    public List<Docente> listarTodos() {
        return servicio.obtenerTodosLosDocentes();
    }

    public Optional<Docente> buscarPorId(int id) {
        return servicio.buscarDocentePorId(id);
    }

    public void actualizar(int id, String nombre, String especialidad, String email) {
        servicio.actualizarDocente(new Docente(id, nombre, especialidad, email));
    }

    public void eliminar(int id) {
        servicio.eliminarDocente(id);
    }
}
