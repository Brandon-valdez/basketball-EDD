package dao;

import db.Conexion;
import modelo.Suplente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SuplenteDAO {

    private Connection getConn() throws SQLException {
        return Conexion.getInstancia().getConexion();
    }

    public boolean insertar(Suplente suplente) throws SQLException {
        String sql = "INSERT INTO SUPLENTES (id_alineacion, id_jugador, orden_ingreso) VALUES (?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, suplente.getIdAlineacion());
            ps.setInt(2, suplente.getIdJugador());
            if (suplente.getOrdenIngreso() == null) {
                ps.setNull(3, java.sql.Types.INTEGER);
            } else {
                ps.setInt(3, suplente.getOrdenIngreso());
            }
            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        suplente.setIdSuplente(rs.getInt(1));
                    }
                }
                return true;
            }
            return false;
        }
    }

    public boolean actualizar(Suplente suplente) throws SQLException {
        String sql = "UPDATE SUPLENTES SET id_alineacion=?, id_jugador=?, orden_ingreso=? WHERE id_suplente=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, suplente.getIdAlineacion());
            ps.setInt(2, suplente.getIdJugador());
            if (suplente.getOrdenIngreso() == null) {
                ps.setNull(3, java.sql.Types.INTEGER);
            } else {
                ps.setInt(3, suplente.getOrdenIngreso());
            }
            ps.setInt(4, suplente.getIdSuplente());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int idSuplente) throws SQLException {
        String sql = "DELETE FROM SUPLENTES WHERE id_suplente=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idSuplente);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Suplente> listarPorAlineacion(int idAlineacion) throws SQLException {
        List<Suplente> lista = new ArrayList<>();
        String sql = "SELECT id_suplente, id_alineacion, id_jugador, orden_ingreso FROM SUPLENTES WHERE id_alineacion=? ORDER BY orden_ingreso, id_suplente";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idAlineacion);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Suplente(
                            rs.getInt("id_suplente"),
                            rs.getInt("id_alineacion"),
                            rs.getInt("id_jugador"),
                            (Integer) rs.getObject("orden_ingreso")
                    ));
                }
            }
        }
        return lista;
    }
}
