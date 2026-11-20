# PARTE3 — Inscripción, menús Swing y consola, Main y documentación

Rama: `feature/practica-inscripcion-integracion`

Espera a que la persona anterior haga `push` antes de crear tu rama.

## 1. Crear la rama
```bash
git fetch origin
git checkout -b feature/practica-inscripcion-integracion origin/feature/practica-grupo-estudiante
```

## 2. Copiar los archivos
Copia el contenido de esta carpeta (respetando las rutas) sobre la carpeta de tu repositorio.

## 3. Subir
```bash
git add src/main/java/com/uniajc/modelo/Inscripcion.java \
  src/main/java/com/uniajc/dao/InscripcionDao.java \
  src/main/java/com/uniajc/servicios/InscripcionService.java \
  src/main/java/com/uniajc/controlador/ControladorInscripcion.java \
  src/main/java/com/uniajc/vista/VistaInscripcion.java \
  src/main/java/com/uniajc/vista/VistaInscripcionSwing.java \
  src/test/java/com/uniajc/servicios/InscripcionServiceTest.java \
  src/main/java/com/uniajc/Main.java \
  src/main/java/com/uniajc/vista/VistaPrincipalSwing.java \
  src/main/java/com/uniajc/vista/MenuPrincipalConsola.java \
  README.md \
  REPARTO.md \
  GUIA-IMPLEMENTACION.md
git commit -m "feat: Inscripcion, menús, Main y documentación"
git push -u origin feature/practica-inscripcion-integracion
```

Luego abre el Pull Request hacia `main` (orden: Parte 1, Parte 2, Parte 3).
