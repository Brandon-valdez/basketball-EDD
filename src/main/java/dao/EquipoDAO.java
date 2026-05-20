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
        Equipo e = new Equipo();
        e.setIdEquipo(rs.getInt("id_equipo"));
        e.setNombre(rs.getString("nombre"));
        e.setCiudad(rs.getString("ciudad"));
        e.setIdUsuario(rs.getInt("id_usuario"));
        try { e.setNombreDirector(rs.getString("nombre_director")); } catch (SQLException ignored) {}
        return e;
    }

    public List<Equipo> listar() throws SQLException {
        List<Equipo> lista = new ArrayList<>();
        String sql = """
                SELECT e.*, u.nombre AS nombre_director
                FROM EQUIPOS e LEFT JOIN USUARIOS u ON u.id_usuario = e.id_usuario
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
                WHERE et.id_torneo = ?
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
        String sql = "SELECT e.*, u.nombre AS nombre_director FROM EQUIPOS e LEFT JOIN USUARIOS u ON u.id_usuario = e.id_usuario WHERE e.id_usuario=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public Equipo buscarPorId(int id) throws SQLException {
        String sql = "SELECT e.*, u.nombre AS nombre_director FROM EQUIPOS e LEFT JOIN USUARIOS u ON u.id_usuario = e.id_usuario WHERE e.id_equipo=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public boolean insertar(Equipo e) throws SQLException {
        String sql = "INSERT INTO EQUIPOS (nombre, ciudad, id_usuario) VALUES (?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, e.getNombre());
            ps.setString(2, e.getCiudad());
            ps.setInt(3, e.getIdUsuario());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizar(Equipo e) throws SQLException {
        String sql = "UPDATE EQUIPOS SET nombre=?, ciudad=?, id_usuario=? WHERE id_equipo=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, e.getNombre());
            ps.setString(2, e.getCiudad());
            ps.setInt(3, e.getIdUsuario());
            ps.setInt(4, e.getIdEquipo());
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
}