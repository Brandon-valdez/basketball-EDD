package modelo;

public class Estadistica {
    private int    idEstadistica;
    private int    idJugador;
    private int    idPartido;
    private int    puntos;
    private int    rebotes;
    private int    asistencias;
    private int    faltas;
    private int    minutosJugados;

    // Extras para vistas
    private String nombreJugador;
    private String posicion;
    private String nombreEquipo;

    public Estadistica() {}

    public Estadistica(int idJugador, int idPartido, int puntos, int rebotes,
                       int asistencias, int faltas, int minutosJugados) {
        this.idJugador      = idJugador;
        this.idPartido      = idPartido;
        this.puntos         = puntos;
        this.rebotes        = rebotes;
        this.asistencias    = asistencias;
        this.faltas         = faltas;
        this.minutosJugados = minutosJugados;
    }

    public int    getIdEstadistica()  { return idEstadistica; }
    public int    getIdJugador()      { return idJugador; }
    public int    getIdPartido()      { return idPartido; }
    public int    getPuntos()         { return puntos; }
    public int    getRebotes()        { return rebotes; }
    public int    getAsistencias()    { return asistencias; }
    public int    getFaltas()         { return faltas; }
    public int    getMinutosJugados() { return minutosJugados; }
    public String getNombreJugador()  { return nombreJugador; }
    public String getPosicion()       { return posicion; }
    public String getNombreEquipo()   { return nombreEquipo; }

    public void setIdEstadistica(int id)         { this.idEstadistica  = id; }
    public void setIdJugador(int id)             { this.idJugador      = id; }
    public void setIdPartido(int id)             { this.idPartido      = id; }
    public void setPuntos(int puntos)            { this.puntos         = puntos; }
    public void setRebotes(int rebotes)          { this.rebotes        = rebotes; }
    public void setAsistencias(int asistencias)  { this.asistencias    = asistencias; }
    public void setFaltas(int faltas)            { this.faltas         = faltas; }
    public void setMinutosJugados(int min)       { this.minutosJugados = min; }
    public void setNombreJugador(String nombre)  { this.nombreJugador  = nombre; }
    public void setPosicion(String posicion)     { this.posicion       = posicion; }
    public void setNombreEquipo(String nombre)   { this.nombreEquipo   = nombre; }
}