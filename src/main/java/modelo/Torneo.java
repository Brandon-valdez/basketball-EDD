package modelo;

import java.time.LocalDate;
import java.time.LocalTime;

public class Torneo {
    private int       idTorneo;
    private String    nombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String    ubicacion;
    private String    estado;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    public Torneo() {}

    public int       getIdTorneo()    { return idTorneo; }
    public String    getNombre()      { return nombre; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFin()    { return fechaFin; }
    public String    getUbicacion()   { return ubicacion; }
    public String    getEstado()      { return estado; }
    public LocalTime getHoraInicio()  { return horaInicio; }
    public LocalTime getHoraFin()     { return horaFin; }

    public void setIdTorneo(int id)             { this.idTorneo    = id; }
    public void setNombre(String nombre)        { this.nombre      = nombre; }
    public void setFechaInicio(LocalDate fecha) { this.fechaInicio = fecha; }
    public void setFechaFin(LocalDate fecha)    { this.fechaFin    = fecha; }
    public void setUbicacion(String ubicacion)  { this.ubicacion   = ubicacion; }
    public void setEstado(String estado)        { this.estado      = estado; }
    public void setHoraInicio(LocalTime hora)   { this.horaInicio  = hora; }
    public void setHoraFin(LocalTime hora)      { this.horaFin     = hora; }

    @Override
    public String toString() { return nombre + " [" + estado + "]"; }
}