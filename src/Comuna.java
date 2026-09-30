import java.util.ArrayList;
import java.util.List;

public class Comuna {
    private int codigo;
    private String nombre;
    private List<EstacionMeteorologica> estaciones;
    private Region region;

    public Comuna(int codigo, String nombre, Region region){
        this.codigo=codigo;
        this.nombre=nombre;
        this.region=region;
        this.estaciones=new ArrayList<EstacionMeteorologica>();
    }
    public int getCodigo(){
        return codigo;
    }
    public String getNombre(){
        return nombre;
    }
    public void addEstacion(EstacionMeteorologica estacion){
        estaciones.add(estacion);
    }
    public EstacionMeteorologica findEstacionById(String codigo){
        for (EstacionMeteorologica e : estaciones) {
            String[] datos = e.toString().split(";");
            if(datos[0].equals(codigo)){
                return e;
            }
        }
        return null;
    }
    public Region getRegion(){
        return region;
    }
    public int getCantidadEstaciones(){
        return estaciones.size();
    }
    public int getCantidadEstacionesActivas(){
        int activas = 0;
        for (EstacionMeteorologica e : estaciones) {
            String[] datos = e.toString().split(";");
            if (datos[5].equalsIgnoreCase("ACTIVO")) {
                activas++;
            }
        }
        return activas;
    }
}
