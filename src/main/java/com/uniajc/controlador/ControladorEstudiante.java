package com.uniajc.controlador;

import java.util.List;
import java.util.Optional;

import com.uniajc.dao.EstudianteDao;
import com.uniajc.modelo.Estudiante;
import com.uniajc.servicios.EstudianteService;

public class ControladorEstudiante {

    private final EstudianteService servicio;

    public ControladorEstudiante() {
        this(new EstudianteService(new EstudianteDao()));
    }

    public ControladorEstudiante(EstudianteService servicio) {
        this.servicio = servicio;
    }

    public void registrar(String nombre, String apellido, String email) {
        servicio.registrarEstudiante(new Estudiante(nombre, apellido, email));
    }

    public List<Estudiante> listarTodos() {
        return servicio.obtenerTodosLosEstudiantes();
    }

    public Optional<Estudiante> buscarPorId(int id) {
        return servicio.buscarEstudiantePorId(id);
    }

    public void actualizar(int id, String nombre, String apellido, String email) {
        servicio.actualizarEstudiante(new Estudiante(id, nombre, apellido, email));
    }

    public void eliminar(int id) {
        servicio.eliminarEstudiante(id);
    }
}
