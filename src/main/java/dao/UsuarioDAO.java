package dao;

import db.Conexion;
import modelo.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    private Connection getConn() throws SQLException {
        return Conexion.getInstancia().getConexion();
    }

    /**
     * Autenticación: busca usuario por email y contraseña (MD5).
     * Retorna el Usuario con su rol, o null si no coincide.
     */
    public Usuario login(String email, String password) throws SQLException {
        String sql = """
                SELECT u.id_usuario, u.nombre, u.email, u.id_rol, r.nombre_rol
                FROM USUARIOS u
                JOIN ROL r ON r.id_rol = u.id_rol
                WHERE u.email = ? AND u.contrasena = SHA2(?, 256)
                """;
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setIdUsuario(rs.getInt("id_usuario"));
                    u.setNombre(rs.getString("nombre"));
                    u.setEmail(rs.getString("email"));
                    u.setIdRol(rs.getInt("id_rol"));
                    u.setNombreRol(rs.getString("nombre_rol"));
                    return u;
                }
            }
        }
        return null;
    }

    /** Lista todos los usuarios con su rol */
    public List<Usuario> listar() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = """
                SELECT u.id_usuario, u.nombre, u.email, u.id_rol, r.nombre_rol
                FROM USUARIOS u JOIN ROL r ON r.id_rol = u.id_rol
                ORDER BY u.nombre
                """;
        try (Statement st = getConn().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setNombre(rs.getString("nombre"));
                u.setEmail(rs.getString("email"));
                u.setIdRol(rs.getInt("id_rol"));
                u.setNombreRol(rs.getString("nombre_rol"));
                lista.add(u);
            }
        }
        return lista;
    }

    /** Inserta un nuevo usuario (contraseña guardada como MD5) */
    public boolean insertar(Usuario u) throws SQLException {
        String sql = "INSERT INTO USUARIOS (nombre, email, contrasena, id_rol) VALUES (?, ?, SHA2(?, 256), ?)";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getContrasena());
            ps.setInt(4, u.getIdRol());
            return ps.executeUpdate() > 0;
        }
    }

    /** Actualiza nombre, email y rol (no cambia contraseña) */
    public boolean actualizar(Usuario u) throws SQLException {
        String sql = "UPDATE USUARIOS SET nombre=?, email=?, id_rol=? WHERE id_usuario=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getEmail());
            ps.setInt(3, u.getIdRol());
            ps.setInt(4, u.getIdUsuario());
            return ps.executeUpdate() > 0;
        }
    }

    /** Cambia la contraseña de un usuario */
    public boolean cambiarContrasena(int idUsuario, String nuevaContrasena) throws SQLException {
        String sql = "UPDATE USUARIOS SET contrasena=SHA2(?, 256) WHERE id_usuario=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, nuevaContrasena);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        }
    }

    /** Elimina un usuario por ID */
    public boolean eliminar(int idUsuario) throws SQLException {
        String sql = "DELETE FROM USUARIOS WHERE id_usuario=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;
        }
    }

    /** Verifica si un email ya existe (para validación antes de insertar) */
    public boolean existeEmail(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM USUARIOS WHERE email=?";
        try (PreparedStatement ps = getConn().prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
}