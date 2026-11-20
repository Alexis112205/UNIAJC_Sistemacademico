package com.uniajc.controlador;

import java.util.List;
import java.util.Optional;

import com.uniajc.dao.InscripcionDao;
import com.uniajc.modelo.Inscripcion;
import com.uniajc.modelo.Opcion;
import com.uniajc.servicios.InscripcionService;

public class ControladorInscripcion {

    private final InscripcionService servicio;

    public ControladorInscripcion() {
        this(new InscripcionService(new InscripcionDao()));
    }

    public ControladorInscripcion(InscripcionService servicio) {
        this.servicio = servicio;
    }

    public void registrar(int idEstudiante, int idGrupo) {
        servicio.registrarInscripcion(new Inscripcion(idEstudiante, idGrupo));
    }

    public List<Inscripcion> listarTodas() {
        return servicio.obtenerTodasLasInscripciones();
    }

    public Optional<Inscripcion> buscarPorId(int id) {
        return servicio.buscarInscripcionPorId(id);
    }

    public void actualizar(int id, Double notaFinal, String estado) {
        Inscripcion cambios = new Inscripcion();
        cambios.setId(id);
        cambios.setFinalGrade(notaFinal);
        cambios.setStatus(estado);
        servicio.actualizarInscripcion(cambios);
    }

    public void eliminar(int id) {
        servicio.eliminarInscripcion(id);
    }

    public List<Opcion> listarEstudiantes() {
        return servicio.listarEstudiantes();
    }

    public List<Opcion> listarGrupos() {
        return servicio.listarGrupos();
    }
}
