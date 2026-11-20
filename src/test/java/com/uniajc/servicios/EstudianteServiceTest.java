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

import com.uniajc.dao.EstudianteDao;
import com.uniajc.modelo.Estudiante;

class EstudianteServiceTest {

    private FakeEstudianteDao dao;
    private EstudianteService servicio;

    @BeforeEach
    void preparar() {
        dao = new FakeEstudianteDao();
        servicio = new EstudianteService(dao);
    }

    @Test
    void registrarAsignaIdYNormalizaEspacios() {
        Estudiante estudiante = new Estudiante("  Ana ", " Pérez ", " ana@uniajc.edu.co ");
        servicio.registrarEstudiante(estudiante);
        assertEquals(1, estudiante.getId());
        assertEquals("Ana", estudiante.getName());
        assertEquals("ana@uniajc.edu.co", estudiante.getEmail());
    }

    @Test
    void registrarRechazaNombreCorto() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarEstudiante(new Estudiante("A", "Pérez", "ana@uniajc.edu.co")));
    }

    @Test
    void registrarRechazaApellidoVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarEstudiante(new Estudiante("Ana", "", "ana@uniajc.edu.co")));
    }

    @Test
    void registrarRechazaCorreoInvalido() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarEstudiante(new Estudiante("Ana", "Pérez", "correo-invalido")));
    }

    @Test
    void registrarRechazaCorreoDuplicado() {
        servicio.registrarEstudiante(new Estudiante("Ana", "Pérez", "ana@uniajc.edu.co"));
        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarEstudiante(new Estudiante("Luis", "Gómez", "ANA@uniajc.edu.co")));
    }

    @Test
    void actualizarRechazaCorreoDeOtroEstudiante() {
        servicio.registrarEstudiante(new Estudiante("Ana", "Pérez", "ana@uniajc.edu.co"));
        Estudiante luis = new Estudiante("Luis", "Gómez", "luis@uniajc.edu.co");
        servicio.registrarEstudiante(luis);
        Estudiante cambios = new Estudiante(luis.getId(), "Luis", "Gómez", "ana@uniajc.edu.co");
        assertThrows(IllegalArgumentException.class, () -> servicio.actualizarEstudiante(cambios));
    }

    @Test
    void actualizarPermiteConservarElMismoCorreo() {
        Estudiante ana = new Estudiante("Ana", "Pérez", "ana@uniajc.edu.co");
        servicio.registrarEstudiante(ana);
        servicio.actualizarEstudiante(new Estudiante(ana.getId(), "Ana María", "Pérez", "ana@uniajc.edu.co"));
        assertEquals("Ana María", servicio.buscarEstudiantePorId(ana.getId()).orElseThrow().getName());
    }

    @Test
    void actualizarRechazaEstudianteInexistente() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.actualizarEstudiante(new Estudiante(99, "Ana", "Pérez", "ana@uniajc.edu.co")));
    }

    @Test
    void eliminarRechazaEstudianteConInscripciones() {
        Estudiante ana = new Estudiante("Ana", "Pérez", "ana@uniajc.edu.co");
        servicio.registrarEstudiante(ana);
        dao.inscripciones.put(ana.getId(), 2);
        assertThrows(IllegalArgumentException.class, () -> servicio.eliminarEstudiante(ana.getId()));
        assertEquals(1, servicio.obtenerTodosLosEstudiantes().size());
    }

    @Test
    void eliminarQuitaAlEstudianteSinInscripciones() {
        Estudiante ana = new Estudiante("Ana", "Pérez", "ana@uniajc.edu.co");
        servicio.registrarEstudiante(ana);
        servicio.eliminarEstudiante(ana.getId());
        assertTrue(servicio.obtenerTodosLosEstudiantes().isEmpty());
    }

    @Test
    void buscarPorIdRechazaIdInvalido() {
        assertThrows(IllegalArgumentException.class, () -> servicio.buscarEstudiantePorId(0));
    }

    @Test
    void constructorRechazaDaoNulo() {
        assertThrows(IllegalArgumentException.class, () -> new EstudianteService(null));
    }

    private static class FakeEstudianteDao extends EstudianteDao {

        private final Map<Integer, Estudiante> datos = new LinkedHashMap<>();
        private final Map<Integer, Integer> inscripciones = new LinkedHashMap<>();
        private int siguienteId = 1;

        @Override
        public void guardar(Estudiante estudiante) {
            estudiante.setId(siguienteId++);
            datos.put(estudiante.getId(), estudiante);
        }

        @Override
        public List<Estudiante> obtenerTodos() {
            return new ArrayList<>(datos.values());
        }

        @Override
        public Optional<Estudiante> buscarPorId(int id) {
            return Optional.ofNullable(datos.get(id));
        }

        @Override
        public Optional<Estudiante> buscarPorEmail(String email) {
            return datos.values().stream().filter(e -> e.getEmail().equalsIgnoreCase(email)).findFirst();
        }

        @Override
        public int contarInscripciones(int idEstudiante) {
            return inscripciones.getOrDefault(idEstudiante, 0);
        }

        @Override
        public void actualizar(Estudiante estudiante) {
            datos.put(estudiante.getId(), estudiante);
        }

        @Override
        public void eliminar(int id) {
            datos.remove(id);
        }
    }
}
