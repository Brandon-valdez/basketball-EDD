package dao;

import db.Conexion;
import modelo.Resultado;

import java.sql.*;

public class ResultadoDAO {

    private Connection getConn() throws SQLException {
        return Conexion.getInstancia().getConexion();
    }

    public Resultado buscarPorPartido(int idPartido) throws SQLException {
        String sql = "SELECT * FROM RESULTADOS WHERE id_partido=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idPartido);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Resultado(
                        rs.getInt("id_resultado"),
                        rs.getInt("id_partido"),
                        rs.getInt("puntos_local"),
                        rs.getInt("puntos_visit"),
                        rs.getString("estado")
                    );
                }
            }
        }
        return null;
    }

    /** Actualiza el resultado y lo marca como finalizado */
    public boolean finalizarPartido(int idPartido, int puntosLocal, int puntosVisit) throws SQLException {
        String sql = "UPDATE RESULTADOS SET puntos_local=?, puntos_visit=?, estado='finalizado' WHERE id_partido=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, puntosLocal);
            ps.setInt(2, puntosVisit);
            ps.setInt(3, idPartido);
            return ps.executeUpdate() > 0;
        }
    }
}