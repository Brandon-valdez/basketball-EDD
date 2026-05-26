package servicio;

import dao.ArbitroDAO;
import dao.EquipoDAO;
import dao.PartidoDAO;
import modelo.Arbitro;
import modelo.Equipo;
import modelo.Partido;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Servicio que genera automáticamente el fixture de un torneo.
 *
 * Algoritmo: Round-Robin (todos contra todos, una sola vuelta).
 *   - N equipos → N*(N-1)/2 partidos
 *   - Se generan jornadas: cada jornada tiene N/2 partidos simultáneos
 *   - Los árbitros se asignan rotativamente
 *   - Los partidos se espacian cada 7 días por jornada
 *
 * Ejemplo con 4 equipos (A=1, B=2, C=3, D=4):
 *   Jornada 1: A-B, C-D
 *   Jornada 2: A-C, B-D
 *   Jornada 3: A-D, B-C
 */
public class PartidoService {

    private final EquipoDAO  equipoDAO  = new EquipoDAO();
    private final ArbitroDAO arbitroDAO = new ArbitroDAO();
    private final PartidoDAO partidoDAO = new PartidoDAO();

    /**
     * Genera el fixture completo para un torneo.
     *
     * @param idTorneo    ID del torneo
     * @param fechaInicio Fecha/hora del primer partido
     * @param lugar       Lugar donde se juegan los partidos
     * @param diasEntreJornadas  Días entre cada jornada (ej: 7)
     * @return cantidad de partidos generados
     */
    public int generarFixture(int idTorneo, LocalDateTime fechaInicio,
                              String lugar, int diasEntreJornadas) throws SQLException {

        List<Equipo>  equipos  = equipoDAO.listarPorTorneo(idTorneo);
        List<Arbitro> arbitros = arbitroDAO.listar();

        if (equipos.size() < 2) {
            throw new IllegalStateException("Se necesitan al menos 2 equipos inscritos para generar el fixture.");
        }
        if (arbitros.isEmpty()) {
            throw new IllegalStateException("No hay árbitros registrados en el sistema.");
        }

        // Si hay número impar de equipos, agregamos un "bye" (null) para que el algoritmo funcione
        boolean byeAgregado = false;
        if (equipos.size() % 2 != 0) {
            equipos.add(null); // null = descanso
            byeAgregado = true;
        }

        int n = equipos.size();
        int totalJornadas = n - 1;
        int partidosPorJornada = n / 2;

        // Algoritmo de rotación clásico: fijamos el primer equipo y rotamos el resto
        List<Equipo> circulo = new ArrayList<>(equipos.subList(1, n)); // todos menos el [0]
        Equipo fijo = equipos.get(0);

        int arbitroIdx = 0;
        int totalGenerados = 0;

        for (int jornada = 0; jornada < totalJornadas; jornada++) {
            LocalDateTime fechaJornada = fechaInicio.plusDays((long) jornada * diasEntreJornadas);

            // Construimos los emparejamientos de esta jornada
            List<int[]> pares = new ArrayList<>();
            pares.add(new int[]{ idxEquipo(fijo, equipos), idxEquipo(circulo.get(0), equipos) });

            for (int i = 1; i < partidosPorJornada; i++) {
                int izq = i;
                int der = n - 1 - i;
                pares.add(new int[]{ idxEquipo(circulo.get(izq), equipos),
                                     idxEquipo(circulo.get(der), equipos) });
            }

            // Crear partidos para esta jornada (dos partidos por hora, cada 2h)
            int horaOffset = 0;
            for (int[] par : pares) {
                if (par[0] < 0 || par[1] < 0) { // skip si alguno es "bye"
                    horaOffset++;
                    continue;
                }
                Equipo local    = equipos.get(par[0]);
                Equipo visitante = equipos.get(par[1]);

                // Alternamos local/visitante en jornadas pares/impares
                if (jornada % 2 == 1) {
                    Equipo tmp = local; local = visitante; visitante = tmp;
                }

                Partido p = new Partido();
                p.setFecha(fechaJornada.plusHours(horaOffset * 2L));
                p.setLugar(lugar);
                p.setIdEquipoLocal(local.getIdEquipo());
                p.setIdEquipoVisit(visitante.getIdEquipo());
                p.setIdTorneo(idTorneo);
                p.setIdArbitro(arbitros.get(arbitroIdx % arbitros.size()).getIdArbitro());

                partidoDAO.insertar(p);
                arbitroIdx++;
                totalGenerados++;
                horaOffset++;
            }

            // Rotar el círculo (el primero va al final)
            circulo.add(0, circulo.remove(circulo.size() - 1));
        }

        return totalGenerados;
    }

    /** Devuelve el índice en la lista, -1 si el equipo es null (bye) */
    private int idxEquipo(Equipo e, List<Equipo> lista) {
        if (e == null) return -1;
        return lista.indexOf(e);
    }
}