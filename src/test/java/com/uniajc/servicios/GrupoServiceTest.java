package com.uniajc.servicios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.uniajc.dao.GrupoDao;
import com.uniajc.modelo.Grupo;

class GrupoServiceTest {

    private FakeGrupoDao dao;
    private GrupoService servicio;

    @BeforeEach
    void preparar() {
        dao = new FakeGrupoDao();
        dao.materias.add(1);
        dao.materias.add(2);
        dao.docentes.add(10);
        dao.docentes.add(20);
        servicio = new GrupoService(dao);
    }

    @Test
    void registrarAsignaIdYNormalizaTextos() {
        Grupo grupo = new Grupo(1, 10, " A-101 ", " Lunes 7-9 ");
        servicio.registrarGrupo(grupo);
        assertEquals(1, grupo.getId());
        assertEquals("A-101", grupo.getClassroom());
        assertEquals("Lunes 7-9", grupo.getSchedule());
    }

    @Test
    void registrarRechazaMateriaInexistente() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarGrupo(new Grupo(99, 10, "A-101", "Lunes 7-9")));
    }

    @Test
    void registrarRechazaDocenteInexistente() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarGrupo(new Grupo(1, 99, "A-101", "Lunes 7-9")));
    }

    @Test
    void registrarRechazaAulaVacia() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarGrupo(new Grupo(1, 10, " ", "Lunes 7-9")));
    }

    @Test
    void registrarRechazaHorarioVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarGrupo(new Grupo(1, 10, "A-101", "")));
    }

    @Test
    void registrarRechazaAulaOcupadaEnElMismoHorario() {
        servicio.registrarGrupo(new Grupo(1, 10, "A-101", "Lunes 7-9"));
        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarGrupo(new Grupo(2, 20, "a-101", "lunes 7-9")));
    }

    @Test
    void registrarRechazaDocenteOcupadoEnElMismoHorario() {
        servicio.registrarGrupo(new Grupo(1, 10, "A-101", "Lunes 7-9"));
        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarGrupo(new Grupo(2, 10, "B-202", "Lunes 7-9")));
    }

    @Test
    void registrarPermiteMismoDocenteEnOtroHorario() {
        servicio.registrarGrupo(new Grupo(1, 10, "A-101", "Lunes 7-9"));
        servicio.registrarGrupo(new Grupo(2, 10, "A-101", "Martes 7-9"));
        assertEquals(2, servicio.obtenerTodosLosGrupos().size());
    }

    @Test
    void actualizarNoChocaConSigoMismo() {
        Grupo grupo = new Grupo(1, 10, "A-101", "Lunes 7-9");
        servicio.registrarGrupo(grupo);
        servicio.actualizarGrupo(new Grupo(grupo.getId(), 2, 10, "A-101", "Lunes 7-9"));
        assertEquals(2, servicio.buscarGrupoPorId(grupo.getId()).orElseThrow().getIdMateria());
    }

    @Test
    void actualizarRechazaGrupoInexistente() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.actualizarGrupo(new Grupo(88, 1, 10, "A-101", "Lunes 7-9")));
    }

    @Test
    void eliminarRechazaGrupoConInscritos() {
        Grupo grupo = new Grupo(1, 10, "A-101", "Lunes 7-9");
        servicio.registrarGrupo(grupo);
        dao.inscripciones.put(grupo.getId(), 3);
        assertThrows(IllegalArgumentException.class, () -> servicio.eliminarGrupo(grupo.getId()));
    }

    @Test
    void eliminarQuitaElGrupoSinInscritos() {
        Grupo grupo = new Grupo(1, 10, "A-101", "Lunes 7-9");
        servicio.registrarGrupo(grupo);
        servicio.eliminarGrupo(grupo.getId());
        assertTrue(servicio.obtenerTodosLosGrupos().isEmpty());
    }

    private static class FakeGrupoDao extends GrupoDao {

        private final Map<Integer, Grupo> datos = new LinkedHashMap<>();
        private final List<Integer> materias = new ArrayList<>();
        private final List<Integer> docentes = new ArrayList<>();
        private final Map<Integer, Integer> inscripciones = new LinkedHashMap<>();
        private int siguienteId = 1;

        @Override
        public void guardar(Grupo grupo) {
            grupo.setId(siguienteId++);
            datos.put(grupo.getId(), grupo);
        }

        @Override
        public List<Grupo> obtenerTodos() {
            return new ArrayList<>(datos.values());
        }

        @Override
        public Optional<Grupo> buscarPorId(int id) {
            return Optional.ofNullable(datos.get(id));
        }

        @Override
        public boolean existeMateria(int idMateria) {
            return materias.contains(idMateria);
        }

        @Override
        public boolean existeDocente(int idDocente) {
            return docentes.contains(idDocente);
        }

        @Override
        public boolean existeConflictoAula(String aula, String horario, int excluirId) {
            return datos.values().stream().anyMatch(g -> g.getId() != excluirId
                    && g.getClassroom().equalsIgnoreCase(aula) && g.getSchedule().equalsIgnoreCase(horario));
        }

        @Override
        public boolean existeConflictoDocente(int idDocente, String horario, int excluirId) {
            return datos.values().stream().anyMatch(g -> g.getId() != excluirId
                    && g.getIdDocente() == idDocente && g.getSchedule().equalsIgnoreCase(horario));
        }

        @Override
        public int contarInscripciones(int idGrupo) {
            return inscripciones.getOrDefault(idGrupo, 0);
        }

        @Override
        public void actualizar(Grupo grupo) {
            datos.put(grupo.getId(), grupo);
        }

        @Override
        public void eliminar(int id) {
            datos.remove(id);
        }
    }
}
