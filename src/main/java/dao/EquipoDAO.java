package dao;

import db.Conexion;
import modelo.Equipo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipoDAO {

    private Connection getConn() throws SQLException {
        return Conexion.getInstancia().getConexion();
    }

    private Equipo mapear(ResultSet rs) throws SQLException {
        return new Equipo(
                rs.getInt("id_equipo"),
                rs.getString("nombre"),
                rs.getString("ciudad"),
                rs.getInt("id_usuario"),
                rs.getString("logo"),
                rs.getString("fechaCreacion"),
                rs.getInt("estado"),
                rs.getString("nombre_director")
        );
    }

    public List<Equipo> listar() throws SQLException {
        List<Equipo> lista = new ArrayList<>();
        String sql = """
                SELECT e.*, u.nombre AS nombre_director
                FROM EQUIPOS e LEFT JOIN USUARIOS u ON u.id_usuario = e.id_usuario
                WHERE e.estado = 1
                ORDER BY e.nombre
                """;
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    /** Equipos inscritos en un torneo específico */
    public List<Equipo> listarPorTorneo(int idTorneo) throws SQLException {
        List<Equipo> lista = new ArrayList<>();
        String sql = """
                SELECT e.*, u.nombre AS nombre_director
                FROM EQUIPOS e
                JOIN EQUIPO_TORNEO et ON et.id_equipo = e.id_equipo
                LEFT JOIN USUARIOS u ON u.id_usuario = e.id_usuario
                WHERE et.id_torneo = ? AND e.estado = 1
                ORDER BY e.nombre
                """;
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idTorneo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    /** Equipo del director logueado */
    public Equipo buscarPorUsuario(int idUsuario) throws SQLException {
        String sql = "SELECT e.*, u.nombre AS nombre_director FROM EQUIPOS e LEFT JOIN USUARIOS u ON u.id_usuario = e.id_usuario WHERE e.id_usuario=? AND e.estado = 1";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public Equipo buscarPorId(int id) throws SQLException {
        String sql = "SELECT e.*, u.nombre AS nombre_director FROM EQUIPOS e LEFT JOIN USUARIOS u ON u.id_usuario = e.id_usuario WHERE e.id_equipo=? AND e.estado = 1";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public boolean insertar(Equipo e) throws SQLException {
        String sql = "INSERT INTO EQUIPOS (nombre, ciudad, id_usuario, logo, fechaCreacion, estado) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, e.getNombre());
            ps.setString(2, e.getCiudad());
            ps.setInt(3, e.getIdUsuario());
            ps.setString(4, e.getLogo());
            ps.setString(5, e.getFechaCreacion());
            ps.setInt(6, e.getEstado());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizar(Equipo e) throws SQLException {
        String sql = "UPDATE EQUIPOS SET nombre=?, ciudad=?, id_usuario=?, logo=?, fechaCreacion=?, estado=? WHERE id_equipo=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, e.getNombre());
            ps.setString(2, e.getCiudad());
            ps.setInt(3, e.getIdUsuario());
            ps.setString(4, e.getLogo());
            ps.setString(5, e.getFechaCreacion());
            ps.setInt(6, e.getEstado());
            ps.setInt(7, e.getIdEquipo());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int idEquipo) throws SQLException {
        String sql = "DELETE FROM EQUIPOS WHERE id_equipo=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idEquipo);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean desactivar(int idEquipo) throws SQLException {
        String sql = "UPDATE EQUIPOS SET estado=? WHERE id_equipo=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, 0);
            ps.setInt(2, idEquipo);
            return ps.executeUpdate() > 0;
        }
    }
}