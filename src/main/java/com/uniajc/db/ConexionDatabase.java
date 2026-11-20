package com.uniajc.db;

import java.sql.Connection;
import com.uniajc.config.ConexionPostgresDatabase;

public class ConexionDatabase {

    private ConexionDatabase() {
    }

    public static Connection getConnection() {
        return ConexionPostgresDatabase.getConnection();
    }

    public static void closeConnection() {
        ConexionPostgresDatabase.closeConnection();
    }
}
