package com.uniajc.vista;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public abstract class VistaConsola {

    protected final Scanner scanner;

    protected VistaConsola(Scanner scanner) {
        this.scanner = scanner;
    }

    protected abstract String titulo();

    protected abstract Map<String, Runnable> acciones();

    public void iniciar() {
        Map<String, Runnable> acciones = acciones();
        List<String> nombres = new ArrayList<>(acciones.keySet());
        List<Runnable> tareas = new ArrayList<>(acciones.values());
        while (true) {
            mostrarMenu(nombres);
            int opcion;
            try {
                opcion = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número válido.");
                continue;
            } catch (NoSuchElementException e) {
                return;
            }
            if (opcion == 0) {
                return;
            }
            if (opcion < 1 || opcion > tareas.size()) {
                System.out.println("Opción no válida.");
                continue;
            }
            try {
                tareas.get(opcion - 1).run();
            } catch (RuntimeException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    protected String leerTexto(String mensaje) {
        System.out.print(mensaje + ": ");
        return scanner.nextLine().trim();
    }

    protected int leerEntero(String mensaje) {
        String texto = leerTexto(mensaje);
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Debe ingresar un número entero.");
        }
    }

    protected Double leerDecimalOpcional(String mensaje) {
        String texto = leerTexto(mensaje);
        if (texto.isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(texto.replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Debe ingresar un número decimal.");
        }
    }

    private void mostrarMenu(List<String> nombres) {
        System.out.println();
        System.out.println("=== " + titulo() + " ===");
        for (int i = 0; i < nombres.size(); i++) {
            System.out.println((i + 1) + ". " + nombres.get(i));
        }
        System.out.println("0. Volver");
        System.out.print("Seleccione una opción: ");
    }
}
