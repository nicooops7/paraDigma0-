package modelo;

import java.util.Optional;

public class SensorPrecipitacion extends Sensor implements Alertable {

    public SensorPrecipitacion(String codigo, String marca, String modelo, EstacionMeteorologica estacion) {
        super(codigo, marca, modelo, estacion);
    }

    @Override
    public TipoSensor getTipo() { return TipoSensor.PRECIPITACION; }

    @Override
    public String getUnidad() { return "mm"; }

    @Override
    public boolean esValorAdmisible(float valor) {
        return valor >= 0.0f && valor <= 500.0f;
    }

    @Override
    public Optional<NivelAlerta> evaluaAlerta(float valor) {
        if (valor >= 60.0f) return Optional.of(NivelAlerta.ALARMA);
        if (valor >= 40.0f) return Optional.of(NivelAlerta.ALERTA);
        if (valor >= 20.0f) return Optional.of(NivelAlerta.AVISO);
        return Optional.empty();
    }

    @Override
    public String getFenomeno(float valor) {
        return "Precipitación intensa";
    }
}