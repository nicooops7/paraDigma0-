package controlador;

import modelo.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;



public class InstitutoMeteorologia {
    private static InstitutoMeteorologia instance;
    private ArrayList<Region> regiones;
    private ArrayList<EstacionMeteorologica> estaciones;

    private InstitutoMeteorologia() {
        regiones = new ArrayList<>();
        estaciones = new ArrayList<>();
    }
    public static InstitutoMeteorologia getInstance() {
        if (instance == null) {
            instance = new InstitutoMeteorologia();
        }
        return instance;
    }

    public void creaRegion(int codigo, String nombre) throws MeteorologiaException {
        if (findRegion(codigo).isPresent()) {
            throw new MeteorologiaException("Ya existe una región con este codigo");
        }

        for (Region region : regiones) {
            if (region.getNombre().equalsIgnoreCase(nombre)) {
                throw new MeteorologiaException("Ya existe una región con este nombre");
            }
        }

        Region nueva = new Region(codigo, nombre);
        regiones.add(nueva);
    }

    public void creaComuna(int codigo, String nombre, int codigoRegion) throws MeteorologiaException {
        Optional<Region> optionalRegion = findRegion(codigoRegion);
        if (optionalRegion.isEmpty()) {
            throw new MeteorologiaException("No existe una región con ese código");
        }

        Region region = optionalRegion.get();
        region.addComuna(codigo, nombre);
    }

    public void creaEstacion(String cod, String nombre, float lon, float lat, float alt, int codRegion, int codComuna) throws MeteorologiaException {
        if (findEstacion(cod).isPresent()) {
            throw new MeteorologiaException("Ya existe una estación con ese código");
        }

        Optional<Region> optRegion = findRegion(codRegion);
        if (optRegion.isEmpty()) {
            throw new MeteorologiaException("No existe una región con ese código");
        }
        Region region = optRegion.get();

        Optional<Comuna> optComuna = region.findComunaById(codComuna);
        if (optComuna.isEmpty()) {
            throw new MeteorologiaException("No existe una comuna con ese código en la región " + region.getNombre());
        }
        Comuna comuna = optComuna.get();

        EstacionMeteorologica estacion = new EstacionMeteorologica(cod, nombre, lon, lat, alt, comuna);
        comuna.addEstacion(estacion);
        estaciones.add(estacion);
    }

    public boolean instalaSensor(String cod, String marca, String modelo, TipoSensor tipo, String codigoEstacion){

        for(Region r : regiones){

            Comuna[] comunas = r.getComunas();

            for(Comuna c : comunas){

                Optional<EstacionMeteorologica> estacion = c.findEstacionById(codigoEstacion);

                if(estacion != null){
                    return estacion.get().instalaSensor(cod,marca,modelo,tipo);
                }
            }
        }
        return false;
    }

    public boolean registraMedicion(LocalDateTime fechaHora, float valor, String codEstacion, String codSensor){
        for(Region r : regiones){
            for(Comuna c : r.getComunas()){
                Optional<EstacionMeteorologica> estacion = c.findEstacionById(codEstacion);
                if(estacion != null){
                    return estacion.get().registraMedicion(fechaHora, valor, codSensor);
                }
            }
        }
        return false;
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
                matriz[fila][3] = String.valueOf(c.getEstaciones().length);
                matriz[fila][4] = String.valueOf(c.getCantidadEstacionesActivas());
                fila++;
            }
        }
        return matriz;
    }
    public String[][] listaEstaciones(int codigoRegion, int codigoComuna) throws MeteorologiaException {
        Optional<Region> optionalRegion = findRegion(codigoRegion);
        if (optionalRegion.isEmpty()) {
            throw new MeteorologiaException("No existe una región con ese código");
        }
        Region regionObjetivo = optionalRegion.get();

        Optional<Comuna> optionalComuna = regionObjetivo.findComunaById(codigoComuna);
        if (optionalComuna.isEmpty()) {
            throw new MeteorologiaException("No existe una comuna con ese código en la región " + regionObjetivo.getNombre());
        }
        Comuna comunaObjetivo = optionalComuna.get();

        EstacionMeteorologica[] estacionesComuna = comunaObjetivo.getEstaciones();
        if (estacionesComuna.length == 0) {
            return new String[0][0];
        }

        String[][] matriz = new String[estacionesComuna.length][5];
        for (int i = 0; i < estacionesComuna.length; i++) {
            EstacionMeteorologica est = estacionesComuna[i];
            String[] datosEstacion = est.toString().split(";");
            matriz[i][0] = datosEstacion[0];
            matriz[i][1] = datosEstacion[1];
            matriz[i][2] = "(" + datosEstacion[2] + "; " + datosEstacion[3] + "; " + datosEstacion[4] + " m)";
            matriz[i][3] = datosEstacion[5];
            matriz[i][4] = String.valueOf(est.getResumenSensores().length);
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
    private Optional<Region> findRegion(int codigo) {
        for (Region r : regiones) {
            if (r.getCodigo() == codigo) {
                return Optional.of(r);
            }
        }
        return Optional.empty();
    }

    private Optional<Comuna> findComuna(int codigoRegion, int codigoComuna) {
        Optional<Region> optionalRegion = findRegion(codigoRegion);
        if (optionalRegion.isPresent()) {
            return optionalRegion.get().findComunaById(codigoComuna);
        }
        return Optional.empty();
    }

    private Optional<EstacionMeteorologica> findEstacion(String codigo) {
        for (EstacionMeteorologica est : estaciones) {
            if (est.getCodigo().equals(codigo)) {
                return Optional.of(est);
            }
        }
        return Optional.empty();
    }

    private Optional<Sensor> findSensor(String codigoEstacion, String codigoSensor) {
        Optional<EstacionMeteorologica> optionalEstacion = findEstacion(codigoEstacion);
        if (optionalEstacion.isPresent()) {
            return optionalEstacion.get().findSensorById(codigoSensor);
        }
        return Optional.empty();
    }

}