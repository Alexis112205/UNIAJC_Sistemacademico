# Reparto del trabajo (3 personas, 3 ramas)

El proyecto completo ya está en esta carpeta. Cada persona sube **solo sus archivos** a su rama.

Las ramas van en cadena porque comparten código: la Persona 2 parte de la rama de la Persona 1, y la Persona 3 parte de la rama de la Persona 2. Cada una puede trabajar y revisar su parte desde ya, pero el `git checkout` de la rama solo se puede hacer cuando la anterior ya hizo `push`.

| Persona | Rama | Parte |
|---|---|---|
| 1 | `feature/practica-base-docente-materia` | Base compartida, Docente y Materia |
| 2 | `feature/practica-grupo-estudiante` | Grupo y pruebas de Estudiante |
| 3 | `feature/practica-inscripcion-integracion` | Inscripción, menús Swing y consola, Main y documentación |

Cambien los nombres de rama a `feature/practica-[SuNombre]` si el profe lo exige.

## Persona 1

1. Crear la rama desde `main`:

```bash
git checkout main
git pull
git checkout -b feature/practica-base-docente-materia
```

2. Copiar el contenido de esta carpeta sobre la carpeta del repositorio, reemplazando los archivos existentes.

3. Subir sus archivos:

```bash
git add .gitignore pom.xml config.properties.example \
  src/main/resources/sql/init.sql \
  src/main/java/com/uniajc/config \
  src/main/java/com/uniajc/db \
  src/main/java/com/uniajc/modelo/Opcion.java \
  src/main/java/com/uniajc/modelo/Estudiante.java \
  src/main/java/com/uniajc/modelo/Docente.java \
  src/main/java/com/uniajc/modelo/Materia.java \
  src/main/java/com/uniajc/dao/DaoException.java \
  src/main/java/com/uniajc/dao/EstudianteDao.java \
  src/main/java/com/uniajc/dao/DocenteDao.java \
  src/main/java/com/uniajc/dao/MateriaDao.java \
  src/main/java/com/uniajc/servicios/EstudianteService.java \
  src/main/java/com/uniajc/servicios/DocenteService.java \
  src/main/java/com/uniajc/servicios/MateriaService.java \
  src/main/java/com/uniajc/controlador/ControladorEstudiante.java \
  src/main/java/com/uniajc/controlador/ControladorDocente.java \
  src/main/java/com/uniajc/controlador/ControladorMateria.java \
  src/main/java/com/uniajc/vista/PanelCrudSwing.java \
  src/main/java/com/uniajc/vista/VistaConsola.java \
  src/main/java/com/uniajc/vista/VistaEstudiante.java \
  src/main/java/com/uniajc/vista/VistaEstudianteSwing.java \
  src/main/java/com/uniajc/vista/VistaDocente.java \
  src/main/java/com/uniajc/vista/VistaDocenteSwing.java \
  src/main/java/com/uniajc/vista/VistaMateria.java \
  src/main/java/com/uniajc/vista/VistaMateriaSwing.java \
  src/test/java/com/uniajc/servicios/DocenteServiceTest.java \
  src/test/java/com/uniajc/servicios/MateriaServiceTest.java
git commit -m "feat: base compartida, Docente y Materia"
git push -u origin feature/practica-base-docente-materia
```

## Persona 2

1. Esperar el push de la Persona 1 y crear la rama desde la de ella:

```bash
git fetch origin
git checkout -b feature/practica-grupo-estudiante origin/feature/practica-base-docente-materia
```

2. Copiar el contenido de esta carpeta sobre la carpeta del repositorio, reemplazando los archivos existentes.

3. Subir sus archivos:

```bash
git add src/main/java/com/uniajc/modelo/Grupo.java \
  src/main/java/com/uniajc/dao/GrupoDao.java \
  src/main/java/com/uniajc/servicios/GrupoService.java \
  src/main/java/com/uniajc/controlador/ControladorGrupo.java \
  src/main/java/com/uniajc/vista/VistaGrupo.java \
  src/main/java/com/uniajc/vista/VistaGrupoSwing.java \
  src/test/java/com/uniajc/servicios/GrupoServiceTest.java \
  src/test/java/com/uniajc/servicios/EstudianteServiceTest.java
git commit -m "feat: Grupo con validación de aula y docente, pruebas de Estudiante"
git push -u origin feature/practica-grupo-estudiante
```

## Persona 3

1. Esperar el push de la Persona 2 y crear la rama desde la de ella:

```bash
git fetch origin
git checkout -b feature/practica-inscripcion-integracion origin/feature/practica-grupo-estudiante
```

2. Copiar el contenido de esta carpeta sobre la carpeta del repositorio, reemplazando los archivos existentes.

3. Subir sus archivos:

```bash
git add src/main/java/com/uniajc/modelo/Inscripcion.java \
  src/main/java/com/uniajc/dao/InscripcionDao.java \
  src/main/java/com/uniajc/servicios/InscripcionService.java \
  src/main/java/com/uniajc/controlador/ControladorInscripcion.java \
  src/main/java/com/uniajc/vista/VistaInscripcion.java \
  src/main/java/com/uniajc/vista/VistaInscripcionSwing.java \
  src/test/java/com/uniajc/servicios/InscripcionServiceTest.java
git commit -m "feat: Inscripcion con reglas de nota y estado"
git add src/main/java/com/uniajc/Main.java \
  src/main/java/com/uniajc/vista/VistaPrincipalSwing.java \
  src/main/java/com/uniajc/vista/MenuPrincipalConsola.java \
  README.md REPARTO.md GUIA-IMPLEMENTACION.md
git commit -m "feat: menú Swing y consola, Main y documentación"
git push -u origin feature/practica-inscripcion-integracion
```

## Pull Requests

Abrir los PR en este orden: Persona 1, Persona 2, Persona 3. Cada uno hacia `main`, y el siguiente solo después de aprobar el anterior.

## Antes de probar

- Crear `config.properties` a partir de `config.properties.example` con las credenciales de Neon. Ese archivo no se sube a GitHub.
- Ejecutar `src/main/resources/sql/init.sql` en la base de datos.
- `mvn test` necesita internet la primera vez para descargar JUnit.
- Ejecutar: `mvn exec:java -Dexec.mainClass=com.uniajc.Main` (ventana) o con `-Dexec.args=consola`.
