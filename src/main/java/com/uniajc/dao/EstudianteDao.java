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
import com.uniajc.modelo.Estudiante;

public class EstudianteDao {

    private static final String TABLA = "\"practica-mvc\".estudiantes";
    private static final String TABLA_INSCRIPCIONES = "\"practica-mvc\".inscripciones";

    public void guardar(Estudiante estudiante) {
        String sql = "INSERT INTO " + TABLA + " (name, lastname, email) VALUES (?, ?, ?)";
        Connection conn = ConexionPostgresDatabase.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, estudiante.getName());
            pstmt.setString(2, estudiante.getLastname());
            pstmt.setString(3, estudiante.getEmail());
            pstmt.executeUpdate();
            try (ResultSet claves = pstmt.getGeneratedKeys()) {
                if (claves.next()) {
                    estudiante.setId(claves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al guardar el estudiante: " + e.getMessage(), e);
        }
    }

    public List<Estudiante> obtenerTodos() {
        List<Estudiante> lista = new ArrayList<>();
        String sql = "SELECT id, name, lastname, email FROM " + TABLA + " ORDER BY id ASC";
        Connection conn = ConexionPostgresDatabase.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearEstudiante(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("Error al consultar los estudiantes: " + e.getMessage(), e);
        }
        return lista;
    }

    public Optional<Estudiante> buscarPorId(int id) {
        String sql = "SELECT id, name, lastname, email FROM " + TABLA + " WHERE id = ?";
        Connection conn = ConexionPostgresDatabase.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearEstudiante(rs));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al buscar el estudiante por ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Optional<Estudiante> buscarPorEmail(String email) {
        String sql = "SELECT id, name, lastname, email FROM " + TABLA + " WHERE LOWER(email) = LOWER(?)";
        Connection conn = ConexionPostgresDatabase.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearEstudiante(rs));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al buscar el estudiante por correo: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public int contarInscripciones(int idEstudiante) {
        String sql = "SELECT COUNT(*) FROM " + TABLA_INSCRIPCIONES + " WHERE id_estudiante = ?";
        Connection conn = ConexionPostgresDatabase.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idEstudiante);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new DaoException("Error al contar las inscripciones del estudiante: " + e.getMessage(), e);
        }
    }

    public void actualizar(Estudiante estudiante) {
        String sql = "UPDATE " + TABLA + " SET name = ?, lastname = ?, email = ? WHERE id = ?";
        Connection conn = ConexionPostgresDatabase.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, estudiante.getName());
            pstmt.setString(2, estudiante.getLastname());
            pstmt.setString(3, estudiante.getEmail());
            pstmt.setInt(4, estudiante.getId());
            if (pstmt.executeUpdate() == 0) {
                throw new DaoException("No existe un estudiante con ID: " + estudiante.getId());
            }
        } catch (SQLException e) {
            throw new DaoException("Error al actualizar el estudiante: " + e.getMessage(), e);
        }
    }

    public void eliminar(int id) {
        String sql = "DELETE FROM " + TABLA + " WHERE id = ?";
        Connection conn = ConexionPostgresDatabase.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            if (pstmt.executeUpdate() == 0) {
                throw new DaoException("No existe un estudiante con ID: " + id);
            }
        } catch (SQLException e) {
            throw new DaoException("Error al eliminar el estudiante: " + e.getMessage(), e);
        }
    }

    private Estudiante mapearEstudiante(ResultSet rs) throws SQLException {
        return new Estudiante(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("lastname"),
                rs.getString("email"));
    }
}
