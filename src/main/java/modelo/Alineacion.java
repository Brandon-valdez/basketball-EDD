package modelo;

public class Alineacion {
    private int     idAlineacion;
    private int     idPartido;
    private int     idEquipo;
    private int     idJugador;
    private boolean titular;

    // Extras para vistas
    private String nombreJugador;
    private String posicion;
    private int    numeroCamiseta;

    public Alineacion() {}

    public Alineacion(int idPartido, int idEquipo, int idJugador, boolean titular) {
        this.idPartido = idPartido;
        this.idEquipo  = idEquipo;
        this.idJugador = idJugador;
        this.titular   = titular;
    }

    public int     getIdAlineacion()   { return idAlineacion; }
    public int     getIdPartido()      { return idPartido; }
    public int     getIdEquipo()       { return idEquipo; }
    public int     getIdJugador()      { return idJugador; }
    public boolean isTitular()         { return titular; }
    public String  getNombreJugador()  { return nombreJugador; }
    public String  getPosicion()       { return posicion; }
    public int     getNumeroCamiseta() { return numeroCamiseta; }

    public void setIdAlineacion(int id)          { this.idAlineacion   = id; }
    public void setIdPartido(int id)             { this.idPartido      = id; }
    public void setIdEquipo(int id)              { this.idEquipo       = id; }
    public void setIdJugador(int id)             { this.idJugador      = id; }
    public void setTitular(boolean titular)      { this.titular        = titular; }
    public void setNombreJugador(String nombre)  { this.nombreJugador  = nombre; }
    public void setPosicion(String posicion)     { this.posicion       = posicion; }
    public void setNumeroCamiseta(int num)       { this.numeroCamiseta = num; }
}