package modelo;

import controlador.MeteorologiaException;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;


public abstract class Sensor implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter FORMATO_DUPLICADA = DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm");

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
    public abstract boolean esValorAdmisible(float valor);

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
        return mediciones.stream().sorted(Comparator.comparing(Medicion::getFechaHora)).toArray(Medicion[]::new);
    }

    public void addMedicion(LocalDateTime fechaHora, float valor) throws MeteorologiaException {
        if (estado== Estado.INACTIVO){
            throw new MeteorologiaException("El sensor " + codigo + " no está activo.");
        }
        if (!Float.isFinite(valor) || !esValorAdmisible(valor)){
            throw new MeteorologiaException("El valor " + String.format(java.util.Locale.US, "%.1f", valor) + " " + getUnidad() + " no es admisible para un sensor de tipo " + getTipo() + ".");
        }
        if (mediciones.stream().anyMatch(m -> m.getFechaHora().isEqual(fechaHora))) {
            throw new MeteorologiaException("El sensor " + codigo + " ya registra una medición el " + fechaHora.format(FORMATO_DUPLICADA) + ".");
        }
        mediciones.add(new Medicion(fechaHora, valor));
    }

    public Medicion[] getMedicionesBetween(LocalDateTime inicio, LocalDateTime fin) {
        return mediciones.stream().filter(m -> !m.getFechaHora().isBefore(inicio) && !m.getFechaHora().isAfter(fin)).sorted(Comparator.comparing(Medicion::getFechaHora)).toArray(Medicion[]::new);
    }

    public Optional<ResumenMediciones> getResumenBetween(LocalDateTime inicio, LocalDateTime fin) {
        return Optional.of(getMedicionesBetween(inicio, fin)).filter(r -> r.length > 0).map(ResumenMediciones::new);
    }

}
