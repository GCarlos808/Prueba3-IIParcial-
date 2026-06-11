package prueba3.iiparcial;

import java.io.IOException;
import java.util.Scanner;

public class Empresa {

    public static void main(String[] args) { 
        
        Scanner lea = new Scanner(System.in);
        EmpleadoManager mg = new EmpleadoManager();
        
        int opcion = 0;
        
        do {
            System.out.println("\nMENU\n");
            System.out.println("1- Agregar Empleado");
            System.out.println("2- Listar Empleados Contratados");
            System.out.println("3- Agregar Venta a Empleado");
            System.out.println("4- Pagar Empleado");
            System.out.println("5- Despedir Empleado");
            System.out.println("6- Reporte de Empleado");
            System.out.println("7- Salir");
            System.out.print("Escoja una opcion: ");
            
            try {
                opcion = Integer.parseInt(lea.nextLine().trim());

                switch (opcion) {
                    
                    case 1:
                        System.out.print("Nombre del empleado: ");
                        String nombre = lea.nextLine().trim();
                        System.out.print("Salario base: ");
                        double salario = Double.parseDouble(lea.nextLine().trim());
                        mg.addEmployee(nombre, salario);
                        break;
                        
                    case 2:
                        System.out.println("\n--- Empleados Contratados ---");
                        mg.employeeList();
                        break;
                        
                    case 3:
                        System.out.print("Codigo del empleado: ");
                        int codVenta = Integer.parseInt(lea.nextLine().trim());
                        System.out.print("Monto de la venta: ");
                        double monto = Double.parseDouble(lea.nextLine().trim());
                        mg.addSaleToEmployee(codVenta, monto);
                        break;
                        
                    case 4:
                        System.out.print("Codigo del empleado a pagar: ");
                        int codPago = Integer.parseInt(lea.nextLine().trim());
                        mg.payEmployee(codPago);
                        break;
                        
                    case 5:
                        System.out.print("Codigo del empleado a despedir: ");
                        int codDespido = Integer.parseInt(lea.nextLine().trim());
                        mg.fireEmployee(codDespido);
                        break;
                        
                    case 6:
                        System.out.print("Codigo del empleado: ");
                        int codReporte = Integer.parseInt(lea.nextLine().trim());
                        mg.printEmployee(codReporte);
                        break;
                        
                    case 7:
                        System.out.println("EXIT!");
                        break;
                        
                    default:
                        System.out.println("Esa no es una opcion valida. Intente de nuevo.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada no permitida: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            
        } while (opcion != 7);
        
        lea.close();
    }
}
