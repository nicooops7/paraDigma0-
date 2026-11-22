package vista;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import controlador.*;
import modelo.*;


public class InterfazUsuario {
    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static InterfazUsuario instancia;
    private Scanner sc;
    private InstitutoMeteorologia instituto;

    private InterfazUsuario(){
        sc = new Scanner(System.in);
        instituto = InstitutoMeteorologia.getInstance();
    }

    private int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número entero válido.");
            }
        }
    }

    private float leerFloat(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                float numero = Float.parseFloat(sc.nextLine().trim().replace(',', '.'));
                if (Float.isFinite(numero)) return numero;
            } catch (NumberFormatException e) {
                // Volver a solicitar el número.
            }
            System.out.println("Ingrese un número válido.");
        }
    }

    private String leerTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = sc.nextLine().trim();
            if (!texto.isEmpty() && !texto.contains(";")) return texto;
            System.out.println("Ingrese un texto no vacío y sin punto y coma.");
        }
    }

    public static synchronized InterfazUsuario getInstance(){
        if (instancia == null) {
            instancia = new InterfazUsuario();
        }
        return instancia;
    }

    public void crearRegion(){
        System.out.println("\nCREAR REGIÓN");
        System.out.println("ingrese codigo");
        int codigo = leerEntero("Código: ");
        System.out.println("ingrese nombre");
        String nombre = leerTexto("Nombre: ");

        try{
            instituto.creaRegion(codigo, nombre);
            System.out.println(" Región creada exitosamente.");
        }catch(MeteorologiaException e){
            System.out.println("> Error: " + e.getMessage());
        }
    }

    public void crearComuna(){
        System.out.println("\nCREAR COMUNA");
        System.out.println(" Ingrese codigo de comuna");
        int codCom = leerEntero("Código: ");
        System.out.println("ingrese nombre");
        String nom = leerTexto("Nombre: ");
        System.out.println("ingrese Codigo de la región");
        int codReg = leerEntero("Código de región: ");
        try{
            instituto.creaComuna(codCom,nom,codReg);
            System.out.println("Comuna creada exitosamente.");
        }catch(MeteorologiaException e){
            System.out.println("> Error: " + e.getMessage());
        }
    }

    public void crearEstacion(){
        System.out.println("\nCREAR ESTACIÓN METEOROLÓGICA");
        System.out.println("ingrese codigo");
        String cod = leerTexto("Código: ");
        System.out.println("ingrese nombre");
        String nom = leerTexto("Nombre: ");
        System.out.println("ingrese longitud");
        float lon = leerFloat("Longitud: ");
        System.out.println("ingrese latitud");
        float lat = leerFloat("Latitud: ");
        System.out.println("ingrese altitud");
        float alt = leerFloat("Altitud: ");
        System.out.println("ingrese codigo de la región");
        int codReg = leerEntero("Código de región: ");
        System.out.println("ingrese codigo de la comuna");
        int codCom = leerEntero("Código: ");

        try{
            instituto.creaEstacion(cod,nom,lon,lat,alt,codReg,codCom);
            System.out.println("Estación creada exitosamente.");
        }catch(MeteorologiaException e){
            System.out.println("> Error: " + e.getMessage());
        }
    }

    public void listarRegiones() {
        String[][] datos = instituto.listaRegiones();
        if (datos.length == 0) {
            System.out.println("No existen regiones registradas.");
            return;
        }
        System.out.printf("%-10s %-20s %-12s %-12s%n", "CÓDIGO", "REGIÓN", "COMUNAS", "ESTACIONES");
        for (String[] r : datos) {
            System.out.printf("%-10s %-20s %-12s %-12s%n", r[0], r[1], r[2], r[3]);
        }
    }

    public void listarComunas() {
        String[][] datos = instituto.listaComunas();
        if (datos.length == 0) {
            System.out.println("No existen comunas registradas.");
            return;
        }
        System.out.printf("%-10s %-20s %-20s %-12s %-12s%n",
                "CÓDIGO", "COMUNA", "REGIÓN", "ESTACIONES", "ACTIVAS");
        for (String[] c : datos) {
            System.out.printf("%-10s %-20s %-20s %-12s %-12s%n",
                    c[0], c[1], c[2], c[3], c[4]);
        }
    }

    public void listarEstaciones() {
        System.out.println("\nESTACIONES DE UNA COMUNA");
        System.out.print("Código de región: ");
        int codReg = leerEntero("Código de región: ");
        System.out.print("Código de comuna: ");
        int codCom = leerEntero("Código: ");
        try {
            String[][] datos = instituto.listaEstaciones(codReg, codCom);
            if (datos.length == 0) {
                System.out.println("La comuna no registra estaciones.");
                return;
            }
            System.out.printf("%-18s %-24s %-32s %-12s %s%n",
                    "CÓDIGO", "NOMBRE", "POSICIÓN", "ESTADO", "SENSORES ACTIVOS");
            for (String[] e : datos) {
                System.out.printf("%-18s %-24s %-32s %-12s %s%n",
                        e[0], e[1], e[2], e[3], e[4]);
            }
        } catch (MeteorologiaException e) {
            System.out.println("> Error: " + e.getMessage());
        }
    }

    public void listarSensores() {
        System.out.println("\nSENSORES DE UNA ESTACIÓN");
        System.out.print("Código de estación: ");
        String codEst = leerTexto("Código de estación: ");

        try {
            String[][] datos = instituto.listaSensores(codEst);
            if (datos.length == 0) {
                System.out.println("La estacion " + codEst + " no registra sensores.");
                return;
            }
            System.out.printf("%-12s %-20s %-15s %-15s %-10s %-12s %s%n", "CÓDIGO", "TIPO", "MARCA", "MODELO", "UNIDAD", "ESTADO", "ÚLTIMA MEDICIÓN");
            for (String[] s : datos) {
                System.out.printf("%-12s %-20s %-15s %-15s %-10s %-12s %s%n", s[0], s[1], s[2], s[3], s[4], s[5], s[6]);
            }
        } catch (MeteorologiaException e) {
            System.out.println("> Error: " + e.getMessage());
        }
    }

    public void listarMediciones() {
        System.out.println("\nMEDICIONES DE UN SENSOR");
        System.out.print("Código de estación: ");
        String codEst = leerTexto("Código de estación: ");
        System.out.print("Código de sensor: ");
        String codSensor = leerTexto("Código de sensor: ");
        System.out.print("Inicio [dd/MM/yyyy HH:mm]: ");
        String inicioStr = sc.nextLine();
        System.out.print("Fin [dd/MM/yyyy HH:mm]: ");
        String finStr = sc.nextLine();

        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime inicio = LocalDateTime.parse(inicioStr, fmt);
            LocalDateTime fin = LocalDateTime.parse(finStr, fmt);

            String[][] datos = instituto.listaMediciones(codEst, codSensor, inicio, fin);
            if (datos.length == 0) {
                System.out.println("El sensor " + codSensor + " no registra mediciones en el período indicado.");
                return;
            }
            System.out.printf("%-14s %-9s %-12s %s%n", "FECHA", "HORA", "VALOR", "UNIDAD");
            for (String[] m : datos) {
                System.out.printf("%-14s %-9s %-12s %s%n", m[0], m[1], m[2], m[3]);
            }
        } catch (Exception e) {
            System.out.println("> Error: " + e.getMessage());
        }
    }

    public void listarResumenMediciones() {
        System.out.println("\nRESUMEN ESTADÍSTICO DE UN SENSOR");
        System.out.print("Código de estación: ");
        String codEst = leerTexto("Código de estación: ");
        System.out.print("Código de sensor: ");
        String codSensor = leerTexto("Código de sensor: ");
        System.out.print("Inicio [dd/MM/yyyy HH:mm]: ");
        String inicioStr = sc.nextLine();
        System.out.print("Fin [dd/MM/yyyy HH:mm]: ");
        String finStr = sc.nextLine();

        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime inicio = LocalDateTime.parse(inicioStr, fmt);
            LocalDateTime fin = LocalDateTime.parse(finStr, fmt);

            String[] res = instituto.listaResumenMediciones(codEst, codSensor, inicio, fin);
            if (res.length == 0) {
                System.out.println("El sensor " + codSensor + " no registra mediciones en el período indicado.");
                return;
            }
            System.out.println("\nRESUMEN ESTADÍSTICO DEL SENSOR " + codSensor + " (" + codEst + ")");
            System.out.println("Período: " + inicioStr + " a " + finStr);
            System.out.println("Tipo de sensor     : " + res[0]);
            System.out.println("Cantidad de mediciones : " + res[2]);
            System.out.println("Mínimo             : " + res[3] + " " + res[1] + " (" + res[4] + ")");
            System.out.println("Máximo             : " + res[5] + " " + res[1] + " (" + res[6] + ")");
            System.out.println("Promedio           : " + res[7] + " " + res[1]);
        } catch (Exception e) {
            System.out.println("> Error: " + e.getMessage());
        }
    }

}