package dao;

import db.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO de informes: consulta las vistas SQL ya creadas en la BD
 * y devuelve los datos como listas de mapas (columna → valor),
 * listas de arrays, o un TableModel listo para JTable.
 *
 * Las vistas que usa:
 *   v_tabla_posiciones  → clasificación por torneo
 *   v_top_jugadores     → ranking de jugadores
 *   v_historial_partidos → todos los partidos con resultado
 */
public class InformeDAO {

    private Connection getConn() throws SQLException {
        return Conexion.getInstancia().getConexion();
    }

    // ─────────────────────────────────────────────────────────────
    // 1. TABLA DE POSICIONES
    // ─────────────────────────────────────────────────────────────

    /**
     * Retorna la tabla de posiciones de un torneo.
     * Columnas: Equipo, Ciudad, PJ, V, D, PF, PC, Diferencia
     */
    public Object[][] tablaPosiciones(int idTorneo) throws SQLException {
        String sql = """
                SELECT equipo,
                       victorias, derrotas,
                       (victorias + derrotas)  AS pj,
                       puntos_favor, puntos_contra,
                       (puntos_favor - puntos_contra) AS diferencia
                FROM v_tabla_posiciones
                WHERE id_torneo = ?
                ORDER BY victorias DESC, diferencia DESC
                """;
        return ejecutarQuery(sql, idTorneo);
    }

    public static final String[] COLS_POSICIONES = {
        "Equipo", "V", "D", "PJ", "Pts Favor", "Pts Contra", "Diferencia"
    };

    // ─────────────────────────────────────────────────────────────
    // 2. TOP JUGADORES
    // ─────────────────────────────────────────────────────────────

    /**
     * Ranking global de jugadores (todos los torneos).
     * Columnas: Jugador, Equipo, PJ, Pts, Reb, Ast, Faltas, Prom.Pts, Prom.Reb, Prom.Ast
     */
    public Object[][] topJugadores() throws SQLException {
        String sql = """
                SELECT jugador, equipo,
                       partidos_jugados,
                       total_puntos, total_rebotes, total_asistencias, total_faltas,
                       promedio_puntos, promedio_rebotes, promedio_asistencias
                FROM v_top_jugadores
                ORDER BY total_puntos DESC
                LIMIT 50
                """;
        return ejecutarQuery(sql);
    }

    /** Top N anotadores */
    public Object[][] topAnotadores(int limite) throws SQLException {
        String sql = "SELECT jugador, equipo, total_puntos, promedio_puntos FROM v_top_jugadores ORDER BY total_puntos DESC LIMIT ?";
        return ejecutarQuery(sql, limite);
    }

    /** Top N reboteadores */
    public Object[][] topReboteadores(int limite) throws SQLException {
        String sql = "SELECT jugador, equipo, total_rebotes, promedio_rebotes FROM v_top_jugadores ORDER BY total_rebotes DESC LIMIT ?";
        return ejecutarQuery(sql, limite);
    }

    /** Top N asistidores */
    public Object[][] topAsistidores(int limite) throws SQLException {
        String sql = "SELECT jugador, equipo, total_asistencias, promedio_asistencias FROM v_top_jugadores ORDER BY total_asistencias DESC LIMIT ?";
        return ejecutarQuery(sql, limite);
    }

    public static final String[] COLS_TOP_JUGADORES = {
        "Jugador", "Equipo", "PJ", "Pts", "Reb", "Ast", "Faltas",
        "Prom.Pts", "Prom.Reb", "Prom.Ast"
    };

    // ─────────────────────────────────────────────────────────────
    // 3. HISTORIAL DE PARTIDOS
    // ─────────────────────────────────────────────────────────────

    /** Todos los partidos de un torneo con resultado */
    public Object[][] historialPorTorneo(int idTorneo) throws SQLException {
        String sql = """
                SELECT fecha, equipo_local, puntos_local, puntos_visit,
                       equipo_visitante, ganador, arbitro, lugar
                FROM v_historial_partidos
                WHERE torneo IN (SELECT nombre FROM TORNEOS WHERE id_torneo = ?)
                ORDER BY fecha DESC
                """;
        return ejecutarQuery(sql, idTorneo);
    }

    /** Historial completo (todos los torneos) */
    public Object[][] historialCompleto() throws SQLException {
        String sql = """
                SELECT torneo, fecha, equipo_local, puntos_local,
                       puntos_visit, equipo_visitante, estado, ganador
                FROM v_historial_partidos
                ORDER BY fecha DESC
                """;
        return ejecutarQuery(sql);
    }

    public static final String[] COLS_HISTORIAL = {
        "Torneo", "Fecha", "Local", "Pts Local", "Pts Visit", "Visitante", "Estado", "Ganador"
    };

    // ─────────────────────────────────────────────────────────────
    // 4. ESTADÍSTICAS POR PARTIDO (detalle)
    // ─────────────────────────────────────────────────────────────

    /** Box score completo de un partido */
    public Object[][] boxScore(int idPartido) throws SQLException {
        String sql = """
                SELECT eq.nombre AS equipo,
                       CONCAT(j.nombre,' ',j.apellido) AS jugador,
                       j.posicion,
                       es.minutos_jugados, es.puntos, es.rebotes,
                       es.asistencias, es.faltas
                FROM ESTADISTICA es
                JOIN JUGADORES j ON j.id_jugador = es.id_jugador
                JOIN EQUIPOS eq  ON eq.id_equipo  = j.id_equipo
                WHERE es.id_partido = ?
                ORDER BY eq.nombre, es.puntos DESC
                """;
        return ejecutarQuery(sql, idPartido);
    }

    public static final String[] COLS_BOX_SCORE = {
        "Equipo", "Jugador", "Pos", "Min", "Pts", "Reb", "Ast", "Faltas"
    };

    // ─────────────────────────────────────────────────────────────
    // 5. RESUMEN DE TORNEO (para portada del informe)
    // ─────────────────────────────────────────────────────────────

    /**
     * Retorna un mapa con los datos generales del torneo:
     * nombre, fechas, ubicacion, total_equipos, total_partidos,
     * partidos_finalizados, total_jugadores
     */
    public Map<String, String> resumenTorneo(int idTorneo) throws SQLException {
        Map<String, String> datos = new LinkedHashMap<>();
        String sql = """
                SELECT t.nombre, t.fecha_inicio, t.fecha_fin, t.ubicacion, t.estado,
                       COUNT(DISTINCT et.id_equipo)  AS total_equipos,
                       COUNT(DISTINCT p.id_partido)  AS total_partidos,
                       SUM(CASE WHEN r.estado='finalizado' THEN 1 ELSE 0 END) AS finalizados,
                       COUNT(DISTINCT j.id_jugador)  AS total_jugadores
                FROM TORNEOS t
                LEFT JOIN EQUIPO_TORNEO et ON et.id_torneo = t.id_torneo
                LEFT JOIN PARTIDOS p       ON p.id_torneo  = t.id_torneo
                LEFT JOIN RESULTADOS r     ON r.id_partido = p.id_partido
                LEFT JOIN JUGADORES j      ON j.id_equipo  = et.id_equipo
                WHERE t.id_torneo = ?
                GROUP BY t.id_torneo
                """;
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idTorneo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    datos.put("Torneo",              rs.getString("nombre"));
                    datos.put("Inicio",              rs.getString("fecha_inicio"));
                    datos.put("Fin",                 rs.getString("fecha_fin"));
                    datos.put("Ubicación",           rs.getString("ubicacion"));
                    datos.put("Estado",              rs.getString("estado"));
                    datos.put("Equipos",             rs.getString("total_equipos"));
                    datos.put("Partidos totales",    rs.getString("total_partidos"));
                    datos.put("Partidos jugados",    rs.getString("finalizados"));
                    datos.put("Jugadores",           rs.getString("total_jugadores"));
                }
            }
        }
        return datos;
    }

    // ─────────────────────────────────────────────────────────────
    // UTILIDADES INTERNAS
    // ─────────────────────────────────────────────────────────────

    /** Ejecuta una query sin parámetros y devuelve Object[][] */
    private Object[][] ejecutarQuery(String sql) throws SQLException {
        return ejecutarQuery(sql, (Object[]) null);
    }

    /** Ejecuta una query con 1 o más parámetros y devuelve Object[][] */
    private Object[][] ejecutarQuery(String sql, Object... params) throws SQLException {
        List<Object[]> filas = new ArrayList<>();
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    ps.setObject(i + 1, params[i]);
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData meta = rs.getMetaData();
                int cols = meta.getColumnCount();
                while (rs.next()) {
                    Object[] fila = new Object[cols];
                    for (int c = 1; c <= cols; c++) {
                        fila[c - 1] = rs.getObject(c);
                    }
                    filas.add(fila);
                }
            }
        }
        return filas.toArray(new Object[0][]);
    }
}