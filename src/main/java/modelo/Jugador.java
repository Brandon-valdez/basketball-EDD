package modelo;

import java.time.LocalDate;

public class Jugador {
    private int       idJugador;
    private String    nombre;
    private String    apellido;
    private LocalDate fechaNac;
    private String    posicion;
    private int       numeroCamiseta;
    private int       idEquipo;
    private String    imagen;
    private int       estado;
    private String    nombreEquipo; // extra para vistas

    public Jugador() {}

    public Jugador(int idJugador, String nombre, String apellido,
                   LocalDate fechaNac, String posicion, int numeroCamiseta, int idEquipo) {
        this.idJugador      = idJugador;
        this.nombre         = nombre;
        this.apellido       = apellido;
        this.fechaNac       = fechaNac;
        this.posicion       = posicion;
        this.numeroCamiseta = numeroCamiseta;
        this.idEquipo       = idEquipo;
    }

    public Jugador(int idJugador, String nombre, String apellido,
                   LocalDate fechaNac, String posicion, int numeroCamiseta,
                   int idEquipo, String imagen, int estado) {
        this.idJugador      = idJugador;
        this.nombre         = nombre;
        this.apellido       = apellido;
        this.fechaNac       = fechaNac;
        this.posicion       = posicion;
        this.numeroCamiseta = numeroCamiseta;
        this.idEquipo       = idEquipo;
        this.imagen         = imagen;
        this.estado         = estado;
    }

    public int       getIdJugador()      { return idJugador; }
    public String    getNombre()         { return nombre; }
    public String    getApellido()       { return apellido; }
    public LocalDate getFechaNac()       { return fechaNac; }
    public String    getPosicion()       { return posicion; }
    public int       getNumeroCamiseta() { return numeroCamiseta; }
    public int       getIdEquipo()       { return idEquipo; }
    public String    getImagen()         { return imagen; }
    public int       getEstado()         { return estado; }
    public String    getNombreEquipo()   { return nombreEquipo; }
    public String    getNombreCompleto() { return nombre + " " + apellido; }

    public void setIdJugador(int id)              { this.idJugador      = id; }
    public void setNombre(String nombre)          { this.nombre         = nombre; }
    public void setApellido(String apellido)      { this.apellido       = apellido; }
    public void setFechaNac(LocalDate fecha)      { this.fechaNac       = fecha; }
    public void setPosicion(String posicion)      { this.posicion       = posicion; }
    public void setNumeroCamiseta(int num)        { this.numeroCamiseta = num; }
    public void setIdEquipo(int idEquipo)         { this.idEquipo       = idEquipo; }
    public void setImagen(String imagen)          { this.imagen         = imagen; }
    public void setEstado(int estado)             { this.estado         = estado; }
    public void setNombreEquipo(String nombre)    { this.nombreEquipo   = nombre; }

    @Override
    public String toString() { return "#" + numeroCamiseta + " " + getNombreCompleto() + " - " + posicion; }
}