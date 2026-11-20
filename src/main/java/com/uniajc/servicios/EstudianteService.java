package com.uniajc.servicios;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import com.uniajc.dao.EstudianteDao;
import com.uniajc.modelo.Estudiante;

public class EstudianteService {

    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final EstudianteDao estudianteDao;

    public EstudianteService(EstudianteDao estudianteDao) {
        if (estudianteDao == null) {
            throw new IllegalArgumentException("El DAO no puede ser nulo.");
        }
        this.estudianteDao = estudianteDao;
    }

    public void registrarEstudiante(Estudiante estudiante) {
        validar(estudiante);
        if (estudianteDao.buscarPorEmail(estudiante.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un estudiante con el correo: " + estudiante.getEmail());
        }
        estudianteDao.guardar(estudiante);
    }

    public List<Estudiante> obtenerTodosLosEstudiantes() {
        return estudianteDao.obtenerTodos();
    }

    public Optional<Estudiante> buscarEstudiantePorId(int id) {
        exigirIdValido(id);
        return estudianteDao.buscarPorId(id);
    }

    public void actualizarEstudiante(Estudiante estudiante) {
        if (estudiante == null) {
            throw new IllegalArgumentException("El estudiante no puede ser nulo.");
        }
        exigirIdValido(estudiante.getId());
        if (estudianteDao.buscarPorId(estudiante.getId()).isEmpty()) {
            throw new IllegalArgumentException("No existe un estudiante con ID: " + estudiante.getId());
        }
        validar(estudiante);
        Optional<Estudiante> conMismoCorreo = estudianteDao.buscarPorEmail(estudiante.getEmail());
        if (conMismoCorreo.isPresent() && conMismoCorreo.get().getId() != estudiante.getId()) {
            throw new IllegalArgumentException("El correo ya está en uso por otro estudiante.");
        }
        estudianteDao.actualizar(estudiante);
    }

    public void eliminarEstudiante(int id) {
        exigirIdValido(id);
        if (estudianteDao.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe un estudiante con ID: " + id);
        }
        if (estudianteDao.contarInscripciones(id) > 0) {
            throw new IllegalArgumentException("No se puede eliminar: el estudiante tiene inscripciones registradas.");
        }
        estudianteDao.eliminar(id);
    }

    private void exigirIdValido(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a cero.");
        }
    }

    private void validar(Estudiante e) {
        if (e == null) {
            throw new IllegalArgumentException("El estudiante no puede ser nulo.");
        }
        if (e.getName() == null || e.getName().trim().length() < 2) {
            throw new IllegalArgumentException("El nombre es obligatorio y debe tener al menos 2 caracteres.");
        }
        if (e.getLastname() == null || e.getLastname().trim().length() < 2) {
            throw new IllegalArgumentException("El apellido es obligatorio y debe tener al menos 2 caracteres.");
        }
        if (e.getEmail() == null || !EMAIL.matcher(e.getEmail().trim()).matches()) {
            throw new IllegalArgumentException("El formato del correo electrónico es inválido.");
        }
        e.setName(e.getName().trim());
        e.setLastname(e.getLastname().trim());
        e.setEmail(e.getEmail().trim());
    }
}
