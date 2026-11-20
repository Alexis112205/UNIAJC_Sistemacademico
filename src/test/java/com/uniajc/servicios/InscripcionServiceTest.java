package com.uniajc.servicios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.uniajc.dao.InscripcionDao;
import com.uniajc.modelo.Inscripcion;

class InscripcionServiceTest {

    private FakeInscripcionDao dao;
    private InscripcionService servicio;

    @BeforeEach
    void preparar() {
        dao = new FakeInscripcionDao();
        dao.estudiantes.add(1);
        dao.estudiantes.add(2);
        dao.grupos.add(5);
        dao.grupos.add(6);
        servicio = new InscripcionService(dao);
    }

    @Test
    void registrarCreaInscripcionActivaSinNota() {
        Inscripcion inscripcion = new Inscripcion(1, 5);
        inscripcion.setFinalGrade(4.5);
        inscripcion.setStatus(Inscripcion.APROBADO);
        servicio.registrarInscripcion(inscripcion);
        assertEquals(1, inscripcion.getId());
        assertEquals(Inscripcion.ACTIVO, inscripcion.getStatus());
        assertNull(inscripcion.getFinalGrade());
    }

    @Test
    void registrarRechazaEstudianteInexistente() {
        assertThrows(IllegalArgumentException.class, () -> servicio.registrarInscripcion(new Inscripcion(99, 5)));
    }

    @Test
    void registrarRechazaGrupoInexistente() {
        assertThrows(IllegalArgumentException.class, () -> servicio.registrarInscripcion(new Inscripcion(1, 99)));
    }

    @Test
    void registrarRechazaInscripcionDuplicada() {
        servicio.registrarInscripcion(new Inscripcion(1, 5));
        assertThrows(IllegalArgumentException.class, () -> servicio.registrarInscripcion(new Inscripcion(1, 5)));
    }

    @Test
    void registrarPermiteMismoEstudianteEnOtroGrupo() {
        servicio.registrarInscripcion(new Inscripcion(1, 5));
        servicio.registrarInscripcion(new Inscripcion(1, 6));
        assertEquals(2, servicio.obtenerTodasLasInscripciones().size());
    }

    @Test
    void actualizarAprobadoConNotaSuficiente() {
        Inscripcion inscripcion = crearInscripcion();
        servicio.actualizarInscripcion(cambios(inscripcion.getId(), 3.0, Inscripcion.APROBADO));
        Inscripcion guardada = servicio.buscarInscripcionPorId(inscripcion.getId()).orElseThrow();
        assertEquals(Inscripcion.APROBADO, guardada.getStatus());
        assertEquals(3.0, guardada.getFinalGrade().doubleValue());
    }

    @Test
    void actualizarRechazaAprobadoConNotaBaja() {
        Inscripcion inscripcion = crearInscripcion();
        assertThrows(IllegalArgumentException.class,
                () -> servicio.actualizarInscripcion(cambios(inscripcion.getId(), 2.9, Inscripcion.APROBADO)));
    }

    @Test
    void actualizarRechazaAprobadoSinNota() {
        Inscripcion inscripcion = crearInscripcion();
        assertThrows(IllegalArgumentException.class,
                () -> servicio.actualizarInscripcion(cambios(inscripcion.getId(), null, Inscripcion.APROBADO)));
    }

    @Test
    void actualizarReprobadoConNotaBaja() {
        Inscripcion inscripcion = crearInscripcion();
        servicio.actualizarInscripcion(cambios(inscripcion.getId(), 1.5, Inscripcion.REPROBADO));
        assertEquals(Inscripcion.REPROBADO, servicio.buscarInscripcionPorId(inscripcion.getId()).orElseThrow().getStatus());
    }

    @Test
    void actualizarRechazaReprobadoConNotaAprobatoria() {
        Inscripcion inscripcion = crearInscripcion();
        assertThrows(IllegalArgumentException.class,
                () -> servicio.actualizarInscripcion(cambios(inscripcion.getId(), 4.0, Inscripcion.REPROBADO)));
    }

    @Test
    void actualizarRechazaActivoConNota() {
        Inscripcion inscripcion = crearInscripcion();
        assertThrows(IllegalArgumentException.class,
                () -> servicio.actualizarInscripcion(cambios(inscripcion.getId(), 3.5, Inscripcion.ACTIVO)));
    }

    @Test
    void actualizarPermiteCancelarSinNota() {
        Inscripcion inscripcion = crearInscripcion();
        servicio.actualizarInscripcion(cambios(inscripcion.getId(), null, Inscripcion.CANCELADO));
        assertEquals(Inscripcion.CANCELADO, servicio.buscarInscripcionPorId(inscripcion.getId()).orElseThrow().getStatus());
    }

    @Test
    void actualizarRechazaNotaFueraDeRango() {
        Inscripcion inscripcion = crearInscripcion();
        assertThrows(IllegalArgumentException.class,
                () -> servicio.actualizarInscripcion(cambios(inscripcion.getId(), 5.1, Inscripcion.APROBADO)));
        assertThrows(IllegalArgumentException.class,
                () -> servicio.actualizarInscripcion(cambios(inscripcion.getId(), -0.1, Inscripcion.REPROBADO)));
    }

    @Test
    void actualizarRechazaEstadoDesconocido() {
        Inscripcion inscripcion = crearInscripcion();
        assertThrows(IllegalArgumentException.class,
                () -> servicio.actualizarInscripcion(cambios(inscripcion.getId(), null, "PENDIENTE")));
    }

    @Test
    void actualizarNoCambiaEstudianteNiGrupo() {
        Inscripcion inscripcion = crearInscripcion();
        Inscripcion intento = cambios(inscripcion.getId(), 4.0, Inscripcion.APROBADO);
        intento.setIdEstudiante(2);
        intento.setIdGrupo(6);
        servicio.actualizarInscripcion(intento);
        Inscripcion guardada = servicio.buscarInscripcionPorId(inscripcion.getId()).orElseThrow();
        assertEquals(1, guardada.getIdEstudiante());
        assertEquals(5, guardada.getIdGrupo());
    }

    @Test
    void actualizarRechazaInscripcionInexistente() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.actualizarInscripcion(cambios(77, 4.0, Inscripcion.APROBADO)));
    }

    @Test
    void eliminarQuitaLaInscripcion() {
        Inscripcion inscripcion = crearInscripcion();
        servicio.eliminarInscripcion(inscripcion.getId());
        assertTrue(servicio.obtenerTodasLasInscripciones().isEmpty());
    }

    @Test
    void eliminarRechazaInscripcionInexistente() {
        assertThrows(IllegalArgumentException.class, () -> servicio.eliminarInscripcion(77));
    }

    private Inscripcion crearInscripcion() {
        Inscripcion inscripcion = new Inscripcion(1, 5);
        servicio.registrarInscripcion(inscripcion);
        return inscripcion;
    }

    private Inscripcion cambios(int id, Double nota, String estado) {
        Inscripcion cambios = new Inscripcion();
        cambios.setId(id);
        cambios.setFinalGrade(nota);
        cambios.setStatus(estado);
        return cambios;
    }

    private static class FakeInscripcionDao extends InscripcionDao {

        private final Map<Integer, Inscripcion> datos = new LinkedHashMap<>();
        private final List<Integer> estudiantes = new ArrayList<>();
        private final List<Integer> grupos = new ArrayList<>();
        private int siguienteId = 1;

        @Override
        public void guardar(Inscripcion inscripcion) {
            inscripcion.setId(siguienteId++);
            datos.put(inscripcion.getId(), inscripcion);
        }

        @Override
        public List<Inscripcion> obtenerTodos() {
            return new ArrayList<>(datos.values());
        }

        @Override
        public Optional<Inscripcion> buscarPorId(int id) {
            return Optional.ofNullable(datos.get(id));
        }

        @Override
        public Optional<Inscripcion> buscarPorEstudianteYGrupo(int idEstudiante, int idGrupo) {
            return datos.values().stream()
                    .filter(i -> i.getIdEstudiante() == idEstudiante && i.getIdGrupo() == idGrupo)
                    .findFirst();
        }

        @Override
        public boolean existeEstudiante(int idEstudiante) {
            return estudiantes.contains(idEstudiante);
        }

        @Override
        public boolean existeGrupo(int idGrupo) {
            return grupos.contains(idGrupo);
        }

        @Override
        public void actualizar(Inscripcion inscripcion) {
            datos.put(inscripcion.getId(), inscripcion);
        }

        @Override
        public void eliminar(int id) {
            datos.remove(id);
        }
    }
}
