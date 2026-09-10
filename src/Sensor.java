import java.time.LocalDateTime;

public abstract class Sensor  {
private String codigo;
private String marca;
private String modelo;
private Estado estado;
protected Sensor (String codigo,String marca,String modelo, EstacionMeteorologica estacion){
  this.codigo=codigo;
  this.marca=marca;
  this.modelo=modelo;
  this.estacion=estacion;
}
    public String getCodigo() {
        return codigo;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public boolean addMedicion(LocalDateTime fechaHora, float valor) {

        if (this.estado != Estado.ACTIVO) {
            return false;
        }






        public abstract String getUnidad();
        public abstract boolean esValorAdmisible(float valor);
}
