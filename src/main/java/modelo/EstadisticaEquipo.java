package modelo;

public class EstadisticaEquipo {
    private int    idEstadisticaEquipo;
    private int    idPartido;
    private int    idEquipo;
    private int    puntos;
    private int    faltas;
    private int    triplesAnotados;
    private int    tirosLibresAnotados;
    private int    rebotes;
    private int    asistencias;

    // Extras para vistas
    private String nombreEquipo;

    public EstadisticaEquipo() {}

    public EstadisticaEquipo(int idPartido, int idEquipo, int puntos, int faltas,
                            int triplesAnotados, int tirosLibresAnotados,
                            int rebotes, int asistencias) {
        this.idPartido              = idPartido;
        this.idEquipo               = idEquipo;
        this.puntos                 = puntos;
        this.faltas                 = faltas;
        this.triplesAnotados        = triplesAnotados;
        this.tirosLibresAnotados    = tirosLibresAnotados;
        this.rebotes                = rebotes;
        this.asistencias            = asistencias;
    }

    // Getters
    public int getIdEstadisticaEquipo()  { return idEstadisticaEquipo; }
    public int getIdPartido()            { return idPartido; }
    public int getIdEquipo()             { return idEquipo; }
    public int getPuntos()               { return puntos; }
    public int getFaltas()               { return faltas; }
    public int getTriplesAnotados()      { return triplesAnotados; }
    public int getTirosLibresAnotados()  { return tirosLibresAnotados; }
    public int getRebotes()              { return rebotes; }
    public int getAsistencias()          { return asistencias; }
    public String getNombreEquipo()      { return nombreEquipo; }

    // Setters
    public void setIdEstadisticaEquipo(int id)            { this.idEstadisticaEquipo = id; }
    public void setIdPartido(int id)                      { this.idPartido = id; }
    public void setIdEquipo(int id)                       { this.idEquipo = id; }
    public void setPuntos(int puntos)                     { this.puntos = puntos; }
    public void setFaltas(int faltas)                     { this.faltas = faltas; }
    public void setTriplesAnotados(int triples)           { this.triplesAnotados = triples; }
    public void setTirosLibresAnotados(int tiros)         { this.tirosLibresAnotados = tiros; }
    public void setRebotes(int rebotes)                   { this.rebotes = rebotes; }
    public void setAsistencias(int asistencias)           { this.asistencias = asistencias; }
    public void setNombreEquipo(String nombre)            { this.nombreEquipo = nombre; }
}
