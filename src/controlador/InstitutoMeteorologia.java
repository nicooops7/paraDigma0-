package controlador;

import java.time.LocalDateTime;
import java.util.ArrayList;


public class InstitutoMeteorologia {
    private ArrayList<Region> regiones;
    private ArrayList<EstacionMeteorologica> estaciones;

    public InstitutoMeteorologia(){
        regiones = new ArrayList<>();
        estaciones = new ArrayList<>();
    }

    public boolean creaRegion(int codigo , String nombre ){
        for (Region region : regiones){
            if (region.getCodigo() == codigo ||region.getNombre().equalsIgnoreCase(nombre)){
                return false;
            }
        }
        Region nueva = new Region(codigo, nombre);
        regiones.add(nueva);
        return true;
    }
    public boolean creaComuna(int codigo, String nombre , int codigoRegion){
        Region region = null;
        for(Region r : regiones){
            if(r.getCodigo()==codigoRegion){
                region = r;
                break;
            }
        }

        if(region == null){
            return false;
        }

        return region.addComuna(codigo, nombre);
    }

    public boolean creaEstacion(String cod, String nombre, float lon, float lat, float alt, int codRegion, int codComuna){
        Region region = null;
        for(Region r:regiones){
            if(r.getCodigo() == codRegion){
                region = r;
                break;
            }
        }

        if(region == null){
            return false;
        }

        Comuna comuna = region.findComunaById(codComuna);
        if(comuna == null){
            return false;
        }

        for(Region r:regiones){
            Comuna[] comunas = r.getComunas();
            for (Comuna c : comunas ){
                if(c.findEstacionById(cod) != null){
                    return false;
                }
            }
        }
        EstacionMeteorologica estacion = new EstacionMeteorologica(cod,nombre, lon, lat, alt,comuna);
        comuna.addEstacion(estacion);
        estaciones.add(estacion);
        return true;
    }

    public void instalaSensor(String cod, String marca, String modelo, TipoSensor tipo, String codigoEstacion) throws MeteorologiaException{
        EstacionMeteorologica estacion= findEstacion(codigoEstacion).orElseThrow(() -> new MeteorologiaException("No existe una estación con el codigo "+codigoEstacion));
        estacion.instalaSensor(cod, marca, modelo, tipo);
    }

    public void registraMedicion(LocalDateTime fechaHora, float valor, String codEstacion, String codSensor) throws MeteorologiaException {
        EstacionMeteorologica estacion= findEstacion(codEstacion).orElseThrow(() -> new MeteorologiaException("No existe una estación con el codigo "+codEstacion));
        estacion.registraMedicion(fechaHora, valor, codSensor);
    }

    public String[][] listaRegiones(){
        if (regiones==null||regiones.isEmpty()){
            return new String[0][0];
        }
        String[][] matriz=new String[regiones.size()][4];

        for (int i = 0; i<regiones.size(); i++){
            Region r=regiones.get(i);
            matriz[i][0]= String.valueOf(r.getCodigo());
            matriz[i][1]= r.getNombre();
            matriz[i][2]= String.valueOf(r.getComunas().length);
            matriz[i][3]= String.valueOf(r.getCantidadEstaciones());
        }
        return matriz;
    }
    public String[][] listaComunas() {
        int totalComunas= 0;
        for (Region r:regiones) {
            totalComunas+= r.getComunas().length;
        }

        if (totalComunas == 0) {
            return new String[0][0];
        }

        String[][] matriz= new String[totalComunas][5];
        int fila= 0;

        for (Region r:regiones) {
            for (Comuna c : r.getComunas()) {
                matriz[fila][0] = String.valueOf(c.getCodigo());
                matriz[fila][1] = c.getNombre();
                matriz[fila][2] = r.getNombre();
                matriz[fila][3] = String.valueOf(c.getCantidadEstaciones());
                matriz[fila][4] = String.valueOf(c.getCantidadEstacionesActivas());
                fila++;
            }
        }
        return matriz;
    }
    public String[][] listaEstaciones(int codigoRegion, int codigoComuna) {
        Region regionObjetivo = null;
        for (Region r : regiones) {
            if (r.getCodigo() == codigoRegion) {
                regionObjetivo = r;
                break;
            }
        }

        if (regionObjetivo == null) {
            return new String[0][0];
        }

        Comuna comunaObjetivo = regionObjetivo.findComunaById(codigoComuna);
        if (comunaObjetivo == null) {
            return new String[0][0];
        }

        int cantidadEstaciones = 0;
        for (EstacionMeteorologica est : estaciones) {

            String[] datosEstacion = est.toString().split(";");
            String codigoEst = datosEstacion[0];

            if (comunaObjetivo.findEstacionById(codigoEst) != null) {
                cantidadEstaciones++;
            }
        }

        if (cantidadEstaciones == 0) {
            return new String[0][0];
        }


        String[][] matriz = new String[cantidadEstaciones][5];
        int fila = 0;

        for (EstacionMeteorologica est : estaciones) {
            String[] datosEstacion = est.toString().split(";");
            String codigoEst = datosEstacion[0];

            if (comunaObjetivo.findEstacionById(codigoEst) != null) {
                matriz[fila][0] = datosEstacion[0];
                matriz[fila][1] = datosEstacion[1];


                matriz[fila][2] = "(" + datosEstacion[2] + "; " + datosEstacion[3] + "; " + datosEstacion[4] + " m)";

                matriz[fila][3] = datosEstacion[5];


                matriz[fila][4] = String.valueOf(est.getResumenSensores().length);

                fila++;
            }
        }
        return matriz;
    }

    public String[][] listaSensores(String codigoEstacion) {
        for (EstacionMeteorologica estacion : estaciones) {
            String[] datosEstacion = estacion.toString().split(";");
            String codigoEst = datosEstacion[0];

            if (codigoEst.equals(codigoEstacion)) {

                return estacion.getResumenSensores();
            }
        }

        return new String[0][0];
    }

    public String[][] listaMediciones(String codEstacion, String codSensor, LocalDateTime inicio, LocalDateTime fin) {
        for (EstacionMeteorologica estacion : estaciones) {

            String[] datosEstacion = estacion.toString().split(";");
            String codigoEst = datosEstacion[0];

            if(codigoEst.equals(codEstacion)) {

                return estacion.getMedicionesSensorBetween(codSensor, inicio, fin);
            }
        }

        return new String[0][0];
    }

    public String[] listaResumenMediciones(String codigoEstacion, String codigoSensor, LocalDateTime inicio, LocalDateTime fin) throws MeteorologiaException {
        if (fin.isBefore(inicio)) {
            throw new MeteorologiaException("La fecha y hora de término (" +fin+ ") es anterior a la de inicio (" +inicio+ ")");
        }
        EstacionMeteorologica estacion = findEstacion(codigoEstacion).orElseThrow(() -> new MeteorologiaException("No existe una estación con el código "+codigoEstacion));

        Sensor sensor = estacion.findSensorById(codigoSensor).orElseThrow(() -> new MeteorologiaException("No existe el sensor "+codigoSensor+" en la estación "+codigoEstacion));

        Optional<ResumenMediciones> resumenOpt = sensor.getResumenBetween(inicio, fin);
        if (!resumenOpt.isPresent()) {
            return new String[0];
        }

        ResumenMediciones r = resumenOpt.get();
        String unidad = sensor.getUnidad();

        return new String[] {
                sensor.getTipo().toString(),
                String.valueOf(r.getCantidad()),
                r.getMinimo().getValor() + " " + unidad,
                r.getMinimo().getFechaHora().toString(),
                r.getMaximo().getValor() + " " + unidad,
                r.getMaximo().getFechaHora().toString(),
                String.format("%.1f %s", r.getPromedio(), unidad)
        };
    }

    public String[][] listaAlertas(String codigoEstacion, LocalDateTime inicio, LocalDateTime fin) throws MeteorologiaException {
        if (fin.isBefore(inicio)) {
            throw new MeteorologiaException("La fecha y hora de término (" + fin + ") es anterior a la de inicio (" + inicio + ").");
        }

        EstacionMeteorologica estacion = findEstacion(codigoEstacion)
                .orElseThrow(() -> new MeteorologiaException("No existe una estación con el código " + codigoEstacion + "."));

        AlertaMeteorologica[] alertas = estacion.getAlertasBetween(inicio, fin);
        String[][] matriz = new String[alertas.length][6];

        for (int i = 0; i < alertas.length; i++) {
            AlertaMeteorologica a = alertas[i];
            matriz[i][0]= a.getFechaHora().toLocalDate().toString();
            matriz[i][1]= a.getFechaHora().toLocalTime().toString();
            matriz[i][2]= a.getSensor().getCodigo();
            matriz[i][3] = a.getFenomeno();
            matriz[i][4] = a.getValor() + " " + a.getSensor().getUnidad();
            matriz[i][5] = a.getNivel().toString();
        }
        return matriz;
    }
}