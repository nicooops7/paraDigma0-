package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Comuna implements Serializable {
    private static final long serialversionUID = 1L;

    private int codigo;
    private String nombre;
    private List<EstacionMeteorologica> estaciones;
    private Region region;

    public Comuna(int codigo, String nombre, Region region){
        this.codigo=codigo;
        this.nombre=nombre;
        this.region = region;
        this.estaciones = new ArrayList<>();
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

    public Optional<EstacionMeteorologica> findEstacionById(String codigo){
        return estaciones.stream().filter(e -> e.getCodigo().equals(codigo)).findFirst();
    }

    public Region getRegion(){
        return region;
    }


    public EstacionMeteorologica[] getEstaciones(){
        return estaciones.toArray(new EstacionMeteorologica[0]);
    }

    public int getCantidadEstacionesActivas(){
        return (int) estaciones.stream().filter(e -> e.getEstado() == Estado.ACTIVO).count();
    }

}
