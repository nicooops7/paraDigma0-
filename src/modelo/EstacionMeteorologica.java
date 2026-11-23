package modelo;

import controlador.MeteorologiaException;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class EstacionMeteorologica {
    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private String codigo;
    private String nombre;
    private float longitud;
    private float latitud;
    private float altitud;
    private Estado estado;
    private Comuna comuna;

    private List<Sensor> sensores;
    private List<AlertaMeteorologica> alertas;

    public EstacionMeteorologica(String cod, String nombre, float lon,
                                 float lat, float alt, Comuna comuna) {
        this.codigo = cod;
        this.nombre = nombre;
        this.longitud = lon;
        this.latitud = lat;
        this.altitud = alt;
        this.estado = Estado.ACTIVO;
        this.comuna = comuna;
        this.sensores = new ArrayList<>();
        this.alertas = new ArrayList<>();
    }
    public void instalaSensor(String codigo, String marca, String modelo, TipoSensor tipo) throws MeteorologiaException{
        if (estado == Estado.INACTIVO) {
            throw new MeteorologiaException("La estación " + this.codigo + " no está activa.");
        }
        Optional<Sensor> conflicto = sensores.stream()
                .filter(s -> s.getCodigo().equals(codigo)
                        || (s.getEstado() == Estado.ACTIVO && s.getTipo() == tipo))
                .findFirst();
        if (conflicto.isPresent()) {
            Sensor s = conflicto.get();
            if (s.getCodigo().equals(codigo)) {
                throw new MeteorologiaException("La estación " + this.codigo + " ya tiene un sensor con el código " + codigo + ".");
            }
            throw new MeteorologiaException("La estación " + this.codigo + " ya tiene un sensor ACTIVO de tipo " + tipo + " (" + s.getCodigo() + ").");
        }

        Sensor nuevo;
        switch (tipo) {
            case HUMEDAD -> nuevo = new SensorHumedad(codigo, marca, modelo, this);
            case TEMPERATURA -> nuevo = new SensorTemperatura(codigo, marca, modelo, this);
            case PRESION -> nuevo = new SensorPresion(codigo, marca, modelo, this);
            case VIENTO -> nuevo = new SensorViento(codigo, marca, modelo, this);
            case PRECIPITACION -> nuevo = new SensorPrecipitacion(codigo, marca, modelo, this);
            default -> throw new IllegalArgumentException("Tipo no válido");
        }
        sensores.add(nuevo);
    }

    public void cambiaEstadoSensor(String codigoSensor, Estado nuevoEstado) throws MeteorologiaException {
        Sensor sensor = findSensorById(codigoSensor).orElseThrow(() ->
                new MeteorologiaException("No existe el sensor " + codigoSensor + " en la estación " + codigo + "."));
        if (sensor.getEstado() == nuevoEstado) {
            throw new MeteorologiaException("El sensor " + codigoSensor + " ya se encuentra en estado " + nuevoEstado + ".");
        }
        if (nuevoEstado == Estado.ACTIVO) {
            Optional<Sensor> otro = sensores.stream().filter(o -> o != sensor && o.getEstado() == Estado.ACTIVO && o.getTipo() == sensor.getTipo()).findFirst();
            if (otro.isPresent()) {
                throw new MeteorologiaException("No es posible activar el sensor " + codigoSensor + ": la estación ya tiene activo el sensor " + otro.get().getCodigo() + " de tipo " + sensor.getTipo() + ".");
            }
        }
        sensor.setEstado(nuevoEstado);
    }

    public Sensor[] getSensores() {
        return sensores.toArray(new Sensor[0]);
    }

    public void registraMedicion(LocalDateTime fechaHora, float valor, String codigoSensor) throws MeteorologiaException {
        if (this.estado == Estado.INACTIVO) {
            throw new MeteorologiaException("La estación "+codigo+" no esta activa");
        }
        Sensor sensor = findSensorById(codigoSensor).orElseThrow(() -> new MeteorologiaException("no existe el sensor "+codigoSensor+" en la estación "+codigo));
        sensor.addMedicion(fechaHora, valor);

        if (sensor instanceof Alertable) {
            Alertable alertable = (Alertable) sensor;
            Optional<NivelAlerta> nivelOpt = alertable.evaluaAlerta(valor);

            if (nivelOpt.isPresent()) {
                String fenomeno = alertable.getFenomeno(valor);
                AlertaMeteorologica alerta = new AlertaMeteorologica(fechaHora, valor, nivelOpt.get(), fenomeno, sensor);
                this.alertas.add(alerta);
            }
        }
    }
    public AlertaMeteorologica[] getAlertasBetween(LocalDateTime inicio, LocalDateTime fin) {
        return alertas.stream().filter(a -> !a.getFechaHora().isBefore(inicio) && !a.getFechaHora().isAfter(fin)).sorted(Comparator.comparing(AlertaMeteorologica::getFechaHora)).toArray(AlertaMeteorologica[]::new);
    }

    @Override
    public String toString(){
        return codigo+";"+nombre+";"+longitud+";"+latitud+";"+altitud+";"+estado+";"+sensores;
    }

    public String[][] getResumenSensores() {
        return sensores.stream().map(s -> new String[] {s.getCodigo(), s.getTipo().toString(), s.getMarca(), s.getModelo(), s.getUnidad(), s.getEstado().toString(), descripcionUltimaMedicion(s)}).toArray(String[][]::new);
    }

    private String descripcionUltimaMedicion(Sensor sensor) {
        Medicion[] mediciones = sensor.getMediciones();
        if (mediciones.length == 0) {
            return "Sin mediciones";
        }
        // La última medición es la más reciente
        Medicion ultima = mediciones[mediciones.length - 1];
        return ultima.getFechaHora().format(FORMATO_FECHA_HORA) + " " + ultima.getValor() + " " + sensor.getUnidad();
    }

    public String[][] getMedicionesSensorBetween(String codigoSensor, LocalDateTime inicio, LocalDateTime fin){
        return findSensorById(codigoSensor)
                .map(s -> Arrays.stream(s.getMedicionesBetween(inicio, fin)).map(m -> new String[] {m.getFechaHora().format(FORMATO_FECHA), m.getFechaHora().format(FORMATO_HORA), String.valueOf(m.getValor()), s.getUnidad()}).toArray(String[][]::new)).orElse(new String[0][0]);
    }

    public Optional<Sensor> findSensorById(String codigo) {
        return sensores.stream().filter(s -> s.getCodigo().equalsIgnoreCase(codigo)).findFirst();
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPosicion() {
        return "(" + longitud + "; " + latitud + "; " + altitud + " m)";
    }

    public Estado getEstado() {
        return estado;
    }

    public Comuna getComuna() {
        return comuna;
    }

}

