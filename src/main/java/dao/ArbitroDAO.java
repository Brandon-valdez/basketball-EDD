package dao;

import db.Conexion;
import modelo.Arbitro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class arbitroDAO {

    private Connection getConn() throws SQLException {
        return Conexion.getInstancia().getConexion();
    }

    private Arbitro mapear(ResultSet rs) throws SQLException {
        return new Arbitro(
            rs.getInt("id_arbitro"),
            rs.getString("nombre"),
            rs.getString("apellido"),
            rs.getString("licencia")
        );
    }

    public List<Arbitro> listar() throws SQLException {
        List<Arbitro> lista = new ArrayList<>();
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM ARBITRO ORDER BY apellido")) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public Arbitro buscarPorId(int id) throws SQLException {
        try (PreparedStatement ps = getConn().prepareStatement("SELECT * FROM ARBITRO WHERE id_arbitro=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public boolean insertar(Arbitro a) throws SQLException {
        String sql = "INSERT INTO ARBITRO (nombre, apellido, licencia) VALUES (?,?,?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, a.getNombre());
            ps.setString(2, a.getApellido());
            ps.setString(3, a.getLicencia());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizar(Arbitro a) throws SQLException {
        String sql = "UPDATE ARBITRO SET nombre=?, apellido=?, licencia=? WHERE id_arbitro=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, a.getNombre());
            ps.setString(2, a.getApellido());
            ps.setString(3, a.getLicencia());
            ps.setInt(4, a.getIdArbitro());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int idArbitro) throws SQLException {
        try (PreparedStatement ps = getConn().prepareStatement("DELETE FROM ARBITRO WHERE id_arbitro=?")) {
            ps.setInt(1, idArbitro);
            return ps.executeUpdate() > 0;
        }
    }
}