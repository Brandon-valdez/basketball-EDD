package modelo;

import java.time.LocalDate;

public class Torneo {
    private int       idTorneo;
    private String    nombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String    ubicacion;
    private String    estado;

    public Torneo() {}

    public Torneo(int idTorneo, String nombre, LocalDate fechaInicio,
                  LocalDate fechaFin, String ubicacion, String estado) {
        this.idTorneo   = idTorneo;
        this.nombre     = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFin   = fechaFin;
        this.ubicacion  = ubicacion;
        this.estado     = estado;
    }

    public int       getIdTorneo()    { return idTorneo; }
    public String    getNombre()      { return nombre; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFin()    { return fechaFin; }
    public String    getUbicacion()   { return ubicacion; }
    public String    getEstado()      { return estado; }

    public void setIdTorneo(int id)              { this.idTorneo    = id; }
    public void setNombre(String nombre)         { this.nombre      = nombre; }
    public void setFechaInicio(LocalDate fecha)  { this.fechaInicio = fecha; }
    public void setFechaFin(LocalDate fecha)     { this.fechaFin    = fecha; }
    public void setUbicacion(String ubicacion)   { this.ubicacion   = ubicacion; }
    public void setEstado(String estado)         { this.estado      = estado; }

    @Override
    public String toString() { return nombre + " [" + estado + "]"; }
}