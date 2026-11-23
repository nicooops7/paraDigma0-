package controlador;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PersistenciaObjetos {
    public static final String CARPETA = "datos";

    private PersistenciaObjetos() {
    }

    public static void escribeObjeto(String nombreArchivo, Object objeto) throws MeteorologiaException {
        try {
            Files.createDirectories(Paths.get(CARPETA));
            Path ruta = Paths.get(CARPETA, nombreArchivo);
            try (ObjectOutputStream salida = new ObjectOutputStream(Files.newOutputStream(ruta))) {
                salida.writeObject(objeto);
            }
        } catch (IOException e) {
            throw new MeteorologiaException("No fue posible escribir el archivo " + nombreArchivo + ".");
        }
    }

    public static Object leeObjeto(String nombreArchivo) throws MeteorologiaException {
        Path ruta = Paths.get(CARPETA, nombreArchivo);
        if (!Files.exists(ruta)) {
            throw new MeteorologiaException("No se encontró el archivo " + nombreArchivo + ".");
        }
        try (ObjectInputStream entrada = new ObjectInputStream(Files.newInputStream(ruta))) {
            return entrada.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new MeteorologiaException("No fue posible leer el archivo " + nombreArchivo + ".");
        }
    }
}