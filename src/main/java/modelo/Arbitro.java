package modelo;

public class Arbitro {
    private int    idArbitro;
    private String nombre;
    private String apellido;
    private String licencia;

    public Arbitro() {}

    public Arbitro(int idArbitro, String nombre, String apellido, String licencia) {
        this.idArbitro = idArbitro;
        this.nombre    = nombre;
        this.apellido  = apellido;
        this.licencia  = licencia;
    }

    public int    getIdArbitro() { return idArbitro; }
    public String getNombre()    { return nombre; }
    public String getApellido()  { return apellido; }
    public String getLicencia()  { return licencia; }
    public String getNombreCompleto() { return nombre + " " + apellido; }

    public void setIdArbitro(int id)           { this.idArbitro = id; }
    public void setNombre(String nombre)       { this.nombre    = nombre; }
    public void setApellido(String apellido)   { this.apellido  = apellido; }
    public void setLicencia(String licencia)   { this.licencia  = licencia; }

    @Override
    public String toString() { return getNombreCompleto() + " [" + licencia + "]"; }
}