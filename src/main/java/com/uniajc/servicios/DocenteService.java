package com.uniajc.servicios;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import com.uniajc.dao.DocenteDao;
import com.uniajc.modelo.Docente;

public class DocenteService {

    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final DocenteDao docenteDao;

    public DocenteService(DocenteDao docenteDao) {
        if (docenteDao == null) {
            throw new IllegalArgumentException("El DAO no puede ser nulo.");
        }
        this.docenteDao = docenteDao;
    }

    public void registrarDocente(Docente docente) {
        validar(docente);
        if (docenteDao.buscarPorEmail(docente.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un docente con el correo: " + docente.getEmail());
        }
        docenteDao.guardar(docente);
    }

    public List<Docente> obtenerTodosLosDocentes() {
        return docenteDao.obtenerTodos();
    }

    public Optional<Docente> buscarDocentePorId(int id) {
        exigirIdValido(id);
        return docenteDao.buscarPorId(id);
    }

    public void actualizarDocente(Docente docente) {
        if (docente == null) {
            throw new IllegalArgumentException("El docente no puede ser nulo.");
        }
        exigirIdValido(docente.getId());
        if (docenteDao.buscarPorId(docente.getId()).isEmpty()) {
            throw new IllegalArgumentException("No existe un docente con ID: " + docente.getId());
        }
        validar(docente);
        Optional<Docente> conMismoCorreo = docenteDao.buscarPorEmail(docente.getEmail());
        if (conMismoCorreo.isPresent() && conMismoCorreo.get().getId() != docente.getId()) {
            throw new IllegalArgumentException("El correo ya está en uso por otro docente.");
        }
        docenteDao.actualizar(docente);
    }

    public void eliminarDocente(int id) {
        exigirIdValido(id);
        if (docenteDao.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("No existe un docente con ID: " + id);
        }
        if (docenteDao.contarGrupos(id) > 0) {
            throw new IllegalArgumentException("No se puede eliminar: el docente tiene grupos asignados.");
        }
        docenteDao.eliminar(id);
    }

    private void exigirIdValido(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a cero.");
        }
    }

    private void validar(Docente d) {
        if (d == null) {
            throw new IllegalArgumentException("El docente no puede ser nulo.");
        }
        if (d.getName() == null || d.getName().trim().length() < 2) {
            throw new IllegalArgumentException("El nombre es obligatorio y debe tener al menos 2 caracteres.");
        }
        if (d.getSpecialty() == null || d.getSpecialty().trim().length() < 2) {
            throw new IllegalArgumentException("La especialidad es obligatoria y debe tener al menos 2 caracteres.");
        }
        if (d.getEmail() == null || !EMAIL.matcher(d.getEmail().trim()).matches()) {
            throw new IllegalArgumentException("El formato del correo electrónico es inválido.");
        }
        d.setName(d.getName().trim());
        d.setSpecialty(d.getSpecialty().trim());
        d.setEmail(d.getEmail().trim());
    }
}
