package com.uniajc.vista;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.uniajc.controlador.ControladorInscripcion;
import com.uniajc.modelo.Inscripcion;
import com.uniajc.modelo.Opcion;

public class VistaInscripcion extends VistaConsola {

    private final ControladorInscripcion controlador;

    public VistaInscripcion(Scanner scanner) {
        this(scanner, new ControladorInscripcion());
    }

    public VistaInscripcion(Scanner scanner, ControladorInscripcion controlador) {
        super(scanner);
        this.controlador = controlador;
    }

    @Override
    protected String titulo() {
        return "INSCRIPCIONES";
    }

    @Override
    protected Map<String, Runnable> acciones() {
        Map<String, Runnable> acciones = new LinkedHashMap<>();
        acciones.put("Listar inscripciones", this::listar);
        acciones.put("Inscribir estudiante en un grupo", this::registrar);
        acciones.put("Buscar inscripción por ID", this::buscar);
        acciones.put("Registrar nota y estado", this::actualizar);
        acciones.put("Eliminar inscripción", this::eliminar);
        return acciones;
    }

    private void listar() {
        List<Inscripcion> lista = controlador.listarTodas();
        if (lista.isEmpty()) {
            System.out.println("No hay inscripciones registradas.");
            return;
        }
        System.out.printf("%-6s %-28s %-34s %-8s %-12s%n", "ID", "ESTUDIANTE", "GRUPO", "NOTA", "ESTADO");
        for (Inscripcion i : lista) {
            String nota = i.getFinalGrade() == null ? "-" : String.format("%.2f", i.getFinalGrade());
            System.out.printf("%-6d %-28s %-34s %-8s %-12s%n",
                    i.getId(), i.getEstudianteNombre(), i.getGrupoDescripcion(), nota, i.getStatus());
        }
    }

    private void registrar() {
        int idEstudiante = elegir("Estudiantes disponibles", controlador.listarEstudiantes(), "ID del estudiante");
        int idGrupo = elegir("Grupos disponibles", controlador.listarGrupos(), "ID del grupo");
        controlador.registrar(idEstudiante, idGrupo);
        System.out.println("Estudiante inscrito correctamente.");
    }

    private void buscar() {
        int id = leerEntero("ID");
        controlador.buscarPorId(id).ifPresentOrElse(
                i -> System.out.println("Encontrada: " + i),
                () -> System.out.println("No existe una inscripción con ID " + id));
    }

    private void actualizar() {
        int id = leerEntero("ID de la inscripción");
        if (controlador.buscarPorId(id).isEmpty()) {
            System.out.println("Inscripción no encontrada.");
            return;
        }
        Double nota = leerDecimalOpcional("Nota final (vacío si no aplica)");
        String estado = leerTexto("Estado " + Inscripcion.ESTADOS).toUpperCase();
        controlador.actualizar(id, nota, estado);
        System.out.println("Inscripción actualizada correctamente.");
    }

    private void eliminar() {
        int id = leerEntero("ID de la inscripción a eliminar");
        controlador.eliminar(id);
        System.out.println("Inscripción eliminada correctamente.");
    }

    private int elegir(String encabezado, List<Opcion> opciones, String mensaje) {
        System.out.println(encabezado + ":");
        for (Opcion opcion : opciones) {
            System.out.println("  " + opcion.id() + " - " + opcion.etiqueta());
        }
        return leerEntero(mensaje);
    }
}
