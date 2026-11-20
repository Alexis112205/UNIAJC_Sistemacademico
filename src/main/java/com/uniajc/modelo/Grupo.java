package com.uniajc.modelo;

import java.io.Serializable;
import java.util.Objects;

public class Grupo implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private int idMateria;
    private int idDocente;
    private String classroom;
    private String schedule;
    private String materiaNombre;
    private String docenteNombre;

    public Grupo() {
    }

    public Grupo(int idMateria, int idDocente, String classroom, String schedule) {
        this.idMateria = idMateria;
        this.idDocente = idDocente;
        this.classroom = classroom;
        this.schedule = schedule;
    }

    public Grupo(int id, int idMateria, int idDocente, String classroom, String schedule) {
        this(idMateria, idDocente, classroom, schedule);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdMateria() {
        return idMateria;
    }

    public void setIdMateria(int idMateria) {
        this.idMateria = idMateria;
    }

    public int getIdDocente() {
        return idDocente;
    }

    public void setIdDocente(int idDocente) {
        this.idDocente = idDocente;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getMateriaNombre() {
        return materiaNombre;
    }

    public void setMateriaNombre(String materiaNombre) {
        this.materiaNombre = materiaNombre;
    }

    public String getDocenteNombre() {
        return docenteNombre;
    }

    public void setDocenteNombre(String docenteNombre) {
        this.docenteNombre = docenteNombre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Grupo that = (Grupo) o;
        return id == that.id && idMateria == that.idMateria && idDocente == that.idDocente
                && Objects.equals(classroom, that.classroom) && Objects.equals(schedule, that.schedule);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idMateria, idDocente, classroom, schedule);
    }

    @Override
    public String toString() {
        return "Grupo{id=" + id + ", idMateria=" + idMateria + ", idDocente=" + idDocente
                + ", classroom='" + classroom + "', schedule='" + schedule + "'}";
    }
}
