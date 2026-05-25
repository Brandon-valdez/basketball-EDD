package modelo;

public class Alineacion {
    private int idAlineacion;
    private int idPartido;
    private int idEquipo;
    private int baseId;
    private int escoltaId;
    private int aleroId;
    private int alaPivotId;
    private int pivotId;

    public Alineacion(int idAlineacion, int idPartido, int idEquipo, int baseId, int escoltaId,
            int aleroId, int alaPivotId, int pivotId) {
        this.idAlineacion = idAlineacion;
        this.idPartido = idPartido;
        this.idEquipo = idEquipo;
        this.baseId = baseId;
        this.escoltaId = escoltaId;
        this.aleroId = aleroId;
        this.alaPivotId = alaPivotId;
        this.pivotId = pivotId;
    }

    public int getIdAlineacion() {
        return idAlineacion;
    }

    public int getIdPartido() {
        return idPartido;
    }

    public int getIdEquipo() {
        return idEquipo;
    }

    public int getBaseId() {
        return baseId;
    }

    public int getEscoltaId() {
        return escoltaId;
    }

    public int getAleroId() {
        return aleroId;
    }

    public int getAlaPivotId() {
        return alaPivotId;
    }

    public int getPivotId() {
        return pivotId;
    }

    public void setIdAlineacion(int idAlineacion) {
        this.idAlineacion = idAlineacion;
    }

    public void setIdPartido(int idPartido) {
        this.idPartido = idPartido;
    }

    public void setIdEquipo(int idEquipo) {
        this.idEquipo = idEquipo;
    }

    public void setBaseId(int baseId) {
        this.baseId = baseId;
    }

    public void setEscoltaId(int escoltaId) {
        this.escoltaId = escoltaId;
    }

    public void setAleroId(int aleroId) {
        this.aleroId = aleroId;
    }

    public void setAlaPivotId(int alaPivotId) {
        this.alaPivotId = alaPivotId;
    }

    public void setPivotId(int pivotId) {
        this.pivotId = pivotId;
    }
}