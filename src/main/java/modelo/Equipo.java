package modelo;

public class Equipo {
    private int    idEquipo;
    private String nombre;
    private String ciudad;
    private int    idUsuario;
    private String nombreDirector; // extra para vistas

    public Equipo() {}

    public Equipo(int idEquipo, String nombre, String ciudad, int idUsuario) {
        this.idEquipo  = idEquipo;
        this.nombre    = nombre;
        this.ciudad    = ciudad;
        this.idUsuario = idUsuario;
    }

    public int    getIdEquipo()        { return idEquipo; }
    public String getNombre()          { return nombre; }
    public String getCiudad()          { return ciudad; }
    public int    getIdUsuario()       { return idUsuario; }
    public String getNombreDirector()  { return nombreDirector; }

    public void setIdEquipo(int id)               { this.idEquipo        = id; }
    public void setNombre(String nombre)          { this.nombre          = nombre; }
    public void setCiudad(String ciudad)          { this.ciudad          = ciudad; }
    public void setIdUsuario(int idUsuario)       { this.idUsuario       = idUsuario; }
    public void setNombreDirector(String nombre)  { this.nombreDirector  = nombre; }

    @Override
    public String toString() { return nombre; }
}