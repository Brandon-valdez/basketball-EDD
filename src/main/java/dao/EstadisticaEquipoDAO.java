package dao;

import db.Conexion;
import modelo.EstadisticaEquipo;

import java.sql.*;

public class EstadisticaEquipoDAO {

    private Connection getConn() throws SQLException {
        return Conexion.getInstancia().getConexion();
    }

    /** Busca estadísticas de un equipo en un partido específico */
    public EstadisticaEquipo buscar(int idPartido, int idEquipo) throws SQLException {
        String sql = "SELECT * FROM ESTADISTICA_EQUIPO WHERE id_partido=? AND id_equipo=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idPartido);
            ps.setInt(2, idEquipo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    EstadisticaEquipo e = new EstadisticaEquipo();
                    try {
                        e.setIdEstadisticaEquipo(rs.getInt("id_estadistica_equipo"));
                    } catch (SQLException ex) {
                        // La columna podría no existir o tener otro nombre
                        try {
                            e.setIdEstadisticaEquipo(rs.getInt("id"));
                        } catch (SQLException ex2) {
                            e.setIdEstadisticaEquipo(0);
                        }
                    }
                    e.setIdPartido(rs.getInt("id_partido"));
                    e.setIdEquipo(rs.getInt("id_equipo"));
                    e.setPuntos(rs.getInt("puntos"));
                    e.setFaltas(rs.getInt("faltas"));
                    e.setTriplesAnotados(rs.getInt("triples_anotados"));
                    e.setTirosLibresAnotados(rs.getInt("tiros_libres_anotados"));
                    e.setRebotes(rs.getInt("rebotes"));
                    e.setAsistencias(rs.getInt("asistencias"));
                    return e;
                }
            }
        }
        return null;
    }

    /** Inserta o actualiza las estadísticas de un equipo en un partido */
    public boolean guardar(EstadisticaEquipo e) throws SQLException {
        EstadisticaEquipo existente = buscar(e.getIdPartido(), e.getIdEquipo());

        if (existente != null) {
            String sql = """
                UPDATE ESTADISTICA_EQUIPO
                SET puntos=?, faltas=?, triples_anotados=?,
                    tiros_libres_anotados=?, rebotes=?, asistencias=?
                WHERE id_partido=? AND id_equipo=?
                """;
            try (PreparedStatement ps = getConn().prepareStatement(sql)) {
                ps.setInt(1, e.getPuntos());
                ps.setInt(2, e.getFaltas());
                ps.setInt(3, e.getTriplesAnotados());
                ps.setInt(4, e.getTirosLibresAnotados());
                ps.setInt(5, e.getRebotes());
                ps.setInt(6, e.getAsistencias());
                ps.setInt(7, e.getIdPartido());
                ps.setInt(8, e.getIdEquipo());
                return ps.executeUpdate() > 0;
            }
        } else {
            String sql = """
                INSERT INTO ESTADISTICA_EQUIPO
                (id_partido, id_equipo, puntos, faltas, triples_anotados,
                 tiros_libres_anotados, rebotes, asistencias)
                VALUES (?,?,?,?,?,?,?,?)
                """;
            try (PreparedStatement ps = getConn().prepareStatement(sql)) {
                ps.setInt(1, e.getIdPartido());
                ps.setInt(2, e.getIdEquipo());
                ps.setInt(3, e.getPuntos());
                ps.setInt(4, e.getFaltas());
                ps.setInt(5, e.getTriplesAnotados());
                ps.setInt(6, e.getTirosLibresAnotados());
                ps.setInt(7, e.getRebotes());
                ps.setInt(8, e.getAsistencias());
                return ps.executeUpdate() > 0;
            }
        }
    }
}