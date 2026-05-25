package dao;

import db.Conexion;
import modelo.Jugador;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JugadorDAO {

    private Connection getConn() throws SQLException {
        return Conexion.getInstancia().getConexion();
    }

    private Jugador mapear(ResultSet rs) throws SQLException {
        Jugador j = new Jugador();
        j.setIdJugador(rs.getInt("id_jugador"));
        j.setNombre(rs.getString("nombre"));
        j.setApellido(rs.getString("apellido"));
        Date fn = rs.getDate("fecha_nac");
        if (fn != null) j.setFechaNac(fn.toLocalDate());
        j.setPosicion(rs.getString("posicion"));
        j.setNumeroCamiseta(rs.getInt("numero_camiseta"));
        j.setIdEquipo(rs.getInt("id_equipo"));
        j.setImagen(rs.getString("imagen"));
        j.setEstado(rs.getInt("estado"));
        try { j.setNombreEquipo(rs.getString("nombre_equipo")); } catch (SQLException ignored) {}
        return j;
    }

    public List<Jugador> listarPorEquipo(int idEquipo) throws SQLException {
        List<Jugador> lista = new ArrayList<>();
        String sql = """
                SELECT j.*, e.nombre AS nombre_equipo
                FROM JUGADORES j JOIN EQUIPOS e ON e.id_equipo = j.id_equipo
                WHERE j.id_equipo = ? AND j.estado = 1
                ORDER BY j.numero_camiseta
                """;
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idEquipo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public List<Jugador> listarTodos() throws SQLException {
        List<Jugador> lista = new ArrayList<>();
        String sql = """
                SELECT j.*, e.nombre AS nombre_equipo
                FROM JUGADORES j JOIN EQUIPOS e ON e.id_equipo = j.id_equipo
                WHERE j.estado = 1
                ORDER BY e.nombre, j.numero_camiseta
                """;
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public Jugador buscarPorId(int idJugador) throws SQLException {
        String sql = "SELECT j.*, e.nombre AS nombre_equipo FROM JUGADORES j JOIN EQUIPOS e ON e.id_equipo=j.id_equipo WHERE j.id_jugador=? AND j.estado = 1";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idJugador);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public boolean insertar(Jugador j) throws SQLException {
        String sql = "INSERT INTO JUGADORES (nombre, apellido, fecha_nac, posicion, numero_camiseta, id_equipo, imagen, estado) VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, j.getNombre());
            ps.setString(2, j.getApellido());
            ps.setDate(3, j.getFechaNac() != null ? Date.valueOf(j.getFechaNac()) : null);
            ps.setString(4, j.getPosicion());
            ps.setInt(5, j.getNumeroCamiseta());
            ps.setInt(6, j.getIdEquipo());
            ps.setString(7, j.getImagen());
            ps.setInt(8, j.getEstado());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizar(Jugador j) throws SQLException {
        String sql = "UPDATE JUGADORES SET nombre=?, apellido=?, fecha_nac=?, posicion=?, numero_camiseta=?, imagen=?, estado=? WHERE id_jugador=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, j.getNombre());
            ps.setString(2, j.getApellido());
            ps.setDate(3, j.getFechaNac() != null ? Date.valueOf(j.getFechaNac()) : null);
            ps.setString(4, j.getPosicion());
            ps.setInt(5, j.getNumeroCamiseta());
            ps.setString(6, j.getImagen());
            ps.setInt(7, j.getEstado());
            ps.setInt(8, j.getIdJugador());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int idJugador) throws SQLException {
        String sql = "UPDATE JUGADORES SET estado = 0 WHERE id_jugador=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idJugador);
            return ps.executeUpdate() > 0;
        }
    }

    /** Verifica si el número de camiseta ya está en uso dentro del equipo */
    public boolean existeNumeroCamiseta(int numero, int idEquipo, int idJugadorExcluir) throws SQLException {
        String sql = "SELECT COUNT(*) FROM JUGADORES WHERE numero_camiseta=? AND id_equipo=? AND id_jugador<>? AND estado = 1";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, numero);
            ps.setInt(2, idEquipo);
            ps.setInt(3, idJugadorExcluir);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
}