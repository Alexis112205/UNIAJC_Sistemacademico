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

import com.uniajc.dao.DocenteDao;
import com.uniajc.modelo.Docente;

class DocenteServiceTest {

    private FakeDocenteDao dao;
    private DocenteService servicio;

    @BeforeEach
    void preparar() {
        dao = new FakeDocenteDao();
        servicio = new DocenteService(dao);
    }

    @Test
    void registrarAsignaIdYNormalizaEspacios() {
        Docente docente = new Docente(" Marta ", " Bases de datos ", " marta@uniajc.edu.co ");
        servicio.registrarDocente(docente);
        assertEquals(1, docente.getId());
        assertEquals("Marta", docente.getName());
        assertEquals("Bases de datos", docente.getSpecialty());
        assertEquals("marta@uniajc.edu.co", docente.getEmail());
    }

    @Test
    void registrarRechazaNombreCorto() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarDocente(new Docente("M", "Bases de datos", "marta@uniajc.edu.co")));
    }

    @Test
    void registrarRechazaEspecialidadVacia() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarDocente(new Docente("Marta", " ", "marta@uniajc.edu.co")));
    }

    @Test
    void registrarRechazaCorreoInvalido() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarDocente(new Docente("Marta", "Bases de datos", "marta@")));
    }

    @Test
    void registrarRechazaCorreoDuplicado() {
        servicio.registrarDocente(new Docente("Marta", "Bases de datos", "marta@uniajc.edu.co"));
        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarDocente(new Docente("Pedro", "Redes", "MARTA@uniajc.edu.co")));
    }

    @Test
    void actualizarRechazaCorreoDeOtroDocente() {
        servicio.registrarDocente(new Docente("Marta", "Bases de datos", "marta@uniajc.edu.co"));
        Docente pedro = new Docente("Pedro", "Redes", "pedro@uniajc.edu.co");
        servicio.registrarDocente(pedro);
        Docente cambios = new Docente(pedro.getId(), "Pedro", "Redes", "marta@uniajc.edu.co");
        assertThrows(IllegalArgumentException.class, () -> servicio.actualizarDocente(cambios));
    }

    @Test
    void actualizarRechazaDocenteInexistente() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.actualizarDocente(new Docente(50, "Marta", "Redes", "marta@uniajc.edu.co")));
    }

    @Test
    void eliminarRechazaDocenteConGrupos() {
        Docente marta = new Docente("Marta", "Bases de datos", "marta@uniajc.edu.co");
        servicio.registrarDocente(marta);
        dao.grupos.put(marta.getId(), 1);
        assertThrows(IllegalArgumentException.class, () -> servicio.eliminarDocente(marta.getId()));
        assertEquals(1, servicio.obtenerTodosLosDocentes().size());
    }

    @Test
    void eliminarQuitaAlDocenteSinGrupos() {
        Docente marta = new Docente("Marta", "Bases de datos", "marta@uniajc.edu.co");
        servicio.registrarDocente(marta);
        servicio.eliminarDocente(marta.getId());
        assertTrue(servicio.obtenerTodosLosDocentes().isEmpty());
    }

    @Test
    void buscarPorIdRechazaIdInvalido() {
        assertThrows(IllegalArgumentException.class, () -> servicio.buscarDocentePorId(-1));
    }

    private static class FakeDocenteDao extends DocenteDao {

        private final Map<Integer, Docente> datos = new LinkedHashMap<>();
        private final Map<Integer, Integer> grupos = new LinkedHashMap<>();
        private int siguienteId = 1;

        @Override
        public void guardar(Docente docente) {
            docente.setId(siguienteId++);
            datos.put(docente.getId(), docente);
        }

        @Override
        public List<Docente> obtenerTodos() {
            return new ArrayList<>(datos.values());
        }

        @Override
        public Optional<Docente> buscarPorId(int id) {
            return Optional.ofNullable(datos.get(id));
        }

        @Override
        public Optional<Docente> buscarPorEmail(String email) {
            return datos.values().stream().filter(d -> d.getEmail().equalsIgnoreCase(email)).findFirst();
        }

        @Override
        public int contarGrupos(int idDocente) {
            return grupos.getOrDefault(idDocente, 0);
        }

        @Override
        public void actualizar(Docente docente) {
            datos.put(docente.getId(), docente);
        }

        @Override
        public void eliminar(int id) {
            datos.remove(id);
        }
    }
}
