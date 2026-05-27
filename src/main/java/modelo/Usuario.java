package modelo;

public class Usuario {
    private int    idUsuario;
    private String nombre;
    private String email;
    private String contrasena;
    private int    idRol;
    private String nombreRol;
    private int    idArbitro; // -1 si el usuario no es árbitro

    public Usuario() {}

    public Usuario(int idUsuario, String nombre, String email, String contrasena, int idRol) {
        this.idUsuario  = idUsuario;
        this.nombre     = nombre;
        this.email      = email;
        this.contrasena = contrasena;
        this.idRol      = idRol;
        this.idArbitro  = -1;
    }

    public int    getIdUsuario()  { return idUsuario; }
    public String getNombre()     { return nombre; }
    public String getEmail()      { return email; }
    public String getContrasena() { return contrasena; }
    public int    getIdRol()      { return idRol; }
    public String getNombreRol()  { return nombreRol; }
    public int    getIdArbitro()  { return idArbitro; }

    public void setIdUsuario(int idUsuario)       { this.idUsuario  = idUsuario; }
    public void setNombre(String nombre)          { this.nombre     = nombre; }
    public void setEmail(String email)            { this.email      = email; }
    public void setContrasena(String contrasena)  { this.contrasena = contrasena; }
    public void setIdRol(int idRol)               { this.idRol      = idRol; }
    public void setNombreRol(String nombreRol)    { this.nombreRol  = nombreRol; }
    public void setIdArbitro(int idArbitro)       { this.idArbitro  = idArbitro; }

    @Override
    public String toString() { return nombre + " (" + email + ")"; }
}