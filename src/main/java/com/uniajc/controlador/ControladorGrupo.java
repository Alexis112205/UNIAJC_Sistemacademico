package com.uniajc.controlador;

import java.util.List;
import java.util.Optional;

import com.uniajc.dao.GrupoDao;
import com.uniajc.modelo.Grupo;
import com.uniajc.modelo.Opcion;
import com.uniajc.servicios.GrupoService;

public class ControladorGrupo {

    private final GrupoService servicio;

    public ControladorGrupo() {
        this(new GrupoService(new GrupoDao()));
    }

    public ControladorGrupo(GrupoService servicio) {
        this.servicio = servicio;
    }

    public void registrar(int idMateria, int idDocente, String aula, String horario) {
        servicio.registrarGrupo(new Grupo(idMateria, idDocente, aula, horario));
    }

    public List<Grupo> listarTodos() {
        return servicio.obtenerTodosLosGrupos();
    }

    public Optional<Grupo> buscarPorId(int id) {
        return servicio.buscarGrupoPorId(id);
    }

    public void actualizar(int id, int idMateria, int idDocente, String aula, String horario) {
        servicio.actualizarGrupo(new Grupo(id, idMateria, idDocente, aula, horario));
    }

    public void eliminar(int id) {
        servicio.eliminarGrupo(id);
    }

    public List<Opcion> listarMaterias() {
        return servicio.listarMaterias();
    }

    public List<Opcion> listarDocentes() {
        return servicio.listarDocentes();
    }
}
