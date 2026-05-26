package modelo;

public class Alineacion {
    public enum TipoAlineacion {
        OFENSIVO("Ofensivo"),
        DEFENSIVO("Defensivo");

        private final String valorDb;

        TipoAlineacion(String valorDb) {
            this.valorDb = valorDb;
        }

        public String getValorDb() {
            return valorDb;
        }

        public static TipoAlineacion fromDb(String valor) {
            if (valor == null) {
                return null;
            }
            String normalizado = valor.trim().toLowerCase();
            if ("ofensivo".equals(normalizado)) {
                return OFENSIVO;
            }
            if ("defensivo".equals(normalizado)) {
                return DEFENSIVO;
            }
            return null;
        }
    }

    private int idAlineacion;
    private int idPartido;
    private int idEquipo;
    private int baseId;
    private int escoltaId;
    private int aleroId;
    private int alaPivotId;
    private int pivotId;
    private TipoAlineacion tipo;

    public Alineacion() {
    }

    public Alineacion(int idAlineacion, int idPartido, int idEquipo, int baseId, int escoltaId,
            int aleroId, int alaPivotId, int pivotId) {
        this(idAlineacion, idPartido, idEquipo, baseId, escoltaId, aleroId, alaPivotId, pivotId, null);
    }

    public Alineacion(int idAlineacion, int idPartido, int idEquipo, int baseId, int escoltaId,
            int aleroId, int alaPivotId, int pivotId, TipoAlineacion tipo) {
        this.idAlineacion = idAlineacion;
        this.idPartido = idPartido;
        this.idEquipo = idEquipo;
        this.baseId = baseId;
        this.escoltaId = escoltaId;
        this.aleroId = aleroId;
        this.alaPivotId = alaPivotId;
        this.pivotId = pivotId;
        this.tipo = tipo;
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

    public TipoAlineacion getTipo() {
        return tipo;
    }

    public String getTipoDb() {
        return tipo == null ? null : tipo.getValorDb();
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

    public void setTipo(TipoAlineacion tipo) {
        this.tipo = tipo;
    }

    public void setTipoDb(String tipo) {
        this.tipo = TipoAlineacion.fromDb(tipo);
    }
}