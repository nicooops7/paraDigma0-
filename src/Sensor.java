import java.time.LocalDateTime;

public abstract class Sensor {
    private String codigo;
    private String marca;
    private String modelo;
    private Estado estado;
    private EstacionMeteorologica estacion;

    private Medicion[] mediciones;
    private int cantidadMediciones;

    protected Sensor(String codigo, String marca, String modelo, EstacionMeteorologica estacion) {
        this.codigo = codigo;
        this.marca = marca;
        this.modelo = modelo;
        this.estacion = estacion;
        this.estado = Estado.ACTIVO;
        this.mediciones = new Medicion[1000];
        this.cantidadMediciones = 0;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public boolean addMedicion(LocalDateTime fechaHora, float valor) {

        if (this.estado != Estado.ACTIVO) {
            return false;
        }


        if (!esValorAdmisible(valor)) {
            return false;
        }


        for (int i = 0; i < cantidadMediciones; i++) {
            if (mediciones[i] != null && mediciones[i].getFechaHora().equals(fechaHora)) {
                return false;
            }
        }


        if (cantidadMediciones >= mediciones.length) {
            return false;
        }


        mediciones[cantidadMediciones] = new Medicion(fechaHora, valor);
        cantidadMediciones++;
        return true;
    }

    public Medicion getLastMedicion() {
        if (cantidadMediciones == 0) {
            return null;
        }


        Medicion masReciente = mediciones[0];
        for (int i = 1; i < cantidadMediciones; i++) {
            if (mediciones[i] != null && mediciones[i].getFechaHora().isAfter(masReciente.getFechaHora())) {
                masReciente = mediciones[i];
            }
        }
        return masReciente;
    }

    public Medicion[] getMedicionesBetween(LocalDateTime inicio, LocalDateTime fin) {
        int contador = 0;


        for (int i = 0; i < cantidadMediciones; i++) {
            if (mediciones[i] != null) {
                LocalDateTime fh = mediciones[i].getFechaHora();

                if (!fh.isBefore(inicio) && !fh.isAfter(fin)) {
                    contador++;
                }
            }
        }


        Medicion[] resultado = new Medicion[contador];
        int indice = 0;

        for (int i = 0; i < cantidadMediciones; i++) {
            if (mediciones[i] != null) {
                LocalDateTime fh = mediciones[i].getFechaHora();
                if (!fh.isBefore(inicio) && !fh.isAfter(fin)) {
                    resultado[indice] = mediciones[i];
                    indice++;
                }
            }
        }
        return resultado;
    }


    public abstract String getUnidad();

    public abstract boolean esValorAdmisible(float valor);
}