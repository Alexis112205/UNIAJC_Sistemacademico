-- =========================================================================
-- PRÁCTICA ACADÉMICA UNIAJC - PROCEDIMIENTOS / FUNCIONES ALMACENADAS
-- Ejecutar este script en la consola de Neon (SQL Editor) o cliente PostgreSQL.
-- Cumple con el requisito: "Mínimo dos procedimientos almacenados o funciones"
-- =========================================================================

-- Asegurar esquema del proyecto
CREATE SCHEMA IF NOT EXISTS "practica-mvc";

-- -------------------------------------------------------------------------
-- 1. Función / Procedimiento para cargar el Combobox de Docentes
-- -------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION "practica-mvc".sp_obtener_docentes_combo()
RETURNS TABLE(id INT, name VARCHAR) AS $$
BEGIN
    RETURN QUERY
    SELECT d.id, d.name
    FROM "practica-mvc".docentes d
    ORDER BY d.name ASC;
END;
$$ LANGUAGE plpgsql;

-- -------------------------------------------------------------------------
-- 2. Función / Procedimiento para cargar el Combobox de Materias
-- -------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION "practica-mvc".sp_obtener_materias_combo()
RETURNS TABLE(id INT, name VARCHAR) AS $$
BEGIN
    RETURN QUERY
    SELECT m.id, m.name
    FROM "practica-mvc".materias m
    ORDER BY m.name ASC;
END;
$$ LANGUAGE plpgsql;

-- -------------------------------------------------------------------------
-- Verificación de funcionamiento:
-- SELECT * FROM "practica-mvc".sp_obtener_docentes_combo();
-- SELECT * FROM "practica-mvc".sp_obtener_materias_combo();
-- =========================================================================
