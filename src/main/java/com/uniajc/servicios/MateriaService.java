package com.uniajc.servicios;

import java.util.List;
import java.util.Optional;

import com.uniajc.dao.MateriaDao;
import com.uniajc.modelo.Materia;

public class MateriaService {

    private final MateriaDao materiaDao;

    public MateriaService(MateriaDao materiaDao) {
        if (materiaDao == null) {
            throw new IllegalArgumentException("El DAO no puede ser nulo.");
        }
        this.materiaDao = materiaDao;
    }

    public void registrarMateria(Materia materia) {
        validar(materia);
        if (materiaDao.buscarPorNombre(materia.getName()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una materia con el nombre: " + materia.getName());
        }
        materiaDao.guardar(materia);
    }

    public List<Materia> obtenerTodasLasMaterias() {
        return materiaDao.obtenerTodos();
    }

    public Optional<Materia> buscarMateriaPorId(int id) {
        exigirIdValido(id);
        return materiaDao.buscarPorId(id);
    }

    public void actualizarMateria(Materia materia) {
        if (materia == null) {
            throw new IllegalArgumentException("La materia no puede ser nula.");
        }
        exigirIdValido(materia.getId());
        if (materiaDao.buscarPorId(materia.getId()).isEmpty()) {
            throw new IllegalArgumentException("No existe una materia con ID: " + materia.getId());
        }
        validar(materia);
        Optional<Materia> conMismoNombre = materiaDao.buscarPorNombre(materia.getName());
        if (conMismoNombre.isPresent() && conMismoNombre.get().getId() != materia.getId()) {
            throw new IllegalArgumentException("Ya existe otra materia con el nombre: " + materia.getName());
        }
        materiaDao.actualizar(materia);
    }

    public void eliminarMateria(int id) {
        exigirIdValido(id);
        if (materiaDao.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe una materia con ID: " + id);
        }
        if (materiaDao.contarGrupos(id) > 0) {
            throw new IllegalArgumentException("No se puede eliminar: la materia tiene grupos asociados.");
        }
        materiaDao.eliminar(id);
    }

    private void exigirIdValido(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a cero.");
        }
    }

    private void validar(Materia m) {
        if (m == null) {
            throw new IllegalArgumentException("La materia no puede ser nula.");
        }
        if (m.getName() == null || m.getName().trim().length() < 2) {
            throw new IllegalArgumentException("El nombre es obligatorio y debe tener al menos 2 caracteres.");
        }
        if (m.getCredits() < Materia.CREDITOS_MINIMOS || m.getCredits() > Materia.CREDITOS_MAXIMOS) {
            throw new IllegalArgumentException("Los créditos deben estar entre " + Materia.CREDITOS_MINIMOS
                    + " y " + Materia.CREDITOS_MAXIMOS + ".");
        }
        m.setName(m.getName().trim());
    }
}
