package util;

public class Constantes {

    // IDs de roles (deben coincidir con la tabla ROL)
    public static final int ROL_ADMIN    = 1;
    public static final int ROL_DIRECTOR = 2;
    public static final int ROL_ARBITRO  = 3;

    // Estados de torneo
    public static final String TORNEO_ACTIVO     = "activo";
    public static final String TORNEO_FINALIZADO = "finalizado";
    public static final String TORNEO_PENDIENTE  = "pendiente";

    // Estados de resultado/partido
    public static final String PARTIDO_PENDIENTE  = "pendiente";
    public static final String PARTIDO_FINALIZADO = "finalizado";

    // Jugadores titulares por equipo en baloncesto
    public static final int TITULARES_POR_EQUIPO = 5;

    // Posiciones válidas
    public static final String[] POSICIONES = {
        "Base", "Escolta", "Alero", "Ala-Pívot", "Pívot"
    };

    private Constantes() {}
}