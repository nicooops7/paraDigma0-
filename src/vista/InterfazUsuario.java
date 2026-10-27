package vista;

import controlador.InstitutoMeteorologia;
import controlador.MeteorologiaException;
import modelo.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;


public class InterfazUsuario {
    Scanner sc = new Scanner(System.in);
    private InstitutoMeteorologia instituto;
    private static InterfazUsuario instance;

    private InterfazUsuario() {
        instituto = InstitutoMeteorologia.getInstance();
    }

    public static InterfazUsuario getInstance() {
        if (instance == null) {
            instance = new InterfazUsuario();
        }
        return instance;
    }

    public void menuPrincipal(){
        int opcion;

        do{
            System.out.println("1. Crear región");
            System.out.println("2. Crear comuna");
            System.out.println("3. Crear estación meteorológica");
            System.out.println("4. Instalar sensor");
            System.out.println("5. Registrar medición");
            System.out.println("6. Cambiar estado de sensor");
            System.out.println("7. Generar listados");
            System.out.println("8. Cargar datos");
            System.out.println("9. Guardar datos");
            System.out.println("10. Salir");
            System.out.print("Opción: ");

            opcion = sc.nextInt();
            sc.nextLine();

            while (opcion < 1 || opcion > 10) {

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
                    cambiarEstadoSensor();
                    break;
                case 7:
                    menuListados();
                    break;
                case 8:
                    cargarDatos();
                    break;
                case 9:
                    guardarDatos();
                    break;
                case 10:
                    salir();
                    break;
            }
        } while (opcion != 10);

    }

    private void salir() {
    }

    private void guardarDatos() {
    }

    private void cargarDatos() {
    }

    private void cambiarEstadoSensor() {
    }

    private void crearRegion(){

        System.out.println("ingrese codigo");
        int codigo = sc.nextInt();
        System.out.println("ingrese nombre");
        String nombre = sc.next();
        try {
            instituto.creaRegion(codigo, nombre);
            System.out.println("Región creada exitosamente.");
        } catch (MeteorologiaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void crearComuna(){

        System.out.println("Ingrese codigo");
        int cod = sc.nextInt();
        sc.nextLine();
        System.out.println("ingrese nombre");
        String nom = sc.nextLine();
        System.out.println("ingrese Codigo de la región");
        int codReg = sc.nextInt();
        try {
            instituto.creaComuna(cod, nom, codReg);
            System.out.println("Comuna creada exitosamente.");
        } catch (MeteorologiaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void crearEstacionMeteorologica(){
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

        try {
            instituto.creaEstacion(cod, nom, lon, lat, alt, codReg, codCom);
            System.out.println("Estación creada exitosamente.");
        } catch (MeteorologiaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void instalarSensor() {
        System.out.println("\nINSTALAR SENSOR");
        System.out.println("--------------------------------------------------");
        System.out.print("Código de estación: ");
        String codEstacion = sc.nextLine();
        System.out.print("Código de sensor: ");
        String codSensor = sc.nextLine();
        System.out.print("Marca: ");
        String marca = sc.nextLine();
        System.out.print("Modelo: ");
        String modelo = sc.nextLine();
        System.out.print("Tipo de sensor (HUMEDAD, TEMPERATURA, PRESION, VIENTO, PRECIPITACION): ");
        TipoSensor tipo = TipoSensor.valueOf(sc.nextLine().toUpperCase());

        try {
            controlador.instalaSensor(codSensor, marca, modelo, tipo, codEstacion);
            System.out.println("> Sensor instalado correctamente.");
        } catch (MeteorologiaException e) {
            System.out.println("> Error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("> Error: Tipo de sensor no válido.");
        }
    }

    private void registrarMedicion() {
        System.out.println("\nREGISTRAR MEDICIÓN");
        System.out.println("--------------------------------------------------");
        System.out.print("Código de estación: ");
        String codEstacion = sc.nextLine();
        System.out.print("Código de sensor: ");
        String codSensor = sc.nextLine();
        System.out.print("Fecha y hora [dd/MM/yyyy HH:mm]: ");
        String fechaStr = sc.nextLine();
        System.out.print("Valor: ");
        float valor = Float.parseFloat(sc.nextLine());

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime fechaHora = LocalDateTime.parse(fechaStr, formatter);

            controlador.registraMedicion(fechaHora, valor, codEstacion, codSensor);
            System.out.println("> Medición registrada correctamente.");
        }catch (DateTimeParseException e) {
            System.out.println("> Error: Formato de fecha u hora incorrecto. Debe ser dd/MM/yyyy HH:mm.");
        }catch (NumberFormatException e) {
            System.out.println("> Error: El valor ingresado debe ser un número decimal valido");
        }catch (MeteorologiaException e) {
            System.out.println("> Error: " + e.getMessage());
        }
    }

    private void menuListados(){
        int opcion= 0;
        do{
            System.out.println("GENERAR LISTADOS");
            System.out.println("================================");
            System.out.println("1. Listar regiones");
            System.out.println("2. Listar comunas");
            System.out.println("3. Listar estaciones meteorológicas");
            System.out.println("4. Listar sensores");
            System.out.println("5. Listar mediciones");
            System.out.println("6. Volver al menú principal");
            System.out.print("Opción: ");
            opcion= sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1:
                    listarRegiones();
                    break;
                case 2:
                    listarComunas();
                    break;
                case 3:
                    listarEstaciones();
                    break;
                case 4:
                    listarSensores();
                    break;
                case 5:
                    listarMediciones();
                    break;
                case 6:
                    System.out.println("Volviendo al menú principal...");
                    break;
                default:
                    System.out.println("Opción inválida. Ingrese un número entre 1 y 6.");
            }
        }while (opcion != 6);
    }
    private void listarRegiones() {
        System.out.println("REGIONES");
        System.out.println("------------------------------------------------------------------");
        String[][] regiones = instituto.listaRegiones();
        if (regiones == null || regiones.length == 0) {
            System.out.println("No existen regiones registradas.");
            return;
        }

        System.out.printf("%-10s %-25s %-15s %-15s%n", "CÓDIGO", "NOMBRE", "CANT. COMUNAS", "CANT. ESTACIONES");
        System.out.println("------------------------------------------------------------------");
        for (String[] fila : regiones) {
            System.out.printf("%-10s %-25s %-15s %-15s%n", fila[0], fila[1], fila[2], fila[3]);
        }
    }
    private void listarComunas() {
        System.out.println("COMUNAS");
        System.out.println("---------------------------------------------------------------------------------------------------");

        String[][] comunas = instituto.listaComunas();
        if (comunas == null || comunas.length == 0) {
            System.out.println("No existen comunas registradas.");
            return;
        }

        System.out.printf("%-10s %-25s %-20s %-20s %-25s%n", "CÓDIGO", "NOMBRE", "NOMBRE REGIÓN", "CANT. ESTACIONES", "CANT. ESTACIONES ACTIVAS");
        System.out.println("---------------------------------------------------------------------------------------------------");
        for (String[] fila : comunas) {
            System.out.printf("%-10s %-25s %-20s %-20s %-25s%n", fila[0], fila[1], fila[2], fila[3], fila[4]);
        }
    }

    private void listarEstaciones() {
        System.out.print("Ingrese código de región: ");
        int codRegion = sc.nextInt();
        sc.nextLine();

        System.out.print("Ingrese código de comuna: ");
        int codComuna = sc.nextInt();
        sc.nextLine();


        try {
            String[][] datos = instituto.listaEstaciones(codRegion, codComuna);

            System.out.println("Estaciones de la comuna " + codComuna);

            if (datos.length == 0) {
                System.out.println("No existen estaciones registradas para esta búsqueda.");
            } else {
                System.out.println("-------------------------------------------------------------------------------------------------");
                System.out.printf("%-15s %-20s %-30s %-10s %s%n", "CÓDIGO", "NOMBRE", "UBICACIÓN", "ESTADO", "SENSORES OPERATIVOS");

                for (int i = 0; i < datos.length; i++) {
                    System.out.printf("%-15s %-20s %-30s %-10s %s%n", datos[i][0], datos[i][1], datos[i][2], datos[i][3], datos[i][4]);
                }
                System.out.println("-------------------------------------------------------------------------------------------------");
            }
        } catch (MeteorologiaException e) {
            System.out.println("Error: " + e.getMessage());
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

        System.out.println("Mediciones del Modelo.Sensor " + codSensor);
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
    private void listarResumenMediciones() {
        System.out.println("\nRESUMEN ESTADÍSTICO DE UN SENSOR");
        System.out.println("--------------------------------------------------");
        System.out.print("Código de estación: ");
        String codEstacion = sc.nextLine();
        System.out.print("Código de sensor  : ");
        String codSensor = sc.nextLine();
        System.out.print("Inicio [dd/MM/yyyy HH:mm]: ");
        String inicioStr = sc.nextLine();
        System.out.print("Fin    [dd/MM/yyyy HH:mm]: ");
        String finStr = sc.nextLine();

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime inicio = LocalDateTime.parse(inicioStr, formatter);
            LocalDateTime fin = LocalDateTime.parse(finStr, formatter);

            String[] R = controlador.listaResumenMediciones(codEstacion, codSensor, inicio, fin);

            if (R.length == 0) {
                System.out.println("> El sensor "+codSensor+" de la estación "+codEstacion+" no registra mediciones en el período indicado");
                return;
            }

            System.out.println("\nRESUMEN ESTADÍSTICO DEL SENSOR " + codSensor + " (" + codEstacion + ")");
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("Período: " + inicioStr + " a " + finStr);
            System.out.printf("Tipo de sensor       : %s\n", R[0]);
            System.out.printf("Cantidad de mediciones: %s\n", R[1]);
            System.out.printf("Mínimo               : %-10s (%s)\n", R[2], R[3]);
            System.out.printf("Máximo               : %-10s (%s)\n", R[4], R[5]);
            System.out.printf("Promedio             : %s\n", R[6]);

        } catch (DateTimeParseException e) {
            System.out.println("> Error: Formato de fecha incorrecto.");
        } catch (MeteorologiaException e) {
            System.out.println("> Error: " + e.getMessage());
        }
    }
    private void listarAlertas() {
        System.out.println("\nALERTAS DE UNA ESTACIÓN");
        System.out.println("--------------------------------------------------");
        System.out.print("Código de estación: ");
        String codEstacion = sc.nextLine();
        System.out.print("Inicio [dd/MM/yyyy HH:mm]: ");
        String inicioStr = sc.nextLine();
        System.out.print("Fin    [dd/MM/yyyy HH:mm]: ");
        String finStr = sc.nextLine();

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime inicio = LocalDateTime.parse(inicioStr, formatter);
            LocalDateTime fin = LocalDateTime.parse(finStr, formatter);

            String[][] A = controlador.listaAlertas(codEstacion, inicio, fin);

            if (A.length == 0) {
                System.out.println("> La estación " + codEstacion + " no registra alertas en el período indicado.");
                return;
            }

            System.out.println("\nALERTAS DE LA ESTACIÓN " + codEstacion);
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("Período: " + inicioStr + " a " + finStr);
            System.out.printf("%-12s %-8s %-8s %-25s %-12s %-10s\n", "FECHA", "HORA", "SENSOR", "FENÓMENO", "VALOR", "NIVEL");

            for (String[] fila : A) {
                System.out.printf("%-12s %-8s %-8s %-25s %-12s %-10s\n", fila[0], fila[1], fila[2], fila[3], fila[4], fila[5]);
            }

        } catch (DateTimeParseException e) {
            System.out.println("> Error: Formato de fecha incorrecto.");
        } catch (MeteorologiaException e) {
            System.out.println("> Error: " + e.getMessage());
        }
    }
}