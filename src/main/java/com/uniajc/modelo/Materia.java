package com.uniajc.modelo;

import java.io.Serializable;
import java.util.Objects;

public class Materia implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int CREDITOS_MINIMOS = 1;
    public static final int CREDITOS_MAXIMOS = 10;

    private int id;
    private String name;
    private int credits;

    public Materia() {
    }

    public Materia(String name, int credits) {
        this.name = name;
        this.credits = credits;
    }

    public Materia(int id, String name, int credits) {
        this.id = id;
        this.name = name;
        this.credits = credits;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Materia that = (Materia) o;
        return id == that.id && credits == that.credits && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, credits);
    }

    @Override
    public String toString() {
        return "Materia{id=" + id + ", name='" + name + "', credits=" + credits + "}";
    }
}
