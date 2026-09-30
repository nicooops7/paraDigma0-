import java.util.ArrayList;
import java.util.List;

public class Region{
    private List<Comuna> comunas;
    private int codigo;
    private String nombre;

    public Region(int cod, String nom) {
        this.codigo=cod;
        this.nombre=nom;
        this.comunas=new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public int getCodigo() {
        return codigo;
    }

    public boolean addComuna(int cod, String nom) {
        for (Comuna c : comunas) {
            if (c.getNombre().equalsIgnoreCase(nom)||c.getCodigo()==cod){
                return false;
            }
        }
        Comuna c = new Comuna(cod, nom, this);
        comunas.add(c);
        return true;
    }
    public Comuna findComunaById(int codigo){
        for (Comuna c : comunas){
            if (c.getCodigo() == codigo){
                return c;
            }
        }
        return null;
    }
    public Comuna[] getComunas(){
        Comuna[] lista= new  Comuna[comunas.size()];
        for (int i = 0; i < comunas.size() ; i++) {
           lista[i]=comunas.get(i);
        }
        return lista;
    }
    public int getCantidadEstaciones(){
        int total=0;
        for (Comuna c : comunas) {
            total+=c.getCantidadEstaciones();
        }
        return total;
    }
}
