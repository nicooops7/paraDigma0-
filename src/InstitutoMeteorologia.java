import java.time.LocalDateTime;
import java.util.ArrayList;

public class InstitutoMeteorologia {
    private ArrayList<Region> regiones;
    public String[][] listaRegiones(){
        if (regiones==null||regiones.isEmpty()){
            return new String[0][0];
        }
        String[][] matriz=new String[regiones.size()][4];

        for (int i = 0; i<regiones.size(); i++){
            Region r=regiones.get(i);
            matriz[i][0]= String.valueOf(r.getCodigo());
            matriz[i][1]= r.getNombre();
            matriz[i][2]= String.valueOf(r.getComunas().length);
            matriz[i][3]= String.valueOf(r.getCantidadEstaciones());
        }
        return matriz;
    }
    public String[][] listaComunas() {
        int totalComunas= 0;
        for (Region r:regiones) {
            totalComunas+= r.getComunas().length;
        }

        if (totalComunas == 0) {
            return new String[0][0];
        }

        String[][] matriz= new String[totalComunas][5];
        int fila= 0;

        for (Region r:regiones) {
            for (Comuna c : r.getComunas()) {
                matriz[fila][0] = String.valueOf(c.getCodigo());
                matriz[fila][1] = c.getNombre();
                matriz[fila][2] = r.getNombre();
                matriz[fila][3] = String.valueOf(c.getCantidadEstaciones());
                matriz[fila][4] = String.valueOf(c.getCantidadEstacionesActivas());
                fila++;
            }
        }
        return matriz;
    }
}