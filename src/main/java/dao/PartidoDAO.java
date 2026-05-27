package dao;

import db.Conexion;
import modelo.Partido;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PartidoDAO {

    private Connection getConn() throws SQLException {
        return Conexion.getInstancia().getConexion();
    }

    private Partido mapear(ResultSet rs) throws SQLException {
        Partido p = new Partido();
        p.setIdPartido(rs.getInt("id_partido"));
        p.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
        p.setLugar(rs.getString("lugar"));
        p.setIdEquipoLocal(rs.getInt("id_equipo_local"));
        p.setIdEquipoVisit(rs.getInt("id_equipo_visit"));
        p.setIdTorneo(rs.getInt("id_torneo"));
        p.setIdArbitro(rs.getInt("id_arbitro"));
        try {
            p.setNombreEquipoLocal(rs.getString("equipo_local"));
            p.setNombreEquipoVisit(rs.getString("equipo_visit"));
            p.setNombreTorneo(rs.getString("nombre_torneo"));
            p.setNombreArbitro(rs.getString("nombre_arbitro"));
            p.setEstadoResultado(rs.getString("estado_resultado"));
            p.setPuntosLocal(rs.getInt("puntos_local"));
            p.setPuntosVisit(rs.getInt("puntos_visit"));
        } catch (SQLException ignored) {}
        return p;
    }

    private static final String SQL_BASE = """
            SELECT p.*,
                   el.nombre                          AS equipo_local,
                   ev.nombre                          AS equipo_visit,
                   t.nombre                           AS nombre_torneo,
                   CONCAT(a.nombre,' ',a.apellido)    AS nombre_arbitro,
                   r.estado                           AS estado_resultado,
                   COALESCE(r.puntos_local, 0)        AS puntos_local,
                   COALESCE(r.puntos_visit, 0)        AS puntos_visit
            FROM PARTIDOS p
            JOIN EQUIPOS  el ON el.id_equipo  = p.id_equipo_local
            JOIN EQUIPOS  ev ON ev.id_equipo  = p.id_equipo_visit
            JOIN TORNEOS  t  ON t.id_torneo   = p.id_torneo
            JOIN ARBITRO  a  ON a.id_arbitro  = p.id_arbitro
            LEFT JOIN RESULTADOS r ON r.id_partido = p.id_partido
            """;

    public List<Partido> listarPorTorneo(int idTorneo) throws SQLException {
        List<Partido> lista = new ArrayList<>();
        String sql = SQL_BASE + " WHERE p.id_torneo=? ORDER BY p.fecha";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idTorneo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    /** Partidos asignados a un árbitro específico */
    public List<Partido> listarPorArbitro(int idArbitro) throws SQLException {
        List<Partido> lista = new ArrayList<>();
        String sql = SQL_BASE + " WHERE p.id_arbitro=? ORDER BY p.fecha DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idArbitro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    /** Partidos en los que participa un equipo */
    public List<Partido> listarPorEquipo(int idEquipo) throws SQLException {
        List<Partido> lista = new ArrayList<>();
        String sql = SQL_BASE + " WHERE p.id_equipo_local=? OR p.id_equipo_visit=? ORDER BY p.fecha DESC";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idEquipo);
            ps.setInt(2, idEquipo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public Partido buscarPorId(int idPartido) throws SQLException {
        String sql = SQL_BASE + " WHERE p.id_partido=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idPartido);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    /**
     * Inserta un partido y crea automáticamente su resultado en estado 'pendiente'.
     * Retorna el id_partido generado.
     */
    public int insertar(Partido p) throws SQLException {
        String sql = "INSERT INTO PARTIDOS (fecha, lugar, id_equipo_local, id_equipo_visit, id_torneo, id_arbitro) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setTimestamp(1, Timestamp.valueOf(p.getFecha()));
            ps.setString(2, p.getLugar());
            ps.setInt(3, p.getIdEquipoLocal());
            ps.setInt(4, p.getIdEquipoVisit());
            ps.setInt(5, p.getIdTorneo());
            ps.setInt(6, p.getIdArbitro());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int idGenerado = keys.getInt(1);
                    // Crear resultado pendiente automáticamente
                    crearResultadoPendiente(idGenerado);
                    return idGenerado;
                }
            }
        }
        return -1;
    }

    private void crearResultadoPendiente(int idPartido) throws SQLException {
        String sql = "INSERT INTO RESULTADOS (id_partido, puntos_local, puntos_visit, estado) VALUES (?,0,0,'pendiente')";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idPartido);
            ps.executeUpdate();
        }
    }

    public boolean eliminar(int idPartido) throws SQLException {
        try (PreparedStatement ps = getConn().prepareStatement("DELETE FROM PARTIDOS WHERE id_partido=?")) {
            ps.setInt(1, idPartido);
            return ps.executeUpdate() > 0;
        }
    }
   /** Reasigna el árbitro de un partido. Solo aplica si el partido está pendiente. */
public boolean reasignarArbitro(int idPartido, int idArbitro) throws SQLException {
    String sql = """
            UPDATE PARTIDOS p
            JOIN RESULTADOS r ON r.id_partido = p.id_partido
            SET p.id_arbitro = ?
            WHERE p.id_partido = ? AND r.estado = 'pendiente'
            """;
    try (PreparedStatement ps = getConn().prepareStatement(sql)) {
        ps.setInt(1, idArbitro);
        ps.setInt(2, idPartido);
        return ps.executeUpdate() > 0;
    }
}

/** Lista todos los partidos pendientes de un torneo para mostrar en el selector. */
public List<Partido> listarPendientesPorTorneo(int idTorneo) throws SQLException {
    List<Partido> lista = new ArrayList<>();
    String sql = SQL_BASE + " WHERE p.id_torneo=? AND r.estado='pendiente' ORDER BY p.fecha";
    try (PreparedStatement ps = getConn().prepareStatement(sql)) {
        ps.setInt(1, idTorneo);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
    }
    return lista;

    }
}