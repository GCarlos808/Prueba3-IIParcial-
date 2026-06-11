package prueba3.iiparcial;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;
import java.util.Date;

public class EmpleadoManager {

    private RandomAccessFile rcods, remps;
    
    public EmpleadoManager() {
        try {
            File mf = new File("company");
            if (!mf.exists()) {
                mf.mkdir();
            }
            
            rcods = new RandomAccessFile("company/codigo.emp", "rw");
            remps = new RandomAccessFile("company/empleado.emp", "rw");
            initCodes();
            
        } catch (IOException e) {
            System.out.println("Error al inicializar archivos: " + e.getMessage());
        }
    }
    
    private void initCodes() throws IOException {
        if (rcods.length() == 0) {
            rcods.writeInt(1);
        }
    }
    
    private int getCode() throws IOException {
        rcods.seek(0);
        int code = rcods.readInt();
        rcods.seek(0);
        rcods.writeInt(code + 1);
        return code;
    }
    
    public void addEmployee(String name, double salary) throws IOException {
        remps.seek(remps.length());
        int code = getCode();
        remps.writeInt(code);
        remps.writeUTF(name);
        remps.writeDouble(salary);
        remps.writeLong(Calendar.getInstance().getTimeInMillis());
        remps.writeLong(0);
        
        createEmployeeFolder(code);
        System.out.println("Empleado registrado con éxito. Código asignado: " + code);
    }
    
    private String employeeFolder(int code) {
        return "company/empleados" + code;
    }
    
    private RandomAccessFile salesFileFor(int code) throws IOException {
        String dirPadre = employeeFolder(code);
        int yearActual = Calendar.getInstance().get(Calendar.YEAR);
        String path = dirPadre + "/ventas" + yearActual + ".emp";
        return new RandomAccessFile(path, "rw");
    }
    
    private void createSalesFileFor(int code) throws IOException {
        RandomAccessFile ryear = salesFileFor(code);
        if (ryear.length() == 0) {
            for (int mes = 0; mes < 12; mes++) {
                ryear.writeDouble(0);
                ryear.writeBoolean(false);
            }
        }
        ryear.close();
    }
    
    private RandomAccessFile billsFileFor(int code) throws IOException {
        String dirPadre = employeeFolder(code);
        String path = dirPadre + "/recibos.emp";
        return new RandomAccessFile(path, "rw");
    }
    
    private void createEmployeeFolder(int code) throws IOException {
        File edir = new File(employeeFolder(code));
        if (!edir.exists()) {
            edir.mkdir();
        }
        createSalesFileFor(code);
    }
    
    public void employeeList() throws IOException {
        remps.seek(0);
        while (remps.getFilePointer() < remps.length()) {
            int code = remps.readInt();
            String name = remps.readUTF();
            double sal = remps.readDouble();
            Date fecha = new Date(remps.readLong());
            long fechaDespido = remps.readLong();
            
            if (fechaDespido == 0) {
                System.out.println(code + ". " + name + " - LPS. " + sal + " - Contratado el: " + fecha);
            }
        }
    }
    
    private boolean isEmployeeActive(int code) throws IOException {
        remps.seek(0);
        
        while (remps.getFilePointer() < remps.length()) {
            int codeI = remps.readInt();
            
            long posNombre = remps.getFilePointer();
            remps.readUTF();
            remps.skipBytes(16);
            long fechaDespido = remps.readLong();
            
            if (codeI == code && fechaDespido == 0) {
                remps.seek(posNombre);
                return true;
            }
        }
        return false;
    }
    
    public boolean fireEmployee(int code) throws IOException {
        if (isEmployeeActive(code)) {
            String name = remps.readUTF();
            remps.skipBytes(16);
            remps.writeLong(new Date().getTime());
            System.out.println("Despidiendo a " + name);
            return true;
        }
        System.out.println("No se pudo despedir (Empleado no activo o no existe).");
        return false;
    }
    
    public void addSaleToEmployee(int code, double monto) throws IOException {
        if (!isEmployeeActive(code)) {
            System.out.println("Empleado no encontrado o no está activo.");
            return;
        }
        
        int mesActual = Calendar.getInstance().get(Calendar.MONTH);
        long posicion = (long) mesActual * 9;
        
        RandomAccessFile rventas = salesFileFor(code);
        rventas.seek(posicion);
        double montoActual = rventas.readDouble();
        rventas.seek(posicion);
        rventas.writeDouble(montoActual + monto);
        rventas.close();
        System.out.println("Venta de Lps. " + monto + " registrada al empleado " + code);
    }
    
    public boolean isEmployeePayed(int code) throws IOException {
        RandomAccessFile rventas = salesFileFor(code);
        int mesActual = Calendar.getInstance().get(Calendar.MONTH);
        long posicion = (long) mesActual * 9 + 8;

        rventas.seek(posicion);
        boolean pagado = rventas.readBoolean();
        rventas.close();

        return pagado;
    }
    
    public void payEmployee(int code) throws IOException {
        if (!isEmployeeActive(code)) {
            System.out.println("No se pudo pagar (empleado no activo).");
            return;
        }
        
        if (isEmployeePayed(code)) {
            System.out.println("No se pudo pagar (ya fue pagado este mes).");
            return;
        }
        String name = remps.readUTF();
        double salarioBase = remps.readDouble();

        Calendar cal = Calendar.getInstance();
        int yearActual = cal.get(Calendar.YEAR);
        int mesActual = cal.get(Calendar.MONTH);

        RandomAccessFile rventas = salesFileFor(code);
        long posVentas = (long) mesActual * 9;
        rventas.seek(posVentas);
        double ventas = rventas.readDouble();
        
        double sueldo = salarioBase + (ventas * 0.10);
        double deduccion = sueldo * 0.035;
        double total = sueldo - deduccion;
        
        RandomAccessFile rrecibos = billsFileFor(code);
        rrecibos.seek(rrecibos.length());
        rrecibos.writeLong(new Date().getTime());
        rrecibos.writeDouble(sueldo);
        rrecibos.writeDouble(deduccion);
        rrecibos.writeInt(yearActual);
        rrecibos.writeInt(mesActual);
        rrecibos.close();
        
        rventas.seek(posVentas + 8);
        rventas.writeBoolean(true);
        rventas.close();

        System.out.printf("Al empleado %s se le pagó Lps. %.2f%n", name, total);
    }

    public void printEmployee(int code) throws IOException {
        if (!isEmployeeActive(code)) {
            System.out.println("Empleado con código " + code + " no encontrado o no activo.");
            return;
        }
        
        String name = remps.readUTF();
        double salario = remps.readDouble();
        Date fechaContr = new Date(remps.readLong());
        System.out.println("Código: " + code + "  Nombre: " + name + "  Salario Base: Lps. " + salario + "  Fecha de contratación: " + fechaContr);

        RandomAccessFile rventas = salesFileFor(code);
        rventas.seek(0);
        
        double totalVentas = 0;
        System.out.println("--- Historial de Ventas Mensuales ---");
        for (int mes = 0; mes < 12; mes++) {
            double montoMes = rventas.readDouble();
            rventas.readBoolean();
            System.out.println("Mes " + (mes + 1) + " : Lps. " + montoMes);
            totalVentas += montoMes;
        }
        
        rventas.close();
        
        System.out.println("Total de ventas del año: Lps. " + totalVentas);
        
        RandomAccessFile rrecibos = billsFileFor(code);
        int totalRecibos = (int) (rrecibos.length() / 32);
        rrecibos.close();
        
        System.out.println("Total de pagos realizados: " + totalRecibos);
    }
}