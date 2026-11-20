package com.uniajc.vista;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.uniajc.controlador.ControladorDocente;
import com.uniajc.modelo.Docente;

public class VistaDocente extends VistaConsola {

    private final ControladorDocente controlador;

    public VistaDocente(Scanner scanner) {
        this(scanner, new ControladorDocente());
    }

    public VistaDocente(Scanner scanner, ControladorDocente controlador) {
        super(scanner);
        this.controlador = controlador;
    }

    @Override
    protected String titulo() {
        return "DOCENTES";
    }

    @Override
    protected Map<String, Runnable> acciones() {
        Map<String, Runnable> acciones = new LinkedHashMap<>();
        acciones.put("Listar docentes", this::listar);
        acciones.put("Registrar docente", this::registrar);
        acciones.put("Buscar docente por ID", this::buscar);
        acciones.put("Actualizar docente", this::actualizar);
        acciones.put("Eliminar docente", this::eliminar);
        return acciones;
    }

    private void listar() {
        List<Docente> lista = controlador.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("No hay docentes registrados.");
            return;
        }
        System.out.printf("%-6s %-25s %-25s %-30s%n", "ID", "NOMBRE", "ESPECIALIDAD", "EMAIL");
        for (Docente d : lista) {
            System.out.printf("%-6d %-25s %-25s %-30s%n", d.getId(), d.getName(), d.getSpecialty(), d.getEmail());
        }
    }

    private void registrar() {
        String nombre = leerTexto("Nombre");
        String especialidad = leerTexto("Especialidad");
        String email = leerTexto("Correo electrónico");
        controlador.registrar(nombre, especialidad, email);
        System.out.println("Docente registrado correctamente.");
    }

    private void buscar() {
        int id = leerEntero("ID");
        controlador.buscarPorId(id).ifPresentOrElse(
                d -> System.out.println("Encontrado: " + d),
                () -> System.out.println("No existe un docente con ID " + id));
    }

    private void actualizar() {
        int id = leerEntero("ID del docente");
        if (controlador.buscarPorId(id).isEmpty()) {
            System.out.println("Docente no encontrado.");
            return;
        }
        String nombre = leerTexto("Nuevo nombre");
        String especialidad = leerTexto("Nueva especialidad");
        String email = leerTexto("Nuevo correo");
        controlador.actualizar(id, nombre, especialidad, email);
        System.out.println("Docente actualizado correctamente.");
    }

    private void eliminar() {
        int id = leerEntero("ID del docente a eliminar");
        controlador.eliminar(id);
        System.out.println("Docente eliminado correctamente.");
    }
}
