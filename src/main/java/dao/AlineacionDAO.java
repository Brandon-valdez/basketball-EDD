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

    /** Alineación de un equipo en un partido (titulares y suplentes) */
    public List<Alineacion> listar(int idPartido, int idEquipo) throws SQLException {
        List<Alineacion> lista = new ArrayList<>();
        String sql = """
                SELECT al.*, CONCAT(j.nombre,' ',j.apellido) AS nombre_jugador,
                       j.posicion, j.numero_camiseta
                FROM ALINEACION al JOIN JUGADORES j ON j.id_jugador = al.id_jugador
                WHERE al.id_partido=? AND al.id_equipo=?
                ORDER BY al.titular DESC, j.numero_camiseta
                """;
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idPartido);
            ps.setInt(2, idEquipo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Alineacion a = new Alineacion();
                    a.setIdAlineacion(rs.getInt("id_alineacion"));
                    a.setIdPartido(rs.getInt("id_partido"));
                    a.setIdEquipo(rs.getInt("id_equipo"));
                    a.setIdJugador(rs.getInt("id_jugador"));
                    a.setTitular(rs.getBoolean("titular"));
                    a.setNombreJugador(rs.getString("nombre_jugador"));
                    a.setPosicion(rs.getString("posicion"));
                    a.setNumeroCamiseta(rs.getInt("numero_camiseta"));
                    lista.add(a);
                }
            }
        }
        return lista;
    }

    /** Guarda la alineación completa de un equipo para un partido (borra y re-inserta) */
    public boolean guardarAlineacion(int idPartido, int idEquipo, List<Alineacion> jugadores) throws SQLException {
        // Borrar alineación previa de ese equipo en ese partido
        String del = "DELETE FROM ALINEACION WHERE id_partido=? AND id_equipo=?";
        try (PreparedStatement ps = getConn().prepareStatement(del)) {
            ps.setInt(1, idPartido);
            ps.setInt(2, idEquipo);
            ps.executeUpdate();
        }
        // Re-insertar
        String ins = "INSERT INTO ALINEACION (id_partido, id_equipo, id_jugador, titular) VALUES (?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(ins)) {
            for (Alineacion a : jugadores) {
                ps.setInt(1, idPartido);
                ps.setInt(2, idEquipo);
                ps.setInt(3, a.getIdJugador());
                ps.setBoolean(4, a.isTitular());
                ps.addBatch();
            }
            int[] resultados = ps.executeBatch();
            return resultados.length == jugadores.size();
        }
    }
}