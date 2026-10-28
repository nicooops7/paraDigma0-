package modelo;

import controlador.MeteorologiaException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EstacionMeteorologica {
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
        this.estado= Estado.ACTIVO;
        this.comuna = comuna;
        this.sensores=new ArrayList<>();
        this.alertas= new ArrayList<>();

    }
    public void instalaSensor(String codigo, String marca, String modelo,
                              TipoSensor tipo) throws MeteorologiaException {
        if (estado == Estado.INACTIVO) {
            throw new MeteorologiaException("La estación " + this.codigo + " no está activa.");
        }
        for (Sensor s : sensores) {
            if (s.getCodigo().equals(codigo)) {
                throw new MeteorologiaException("La estación " + this.codigo
                        + " ya tiene un sensor con el código " + codigo + ".");
            }
            if (s.getEstado() == Estado.ACTIVO && s.getTipo() == tipo) {
                throw new MeteorologiaException("La estación " + this.codigo
                        + " ya tiene un sensor ACTIVO de tipo " + tipo
                        + " (" + s.getCodigo() + ").");
            }
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

    public void cambiaEstadoSensor(String codigoSensor, Estado nuevoEstado)
            throws MeteorologiaException {
        Sensor sensor = findSensorById(codigoSensor).orElseThrow(() ->
                new MeteorologiaException("No existe el sensor " + codigoSensor
                        + " en la estación " + codigo + "."));
        if (sensor.getEstado() == nuevoEstado) {
            throw new MeteorologiaException("El sensor " + codigoSensor
                    + " ya se encuentra en estado " + nuevoEstado + ".");
        }
        if (nuevoEstado == Estado.ACTIVO) {
            for (Sensor otro : sensores) {
                if (otro != sensor && otro.getEstado() == Estado.ACTIVO
                        && otro.getTipo() == sensor.getTipo()) {
                    throw new MeteorologiaException("No es posible activar el sensor "
                            + codigoSensor + ": la estación ya tiene activo el sensor "
                            + otro.getCodigo() + " de tipo " + sensor.getTipo() + ".");
                }
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
        List<AlertaMeteorologica> filtradas = new ArrayList<>();
        for (AlertaMeteorologica a : alertas) {
            if ((a.getFechaHora().isEqual(inicio) || a.getFechaHora().isAfter(inicio)) &&
                    (a.getFechaHora().isEqual(fin) || a.getFechaHora().isBefore(fin))) {
                filtradas.add(a);
            }
        }
        AlertaMeteorologica[] resultado = filtradas.toArray(new AlertaMeteorologica[0]);
        java.util.Arrays.sort(resultado,
                java.util.Comparator.comparing(AlertaMeteorologica::getFechaHora));
        return resultado;
    }


    @Override
    public String toString(){
        return codigo+";"+nombre+";"+longitud+";"+latitud+";"+altitud+";"+estado+";"+sensores;
    }

    public String[][] getResumenSensores() {

        if (sensores.isEmpty()) {
            return new String[0][0];
        }

        String[][] matriz = new String[sensores.size()][7];

        DateTimeFormatter formatoFecha =
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (int i = 0; i < sensores.size(); i++) {

            Sensor s = sensores.get(i);

            matriz[i][0] = s.getCodigo();
            matriz[i][1] = s.getTipo().toString();
            matriz[i][2] = s.getMarca();
            matriz[i][3] = s.getModelo();
            matriz[i][4] = s.getUnidad();
            matriz[i][5] = s.getEstado().toString();

            Medicion[] mediciones = s.getMediciones();

            if (mediciones.length > 0) {

                // La última medición es la más reciente
                Medicion ultima = mediciones[mediciones.length - 1];

                String fechaStr =
                        ultima.getFechaHora().format(formatoFecha);

                matriz[i][6] =
                        fechaStr + " " +
                                ultima.getValor() + " " +
                                s.getUnidad();

            } else {

                matriz[i][6] = "Sin mediciones";
            }
        }

        return matriz;
    }

    public String[][] getMedicionesSensorBetween(String codigoSensor, LocalDateTime inicio, LocalDateTime fin){
        Sensor sensorEncontrado= null;
        for (Sensor s : sensores) {
            if (s.getCodigo().equalsIgnoreCase(codigoSensor)) {
                sensorEncontrado = s;
                break;
            }
        }
        if (sensorEncontrado == null) {
            return new String[0][0];
        }

        Medicion[] mediciones= sensorEncontrado.getMedicionesBetween(inicio, fin);
        if (mediciones== null||mediciones.length == 0) {
            return new String[0][0];
        }

        DateTimeFormatter formatoFecha= DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formatoHora= DateTimeFormatter.ofPattern("HH:mm");
        String[][] matriz= new String[mediciones.length][4];

        for (int i = 0; i < mediciones.length; i++) {
            Medicion m= mediciones[i];
            matriz[i][0]= m.getFechaHora().format(formatoFecha);
            matriz[i][1]= m.getFechaHora().format(formatoHora);
            matriz[i][2]= String.valueOf(m.getValor());
            matriz[i][3]= sensorEncontrado.getUnidad();
        }
        return matriz;
    }

    public Optional<Sensor> findSensorById(String codigo) {
        for (Sensor s : sensores) {
            if (s.getCodigo().equalsIgnoreCase(codigo)) {
                return Optional.of(s);
            }
        }
        return Optional.empty();
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
