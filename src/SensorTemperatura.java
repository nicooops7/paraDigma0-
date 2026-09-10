public class SensorTemperatura extends Sensor {

    public SensorTemperatura(String codigo, String marca, String modelo, EstacionMeteorologica estacion) {
        super(codigo, marca, modelo, estacion);
    }

    public float convertirCelciusAFahrenheit(float valor) {
        return (valor * 9.0f / 5.0f) + 32.0f;
    }

    @Override
    public String getUnidad() {
        return "°C";
    }

    @Override
    public boolean esValorAdmisible(float valor) {

        return valor >= -80.0f && valor <= 60.0f;
    }
}