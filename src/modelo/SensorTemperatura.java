package modelo;

import java.util.Optional;

public class SensorTemperatura extends Sensor implements Alertable {

    public SensorTemperatura(String codigo, String marca, String modelo, EstacionMeteorologica estacion) {
        super(codigo, marca, modelo, estacion);
    }

    @Override
    public TipoSensor getTipo(){
        return TipoSensor.TEMPERATURA;
    }

    @Override
    public String getUnidad(){
        return "°C";
    }

    @Override
    public boolean esValorAdmisible(float valor) {
        return valor>= -80.0f&&valor<= 60.0f;
    }

    @Override
    public Optional<NivelAlerta> evaluaAlerta(float valor) {
        if (valor<= -5.0f) return Optional.of(NivelAlerta.ALARMA);
        if (valor<= -2.0f) return Optional.of(NivelAlerta.ALERTA);
        if (valor<= 0.0f) return Optional.of(NivelAlerta.AVISO);

        if (valor>= 38.0f) return Optional.of(NivelAlerta.ALARMA);
        if (valor>= 35.0f) return Optional.of(NivelAlerta.ALERTA);
        if (valor>= 32.0f) return Optional.of(NivelAlerta.AVISO);

        return Optional.empty();
    }

    @Override
    public String getFenomeno(float valor) {
        if (valor<= 0.0f) return "Helada";
        if (valor>= 32.0f) return "Calor extremo";
        return "";
    }
}