package dao;

import db.Conexion;
import modelo.Torneo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TorneoDAO {

    private Connection getConn() throws SQLException {
        return Conexion.getInstancia().getConexion();
    }

    private Torneo mapear(ResultSet rs) throws SQLException {
        Torneo t = new Torneo();
        t.setIdTorneo(rs.getInt("id_torneo"));
        t.setNombre(rs.getString("nombre"));
        t.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
        Date fin = rs.getDate("fecha_fin");
        if (fin != null) t.setFechaFin(fin.toLocalDate());
        t.setUbicacion(rs.getString("ubicacion"));
        t.setEstado(rs.getString("estado"));
        return t;
    }

    public List<Torneo> listar() throws SQLException {
        List<Torneo> lista = new ArrayList<>();
        String sql = "SELECT * FROM TORNEOS ORDER BY fecha_inicio DESC";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public List<Torneo> listarActivos() throws SQLException {
        List<Torneo> lista = new ArrayList<>();
        String sql = "SELECT * FROM TORNEOS WHERE estado='activo' ORDER BY fecha_inicio";
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public Torneo buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM TORNEOS WHERE id_torneo=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public boolean insertar(Torneo t) throws SQLException {
        String sql = "INSERT INTO TORNEOS (nombre, fecha_inicio, fecha_fin, ubicacion, estado) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, t.getNombre());
            ps.setDate(2, Date.valueOf(t.getFechaInicio()));
            ps.setDate(3, t.getFechaFin() != null ? Date.valueOf(t.getFechaFin()) : null);
            ps.setString(4, t.getUbicacion());
            ps.setString(5, t.getEstado());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizar(Torneo t) throws SQLException {
        String sql = "UPDATE TORNEOS SET nombre=?, fecha_inicio=?, fecha_fin=?, ubicacion=?, estado=? WHERE id_torneo=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, t.getNombre());
            ps.setDate(2, Date.valueOf(t.getFechaInicio()));
            ps.setDate(3, t.getFechaFin() != null ? Date.valueOf(t.getFechaFin()) : null);
            ps.setString(4, t.getUbicacion());
            ps.setString(5, t.getEstado());
            ps.setInt(6, t.getIdTorneo());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean cambiarEstado(int idTorneo, String nuevoEstado) throws SQLException {
        String sql = "UPDATE TORNEOS SET estado=? WHERE id_torneo=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idTorneo);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int idTorneo) throws SQLException {
        String sql = "DELETE FROM TORNEOS WHERE id_torneo=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idTorneo);
            return ps.executeUpdate() > 0;
        }
    }

    /** Inscribe un equipo en un torneo */
    public boolean inscribirEquipo(int idEquipo, int idTorneo) throws SQLException {
        String sql = "INSERT IGNORE INTO EQUIPO_TORNEO (id_equipo, id_torneo) VALUES (?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idEquipo);
            ps.setInt(2, idTorneo);
            return ps.executeUpdate() > 0;
        }
    }

    /** Desinscribe un equipo de un torneo */
    public boolean desinscribirEquipo(int idEquipo, int idTorneo) throws SQLException {
        String sql = "DELETE FROM EQUIPO_TORNEO WHERE id_equipo=? AND id_torneo=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idEquipo);
            ps.setInt(2, idTorneo);
            return ps.executeUpdate() > 0;
        }
    }
}