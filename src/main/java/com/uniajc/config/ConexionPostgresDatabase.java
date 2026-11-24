package com.uniajc.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Arrays;
import java.util.Properties;

public class ConexionPostgresDatabase {

    private static Connection connection;

    private ConexionPostgresDatabase() {
    }

    public static synchronized Connection getConnection() {
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(3)) {
                connection = abrirConexion();
            }
        } catch (SQLException e) {
            connection = null;
            throw new IllegalStateException("No fue posible verificar la conexión: " + e.getMessage(), e);
        }
        return connection;
    }

    public static synchronized void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }

    private static Connection abrirConexion() {
        Properties propiedades = cargarPropiedades();
        asegurarDriver();
        try {
            return DriverManager.getConnection(
                    propiedades.getProperty("URL"),
                    propiedades.getProperty("USERNAME"),
                    propiedades.getProperty("PASSWORD"));
        } catch (SQLException e) {
            throw new IllegalStateException("Error al conectar con la base de datos: " + e.getMessage(), e);
        }
    }

    private static void asegurarDriver() {
        try {
            Class.forName("org.postgresql.Driver");
            return;
        } catch (ClassNotFoundException ignored) {
        }
        File[] candidatos = new File[] {
            new File("lib/postgresql-42.7.13.jar"),
            new File("lib"),
            new File(System.getProperty("user.home"), ".m2/repository/org/postgresql/postgresql/42.7.13/postgresql-42.7.13.jar")
        };
        for (File candidato : candidatos) {
            if (candidato.exists()) {
                File jar = candidato.isDirectory()
                        ? Arrays.stream(candidato.listFiles((d, n) -> n.startsWith("postgresql") && n.endsWith(".jar")))
                                .findFirst().orElse(null)
                        : candidato;
                if (jar != null && jar.exists()) {
                    try {
                        URLClassLoader loader = new URLClassLoader(
                                new URL[] { jar.toURI().toURL() },
                                ConexionPostgresDatabase.class.getClassLoader());
                        Driver driver = (Driver) Class.forName("org.postgresql.Driver", true, loader)
                                .getDeclaredConstructor().newInstance();
                        DriverManager.registerDriver(new DriverShim(driver));
                        return;
                    } catch (Exception ex) {
                        // continuar intentando otros candidatos
                    }
                }
            }
        }
        throw new IllegalStateException("Driver de PostgreSQL no encontrado. Verifique la carpeta lib o dependencias Maven.");
    }

    private static class DriverShim implements Driver {
        private final Driver driver;

        DriverShim(Driver d) {
            this.driver = d;
        }

        @Override
        public boolean acceptsURL(String u) throws SQLException {
            return driver.acceptsURL(u);
        }

        @Override
        public Connection connect(String u, Properties p) throws SQLException {
            return driver.connect(u, p);
        }

        @Override
        public int getMajorVersion() {
            return driver.getMajorVersion();
        }

        @Override
        public int getMinorVersion() {
            return driver.getMinorVersion();
        }

        @Override
        public DriverPropertyInfo[] getPropertyInfo(String u, Properties p) throws SQLException {
            return driver.getPropertyInfo(u, p);
        }

        @Override
        public boolean jdbcCompliant() {
            return driver.jdbcCompliant();
        }

        @Override
        public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
            return driver.getParentLogger();
        }
    }

    private static Properties cargarPropiedades() {
        Properties propiedades = new Properties();
        File archivo = new File("config.properties");
        try (InputStream entrada = archivo.exists()
                ? new FileInputStream(archivo)
                : ConexionPostgresDatabase.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (entrada == null) {
                throw new IllegalStateException("No se encontró el archivo config.properties.");
            }
            propiedades.load(entrada);
        } catch (IOException e) {
            throw new IllegalStateException("Error al leer config.properties: " + e.getMessage(), e);
        }
        for (String clave : new String[] {"URL", "USERNAME", "PASSWORD"}) {
            if (propiedades.getProperty(clave) == null) {
                throw new IllegalStateException("config.properties debe contener URL, USERNAME y PASSWORD.");
            }
        }
        return propiedades;
    }
}
