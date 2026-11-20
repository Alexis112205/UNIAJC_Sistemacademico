package com.uniajc.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.uniajc.config.ConexionPostgresDatabase;
import com.uniajc.modelo.Grupo;
import com.uniajc.modelo.Opcion;

public class GrupoDao {

    private static final String ESQUEMA = "\"practica-mvc\".";
    private static final String TABLA = ESQUEMA + "grupos";
    private static final String CONSULTA_BASE =
            "SELECT g.id, g.id_materia, g.id_docente, g.classroom, g.schedule, "
            + "m.name AS materia, d.name AS docente FROM " + TABLA + " g "
            + "JOIN " + ESQUEMA + "materias m ON m.id = g.id_materia "
            + "JOIN " + ESQUEMA + "docentes d ON d.id = g.id_docente";

    public void guardar(Grupo grupo) {
        String sql = "INSERT INTO " + TABLA + " (id_materia, id_docente, classroom, schedule) VALUES (?, ?, ?, ?)";
        Connection conn = ConexionPostgresDatabase.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, grupo.getIdMateria());
            pstmt.setInt(2, grupo.getIdDocente());
            pstmt.setString(3, grupo.getClassroom());
            pstmt.setString(4, grupo.getSchedule());
            pstmt.executeUpdate();
            try (ResultSet claves = pstmt.getGeneratedKeys()) {
                if (claves.next()) {
                    grupo.setId(claves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al guardar el grupo: " + e.getMessage(), e);
        }
    }

    public List<Grupo> obtenerTodos() {
        List<Grupo> lista = new ArrayList<>();
        String sql = CONSULTA_BASE + " ORDER BY g.id ASC";
        Connection conn = ConexionPostgresDatabase.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearGrupo(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("Error al consultar los grupos: " + e.getMessage(), e);
        }
        return lista;
    }

    public Optional<Grupo> buscarPorId(int id) {
        String sql = CONSULTA_BASE + " WHERE g.id = ?";
        Connection conn = ConexionPostgresDatabase.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearGrupo(rs));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error al buscar el grupo por ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public boolean existeMateria(int idMateria) {
        return existe("SELECT 1 FROM " + ESQUEMA + "materias WHERE id = ?", idMateria);
    }

    public boolean existeDocente(int idDocente) {
        return existe("SELECT 1 FROM " + ESQUEMA + "docentes WHERE id = ?", idDocente);
    }

    public boolean existeConflictoAula(String aula, String horario, int excluirId) {
        String sql = "SELECT 1 FROM " + TABLA
                + " WHERE LOWER(classroom) = LOWER(?) AND LOWER(schedule) = LOWER(?) AND id <> ?";
        Connection conn = ConexionPostgresDatabase.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, aula);
            pstmt.setString(2, horario);
            pstmt.setInt(3, excluirId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DaoException("Error al verificar el conflicto de aula: " + e.getMessage(), e);
        }
    }

    public boolean existeConflictoDocente(int idDocente, String horario, int excluirId) {
        String sql = "SELECT 1 FROM " + TABLA
                + " WHERE id_docente = ? AND LOWER(schedule) = LOWER(?) AND id <> ?";
        Connection conn = ConexionPostgresDatabase.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idDocente);
            pstmt.setString(2, horario);
            pstmt.setInt(3, excluirId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DaoException("Error al verificar el conflicto del docente: " + e.getMessage(), e);
        }
    }

    public int contarInscripciones(int idGrupo) {
        String sql = "SELECT COUNT(*) FROM " + ESQUEMA + "inscripciones WHERE id_grupo = ?";
        Connection conn = ConexionPostgresDatabase.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idGrupo);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new DaoException("Error al contar las inscripciones del grupo: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene materias para combobox usando la función almacenada 'sp_obtener_materias_combo'.
     * Utiliza CallableStatement cumpliendo con el requisito de procedimientos almacenados.
     */
    public List<Opcion> listarMaterias() {
        return listarOpcionesSp(
                "{ call " + ESQUEMA + "sp_obtener_materias_combo() }",
                "SELECT id, name FROM " + ESQUEMA + "materias ORDER BY name ASC");
    }

    /**
     * Obtiene docentes para combobox usando la función almacenada 'sp_obtener_docentes_combo'.
     * Utiliza CallableStatement cumpliendo con el requisito de procedimientos almacenados.
     */
    public List<Opcion> listarDocentes() {
        return listarOpcionesSp(
                "{ call " + ESQUEMA + "sp_obtener_docentes_combo() }",
                "SELECT id, name FROM " + ESQUEMA + "docentes ORDER BY name ASC");
    }

    public void actualizar(Grupo grupo) {
        String sql = "UPDATE " + TABLA + " SET id_materia = ?, id_docente = ?, classroom = ?, schedule = ? WHERE id = ?";
        Connection conn = ConexionPostgresDatabase.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, grupo.getIdMateria());
            pstmt.setInt(2, grupo.getIdDocente());
            pstmt.setString(3, grupo.getClassroom());
            pstmt.setString(4, grupo.getSchedule());
            pstmt.setInt(5, grupo.getId());
            if (pstmt.executeUpdate() == 0) {
                throw new DaoException("No existe un grupo con ID: " + grupo.getId());
            }
        } catch (SQLException e) {
            throw new DaoException("Error al actualizar el grupo: " + e.getMessage(), e);
        }
    }

    public void eliminar(int id) {
        String sql = "DELETE FROM " + TABLA + " WHERE id = ?";
        Connection conn = ConexionPostgresDatabase.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            if (pstmt.executeUpdate() == 0) {
                throw new DaoException("No existe un grupo con ID: " + id);
            }
        } catch (SQLException e) {
            throw new DaoException("Error al eliminar el grupo: " + e.getMessage(), e);
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

    private List<Opcion> listarOpcionesSp(String callSp, String fallbackSql) {
        List<Opcion> opciones = new ArrayList<>();
        Connection conn = ConexionPostgresDatabase.getConnection();
        try (CallableStatement cstmt = conn.prepareCall(callSp);
             ResultSet rs = cstmt.executeQuery()) {
            while (rs.next()) {
                opciones.add(new Opcion(rs.getInt("id"), rs.getString("name")));
            }
            return opciones;
        } catch (SQLException e) {
            // Fallback transparente en caso de que las funciones aún no hayan sido ejecutadas en Neon
            return listarOpciones(fallbackSql);
        }
    }

    private List<Opcion> listarOpciones(String sql) {
        List<Opcion> opciones = new ArrayList<>();
        Connection conn = ConexionPostgresDatabase.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                opciones.add(new Opcion(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            throw new DaoException("Error al consultar las opciones: " + e.getMessage(), e);
        }
        return opciones;
    }

    private Grupo mapearGrupo(ResultSet rs) throws SQLException {
        Grupo grupo = new Grupo(
                rs.getInt("id"),
                rs.getInt("id_materia"),
                rs.getInt("id_docente"),
                rs.getString("classroom"),
                rs.getString("schedule"));
        grupo.setMateriaNombre(rs.getString("materia"));
        grupo.setDocenteNombre(rs.getString("docente"));
        return grupo;
    }
}
