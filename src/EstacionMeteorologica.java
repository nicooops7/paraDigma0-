import java.time.LocalDateTime;

public class EstacionMeteorologica {
    private String codigo;
    private String nombre;
    private float longitud;
    private float latitud;
    private float altitud;
    private Estado estado;

    public EstacionMeteorologica(String cod, String nombre, float lon,
                                 float lat, float alt, Comuna comuna) {
        this.codigo = cod;
        this.nombre = nombre;
        this.longitud = lon;
        this.latitud = lat;
        this.altitud = alt;
    }
    public boolean instalaSensor(String codigo, String marca, String modelo,
                                 TipoSensor tipo){}

    public boolean registraMedicion(LocalDateTime fechaHora, float valor, String codigoSensor){}

    @Override

    public String toString(){}

    public String[][] getResumenSensores(){}

    public String[][] getMedicionSensorBetween(String codigoSensor, LocalDateTime inicio, LocalDateTime fin){}
}
