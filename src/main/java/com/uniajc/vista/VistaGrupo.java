package com.uniajc.vista;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.uniajc.controlador.ControladorGrupo;
import com.uniajc.modelo.Grupo;
import com.uniajc.modelo.Opcion;

public class VistaGrupo extends VistaConsola {

    private final ControladorGrupo controlador;

    public VistaGrupo(Scanner scanner) {
        this(scanner, new ControladorGrupo());
    }

    public VistaGrupo(Scanner scanner, ControladorGrupo controlador) {
        super(scanner);
        this.controlador = controlador;
    }

    @Override
    protected String titulo() {
        return "GRUPOS";
    }

    @Override
    protected Map<String, Runnable> acciones() {
        Map<String, Runnable> acciones = new LinkedHashMap<>();
        acciones.put("Listar grupos", this::listar);
        acciones.put("Registrar grupo", this::registrar);
        acciones.put("Buscar grupo por ID", this::buscar);
        acciones.put("Actualizar grupo", this::actualizar);
        acciones.put("Eliminar grupo", this::eliminar);
        return acciones;
    }

    private void listar() {
        List<Grupo> lista = controlador.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("No hay grupos registrados.");
            return;
        }
        System.out.printf("%-6s %-28s %-24s %-12s %-24s%n", "ID", "MATERIA", "DOCENTE", "AULA", "HORARIO");
        for (Grupo g : lista) {
            System.out.printf("%-6d %-28s %-24s %-12s %-24s%n",
                    g.getId(), g.getMateriaNombre(), g.getDocenteNombre(), g.getClassroom(), g.getSchedule());
        }
    }

    private void registrar() {
        int idMateria = elegir("Materias disponibles", controlador.listarMaterias(), "ID de la materia");
        int idDocente = elegir("Docentes disponibles", controlador.listarDocentes(), "ID del docente");
        String aula = leerTexto("Aula");
        String horario = leerTexto("Horario");
        controlador.registrar(idMateria, idDocente, aula, horario);
        System.out.println("Grupo registrado correctamente.");
    }

    private void buscar() {
        int id = leerEntero("ID");
        controlador.buscarPorId(id).ifPresentOrElse(
                g -> System.out.println("Encontrado: " + g),
                () -> System.out.println("No existe un grupo con ID " + id));
    }

    private void actualizar() {
        int id = leerEntero("ID del grupo");
        if (controlador.buscarPorId(id).isEmpty()) {
            System.out.println("Grupo no encontrado.");
            return;
        }
        int idMateria = elegir("Materias disponibles", controlador.listarMaterias(), "Nuevo ID de la materia");
        int idDocente = elegir("Docentes disponibles", controlador.listarDocentes(), "Nuevo ID del docente");
        String aula = leerTexto("Nueva aula");
        String horario = leerTexto("Nuevo horario");
        controlador.actualizar(id, idMateria, idDocente, aula, horario);
        System.out.println("Grupo actualizado correctamente.");
    }

    private void eliminar() {
        int id = leerEntero("ID del grupo a eliminar");
        controlador.eliminar(id);
        System.out.println("Grupo eliminado correctamente.");
    }

    private int elegir(String encabezado, List<Opcion> opciones, String mensaje) {
        System.out.println(encabezado + ":");
        for (Opcion opcion : opciones) {
            System.out.println("  " + opcion.id() + " - " + opcion.etiqueta());
        }
        return leerEntero(mensaje);
    }
}
