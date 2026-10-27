package Modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public Optional<EstacionMeteorologica> findEstacionById(String codigo) {
        for (EstacionMeteorologica e : estaciones) {
            if (e.getCodigo().equals(codigo)) {
                return Optional.of(e);
            }
        }
        return Optional.empty();
    }
    public Region getRegion(){
        return region;
    }
    public EstacionMeteorologica[] getEstaciones() {
        EstacionMeteorologica[] estaci = new EstacionMeteorologica[estaciones.size()];
        for (int i = 0; i < this.estaciones.size(); i++) {
            estaci[i] = this.estaciones.get(i);
        }
        return estaci;
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
