import java.time.LocalDateTime;
import java.util.ArrayList;
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
        for(Region region : regiones){
            if(region.getCodigo() == codigoRegion){
                return region.addComuna(nombre, codigo);
            }
        }
        return false;
    }
    public boolean creaEstacion(String cod, String nombre, float lon , float lat, float alt, int codRegion, int codComuna){

    }

    public boolean instalaSensor(String cod,String marca, String modelo, TipoSensor tipo, String codigoEstacion){

    }
    public boolean registraMedicion(LocalDateTime fechaHora,float valor, String codEstacion, String codSensor){

    }
    public String[][] listaRegiones(){

    }
    public String[][] listaComunas(){

    }
    public String[][] listaEstaciones(int codigoRegion, int codigoComuna){

    }
    public String[][] listaSensores(String codigoEstacion){

    }
    public String[][] listaMediciones(String codEstacion, String codSensor, LocalDateTime inicio, LocalDateTime fin){

    }
}
