package controlador;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class PersistenciaTexto {
    private static PersistenciaTexto instancia;
    public static final String SEPARADOR = ";";

    private PersistenciaTexto(){}

    public static PersistenciaTexto getInstance(){
        if(instancia == null){
            instancia = new PersistenciaTexto();
        }
        return instancia;
    }



    public String[][] leeRegistros(String nombreArchivo) throws MeteorologiaException {
        Path ruta = Paths.get("datos", nombreArchivo);
        try {
            List<String> lineas = Files.readAllLines(ruta, StandardCharsets.UTF_8);
            String[][] datos = new String[lineas.size()][];
            for (int i = 0; i < lineas.size(); i++) {
                datos[i] = lineas.get(i).split(SEPARADOR, -1);
            }
            return datos;
        } catch (NoSuchFileException e) {
            throw new MeteorologiaException("No se encontró el archivo " + nombreArchivo + ".");
        } catch (IOException e) {
            throw new MeteorologiaException("No fue posible leer el archivo " + nombreArchivo + ".");
        }
    }

    public void escribeRegistros(String nombreArchivo, String[] lineas) throws MeteorologiaException{
        Path carpeta = Paths.get("datos");
        try{
            if(!Files.exists(carpeta)){
                Files.createDirectories(carpeta);
            }
            Path ruta = carpeta.resolve(nombreArchivo);
            List<String> listaLineas = List.of(lineas);
            Files.write(ruta, listaLineas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new MeteorologiaException("No fue posible escribir el archivo "+nombreArchivo);
        }
    }
}
