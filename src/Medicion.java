import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Medicion {

    private final LocalDateTime fechaHora;
    private final float valor;

    public Medicion(LocalDateTime fechaHora, float valor) {

        this.fechaHora = fechaHora;
        this.valor = valor;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public float getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Medicion otraMedicion = (Medicion) obj;


        if (this.fechaHora != null && otraMedicion.fechaHora != null) {
            return this.fechaHora.equals(otraMedicion.fechaHora);
        }
        return false;
    }

    @Override
    public String toString() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String fechaFormateada = "";

        if (this.fechaHora != null) {
            fechaFormateada = this.fechaHora.format(formatter);
        }


        String valorFormateado = String.format(Locale.US, "%.1f", this.valor);


        return fechaFormateada + "; " + valorFormateado;
    }
}