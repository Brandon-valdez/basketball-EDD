package servicio;

import dao.UsuarioDAO;
import modelo.Usuario;
import util.Sesion;

import java.sql.SQLException;

/**
 * Servicio de autenticación.
 * Centraliza la lógica de login/logout para que las vistas solo llamen un método.
 */
public class AuthService {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    /**
     * Intenta autenticar al usuario.
     *
     * @return null si las credenciales son incorrectas,
     *         el Usuario logueado si son correctas (y lo guarda en Sesion)
     */
    public Usuario login(String email, String password) throws SQLException {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return null;
        }
        Usuario u = usuarioDAO.login(email.trim(), password);
        if (u != null) {
            Sesion.getInstancia().setUsuario(u);
        }
        return u;
    }

    public void logout() {
        Sesion.getInstancia().cerrar();
    }
}