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
    public static String SEPARADOR = ";";

    private PersistenciaTexto(){}

    public static PersistenciaTexto getInstance(){
        if(instancia == null){
            instancia = new PersistenciaTexto();
        }
        return instancia;
    }



    public String[] leeRegistros(String nombreArchivo) throws MeteorologiaException{
        Path ruta = Paths.get("datos" , nombreArchivo);
        try{
            List<String> lineasLista = Files.readAllLines(ruta, StandardCharsets.UTF_8);
            return lineasLista.toArray(new String[0]);
        }catch(NoSuchFileException e){
            throw new MeteorologiaException("No se encontró el archivo" +nombreArchivo+".");
        }catch (IOException e){
            throw new MeteorologiaException("No fue posible leer el archivo" +nombreArchivo+".");
        }
    }

    public String[]  escribeRegistros(String nombreArchivo, String[] lineas){

    }
}
