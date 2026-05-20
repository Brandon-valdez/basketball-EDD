package modelo;

import java.time.LocalDateTime;

public class Partido {
    private int           idPartido;
    private LocalDateTime fecha;
    private String        lugar;
    private int           idEquipoLocal;
    private int           idEquipoVisit;
    private int           idTorneo;
    private int           idArbitro;

    // Campos extra para mostrar en vistas/tablas
    private String nombreEquipoLocal;
    private String nombreEquipoVisit;
    private String nombreTorneo;
    private String nombreArbitro;
    private String estadoResultado; // del RESULTADO asociado
    private int    puntosLocal;
    private int    puntosVisit;

    public Partido() {}

    public Partido(int idPartido, LocalDateTime fecha, String lugar,
                   int idEquipoLocal, int idEquipoVisit, int idTorneo, int idArbitro) {
        this.idPartido     = idPartido;
        this.fecha         = fecha;
        this.lugar         = lugar;
        this.idEquipoLocal = idEquipoLocal;
        this.idEquipoVisit = idEquipoVisit;
        this.idTorneo      = idTorneo;
        this.idArbitro     = idArbitro;
    }

    public int           getIdPartido()          { return idPartido; }
    public LocalDateTime getFecha()              { return fecha; }
    public String        getLugar()              { return lugar; }
    public int           getIdEquipoLocal()      { return idEquipoLocal; }
    public int           getIdEquipoVisit()      { return idEquipoVisit; }
    public int           getIdTorneo()           { return idTorneo; }
    public int           getIdArbitro()          { return idArbitro; }
    public String        getNombreEquipoLocal()  { return nombreEquipoLocal; }
    public String        getNombreEquipoVisit()  { return nombreEquipoVisit; }
    public String        getNombreTorneo()       { return nombreTorneo; }
    public String        getNombreArbitro()      { return nombreArbitro; }
    public String        getEstadoResultado()    { return estadoResultado; }
    public int           getPuntosLocal()        { return puntosLocal; }
    public int           getPuntosVisit()        { return puntosVisit; }

    public void setIdPartido(int id)                    { this.idPartido          = id; }
    public void setFecha(LocalDateTime fecha)           { this.fecha              = fecha; }
    public void setLugar(String lugar)                  { this.lugar              = lugar; }
    public void setIdEquipoLocal(int id)                { this.idEquipoLocal      = id; }
    public void setIdEquipoVisit(int id)                { this.idEquipoVisit      = id; }
    public void setIdTorneo(int id)                     { this.idTorneo           = id; }
    public void setIdArbitro(int id)                    { this.idArbitro          = id; }
    public void setNombreEquipoLocal(String nombre)     { this.nombreEquipoLocal  = nombre; }
    public void setNombreEquipoVisit(String nombre)     { this.nombreEquipoVisit  = nombre; }
    public void setNombreTorneo(String nombre)          { this.nombreTorneo       = nombre; }
    public void setNombreArbitro(String nombre)         { this.nombreArbitro      = nombre; }
    public void setEstadoResultado(String estado)       { this.estadoResultado    = estado; }
    public void setPuntosLocal(int pts)                 { this.puntosLocal        = pts; }
    public void setPuntosVisit(int pts)                 { this.puntosVisit        = pts; }

    @Override
    public String toString() {
        return nombreEquipoLocal + " vs " + nombreEquipoVisit + " — " + fecha.toLocalDate();
    }
}