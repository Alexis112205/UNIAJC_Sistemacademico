package com.uniajc.modelo;

public record Opcion(int id, String etiqueta) {

    @Override
    public String toString() {
        return etiqueta;
    }
}
