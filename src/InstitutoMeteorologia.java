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

    public boolean instalaSensor(String cod, String marca, String modelo, TipoSensor tipo, String codigoEstacion){

        for(Region r : regiones){

            Comuna[] comunas = r.getComunas();

            for(Comuna c : comunas){

                EstacionMeteorologica estacion = c.findEstacionById(codigoEstacion);

                if(estacion != null){
                    return estacion.instalaSensor(cod,marca,modelo,tipo);
                }
            }
        }
        return false;
    }

    public boolean registraMedicion(LocalDateTime fechaHora, float valor, String codEstacion, String codSensor){
        for(Region r : regiones){
            for(Comuna c : r.getComunas()){
                EstacionMeteorologica estacion = c.findEstacionById(codEstacion);
                if(estacion != null){
                    return estacion.registraMedicion(fechaHora, valor, codSensor);
                }
            }
        }
        return false;
    }






}
