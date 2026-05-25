package modelo;

public class Suplente {
    private int idSuplente;
    private int idAlineacion;
    private int idJugador;
    private Integer ordenIngreso;

    public Suplente(int idSuplente, int idAlineacion, int idJugador, Integer ordenIngreso) {
        this.idSuplente = idSuplente;
        this.idAlineacion = idAlineacion;
        this.idJugador = idJugador;
        this.ordenIngreso = ordenIngreso;
    }

    public int getIdSuplente() {
        return idSuplente;
    }

    public int getIdAlineacion() {
        return idAlineacion;
    }

    public int getIdJugador() {
        return idJugador;
    }

    public Integer getOrdenIngreso() {
        return ordenIngreso;
    }

    public void setIdSuplente(int idSuplente) {
        this.idSuplente = idSuplente;
    }

    public void setIdAlineacion(int idAlineacion) {
        this.idAlineacion = idAlineacion;
    }

    public void setIdJugador(int idJugador) {
        this.idJugador = idJugador;
    }

    public void setOrdenIngreso(Integer ordenIngreso) {
        this.ordenIngreso = ordenIngreso;
    }
}
