package com.uniajc.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.uniajc.config.ConexionPostgresDatabase;
import com.uniajc.modelo.Inscripcion;
import com.uniajc.modelo.Opcion;

public class InscripcionDao {

    private static final String ESQUEMA = "\"practica-mvc\".";
    private static final String TABLA = ESQUEMA + "inscripciones";
    private static final String CONSULTA_BASE =
            "SELECT i.id, i.id_estudiante, i.id_grupo, i.final_grade, i.status, "
            + "e.name || ' ' || e.lastname AS estudiante, "
            + "m.name || ' - ' || g.schedule AS grupo FROM " + TABLA + " i "
            + "JOIN " + ESQUEMA + "estudiantes e ON e.id = i.id_estudiante "
            + "JOIN " + ESQUEMA + "grupos g ON g.id = i.id_grupo "
            + "JOIN " + ESQUEMA + "materias m ON m.id = g.id_materia";

    public void guardar(Inscripcion inscripcion) {
        String sql = "INSERT INTO " + TABLA + " (id_estudiante, id_grupo, final_grade, status) VALUES (?, ?, ?, ?)";
        Connection conn = ConexionPostgresDatabase.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, inscripcion.getIdEstudiante());
            pstmt.setInt(2, inscripcion.getIdGrupo());
            asignarNota(pstmt, 3, inscripcion.getFinalGrade());
            pstmt.setString(4, inscripcion.getStatus());
            pstmt.executeUpdate();
            try (ResultSet claves = pstmt.getGeneratedKeys()) {
                if (claves.next()) {
                    inscripcion.setId(claves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al guardar la inscripción: " + e.getMessage(), e);
        }
    }

    public List<Inscripcion> obtenerTodos() {
        List<Inscripcion> lista = new ArrayList<>();
        String sql = CONSULTA_BASE + " ORDER BY i.id ASC";
        Connection conn = ConexionPostgresDatabase.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearInscripcion(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("Error al consultar las inscripciones: " + e.getMessage(), e);
        }
        return lista;
    }

    public Optional<Inscripcion> buscarPorId(int id) {
        String sql = CONSULTA_BASE + " WHERE i.id = ?";
        Connection conn = ConexionPostgresDatabase.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearInscripcion(rs));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al buscar la inscripción por ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Optional<Inscripcion> buscarPorEstudianteYGrupo(int idEstudiante, int idGrupo) {
        String sql = CONSULTA_BASE + " WHERE i.id_estudiante = ? AND i.id_grupo = ?";
        Connection conn = ConexionPostgresDatabase.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idEstudiante);
            pstmt.setInt(2, idGrupo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearInscripcion(rs));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al buscar la inscripción: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public boolean existeEstudiante(int idEstudiante) {
        return existe("SELECT 1 FROM " + ESQUEMA + "estudiantes WHERE id = ?", idEstudiante);
    }

    public boolean existeGrupo(int idGrupo) {
        return existe("SELECT 1 FROM " + ESQUEMA + "grupos WHERE id = ?", idGrupo);
    }

    public List<Opcion> listarEstudiantes() {
        return listarOpciones("SELECT id, name || ' ' || lastname AS etiqueta FROM " + ESQUEMA
                + "estudiantes ORDER BY name ASC, lastname ASC");
    }

    public List<Opcion> listarGrupos() {
        return listarOpciones("SELECT g.id, g.id || ' - ' || m.name || ' - ' || g.schedule AS etiqueta FROM "
                + ESQUEMA + "grupos g JOIN " + ESQUEMA + "materias m ON m.id = g.id_materia ORDER BY g.id ASC");
    }

    public void actualizar(Inscripcion inscripcion) {
        String sql = "UPDATE " + TABLA + " SET final_grade = ?, status = ? WHERE id = ?";
        Connection conn = ConexionPostgresDatabase.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            asignarNota(pstmt, 1, inscripcion.getFinalGrade());
            pstmt.setString(2, inscripcion.getStatus());
            pstmt.setInt(3, inscripcion.getId());
            if (pstmt.executeUpdate() == 0) {
                throw new DaoException("No existe una inscripción con ID: " + inscripcion.getId());
            }
        } catch (SQLException e) {
            throw new DaoException("Error al actualizar la inscripción: " + e.getMessage(), e);
        }
    }

    public void eliminar(int id) {
        String sql = "DELETE FROM " + TABLA + " WHERE id = ?";
        Connection conn = ConexionPostgresDatabase.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            if (pstmt.executeUpdate() == 0) {
                throw new DaoException("No existe una inscripción con ID: " + id);
            }
        } catch (SQLException e) {
            throw new DaoException("Error al eliminar la inscripción: " + e.getMessage(), e);
        }
    }

    private void asignarNota(PreparedStatement pstmt, int indice, Double nota) throws SQLException {
        if (nota == null) {
            pstmt.setNull(indice, Types.NUMERIC);
        } else {
            pstmt.setBigDecimal(indice, BigDecimal.valueOf(nota));
        }
    }

    private boolean existe(String sql, int id) {
        Connection conn = ConexionPostgresDatabase.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DaoException("Error al verificar la existencia del registro: " + e.getMessage(), e);
        }
    }

    private List<Opcion> listarOpciones(String sql) {
        List<Opcion> opciones = new ArrayList<>();
        Connection conn = ConexionPostgresDatabase.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                opciones.add(new Opcion(rs.getInt("id"), rs.getString("etiqueta")));
            }
        } catch (SQLException e) {
            throw new DaoException("Error al consultar las opciones: " + e.getMessage(), e);
        }
        return opciones;
    }

    private Inscripcion mapearInscripcion(ResultSet rs) throws SQLException {
        BigDecimal nota = rs.getBigDecimal("final_grade");
        Inscripcion inscripcion = new Inscripcion(
                rs.getInt("id"),
                rs.getInt("id_estudiante"),
                rs.getInt("id_grupo"),
                nota == null ? null : nota.doubleValue(),
                rs.getString("status"));
        inscripcion.setEstudianteNombre(rs.getString("estudiante"));
        inscripcion.setGrupoDescripcion(rs.getString("grupo"));
        return inscripcion;
    }
}
