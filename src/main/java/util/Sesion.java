package util;

import modelo.Usuario;

/**
 * Guarda el usuario que inició sesión.
 * Accesible desde cualquier parte de la app sin pasar parámetros.
 *
 * Uso:
 *   Sesion.getInstancia().setUsuario(u);   // al hacer login
 *   Sesion.getInstancia().getUsuario();    // en cualquier vista/dao
 *   Sesion.getInstancia().cerrar();        // al hacer logout
 */
public class Sesion {

    private static Sesion instancia;
    private Usuario usuarioActual;

    private Sesion() {}

    public static Sesion getInstancia() {
        if (instancia == null) {
            instancia = new Sesion();
        }
        return instancia;
    }

    public void setUsuario(Usuario u) {
        this.usuarioActual = u;
    }

    public Usuario getUsuario() {
        return usuarioActual;
    }

    public boolean estaLogueado() {
        return usuarioActual != null;
    }

    public boolean esAdmin() {
        return estaLogueado() && usuarioActual.getIdRol() == 1;
    }

    public boolean esDirector() {
        return estaLogueado() && usuarioActual.getIdRol() == 2;
    }

    public boolean esArbitro() {
        return estaLogueado() && usuarioActual.getIdRol() == 3;
    }

    public void cerrar() {
        this.usuarioActual = null;
    }
}