import java.util.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class interfazUsuario {
    Scanner sc = new Scanner(System.in);
    private institutoMetereologia instituto;

    public static void main(String[] args) {

        interfazUsuario interfaz = new interfazUsuario();

        interfaz.menuPrincipal();

    }

    private void menuPrincipal() {
        instituto = new institutoMetereologia();

        int opcion;

        do {
            System.out.println("1. Crear región");
            System.out.println("2. Crear comuna");
            System.out.println("3. Crear estación meteorológica");
            System.out.println("4. Instalar sensor");
            System.out.println("5. Registrar medición");
            System.out.println("6. Generar listados");
            System.out.println("7. Salir");

            opcion = sc.nextInt();

            while (opcion < 1 || opcion > 7) {

                System.out.print("Opción inválida. Ingrese nuevamente: ");

                opcion = sc.nextInt();
                sc.nextLine();
            }

            switch (opcion) {
                case 1:
                    crearRegion();

                    break;
                case 2:
                    crearComuna();

                    break;
                case 3:
                    crearEstacionMeteorologica();

                    break;
                case 4:
                    instalarSensor();

                    break;
                case 5:
                    registrarMedicion();

                    break;
                case 6:
                    menuListados();

                    break;
                case 7:
                    System.out.println("Programa FInalizado.");
                    break;
            }

        } while (opcion != 7);

    }

    private void crearRegion() {

        System.out.println("ingrese codigo");
        int codigo = sc.nextInt();
        System.out.println("ingrese nombre");
        String nombre = sc.next();
        boolean x = instituto.creaRegion(codigo, nombre);
        if (x) {
            System.out.println("Region creada exitosamente..");
        }
        System.out.println("No se pudo crear la Region, codigo o nombre ya existe...");

    }

    private void crearComuna() {

        System.out.println(" Ingrese codigo");
        int cod = sc.nextInt();
        System.out.println("ingrese nombre");
        String nom = sc.next();
        System.out.println("ingrese Codigo de la región");
        int codReg = sc.nextInt();
        boolean x = instituto.creaComuna(cod, nom, codReg);
        if (x) {
            System.out.println("Comuna Creada exitosamente..");
        }
        System.out.println("No se pudo crear la Comuna, Region, codigo o nombre ya existe...");
    }

    private void crearEstacionMeteorologica() {
        System.out.println("ingrese codigo");
        String cod = sc.next();
        System.out.println("ingrese nombre");
        String nom = sc.next();
        System.out.println("ingrese longitud");
        float lon = sc.nextFloat();
        System.out.println("ingrese latitud");
        float lat = sc.nextFloat();
        System.out.println("ingrese altitud");
        float alt = sc.nextFloat();
        System.out.println("ingrese codigo de la región");
        int codReg = sc.nextInt();
        System.out.println("ingrese codigo de la comuna");
        int codCom = sc.nextInt();

        boolean x = instituto.creaEstacion(cod, nom, lon, lat, alt, codReg, codCom);

        if (x) {
            System.out.println("Estacion creada exitosamente");
        } else {
            System.out.println("No se pudo crear la Estación...");
        }

    }

    private void instalarSensor() {

    }

    private void registrarMedicion() {

    }

    private void menuListados() {

    }

    public void listarRegiones() {

    }

    private void listarEstaciones() {
        System.out.print("Ingrese código de región: ");
        int codRegion = sc.nextInt();
        sc.nextLine();

        System.out.print("Ingrese código de comuna: ");
        int codComuna = sc.nextInt();
        sc.nextLine();


        String[][] datos = instituto.listaEstaciones(codRegion, codComuna);

        System.out.println("Estaciones de la Comuna " + codComuna);

        if (datos.length == 0) {
            System.out.println("> No existen estaciones registradas para esta búsqueda.");
        } else {
            System.out.println("-------------------------------------------------------------------------------------");

            System.out.printf("%-15s %-20s %-30s %-10s %s", "CÓDIGO", "NOMBRE", "UBICACIÓN", "ESTADO", "SENSORES OPERATIVOS");

            for (int i = 0; i < datos.length; i++) {
                System.out.printf("%-15s %-20s %-30s %-10s %s", datos[i][0], datos[i][1], datos[i][2], datos[i][3], datos[i][4]);
            }
            System.out.println("-------------------------------------------------------------------------------------");
        }
    }

    private void listarSensores() {
        System.out.print("Ingrese código de la estación: ");
        String codEstacion = sc.nextLine();


        String[][] datos = instituto.listaSensores(codEstacion);

        System.out.println("Sensores de " + codEstacion);

        if (datos.length == 0) {
            System.out.println("> No existen sensores registrados para esta estación.");
        } else {
            System.out.println("--------------------------------------------------------------------------------------------------------");

            System.out.printf("%-10s %-15s %-15s %-10s %-10s %-10s %s", "CÓDIGO", "TIPO", "MARCA", "MODELO", "UNIDAD", "ESTADO", "ÚLTIMA MEDICIÓN");

            for (int i = 0; i < datos.length; i++) {
                System.out.printf("%-10s %-15s %-15s %-10s %-10s %-10s %s", datos[i][0], datos[i][1], datos[i][2], datos[i][3], datos[i][4], datos[i][5], datos[i][6]);
            }
            System.out.println("--------------------------------------------------------------------------------------------------------");
        }
    }

    private void listarMediciones() {
        System.out.print("Ingrese código de la estación: ");
        String codEstacion = sc.nextLine();
        System.out.print("Ingrese código del sensor: ");
        String codSensor = sc.nextLine();
        System.out.print("Ingrese fecha y hora de inicio [dd/MM/yyyy HH:mm]: ");
        String strInicio = sc.nextLine();
        System.out.print("Ingrese fecha y hora de fin [dd/MM/yyyy HH:mm]: ");
        String strFin = sc.nextLine();


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime inicio = LocalDateTime.parse(strInicio, formatter);
        LocalDateTime fin = LocalDateTime.parse(strFin, formatter);


        String[][] datos = instituto.listaMediciones(codEstacion, codSensor, inicio, fin);

        System.out.println("Mediciones del Sensor " + codSensor);
        System.out.println("Período: " + strInicio + " a " + strFin);

        if (datos.length == 0) {
            System.out.println("> No existen mediciones para este sensor en el período indicado.");
        } else {
            System.out.println("--------------------------------------------------");

            System.out.printf("%-15s %-10s %-10s %s", "FECHA", "HORA", "VALOR", "UNIDAD");

            for (int i = 0; i < datos.length; i++) {
                System.out.printf("%-15s %-10s %-10s %s", datos[i][0], datos[i][1], datos[i][2], datos[i][3]);
            }
            System.out.println("--------------------------------------------------");
        }
    }
}
