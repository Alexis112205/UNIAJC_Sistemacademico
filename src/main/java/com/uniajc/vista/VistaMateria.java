package com.uniajc.vista;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.uniajc.controlador.ControladorMateria;
import com.uniajc.modelo.Materia;

public class VistaMateria extends VistaConsola {

    private final ControladorMateria controlador;

    public VistaMateria(Scanner scanner) {
        this(scanner, new ControladorMateria());
    }

    public VistaMateria(Scanner scanner, ControladorMateria controlador) {
        super(scanner);
        this.controlador = controlador;
    }

    @Override
    protected String titulo() {
        return "MATERIAS";
    }

    @Override
    protected Map<String, Runnable> acciones() {
        Map<String, Runnable> acciones = new LinkedHashMap<>();
        acciones.put("Listar materias", this::listar);
        acciones.put("Registrar materia", this::registrar);
        acciones.put("Buscar materia por ID", this::buscar);
        acciones.put("Actualizar materia", this::actualizar);
        acciones.put("Eliminar materia", this::eliminar);
        return acciones;
    }

    private void listar() {
        List<Materia> lista = controlador.listarTodas();
        if (lista.isEmpty()) {
            System.out.println("No hay materias registradas.");
            return;
        }
        System.out.printf("%-6s %-35s %-10s%n", "ID", "NOMBRE", "CRÉDITOS");
        for (Materia m : lista) {
            System.out.printf("%-6d %-35s %-10d%n", m.getId(), m.getName(), m.getCredits());
        }
    }

    private void registrar() {
        String nombre = leerTexto("Nombre de la materia");
        int creditos = leerEntero("Créditos");
        controlador.registrar(nombre, creditos);
        System.out.println("Materia registrada correctamente.");
    }

    private void buscar() {
        int id = leerEntero("ID");
        controlador.buscarPorId(id).ifPresentOrElse(
                m -> System.out.println("Encontrada: " + m),
                () -> System.out.println("No existe una materia con ID " + id));
    }

    private void actualizar() {
        int id = leerEntero("ID de la materia");
        if (controlador.buscarPorId(id).isEmpty()) {
            System.out.println("Materia no encontrada.");
            return;
        }
        String nombre = leerTexto("Nuevo nombre");
        int creditos = leerEntero("Nuevos créditos");
        controlador.actualizar(id, nombre, creditos);
        System.out.println("Materia actualizada correctamente.");
    }

    private void eliminar() {
        int id = leerEntero("ID de la materia a eliminar");
        controlador.eliminar(id);
        System.out.println("Materia eliminada correctamente.");
    }
}
