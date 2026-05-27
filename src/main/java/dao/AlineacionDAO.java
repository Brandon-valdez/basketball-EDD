package dao;

import db.Conexion;
import modelo.Alineacion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlineacionDAO {

    private Connection getConn() throws SQLException {
        return Conexion.getInstancia().getConexion();
    }

    public boolean insertar(Alineacion alineacion) throws SQLException {
        String sql = "INSERT INTO ALINEACION (id_partido, id_equipo, base_id, escolta_id, alero_id, ala_pivot_id, pivot_id, tipo) VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, alineacion.getIdPartido());
            ps.setInt(2, alineacion.getIdEquipo());
            ps.setInt(3, alineacion.getBaseId());
            ps.setInt(4, alineacion.getEscoltaId());
            ps.setInt(5, alineacion.getAleroId());
            ps.setInt(6, alineacion.getAlaPivotId());
            ps.setInt(7, alineacion.getPivotId());
            ps.setString(8, alineacion.getTipoDb());
            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        alineacion.setIdAlineacion(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    public boolean actualizar(Alineacion alineacion) throws SQLException {
        String sql = "UPDATE ALINEACION SET id_partido=?, id_equipo=?, base_id=?, escolta_id=?, alero_id=?, ala_pivot_id=?, pivot_id=?, tipo=? WHERE id_alineacion=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, alineacion.getIdPartido());
            ps.setInt(2, alineacion.getIdEquipo());
            ps.setInt(3, alineacion.getBaseId());
            ps.setInt(4, alineacion.getEscoltaId());
            ps.setInt(5, alineacion.getAleroId());
            ps.setInt(6, alineacion.getAlaPivotId());
            ps.setInt(7, alineacion.getPivotId());
            ps.setString(8, alineacion.getTipoDb());
            ps.setInt(9, alineacion.getIdAlineacion());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int idAlineacion) throws SQLException {
        String sql = "DELETE FROM ALINEACION WHERE id_alineacion=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idAlineacion);
            return ps.executeUpdate() > 0;
        }
    }

    public Alineacion buscarPorId(int idAlineacion) throws SQLException {
        String sql = "SELECT id_alineacion, id_partido, id_equipo, base_id, escolta_id, alero_id, ala_pivot_id, pivot_id, tipo FROM ALINEACION WHERE id_alineacion=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idAlineacion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    public Alineacion buscarPorPartidoYEquipo(int idPartido, int idEquipo) throws SQLException {
        String sql = "SELECT id_alineacion, id_partido, id_equipo, base_id, escolta_id, alero_id, ala_pivot_id, pivot_id, tipo FROM ALINEACION WHERE id_partido=? AND id_equipo=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idPartido);
            ps.setInt(2, idEquipo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    public List<Alineacion> listarPorPartido(int idPartido) throws SQLException {
        List<Alineacion> lista = new ArrayList<>();
        String sql = "SELECT id_alineacion, id_partido, id_equipo, base_id, escolta_id, alero_id, ala_pivot_id, pivot_id, tipo FROM ALINEACION WHERE id_partido=? ORDER BY id_equipo";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idPartido);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    public boolean guardar(Alineacion alineacion) throws SQLException {
        Alineacion existente = buscarPorPartidoYEquipo(alineacion.getIdPartido(), alineacion.getIdEquipo());
        if (existente == null) {
            return insertar(alineacion);
        }
        alineacion.setIdAlineacion(existente.getIdAlineacion());
        return actualizar(alineacion);
    }

    private Alineacion mapear(ResultSet rs) throws SQLException {
        Alineacion a = new Alineacion(
                rs.getInt("id_alineacion"),
                rs.getInt("id_partido"),
                rs.getInt("id_equipo"),
                rs.getInt("base_id"),
                rs.getInt("escolta_id"),
                rs.getInt("alero_id"),
                rs.getInt("ala_pivot_id"),
                rs.getInt("pivot_id")
        );
        a.setTipoDb(rs.getString("tipo"));
        return a;
    }
    /**
 * Guarda la alineación y sus suplentes en una sola transacción.
 * Si falla cualquier INSERT, hace rollback completo.
 */
public boolean guardarConSuplentes(Alineacion alineacion,
                                   List<modelo.Suplente> suplentes) throws SQLException {
    Connection conn = getConn();
    try {
        // ── Inicio de transacción ─────────────────────
        conn.setAutoCommit(false);

        // 1. Insertar o actualizar alineación
        Alineacion existente = buscarPorPartidoYEquipo(
            alineacion.getIdPartido(), alineacion.getIdEquipo());

        if (existente == null) {
            insertar(alineacion);
        } else {
            alineacion.setIdAlineacion(existente.getIdAlineacion());
            actualizar(alineacion);
        }

        // 2. Borrar suplentes anteriores y reinsertar
        String sqlDel = "DELETE FROM SUPLENTES WHERE id_alineacion=?";
        try (PreparedStatement ps = conn.prepareStatement(sqlDel)) {
            ps.setInt(1, alineacion.getIdAlineacion());
            ps.executeUpdate();
        }

        String sqlIns = "INSERT INTO SUPLENTES (id_alineacion, id_jugador, orden_ingreso) VALUES (?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sqlIns)) {
            for (modelo.Suplente s : suplentes) {
                ps.setInt(1, alineacion.getIdAlineacion());
                ps.setInt(2, s.getIdJugador());
                if (s.getOrdenIngreso() == null)
                    ps.setNull(3, java.sql.Types.INTEGER);
                else
                    ps.setInt(3, s.getOrdenIngreso());
                ps.addBatch();
            }
            ps.executeBatch();
        }

        // ── Commit ────────────────────────────────────
        conn.commit();
        return true;

    } catch (SQLException e) {
        // ── Rollback si algo falló ────────────────────
        try { conn.rollback(); } catch (SQLException ex) { /* ignorar */ }
        throw e;
    } finally {
        // ── Restaurar autocommit ──────────────────────
        try { conn.setAutoCommit(true); } catch (SQLException ex) { /* ignorar */ }
    }
}
}