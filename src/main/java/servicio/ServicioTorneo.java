package servicio;

import dao.TorneoDAO;
import modelo.Torneo;
import java.sql.SQLException;
import java.util.List;

/**
 * Capa de servicio para Torneos.
 * Centraliza reglas de negocio y delega persistencia al DAO.
 */
public class ServicioTorneo {

    private final TorneoDAO torneoDAO = new TorneoDAO();

    public void registrar(Torneo t) throws SQLException, IllegalArgumentException {
        validar(t);
        torneoDAO.insertar(t);
    }

    public void actualizar(Torneo t) throws SQLException, IllegalArgumentException {
        validar(t);
        torneoDAO.actualizar(t);
    }

    public void eliminar(int idTorneo) throws SQLException {
        torneoDAO.eliminar(idTorneo);
    }

    public List<Torneo> listar() throws SQLException {
        return torneoDAO.listar();
    }

    public List<Torneo> listarActivos() throws SQLException {
        return torneoDAO.listar().stream()
            .filter(t -> "activo".equalsIgnoreCase(t.getEstado()))
            .collect(java.util.stream.Collectors.toList());
    }

    // ── Reglas de negocio ─────────────────────────────
    private void validar(Torneo t) throws IllegalArgumentException {
        if (t.getNombre() == null || t.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre del torneo es obligatorio.");
        if (t.getNombre().trim().length() < 3)
            throw new IllegalArgumentException("El nombre debe tener al menos 3 caracteres.");
        if (t.getFechaInicio() == null)
            throw new IllegalArgumentException("La fecha de inicio es obligatoria.");
        if (t.getHoraInicio() != null && t.getHoraFin() != null
                && !t.getHoraFin().isAfter(t.getHoraInicio()))
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la de inicio.");
    }
}