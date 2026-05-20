package dao;

import db.Conexion;
import modelo.Estadistica;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstadisticaDAO {

    private Connection getConn() throws SQLException {
        return Conexion.getInstancia().getConexion();
    }

    private Estadistica mapear(ResultSet rs) throws SQLException {
        Estadistica e = new Estadistica();
        e.setIdEstadistica(rs.getInt("id_estadistica"));
        e.setIdJugador(rs.getInt("id_jugador"));
        e.setIdPartido(rs.getInt("id_partido"));
        e.setPuntos(rs.getInt("puntos"));
        e.setRebotes(rs.getInt("rebotes"));
        e.setAsistencias(rs.getInt("asistencias"));
        e.setFaltas(rs.getInt("faltas"));
        e.setMinutosJugados(rs.getInt("minutos_jugados"));
        try {
            e.setNombreJugador(rs.getString("nombre_jugador"));
            e.setPosicion(rs.getString("posicion"));
            e.setNombreEquipo(rs.getString("nombre_equipo"));
        } catch (SQLException ignored) {}
        return e;
    }

    /** Estadísticas de todos los jugadores en un partido */
    public List<Estadistica> listarPorPartido(int idPartido) throws SQLException {
        List<Estadistica> lista = new ArrayList<>();
        String sql = """
                SELECT es.*,
                       CONCAT(j.nombre,' ',j.apellido) AS nombre_jugador,
                       j.posicion,
                       eq.nombre AS nombre_equipo
                FROM ESTADISTICA es
                JOIN JUGADORES j ON j.id_jugador = es.id_jugador
                JOIN EQUIPOS eq  ON eq.id_equipo  = j.id_equipo
                WHERE es.id_partido = ?
                ORDER BY eq.nombre, es.puntos DESC
                """;
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idPartido);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    /** Estadísticas acumuladas de un jugador en todos sus partidos */
    public List<Estadistica> listarPorJugador(int idJugador) throws SQLException {
        List<Estadistica> lista = new ArrayList<>();
        String sql = "SELECT es.*, CONCAT(j.nombre,' ',j.apellido) AS nombre_jugador, j.posicion, eq.nombre AS nombre_equipo FROM ESTADISTICA es JOIN JUGADORES j ON j.id_jugador=es.id_jugador JOIN EQUIPOS eq ON eq.id_equipo=j.id_equipo WHERE es.id_jugador=? ORDER BY es.id_partido";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idJugador);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    /** Inserta o actualiza la estadística de un jugador en un partido */
    public boolean guardar(Estadistica e) throws SQLException {
        // Si ya existe, actualiza; si no, inserta
        String check = "SELECT id_estadistica FROM ESTADISTICA WHERE id_jugador=? AND id_partido=?";
        try (PreparedStatement ps = getConn().prepareStatement(check)) {
            ps.setInt(1, e.getIdJugador());
            ps.setInt(2, e.getIdPartido());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // UPDATE
                    String upd = "UPDATE ESTADISTICA SET puntos=?, rebotes=?, asistencias=?, faltas=?, minutos_jugados=? WHERE id_jugador=? AND id_partido=?";
                    try (PreparedStatement pu = getConn().prepareStatement(upd)) {
                        pu.setInt(1, e.getPuntos());
                        pu.setInt(2, e.getRebotes());
                        pu.setInt(3, e.getAsistencias());
                        pu.setInt(4, e.getFaltas());
                        pu.setInt(5, e.getMinutosJugados());
                        pu.setInt(6, e.getIdJugador());
                        pu.setInt(7, e.getIdPartido());
                        return pu.executeUpdate() > 0;
                    }
                }
            }
        }
        // INSERT
        String ins = "INSERT INTO ESTADISTICA (id_jugador, id_partido, puntos, rebotes, asistencias, faltas, minutos_jugados) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(ins)) {
            ps.setInt(1, e.getIdJugador());
            ps.setInt(2, e.getIdPartido());
            ps.setInt(3, e.getPuntos());
            ps.setInt(4, e.getRebotes());
            ps.setInt(5, e.getAsistencias());
            ps.setInt(6, e.getFaltas());
            ps.setInt(7, e.getMinutosJugados());
            return ps.executeUpdate() > 0;
        }
    }
}