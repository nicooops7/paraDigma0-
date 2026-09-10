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
            }
        }
        return false;
    }

    }

}
