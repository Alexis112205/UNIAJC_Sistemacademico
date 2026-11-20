package com.uniajc.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(
                    propiedades.getProperty("URL"),
                    propiedades.getProperty("USERNAME"),
                    propiedades.getProperty("PASSWORD"));
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Driver de PostgreSQL no encontrado.", e);
        } catch (SQLException e) {
            throw new IllegalStateException("Error al conectar con la base de datos: " + e.getMessage(), e);
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
