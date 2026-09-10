public class Comuna {
    private int codigo;
    private String nombre;

    public Comuna(int codigo, String nombre, Region region){
        this.codigo=codigo;
        this.nombre=nombre;
    }
    public int getCodigo(){
        return codigo;
    }
    public String getNombre(){
        return nombre;
    }
    public void addEstacion(EstacionMeteorologica estacion){}
    public EstacionMeteorologica findEstacion(String codigo){}
    public Region getRegion(){}
    public int getCantidadEstaciones(){}
    public int getCantidadEstacionesActivas(){}
}
