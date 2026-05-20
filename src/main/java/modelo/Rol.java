package modelo;

public class Rol {
    private int    idRol;
    private String nombreRol;
    private String descripcion;

    public Rol() {}

    public Rol(int idRol, String nombreRol, String descripcion) {
        this.idRol       = idRol;
        this.nombreRol   = nombreRol;
        this.descripcion = descripcion;
    }

    public int    getIdRol()       { return idRol; }
    public String getNombreRol()   { return nombreRol; }
    public String getDescripcion() { return descripcion; }

    public void setIdRol(int idRol)             { this.idRol       = idRol; }
    public void setNombreRol(String nombreRol)  { this.nombreRol   = nombreRol; }
    public void setDescripcion(String desc)     { this.descripcion = desc; }

    @Override
    public String toString() { return nombreRol; }
}