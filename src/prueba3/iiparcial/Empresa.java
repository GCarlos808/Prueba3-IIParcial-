package prueba3.iiparcial;

import java.util.Scanner;

public class Empresa {

    public static void main(){
        Scanner lea = new Scanner(System.in);
        EmpleadoManager mg = new EmpleadoManager();
        
        int opcion = 0;
        
        System.out.println("\nMENU\n");
        System.out.println("1- Agregar Empleado");
        System.out.println("2- Listar Empleados Contratados");
        System.out.println("3- Agregar Venta o Empleado");
        System.out.println("4- Pagar Empleado");
        System.out.println("5- Despedir Empleado");
        System.out.println("6- Salir");
        System.out.println("Escoja una opción");
    }
}
