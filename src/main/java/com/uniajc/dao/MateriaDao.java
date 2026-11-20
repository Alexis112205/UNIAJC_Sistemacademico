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
import com.uniajc.modelo.Materia;

public class MateriaDao {

    private static final String TABLA = "\"practica-mvc\".materias";
    private static final String TABLA_GRUPOS = "\"practica-mvc\".grupos";
    private static final String COLUMNAS = "id, name, credits";

    public void guardar(Materia materia) {
        String sql = "INSERT INTO " + TABLA + " (name, credits) VALUES (?, ?)";
        Connection conn = ConexionPostgresDatabase.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, materia.getName());
            pstmt.setInt(2, materia.getCredits());
            pstmt.executeUpdate();
            try (ResultSet claves = pstmt.getGeneratedKeys()) {
                if (claves.next()) {
                    materia.setId(claves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al guardar la materia: " + e.getMessage(), e);
        }
    }

    public List<Materia> obtenerTodos() {
        List<Materia> lista = new ArrayList<>();
        String sql = "SELECT " + COLUMNAS + " FROM " + TABLA + " ORDER BY id ASC";
        Connection conn = ConexionPostgresDatabase.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearMateria(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("Error al consultar las materias: " + e.getMessage(), e);
        }
        return lista;
    }

    public Optional<Materia> buscarPorId(int id) {
        String sql = "SELECT " + COLUMNAS + " FROM " + TABLA + " WHERE id = ?";
        Connection conn = ConexionPostgresDatabase.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearMateria(rs));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al buscar la materia por ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Optional<Materia> buscarPorNombre(String nombre) {
        String sql = "SELECT " + COLUMNAS + " FROM " + TABLA + " WHERE LOWER(name) = LOWER(?)";
        Connection conn = ConexionPostgresDatabase.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearMateria(rs));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al buscar la materia por nombre: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public int contarGrupos(int idMateria) {
        String sql = "SELECT COUNT(*) FROM " + TABLA_GRUPOS + " WHERE id_materia = ?";
        Connection conn = ConexionPostgresDatabase.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idMateria);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new DaoException("Error al contar los grupos de la materia: " + e.getMessage(), e);
        }
    }

    public void actualizar(Materia materia) {
        String sql = "UPDATE " + TABLA + " SET name = ?, credits = ? WHERE id = ?";
        Connection conn = ConexionPostgresDatabase.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, materia.getName());
            pstmt.setInt(2, materia.getCredits());
            pstmt.setInt(3, materia.getId());
            if (pstmt.executeUpdate() == 0) {
                throw new DaoException("No existe una materia con ID: " + materia.getId());
            }
        } catch (SQLException e) {
            throw new DaoException("Error al actualizar la materia: " + e.getMessage(), e);
        }
    }

    public void eliminar(int id) {
        String sql = "DELETE FROM " + TABLA + " WHERE id = ?";
        Connection conn = ConexionPostgresDatabase.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            if (pstmt.executeUpdate() == 0) {
                throw new DaoException("No existe una materia con ID: " + id);
            }
        } catch (SQLException e) {
            throw new DaoException("Error al eliminar la materia: " + e.getMessage(), e);
        }
    }

    private Materia mapearMateria(ResultSet rs) throws SQLException {
        return new Materia(rs.getInt("id"), rs.getString("name"), rs.getInt("credits"));
    }
}
