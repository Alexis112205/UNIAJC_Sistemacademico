package com.uniajc.modelo;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Inscripcion implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String ACTIVO = "ACTIVO";
    public static final String APROBADO = "APROBADO";
    public static final String REPROBADO = "REPROBADO";
    public static final String CANCELADO = "CANCELADO";
    public static final List<String> ESTADOS = List.of(ACTIVO, APROBADO, REPROBADO, CANCELADO);

    public static final double NOTA_MINIMA = 0.0;
    public static final double NOTA_MAXIMA = 5.0;
    public static final double NOTA_APROBACION = 3.0;

    private int id;
    private int idEstudiante;
    private int idGrupo;
    private Double finalGrade;
    private String status;
    private String estudianteNombre;
    private String grupoDescripcion;

    public Inscripcion() {
    }

    public Inscripcion(int idEstudiante, int idGrupo) {
        this.idEstudiante = idEstudiante;
        this.idGrupo = idGrupo;
        this.status = ACTIVO;
    }

    public Inscripcion(int id, int idEstudiante, int idGrupo, Double finalGrade, String status) {
        this.id = id;
        this.idEstudiante = idEstudiante;
        this.idGrupo = idGrupo;
        this.finalGrade = finalGrade;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    public Double getFinalGrade() {
        return finalGrade;
    }

    public void setFinalGrade(Double finalGrade) {
        this.finalGrade = finalGrade;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEstudianteNombre() {
        return estudianteNombre;
    }

    public void setEstudianteNombre(String estudianteNombre) {
        this.estudianteNombre = estudianteNombre;
    }

    public String getGrupoDescripcion() {
        return grupoDescripcion;
    }

    public void setGrupoDescripcion(String grupoDescripcion) {
        this.grupoDescripcion = grupoDescripcion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Inscripcion that = (Inscripcion) o;
        return id == that.id && idEstudiante == that.idEstudiante && idGrupo == that.idGrupo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idEstudiante, idGrupo);
    }

    @Override
    public String toString() {
        return "Inscripcion{id=" + id + ", idEstudiante=" + idEstudiante + ", idGrupo=" + idGrupo
                + ", finalGrade=" + finalGrade + ", status='" + status + "'}";
    }
}
