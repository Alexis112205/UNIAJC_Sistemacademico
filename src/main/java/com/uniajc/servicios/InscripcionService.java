package com.uniajc.servicios;

import java.util.List;
import java.util.Optional;

import com.uniajc.dao.InscripcionDao;
import com.uniajc.modelo.Inscripcion;
import com.uniajc.modelo.Opcion;

public class InscripcionService {

    private final InscripcionDao inscripcionDao;

    public InscripcionService(InscripcionDao inscripcionDao) {
        if (inscripcionDao == null) {
            throw new IllegalArgumentException("El DAO no puede ser nulo.");
        }
        this.inscripcionDao = inscripcionDao;
    }

    public void registrarInscripcion(Inscripcion inscripcion) {
        if (inscripcion == null) {
            throw new IllegalArgumentException("La inscripción no puede ser nula.");
        }
        if (inscripcion.getIdEstudiante() <= 0 || !inscripcionDao.existeEstudiante(inscripcion.getIdEstudiante())) {
            throw new IllegalArgumentException("El estudiante seleccionado no existe.");
        }
        if (inscripcion.getIdGrupo() <= 0 || !inscripcionDao.existeGrupo(inscripcion.getIdGrupo())) {
            throw new IllegalArgumentException("El grupo seleccionado no existe.");
        }
        if (inscripcionDao.buscarPorEstudianteYGrupo(inscripcion.getIdEstudiante(), inscripcion.getIdGrupo()).isPresent()) {
            throw new IllegalArgumentException("El estudiante ya está inscrito en ese grupo.");
        }
        inscripcion.setStatus(Inscripcion.ACTIVO);
        inscripcion.setFinalGrade(null);
        inscripcionDao.guardar(inscripcion);
    }

    public List<Inscripcion> obtenerTodasLasInscripciones() {
        return inscripcionDao.obtenerTodos();
    }

    public Optional<Inscripcion> buscarInscripcionPorId(int id) {
        exigirIdValido(id);
        return inscripcionDao.buscarPorId(id);
    }

    public void actualizarInscripcion(Inscripcion cambios) {
        if (cambios == null) {
            throw new IllegalArgumentException("La inscripción no puede ser nula.");
        }
        exigirIdValido(cambios.getId());
        Inscripcion existente = inscripcionDao.buscarPorId(cambios.getId())
                .orElseThrow(() -> new IllegalArgumentException("No existe una inscripción con ID: " + cambios.getId()));
        validarNotaYEstado(cambios.getFinalGrade(), cambios.getStatus());
        existente.setFinalGrade(cambios.getFinalGrade());
        existente.setStatus(cambios.getStatus());
        inscripcionDao.actualizar(existente);
    }

    public void eliminarInscripcion(int id) {
        exigirIdValido(id);
        if (inscripcionDao.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe una inscripción con ID: " + id);
        }
        inscripcionDao.eliminar(id);
    }

    public List<Opcion> listarEstudiantes() {
        return inscripcionDao.listarEstudiantes();
    }

    public List<Opcion> listarGrupos() {
        return inscripcionDao.listarGrupos();
    }

    private void exigirIdValido(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a cero.");
        }
    }

    private void validarNotaYEstado(Double nota, String estado) {
        if (estado == null || !Inscripcion.ESTADOS.contains(estado)) {
            throw new IllegalArgumentException("El estado debe ser uno de: " + String.join(", ", Inscripcion.ESTADOS) + ".");
        }
        if (nota != null && (nota < Inscripcion.NOTA_MINIMA || nota > Inscripcion.NOTA_MAXIMA)) {
            throw new IllegalArgumentException("La nota debe estar entre " + Inscripcion.NOTA_MINIMA
                    + " y " + Inscripcion.NOTA_MAXIMA + ".");
        }
        switch (estado) {
            case Inscripcion.ACTIVO -> {
                if (nota != null) {
                    throw new IllegalArgumentException("Una inscripción activa no puede tener nota final.");
                }
            }
            case Inscripcion.APROBADO -> {
                if (nota == null || nota < Inscripcion.NOTA_APROBACION) {
                    throw new IllegalArgumentException("Para aprobar, la nota final debe ser al menos "
                            + Inscripcion.NOTA_APROBACION + ".");
                }
            }
            case Inscripcion.REPROBADO -> {
                if (nota == null || nota >= Inscripcion.NOTA_APROBACION) {
                    throw new IllegalArgumentException("Para reprobar, la nota final debe ser menor a "
                            + Inscripcion.NOTA_APROBACION + ".");
                }
            }
            default -> {
            }
        }
    }
}
