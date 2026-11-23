package modelo;

import controlador.MeteorologiaException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class Region implements Serializable {
    private static final long serialVersionUID = 1L;

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

    public void addComuna(int cod, String nom) throws MeteorologiaException{
        if (findComunaById(cod).isPresent()) {
            throw new MeteorologiaException("Esta región ya tiene una comuna con el código " + cod);
        }

        if (comunas.stream().anyMatch(c -> c.getNombre().equalsIgnoreCase(nom))) {
            throw new MeteorologiaException("Esta región ya tiene una comuna con el nombre " + nom);
        }

        comunas.add(new Comuna(cod, nom, this));
    }

    public Optional<Comuna> findComunaById(int codigo) {
        return comunas.stream()
                .filter(c -> c.getCodigo() == codigo)
                .findFirst();
    }

    public Comuna[] getComunas(){
        return comunas.toArray(new Comuna[0]);
    }

    public int getCantidadEstaciones() {
        return comunas.stream()
                .mapToInt(c -> c.getEstaciones().length)
                .sum();
    }
}
