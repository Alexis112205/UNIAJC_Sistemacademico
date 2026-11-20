package com.uniajc.servicios;

import java.util.List;
import java.util.Optional;

import com.uniajc.dao.GrupoDao;
import com.uniajc.modelo.Grupo;
import com.uniajc.modelo.Opcion;

public class GrupoService {

    private final GrupoDao grupoDao;

    public GrupoService(GrupoDao grupoDao) {
        if (grupoDao == null) {
            throw new IllegalArgumentException("El DAO no puede ser nulo.");
        }
        this.grupoDao = grupoDao;
    }

    public void registrarGrupo(Grupo grupo) {
        validar(grupo);
        grupoDao.guardar(grupo);
    }

    public List<Grupo> obtenerTodosLosGrupos() {
        return grupoDao.obtenerTodos();
    }

    public Optional<Grupo> buscarGrupoPorId(int id) {
        exigirIdValido(id);
        return grupoDao.buscarPorId(id);
    }

    public void actualizarGrupo(Grupo grupo) {
        if (grupo == null) {
            throw new IllegalArgumentException("El grupo no puede ser nulo.");
        }
        exigirIdValido(grupo.getId());
        if (grupoDao.buscarPorId(grupo.getId()).isEmpty()) {
            throw new IllegalArgumentException("No existe un grupo con ID: " + grupo.getId());
        }
        validar(grupo);
        grupoDao.actualizar(grupo);
    }

    public void eliminarGrupo(int id) {
        exigirIdValido(id);
        if (grupoDao.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe un grupo con ID: " + id);
        }
        if (grupoDao.contarInscripciones(id) > 0) {
            throw new IllegalArgumentException("No se puede eliminar: el grupo tiene estudiantes inscritos.");
        }
        grupoDao.eliminar(id);
    }

    public List<Opcion> listarMaterias() {
        return grupoDao.listarMaterias();
    }

    public List<Opcion> listarDocentes() {
        return grupoDao.listarDocentes();
    }

    private void exigirIdValido(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a cero.");
        }
    }

    private void validar(Grupo g) {
        if (g == null) {
            throw new IllegalArgumentException("El grupo no puede ser nulo.");
        }
        if (g.getIdMateria() <= 0 || !grupoDao.existeMateria(g.getIdMateria())) {
            throw new IllegalArgumentException("La materia seleccionada no existe.");
        }
        if (g.getIdDocente() <= 0 || !grupoDao.existeDocente(g.getIdDocente())) {
            throw new IllegalArgumentException("El docente seleccionado no existe.");
        }
        if (g.getClassroom() == null || g.getClassroom().trim().isEmpty()) {
            throw new IllegalArgumentException("El aula es obligatoria.");
        }
        if (g.getSchedule() == null || g.getSchedule().trim().isEmpty()) {
            throw new IllegalArgumentException("El horario es obligatorio.");
        }
        g.setClassroom(g.getClassroom().trim());
        g.setSchedule(g.getSchedule().trim());
        if (grupoDao.existeConflictoAula(g.getClassroom(), g.getSchedule(), g.getId())) {
            throw new IllegalArgumentException("El aula ya está ocupada en ese horario.");
        }
        if (grupoDao.existeConflictoDocente(g.getIdDocente(), g.getSchedule(), g.getId())) {
            throw new IllegalArgumentException("El docente ya tiene un grupo en ese horario.");
        }
    }
}
