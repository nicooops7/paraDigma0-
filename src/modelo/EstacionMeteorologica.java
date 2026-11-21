package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EstacionMeteorologica {
    private String codigo;
    private String nombre;
    private float longitud;
    private float latitud;
    private float altitud;
    private Estado estado;
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
        this.sensores=new ArrayList<>();
        this.alertas= new ArrayList<>();

    }
    public boolean instalaSensor(String codigo, String marca, String modelo,
                                 TipoSensor tipo){
        for (Sensor s : sensores) {
            if(s.getCodigo().equals(codigo)){
                return false;
            }
            if(s.getEstado()==Estado.ACTIVO && mismoTipo(s, tipo)){
                return false;
            }
        }
        Sensor nuevoSensor= null;
        switch (tipo) {
            case HUMEDAD:
                nuevoSensor=new SensorHumedad(codigo, marca, modelo, this);
                break;
            case TEMPERATURA:
                nuevoSensor=new SensorTemperatura(codigo, marca, modelo, this);
                break;
            case PRESION:
                nuevoSensor=new SensorPresion(codigo, marca, modelo, this);
                break;
            case VIENTO:
                nuevoSensor=new SensorViento(codigo, marca, modelo, this);
                break;
            case PRECIPITACION:
                nuevoSensor=new SensorPrecipitacion(codigo, marca, modelo, this);
                break;
        }
        if(nuevoSensor!=null){
            sensores.add(nuevoSensor);
            return true;
        }
        return false;
    }

    //Metodo auxiliar
    private boolean mismoTipo(Sensor s, TipoSensor tipo){
        return (tipo == TipoSensor.HUMEDAD && s instanceof SensorHumedad) ||
                (tipo == TipoSensor.TEMPERATURA && s instanceof SensorTemperatura) ||
                (tipo == TipoSensor.PRESION && s instanceof SensorPresion) ||
                (tipo == TipoSensor.VIENTO && s instanceof SensorViento) ||
                (tipo == TipoSensor.PRECIPITACION && s instanceof SensorPrecipitacion);
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
        return filtradas.toArray(new AlertaMeteorologica[0]);
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
        DateTimeFormatter formatoFecha= DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (int i = 0; i < sensores.size(); i++) {
            Sensor s= sensores.get(i);

            matriz[i][0]= s.getCodigo();
            matriz[i][1]= s.getClass().getSimpleName();
            matriz[i][2]= s.getMarca();
            matriz[i][3] = s.getModelo();
            matriz[i][4] = s.getUnidad();
            matriz[i][5] = s.getEstado().toString();

            Medicion ultima= s.getLastMedicion();
            if (ultima!= null) {
                String fechaStr= ultima.getFechaHora().format(formatoFecha);
                matriz[i][6]= fechaStr+" "+ultima.getValor()+" "+s.getUnidad();
            }else{
                matriz[i][6]= "Sin mediciones";
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
}
