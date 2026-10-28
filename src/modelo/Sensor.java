package modelo;

import controlador.MeteorologiaException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class Sensor {
    private String codigo;
    private String marca;
    private String modelo;
    private Estado estado;
    private EstacionMeteorologica estacion;
    private List<Medicion> mediciones;

    public Sensor(String codigo, String marca, String modelo, EstacionMeteorologica estacion) {
        this.codigo = codigo;
        this.marca = marca;
        this.modelo = modelo;
        this.estacion = estacion;
        this.estado = Estado.ACTIVO;
        this.mediciones= new ArrayList<>();
    }
    public abstract TipoSensor getTipo();
    public abstract String getUnidad();
    public abstract boolean esValorAdmisible(float valor); //metodo abstracto aunque en el uml aparece static, en la pauta aparece abstract

    public String getCodigo(){
        return codigo;
    }
    public String getMarca(){
        return marca;
    }
    public String getModelo(){
        return modelo;
    }
    public Estado getEstado(){
        return estado;
    }
    public void setEstado(Estado estado){
        this.estado = estado;
    }
    public Medicion[] getMediciones() {
        Medicion[] resultado = mediciones.toArray(new Medicion[0]);
        java.util.Arrays.sort(resultado,
                java.util.Comparator.comparing(Medicion::getFechaHora));
        return resultado;
    }

    public void addMedicion(LocalDateTime fechaHora, float valor) throws MeteorologiaException {
        if (estado== Estado.INACTIVO){
            throw new MeteorologiaException("El sensor " + codigo + " no está activo.");
        }
        if (!Float.isFinite(valor) || !esValorAdmisible(valor)){
            throw new MeteorologiaException("El valor " + String.format(java.util.Locale.US, "%.1f", valor) + " " + getUnidad()
                    + " no es admisible para un sensor de tipo " + getTipo() + ".");
        }
        for (Medicion m : mediciones){
            if (m.getFechaHora().isEqual(fechaHora)){
                throw new MeteorologiaException("El sensor " + codigo + " ya registra una medición el "
                        + fechaHora.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm"))
                        + ".");
            }
        }
        mediciones.add(new Medicion(fechaHora, valor));
    }
    public Medicion[] getMedicionesBetween(LocalDateTime inicio, LocalDateTime fin) {
        List<Medicion> filtradas = new ArrayList<>();
        for (Medicion m : mediciones) {
            if ((m.getFechaHora().isEqual(inicio) || m.getFechaHora().isAfter(inicio)) &&
                    (m.getFechaHora().isEqual(fin) || m.getFechaHora().isBefore(fin))) {
                filtradas.add(m);
            }
        }
        Medicion[] resultado = filtradas.toArray(new Medicion[0]);
        java.util.Arrays.sort(resultado,
                java.util.Comparator.comparing(Medicion::getFechaHora));
        return resultado;
    }

    public Optional<ResumenMediciones> getResumenBetween(LocalDateTime inicio, LocalDateTime fin) {
        Medicion[] R = getMedicionesBetween(inicio, fin);
        if (R.length == 0) {
            return Optional.empty();
        }
        return Optional.of(new ResumenMediciones(R));
    }
}
