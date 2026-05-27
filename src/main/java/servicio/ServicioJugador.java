package servicio;

import dao.JugadorDAO;
import modelo.Jugador;
import java.sql.SQLException;
import java.util.List;

/**
 * Capa de servicio para Jugadores.
 * Centraliza reglas de negocio y delega persistencia al DAO.
 */
public class ServicioJugador {

    private final JugadorDAO jugadorDAO = new JugadorDAO();

    public void registrar(Jugador j) throws SQLException, IllegalArgumentException {
        validar(j);
        jugadorDAO.insertar(j);
    }

    public void actualizar(Jugador j) throws SQLException, IllegalArgumentException {
        validar(j);
        jugadorDAO.actualizar(j);
    }

    public void eliminar(int idJugador) throws SQLException {
        jugadorDAO.eliminar(idJugador);
    }

    public List<Jugador> listarPorEquipo(int idEquipo) throws SQLException {
        return jugadorDAO.listarPorEquipo(idEquipo);
    }

    // ── Reglas de negocio ─────────────────────────────
    private void validar(Jugador j) throws IllegalArgumentException {
        if (j.getNombre() == null || j.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre del jugador es obligatorio.");
        if (j.getApellido() == null || j.getApellido().isBlank())
            throw new IllegalArgumentException("El apellido del jugador es obligatorio.");
        if (j.getNumeroCamiseta() < 0 || j.getNumeroCamiseta() > 99)
            throw new IllegalArgumentException("El dorsal debe estar entre 0 y 99.");
        if (j.getIdEquipo() <= 0)
            throw new IllegalArgumentException("El jugador debe pertenecer a un equipo.");
    }
}