package modelo;

import controlador.MeteorologiaException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public void addComuna(int codigo, String nom) throws MeteorologiaException {
        if (findComunaById(codigo).isPresent()) {
            throw new MeteorologiaException("Esta región ya tiene una comuna con el código " + codigo);
        }

        for (Comuna c : comunas) {
            if (c.getNombre().equalsIgnoreCase(nom)) {
                throw new MeteorologiaException("Esta región ya tiene una comuna con el nombre " + nom);
            }
        }

        Comuna c = new Comuna(codigo, nom, this);
        comunas.add(c);
    }
    public Optional<Comuna> findComunaById(int codigo) {
        for (Comuna c : comunas) {
            if (c.getCodigo() == codigo) {
                return Optional.of(c);
            }
        }
        return Optional.empty();
    }
    public Comuna[] getComunas(){
        Comuna[] lista= new  Comuna[comunas.size()];
        for (int i = 0; i < comunas.size() ; i++) {
           lista[i]=comunas.get(i);
        }
        return lista;
    }
    public int getCantidadEstaciones() {
        int total=0;
        for (Comuna c : comunas) {
            total += c.getEstaciones().length;
        }
        return total;
    }
}
