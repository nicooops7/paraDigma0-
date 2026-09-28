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
        System.out.println("No se pudo crear la Region, codigo o nombre ya existe...");
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
        System.out.println("No se pudo crear la Comuna, Region, codigo o nombre ya existe...");
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
                    tipo = TipoSensor.PREPICITACION;
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

    }
    public void listarRegiones(){

    }
    public void listarComunas(){

    }
    public void listarEstaciones(){

    }
    public void listarSensores(){

    }
    public void listarMediciones(){

    }


}
