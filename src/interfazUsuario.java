import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class InterfazUsuario {
    Scanner sc = new Scanner(System.in);
    private InstitutoMeteorologia instituto;

    public static void main(String[] args){

        InterfazUsuario interfaz = new InterfazUsuario();

        interfaz.menuPrincipal();

    }
    private void menuPrincipal(){
        instituto = new InstitutoMeteorologia();

        int opcion;

        do{
            System.out.println("1. Crear región");
            System.out.println("2. Crear comuna");
            System.out.println("3. Crear estación meteorológica");
            System.out.println("4. Instalar sensor");
            System.out.println("5. Registrar medición");
            System.out.println("6. Generar listados");
            System.out.println("7. Salir");

            opcion = sc.nextInt();
            sc.nextLine();

            while (opcion < 1 || opcion > 7) {

                System.out.print("Opción inválida. Ingrese nuevamente: ");

                opcion = sc.nextInt();
                sc.nextLine();
            }

            switch(opcion){
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

    private void crearRegion(){

        System.out.println("ingrese codigo");
        int codigo = sc.nextInt();
        System.out.println("ingrese nombre");
        String nombre = sc.next();
        boolean x = instituto.creaRegion(codigo, nombre);
        if(x){
            System.out.println("Region creada exitosamente..");
        }
        else System.out.println("No se pudo crear la Region, codigo o nombre ya existe...");
    }

    private void crearComuna(){

        System.out.println(" Ingrese codigo");
        int cod = sc.nextInt();
        System.out.println("ingrese nombre");
        String nom = sc.next();
        System.out.println("ingrese Codigo de la región");
        int codReg = sc.nextInt();
        boolean x = instituto.creaComuna(cod,nom,codReg);
        if(x){
            System.out.println("Comuna Creada exitosamente..");
        }
        else System.out.println("No se pudo crear la Comuna, Region, codigo o nombre ya existe...");
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

        boolean x = instituto.creaEstacion(cod,nom,lon,lat,alt,codReg,codCom);

        if(x){
            System.out.println("Estacion creada exitosamente");
        } else {
            System.out.println("No se pudo crear la Estación...");
        }
    }

    private void instalarSensor(){
        System.out.println("Ingrese codigo");
        String cod = sc.next();
        System.out.println("Ingrese marca");
        String marca = sc.next();
        System.out.println("Ingrese modelo");
        String modelo = sc.next();
        String res;
        TipoSensor tipo = null;
        boolean x = false;
        do{
            System.out.println("Ingrese el tipo de sensor (humedad, temperatura, presion, viento, precipitacion)");
            res = sc.next();

            switch(res){
                case "humedad":
                    tipo = TipoSensor.HUMEDAD;
                    x = true;
                    break;
                case "temperatura":
                    tipo = TipoSensor.TEMPERATURA;
                    x = true;
                    break;
                case "presion":
                    tipo = TipoSensor.PRESION;
                    x = true;
                    break;
                case "viento":
                    tipo = TipoSensor.VIENTO;
                    x = true;
                    break;
                case "precipitacion":
                    tipo = TipoSensor.PRECIPITACION;
                    x = true;
                    break;
                default:
                    System.out.println("No valido");
                    x = false;

            }
        }while(x == false );

        System.out.println("Ingresa codigo de estacióm");
        String codigoEstacion = sc.next();

        x = instituto.instalaSensor(cod,marca,modelo,tipo,codigoEstacion);
        if(x){
            System.out.println("Sensor instalado correctamente");
        }else{
            System.out.println("algun dato ya esta registrado...");
        }
    }

    private void registrarMedicion(){
        System.out.println("Ingrese codigo de Estacion");
        String codEstacion = sc.next();

        sc.nextLine();

        System.out.print("Ingrese Fecha y hora (dd/MM/yyyy HH:mm): ");
        String fechaTexto = sc.nextLine();

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime fechaHora = LocalDateTime.parse(fechaTexto, formato);

        System.out.println("Ingrese valor");
        Float valor = sc.nextFloat();

        System.out.println("Ingrese codigo de Sensor");
        String codSensor = sc.next();

        boolean x = instituto.registraMedicion(fechaHora, valor, codEstacion, codSensor);
        if(x){
            System.out.println("Medición registrada correctamente");
        }else{
            System.out.println("algun dato ya esta registrado...");
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

        System.out.printf("%-10s %-25s %-15s %-15s", "CÓDIGO", "NOMBRE", "CANT. COMUNAS", "CANT. ESTACIONES");
        System.out.println("------------------------------------------------------------------");
        for (String[] fila : regiones) {
            System.out.printf("%-10s %-25s %-15s %-15s", fila[0], fila[1], fila[2], fila[3]);
        }
    }
    private void listarComunas(){
        System.out.println("COMUNAS");
        System.out.println("---------------------------------------------------------------------------------------------------");

        String[][] comunas = instituto.listaComunas();
        if (comunas == null || comunas.length == 0) {
            System.out.println("No existen comunas registradas.");
            return;
        }

        System.out.printf("%-10s %-25s %-20s %-20s %-25s", "CÓDIGO", "NOMBRE", "NOMBRE REGIÓN", "CANT. ESTACIONES", "CANT. ESTACIONES ACTIVAS");
        System.out.println("---------------------------------------------------------------------------------------------------");
        for (String[] fila : comunas) {
            System.out.printf("%-10s %-25s %-20s %-20s %-25s", fila[0], fila[1], fila[2], fila[3], fila[4]);
        }
    }

    private void listarEstaciones() {
        System.out.print("Ingrese código de región: ");
        int codRegion = sc.nextInt();
        sc.nextLine();

        System.out.print("Ingrese código de comuna: ");
        int codComuna = sc.nextInt();
        sc.nextLine();


        String[][] datos = instituto.listaEstaciones(codRegion, codComuna);

        System.out.println("Estaciones de la comuna " + codComuna);

        if (datos.length == 0) {
            System.out.println("> No existen estaciones registradas para esta búsqueda.");
        } else {
            System.out.println("-------------------------------------------------------------------------------------------------");


            System.out.printf("%-15s %-20s %-30s %-10s %s%n", "CÓDIGO", "NOMBRE", "UBICACIÓN", "ESTADO", "SENSORES OPERATIVOS");

            for (int i = 0; i < datos.length; i++) {
                System.out.printf("%-15s %-20s %-30s %-10s %s%n", datos[i][0], datos[i][1], datos[i][2], datos[i][3], datos[i][4]);
            }
            System.out.println("-------------------------------------------------------------------------------------------------");
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