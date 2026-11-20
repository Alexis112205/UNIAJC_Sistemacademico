package com.uniajc.vista;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.uniajc.controlador.ControladorEstudiante;
import com.uniajc.modelo.Estudiante;

public class VistaEstudiante extends VistaConsola {

    private final ControladorEstudiante controlador;

    public VistaEstudiante(Scanner scanner) {
        this(scanner, new ControladorEstudiante());
    }

    public VistaEstudiante(Scanner scanner, ControladorEstudiante controlador) {
        super(scanner);
        this.controlador = controlador;
    }

    @Override
    protected String titulo() {
        return "ESTUDIANTES";
    }

    @Override
    protected Map<String, Runnable> acciones() {
        Map<String, Runnable> acciones = new LinkedHashMap<>();
        acciones.put("Listar estudiantes", this::listar);
        acciones.put("Registrar estudiante", this::registrar);
        acciones.put("Buscar estudiante por ID", this::buscar);
        acciones.put("Actualizar estudiante", this::actualizar);
        acciones.put("Eliminar estudiante", this::eliminar);
        return acciones;
    }

    private void listar() {
        List<Estudiante> lista = controlador.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("No hay estudiantes registrados.");
            return;
        }
        System.out.printf("%-6s %-20s %-20s %-30s%n", "ID", "NOMBRE", "APELLIDO", "EMAIL");
        for (Estudiante e : lista) {
            System.out.printf("%-6d %-20s %-20s %-30s%n", e.getId(), e.getName(), e.getLastname(), e.getEmail());
        }
    }

    private void registrar() {
        String nombre = leerTexto("Nombre");
        String apellido = leerTexto("Apellido");
        String email = leerTexto("Correo electrónico");
        controlador.registrar(nombre, apellido, email);
        System.out.println("Estudiante registrado correctamente.");
    }

    private void buscar() {
        int id = leerEntero("ID");
        controlador.buscarPorId(id).ifPresentOrElse(
                e -> System.out.println("Encontrado: " + e),
                () -> System.out.println("No existe un estudiante con ID " + id));
    }

    private void actualizar() {
        int id = leerEntero("ID del estudiante");
        if (controlador.buscarPorId(id).isEmpty()) {
            System.out.println("Estudiante no encontrado.");
            return;
        }
        String nombre = leerTexto("Nuevo nombre");
        String apellido = leerTexto("Nuevo apellido");
        String email = leerTexto("Nuevo correo");
        controlador.actualizar(id, nombre, apellido, email);
        System.out.println("Estudiante actualizado correctamente.");
    }

    private void eliminar() {
        int id = leerEntero("ID del estudiante a eliminar");
        controlador.eliminar(id);
        System.out.println("Estudiante eliminado correctamente.");
    }
}
