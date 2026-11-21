import java.util.Scanner;

public class InterfazUsuario{
    Scanner sc = new Scanner(System.in);
    private InstitutoMeteorologia instituto;

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
        System.out.println("\nREGIONES");
        System.out.println("------------------------------------------------------------------");
        String[][] regiones = instituto.listaRegiones();
        if (regiones == null || regiones.length == 0) {
            System.out.println("No existen regiones registradas.");
            return;
        }

        System.out.printf("%-10s %-25s %-15s %-15s\n", "CÓDIGO", "NOMBRE", "CANT. COMUNAS", "CANT. ESTACIONES");
        System.out.println("------------------------------------------------------------------");
        for (String[] fila : regiones) {
            System.out.printf("%-10s %-25s %-15s %-15s\n", fila[0], fila[1], fila[2], fila[3]);
        }
    }
    private void listarComunas(){
        System.out.println("\nCOMUNAS");
        System.out.println("---------------------------------------------------------------------------------------------------");

        String[][] comunas = instituto.listaComunas();
        if (comunas == null || comunas.length == 0) {
            System.out.println("No existen comunas registradas.");
            return;
        }

        System.out.printf("%-10s %-25s %-20s %-20s %-25s\n", "CÓDIGO", "NOMBRE", "NOMBRE REGIÓN", "CANT. ESTACIONES", "CANT. ESTACIONES ACTIVAS");
        System.out.println("---------------------------------------------------------------------------------------------------");
        for (String[] fila : comunas) {
            System.out.printf("%-10s %-25s %-20s %-20s %-25s\n", fila[0], fila[1], fila[2], fila[3], fila[4]);
        }
    }
}
