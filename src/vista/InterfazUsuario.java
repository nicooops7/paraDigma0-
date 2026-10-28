package vista;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import controlador.*;
import modelo.*;


public class InterfazUsuario {
    private static InterfazUsuario instancia;
    private Scanner sc;
    private InstitutoMeteorologia instituto;

    private InterfazUsuario(){
        sc = new Scanner(System.in);
        instituto = InstitutoMeteorologia.getInstance();
    }

    public static synchronized InterfazUsuario getInstance(){
        if (instancia == null) {
            instancia = new InterfazUsuario();
        }
        return instancia;
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

            while (!sc.hasNextInt()) {
                System.out.print("Opción inválida. Ingrese nuevamente: ");
                sc.next();
            }
            opcion = sc.nextInt();
            sc.nextLine();

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
                    if(salir()) return;
                    break;
                default:
                    System.out.println("Opción inválida. ingrese una opción entre 1 y 10.");
            }

        } while (true);

    }

    private void crearRegion(){
        System.out.println("\nCREAR REGIÓN");
        System.out.println("ingrese codigo");
        int codigo = sc.nextInt();
        sc.nextLine();
        System.out.println("ingrese nombre");
        String nombre = sc.nextLine();

        try{
            instituto.creaRegion(codigo, nombre);
            System.out.println(" Región creada exitosamente.");
        }catch(MeteorologiaException e){
            System.out.println("Error: "+e.getMessage());
        }
    }

    private void crearComuna(){
        System.out.println("\nCREAR COMUNA");
        System.out.println(" Ingrese codigo de comuna");
        int codCom = sc.nextInt();
        System.out.println("ingrese nombre");
        String nom = sc.nextLine();
        System.out.println("ingrese Codigo de la región");
        int codReg = sc.nextInt();
        try{
            instituto.creaComuna(codCom,nom,codReg);
            System.out.println("Comuna creada exitodamente.");
        }catch(MeteorologiaException e){
            System.out.println("Error: "+e.getMessage());
        }
    }

    private void crearEstacionMeteorologica(){
        System.out.println("\nCREAR ESTACIÓN METEOROLÓGICA");
        System.out.println("ingrese codigo");
        String cod = sc.nextLine();
        System.out.println("ingrese nombre");
        String nom = sc.nextLine();
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
        sc.nextLine();

        try{
            instituto.creaEstacion(cod,nom,lon,lat,alt,codReg,codCom);
            System.out.println("Estación creada exitosamente.");
        }catch(MeteorologiaException e){
            System.out.println("Error: "+e.getMessage());
        }
    }

    private void instalarSensor(){
        System.out.println("\nINSTALAR SENSOR");
        System.out.println("Ingrese codigo de estación");
        String codEst = sc.nextLine();
        System.out.print("Tipo [1 Temp. 2 Hum. 3 Presión 4 Viento 5 Precip.]: ");
        int tOpt = sc.nextInt();
        sc.nextLine();
        TipoSensor tipo = switch (tOpt) {
            case 1 -> TipoSensor.TEMPERATURA;
            case 2 -> TipoSensor.HUMEDAD;
            case 3 -> TipoSensor.PRESION;
            case 4 -> TipoSensor.VIENTO;
            case 5 -> TipoSensor.PRECIPITACION;
            default -> null;
        };

        if(tipo == null){
            System.out.println("Error: Tipo de sensor inválido.");
            return;
        }

        System.out.print("Código de sensor: ");
        String codSensor = sc.nextLine();
        System.out.print("Marca: ");
        String marca = sc.nextLine();
        System.out.print("Modelo: ");
        String modelo = sc.nextLine();

        try {
            instituto.instalaSensor(codSensor, marca, modelo, tipo, codEst);
            System.out.println("> Sensor instalado correctamente.");
        } catch (MeteorologiaException e) {
            System.out.println("> Error: " + e.getMessage());
        }

    }

    private void registrarMedicion(){
        System.out.println("\nREGISTRAR MEDICIÓN");
        System.out.println("Ingrese codigo de Estacion");
        String codEstacion = sc.nextLine();

        System.out.print("Ingrese Fecha y hora (dd/MM/yyyy HH:mm): ");
        String fechaStr = sc.nextLine();

        System.out.println("Ingrese valor");
        Float valor = sc.nextFloat();

        System.out.println("Ingrese codigo de Sensor");
        String codSensor = sc.next();
        sc.nextLine();

        try{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime fechaHora = LocalDateTime.parse(fechaStr, formatter);
            instituto.registraMedicion(fechaHora, valor, codEstacion, codSensor);
            System.out.println("Medición registrada correctamente.");
        }catch(MeteorologiaException e){
            System.out.println("Error: "+e.getMessage());
        }
    }

    private void cambiarEstadoSensor(){
        System.out.println("\nCAMBIAR ESTADO DE SENSOR");
        System.out.print("Código de estación: ");
        String codEst = sc.nextLine();
        System.out.print("Código de sensor: ");
        String codSensor = sc.nextLine();
        System.out.print("Nuevo estado [1 ACTIVO 2 INACTIVO]: ");
        int optEstado = sc.nextInt();
        sc.nextLine();
        Estado estado = (optEstado == 1) ? Estado.ACTIVO : Estado.INACTIVO;

        try {
            instituto.cambiaEstadoSensor(codEst, codSensor, estado);
            System.out.println("Estado del sensor " + codSensor + " actualizado a " + estado + ".");
        } catch (MeteorologiaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void cargarDatos(){
        System.out.println("\nCARGAR DATOS");
        try{
            instituto.cargaDatos();
            System.out.println("Datos cargados correctamente desde la carpeta datos/.");
        }catch(MeteorologiaException e){
            System.out.println("Error: "+e.getMessage());
        }
    }

    private void guardarDatos() {
        System.out.println("\nGUARDAR DATOS");
        try {
            instituto.guardaDatos();
            System.out.println("> Datos guardados correctamente en la carpeta datos/.");
        } catch (MeteorologiaException e) {
            System.out.println("> Error: " + e.getMessage());
        }
    }

    private boolean salir() {
        System.out.print("¿Desea guardar los datos antes de salir? [S/N]: ");
        String respuesta = sc.nextLine().trim();
        if (respuesta.equalsIgnoreCase("S")) {
            try {
                instituto.guardaDatos();
                System.out.println("> Datos guardados correctamente en la carpeta datos/.");
                System.out.println("> Fin del programa.");
                return true;
            } catch (MeteorologiaException e) {
                System.out.println("> Error al guardar: " + e.getMessage());
                System.out.println("Volviendo al menú principal para evitar pérdida de datos.");
                return false;
            }
        } else {
            System.out.println("> Fin del programa.");
            return true;
        }
    }

    private void menuListados() {
        int opcion;
        do {
            System.out.println("\nGENERAR LISTADOS");
            System.out.println("1. Regiones");
            System.out.println("2. Comunas");
            System.out.println("3. Estaciones de una comuna");
            System.out.println("4. Sensores de una estación");
            System.out.println("5. Mediciones de un sensor");
            System.out.println("6. Resumen estadístico de un sensor");
            System.out.println("7. Alertas de una estación");
            System.out.println("8. Volver al menú principal");
            System.out.print("Opción: ");

            while (!sc.hasNextInt()) {
                System.out.print("Opción inválida. Ingrese nuevamente: ");
                sc.next();
            }
            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1 -> listarRegiones();
                case 2 -> listarComunas();
                case 3 -> listarEstaciones();
                case 4 -> listarSensores();
                case 5 -> listarMediciones();
                case 6 -> listarResumenMediciones();
                case 7 -> listarAlertas();
                case 8 -> { return; }
                default -> System.out.println("Opción inválida.");
            }
        } while (true);
    }

    public void listarRegiones() {
        System.out.println("\nREGIONES");
        String[][] datos = instituto.listaRegiones();
        if (datos.length == 0) {
            System.out.println("No existen regiones registradas.");
            return;
        }
        System.out.printf("%-10s %-20s\n", "CÓDIGO", "NOMBRE");
        for (String[] r : datos) {
            System.out.printf("%-10s %-20s\n", r[0], r[1]);
        }
    }

    public void listarComunas() {
        System.out.println("\nCOMUNAS");
        String[][] datos = instituto.listaComunas();
        if (datos.length == 0) {
            System.out.println("No existen comunas registradas.");
            return;
        }
        System.out.printf("%-10s %-20s %-10s %-20s\n", "COD REG", "REGIÓN", "COD COM", "COMUNA");
        for (String[] c : datos) {
            System.out.printf("%-10s %-20s %-10s %-20s\n", c[0], c[1], c[2], c[3]);
        }
    }

    public void listarEstaciones() {
        System.out.println("\nESTACIONES DE UNA COMUNA");
        System.out.print("Código de región: ");
        int codReg = sc.nextInt();
        System.out.print("Código de comuna: ");
        int codCom = sc.nextInt();
        sc.nextLine();

        try {
            String[][] datos = instituto.listaEstaciones(codReg, codCom);
            if (datos.length == 0) {
                System.out.println("La comuna no registra estaciones.");
                return;
            }
            System.out.printf("%-15s %-25s %-10s\n", "CÓDIGO", "NOMBRE", "ESTADO");
            for (String[] e : datos) {
                System.out.printf("%-15s %-25s %-10s\n", e[0], e[1], e[2]);
            }
        } catch (MeteorologiaException e) {
            System.out.println("> Error: " + e.getMessage());
        }
    }

    public void listarSensores() {
        System.out.println("\nSENSORES DE UNA ESTACIÓN");
        System.out.print("Código de estación: ");
        String codEst = sc.nextLine();

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
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void listarMediciones() {
        System.out.println("\nMEDICIONES DE UN SENSOR");
        System.out.print("Código de estación: ");
        String codEst = sc.nextLine();
        System.out.print("Código de sensor: ");
        String codSensor = sc.nextLine();
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
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void listarResumenMediciones() {
        System.out.println("\nRESUMEN ESTADÍSTICO DE UN SENSOR");
        System.out.print("Código de estación: ");
        String codEst = sc.nextLine();
        System.out.print("Código de sensor: ");
        String codSensor = sc.nextLine();
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
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void listarAlertas() {
        System.out.println("\nALERTAS DE UNA ESTACIÓN");
        System.out.print("Código de estación: ");
        String codEst = sc.nextLine();
        System.out.print("Inicio [dd/MM/yyyy HH:mm]: ");
        String inicioStr = sc.nextLine();
        System.out.print("Fin [dd/MM/yyyy HH:mm]: ");
        String finStr = sc.nextLine();

        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime inicio = LocalDateTime.parse(inicioStr, fmt);
            LocalDateTime fin = LocalDateTime.parse(finStr, fmt);

            String[][] datos = instituto.listaAlertas(codEst, inicio, fin);
            if (datos.length == 0) {
                System.out.println("La estación " + codEst + " no registra alertas en el período indicado.");
                return;
            }
            System.out.println("\nALERTAS DE LA ESTACIÓN " + codEst);
            System.out.println("Período: " + inicioStr + " a " + finStr);
            System.out.printf("%-12s %-8s %-10s %-25s %-15s %-10s\n", "FECHA", "HORA", "SENSOR", "FENÓMENO", "VALOR", "NIVEL");
            for (String[] a : datos) {
                System.out.printf("%-12s %-8s %-10s %-25s %-15s %-10s\n", a[0], a[1], a[2], a[3], a[4] + " " + a[5], a[6]);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}


