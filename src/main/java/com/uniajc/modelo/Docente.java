package com.uniajc.modelo;

import java.io.Serializable;
import java.util.Objects;

public class Docente implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String specialty;
    private String email;

    public Docente() {
    }

    public Docente(String name, String specialty, String email) {
        this.name = name;
        this.specialty = specialty;
        this.email = email;
    }

    public Docente(int id, String name, String specialty, String email) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.email = email;
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

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Docente that = (Docente) o;
        return id == that.id && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return "Docente{id=" + id + ", name='" + name + "', specialty='" + specialty + "', email='" + email + "'}";
    }
}
