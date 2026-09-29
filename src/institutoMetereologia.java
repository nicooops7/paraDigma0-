import java.util.ArrayList;
import java.time.LocalDateTime;


public class institutoMetereologia {
    private ArrayList<Region> regiones;
    private ArrayList<EstacionMeteorologica> estaciones;



    public institutoMetereologia(){
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

        return region.addComuna(nombre, codigo);
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
                if(c.findEstacion(cod) != null){
                    return false;
                }
            }
        }
        EstacionMeteorologica estacion = new EstacionMeteorologica(cod,nombre, lon, lat, alt,comuna);
        comuna.addEstacion(estacion);
        estaciones.add(estacion);
        return true;
    }

    public boolean instalaSensor(String cod, String nombre, String marca, String modelo, TipoSensor tipo, String codigoEstacion){

        for(Region r : regiones){

            Comuna[] comunas = r.getComunas();

            for(Comuna c : comunas){

                EstacionMeteorologica estacion = c.findEstacion(codigoEstacion);

                if(estacion != null){
                    return estacion.instalaSensor(cod,marca,modelo,tipo);
                }
            }
        }
        return false;
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
            if (estacion.getCodigo().equals(codigoEstacion)) {

                return estacion.getResumenSensores();
            }
        }

        return new String[0][0];
    }

    public String[][] listaMediciones(String codEstacion, String codSensor, LocalDateTime inicio, LocalDateTime fin) {
        for (EstacionMeteorologica estacion : estaciones) {
            if (estacion.getCodigo().equals(codEstacion)) {

                return estacion.getMedicionesSensorBetween(codSensor, inicio, fin);
            }
        }

        return new String[0][0];
    }



}
