package modelo;

public class ResumenMediciones {
    private final int cantidad;
    private final float promedio;
    private final Medicion minimo;
    private final Medicion maximo;

    public ResumenMediciones(Medicion[] mediciones){
        if(mediciones==null||mediciones.length==0){
            throw new IllegalArgumentException("sin datos");
        }
        this.cantidad=mediciones.length;
        Medicion minTemp=mediciones[0];
        Medicion maxTemp=mediciones[0];
        float suma=0.0f;

        for (Medicion m:mediciones){
            suma+=m.getValor();
            if(m.getValor()<minTemp.getValor() ||
                    (m.getValor()==minTemp.getValor() &&
                            m.getFechaHora().isBefore(minTemp.getFechaHora()))){
                minTemp=m;
            }
            if(m.getValor()>maxTemp.getValor() ||
                    (m.getValor()==maxTemp.getValor() &&
                            m.getFechaHora().isBefore(maxTemp.getFechaHora()))){
                maxTemp=m;
            }
        }
        this.minimo=minTemp;
        this.maximo=maxTemp;
        this.promedio=suma/this.cantidad;
    }
    public int getCantidad(){return cantidad;}
    public float getPromedio(){return promedio;}
    public Medicion getMinimo(){return minimo;}
    public Medicion getMaximo(){return  maximo;}
}
