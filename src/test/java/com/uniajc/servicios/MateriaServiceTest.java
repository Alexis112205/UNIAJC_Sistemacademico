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

import com.uniajc.dao.MateriaDao;
import com.uniajc.modelo.Materia;

class MateriaServiceTest {

    private FakeMateriaDao dao;
    private MateriaService servicio;

    @BeforeEach
    void preparar() {
        dao = new FakeMateriaDao();
        servicio = new MateriaService(dao);
    }

    @Test
    void registrarAsignaIdYNormalizaNombre() {
        Materia materia = new Materia("  Programación  ", 4);
        servicio.registrarMateria(materia);
        assertEquals(1, materia.getId());
        assertEquals("Programación", materia.getName());
    }

    @Test
    void registrarRechazaNombreCorto() {
        assertThrows(IllegalArgumentException.class, () -> servicio.registrarMateria(new Materia("P", 3)));
    }

    @Test
    void registrarRechazaCreditosFueraDeRango() {
        assertThrows(IllegalArgumentException.class, () -> servicio.registrarMateria(new Materia("Cálculo", 0)));
        assertThrows(IllegalArgumentException.class, () -> servicio.registrarMateria(new Materia("Cálculo", 11)));
    }

    @Test
    void registrarAceptaLosLimitesDeCreditos() {
        servicio.registrarMateria(new Materia("Seminario", 1));
        servicio.registrarMateria(new Materia("Proyecto", 10));
        assertEquals(2, servicio.obtenerTodasLasMaterias().size());
    }

    @Test
    void registrarRechazaNombreDuplicado() {
        servicio.registrarMateria(new Materia("Cálculo", 3));
        assertThrows(IllegalArgumentException.class, () -> servicio.registrarMateria(new Materia("CÁLCULO", 4)));
    }

    @Test
    void actualizarRechazaNombreDeOtraMateria() {
        servicio.registrarMateria(new Materia("Cálculo", 3));
        Materia fisica = new Materia("Física", 3);
        servicio.registrarMateria(fisica);
        assertThrows(IllegalArgumentException.class,
                () -> servicio.actualizarMateria(new Materia(fisica.getId(), "Cálculo", 3)));
    }

    @Test
    void actualizarCambiaCreditos() {
        Materia calculo = new Materia("Cálculo", 3);
        servicio.registrarMateria(calculo);
        servicio.actualizarMateria(new Materia(calculo.getId(), "Cálculo", 5));
        assertEquals(5, servicio.buscarMateriaPorId(calculo.getId()).orElseThrow().getCredits());
    }

    @Test
    void actualizarRechazaMateriaInexistente() {
        assertThrows(IllegalArgumentException.class, () -> servicio.actualizarMateria(new Materia(77, "Cálculo", 3)));
    }

    @Test
    void eliminarRechazaMateriaConGrupos() {
        Materia calculo = new Materia("Cálculo", 3);
        servicio.registrarMateria(calculo);
        dao.grupos.put(calculo.getId(), 2);
        assertThrows(IllegalArgumentException.class, () -> servicio.eliminarMateria(calculo.getId()));
    }

    @Test
    void eliminarQuitaLaMateriaSinGrupos() {
        Materia calculo = new Materia("Cálculo", 3);
        servicio.registrarMateria(calculo);
        servicio.eliminarMateria(calculo.getId());
        assertTrue(servicio.obtenerTodasLasMaterias().isEmpty());
    }

    private static class FakeMateriaDao extends MateriaDao {

        private final Map<Integer, Materia> datos = new LinkedHashMap<>();
        private final Map<Integer, Integer> grupos = new LinkedHashMap<>();
        private int siguienteId = 1;

        @Override
        public void guardar(Materia materia) {
            materia.setId(siguienteId++);
            datos.put(materia.getId(), materia);
        }

        @Override
        public List<Materia> obtenerTodos() {
            return new ArrayList<>(datos.values());
        }

        @Override
        public Optional<Materia> buscarPorId(int id) {
            return Optional.ofNullable(datos.get(id));
        }

        @Override
        public Optional<Materia> buscarPorNombre(String nombre) {
            return datos.values().stream().filter(m -> m.getName().equalsIgnoreCase(nombre)).findFirst();
        }

        @Override
        public int contarGrupos(int idMateria) {
            return grupos.getOrDefault(idMateria, 0);
        }

        @Override
        public void actualizar(Materia materia) {
            datos.put(materia.getId(), materia);
        }

        @Override
        public void eliminar(int id) {
            datos.remove(id);
        }
    }
}
