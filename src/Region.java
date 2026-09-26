import java.util.ArrayList;
import java.util.List;

public class Region{
    private List<Comuna> comunas;
    private int codigo;
    private String nombre;

    public Region(int cod, String nom){
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

    public boolean addComuna(String nom, int cod) {
        for (Comuna c : comunas) {
            if (c.getNombre().equalsIgnoreCase(nom)||c.getCodigo()==cod){
                System.out.println("La comuna ya existe");
                return false;
            }else {
                comunas.add(c);
                System.out.println("Se ha añadido la comuna");
                return true;
            }
        }
    }

    public Comuna findComunaById(int codigo){
        for (Comuna c : comunas){
            if (c.getCodigo() == codigo){
                return c;
            }
        }
    }

    public Comuna[] getComunas(){}

    public int getCantidadEstaciones(){}
}
