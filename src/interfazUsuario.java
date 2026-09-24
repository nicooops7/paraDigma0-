import java.util.*;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class interfazUsuario {
    Scanner sc = new Scanner(System.in);
    private ArrayList<institutoMetereologia> metereologias;
    private institutoMetereologia actual;

    public static void main(String[] args){
        menuPrincipal();





    }
    private void menuPrincipal(){
        System.out.println("1. Crear región");
        System.out.println("2. Crear comuna");
        System.out.println("3. Crear estación meteorológica");
        System.out.println("4. Instalar sensor");
        System.out.println("5. Registrar medición");
        System.out.println("6. Generar listados");
        System.out.println("7. Salir");
    }
    private void crearRegion(){

        System.out.println("ingrese codigo y nombre");
        int codigo = sc.nextInt();
        String nombre = sc.next();
        boolean x = actual.creaRegion(codigo, nombre);
        if(x){
            System.out.println("Region creada exitosamente..");
        }
        System.out.println("Region ya existente...");

    }
    private void crearComuna(){

        System.out.println(" Ingrese codigo, nombre y codigo de región");
        int cod = sc.nextInt();
        String nom = sc.next();
        int codReg = sc.nextInt();
        boolean x = actual.creaComuna(cod,nom,codReg);
        if(x){
            System.out.println("Comuna Creada exitosamente..");
        }
        System.out.println("Comuna ya existente");
    }
    private void crearEstacionMeteorologica(){

    }
    private void instalarSensor(){

    }
    private void registrarMedicion(){

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
