package com.uniajc.vista;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class MenuPrincipalConsola extends VistaConsola {

    public MenuPrincipalConsola(Scanner scanner) {
        super(scanner);
    }

    @Override
    protected String titulo() {
        return "SISTEMA ACADÉMICO UNIAJC";
    }

    @Override
    protected Map<String, Runnable> acciones() {
        Map<String, Runnable> acciones = new LinkedHashMap<>();
        acciones.put("Estudiantes", () -> new VistaEstudiante(scanner).iniciar());
        acciones.put("Docentes", () -> new VistaDocente(scanner).iniciar());
        acciones.put("Materias", () -> new VistaMateria(scanner).iniciar());
        acciones.put("Grupos", () -> new VistaGrupo(scanner).iniciar());
        acciones.put("Inscripciones", () -> new VistaInscripcion(scanner).iniciar());
        return acciones;
    }
}
