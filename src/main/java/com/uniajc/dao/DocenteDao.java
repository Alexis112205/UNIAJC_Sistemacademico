package com.uniajc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.uniajc.config.ConexionPostgresDatabase;
import com.uniajc.modelo.Docente;

public class DocenteDao {

    private static final String TABLA = "\"practica-mvc\".docentes";
    private static final String TABLA_GRUPOS = "\"practica-mvc\".grupos";
    private static final String COLUMNAS = "id, name, specialty, email";

    public void guardar(Docente docente) {
        String sql = "INSERT INTO " + TABLA + " (name, specialty, email) VALUES (?, ?, ?)";
        Connection conn = ConexionPostgresDatabase.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, docente.getName());
            pstmt.setString(2, docente.getSpecialty());
            pstmt.setString(3, docente.getEmail());
            pstmt.executeUpdate();
            try (ResultSet claves = pstmt.getGeneratedKeys()) {
                if (claves.next()) {
                    docente.setId(claves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al guardar el docente: " + e.getMessage(), e);
        }
    }

    public List<Docente> obtenerTodos() {
        List<Docente> lista = new ArrayList<>();
        String sql = "SELECT " + COLUMNAS + " FROM " + TABLA + " ORDER BY id ASC";
        Connection conn = ConexionPostgresDatabase.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearDocente(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("Error al consultar los docentes: " + e.getMessage(), e);
        }
        return lista;
    }

    public Optional<Docente> buscarPorId(int id) {
        String sql = "SELECT " + COLUMNAS + " FROM " + TABLA + " WHERE id = ?";
        Connection conn = ConexionPostgresDatabase.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearDocente(rs));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al buscar el docente por ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Optional<Docente> buscarPorEmail(String email) {
        String sql = "SELECT " + COLUMNAS + " FROM " + TABLA + " WHERE LOWER(email) = LOWER(?)";
        Connection conn = ConexionPostgresDatabase.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearDocente(rs));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al buscar el docente por correo: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public int contarGrupos(int idDocente) {
        String sql = "SELECT COUNT(*) FROM " + TABLA_GRUPOS + " WHERE id_docente = ?";
        Connection conn = ConexionPostgresDatabase.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idDocente);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new DaoException("Error al contar los grupos del docente: " + e.getMessage(), e);
        }
    }

    public void actualizar(Docente docente) {
        String sql = "UPDATE " + TABLA + " SET name = ?, specialty = ?, email = ? WHERE id = ?";
        Connection conn = ConexionPostgresDatabase.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, docente.getName());
            pstmt.setString(2, docente.getSpecialty());
            pstmt.setString(3, docente.getEmail());
            pstmt.setInt(4, docente.getId());
            if (pstmt.executeUpdate() == 0) {
                throw new DaoException("No existe un docente con ID: " + docente.getId());
            }
        } catch (SQLException e) {
            throw new DaoException("Error al actualizar el docente: " + e.getMessage(), e);
        }
    }

    public void eliminar(int id) {
        String sql = "DELETE FROM " + TABLA + " WHERE id = ?";
        Connection conn = ConexionPostgresDatabase.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            if (pstmt.executeUpdate() == 0) {
                throw new DaoException("No existe un docente con ID: " + id);
            }
        } catch (SQLException e) {
            throw new DaoException("Error al eliminar el docente: " + e.getMessage(), e);
        }
    }

    private Docente mapearDocente(ResultSet rs) throws SQLException {
        return new Docente(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("specialty"),
                rs.getString("email"));
    }
}
