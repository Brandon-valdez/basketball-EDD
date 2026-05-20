package modelo;

public class Resultado {
    private int    idResultado;
    private int    idPartido;
    private int    puntosLocal;
    private int    puntosVisit;
    private String estado;

    public Resultado() {}

    public Resultado(int idResultado, int idPartido, int puntosLocal, int puntosVisit, String estado) {
        this.idResultado = idResultado;
        this.idPartido   = idPartido;
        this.puntosLocal = puntosLocal;
        this.puntosVisit = puntosVisit;
        this.estado      = estado;
    }

    public int    getIdResultado() { return idResultado; }
    public int    getIdPartido()   { return idPartido; }
    public int    getPuntosLocal() { return puntosLocal; }
    public int    getPuntosVisit() { return puntosVisit; }
    public String getEstado()      { return estado; }

    public void setIdResultado(int id)         { this.idResultado = id; }
    public void setIdPartido(int id)           { this.idPartido   = id; }
    public void setPuntosLocal(int pts)        { this.puntosLocal = pts; }
    public void setPuntosVisit(int pts)        { this.puntosVisit = pts; }
    public void setEstado(String estado)       { this.estado      = estado; }

    @Override
    public String toString() { return puntosLocal + " - " + puntosVisit + " [" + estado + "]"; }
}