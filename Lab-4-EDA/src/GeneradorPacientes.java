import java.util.*;
import java.io.FileWriter;
import java.io.IOException;
public class GeneradorPacientes {
    private static final String[] Nombres = {"Nicolas", "Isaac", "Eduardo", "Benjamin", "Diego", "Vicente", "Rodrigo", "Joaquin", "Jeremias", "Matias", "Martin"};
    private static final String[] Apellidos = {"Allende", "Uribe", "Quezada", "Oyarzun", "Valenzuela", "Estay", "Artes", "Utreras", "Saldias", "Pinochet", "Matthei"};
    private static final String[] Areas = {"SAPU", "Urgencias_Adultos", "Urgencias_Infantiles"};
    private static final int[] Categorias = {1, 2, 3, 4, 5};
    private static final double[] Probabilidades = {0.10, 0.15, 0.18, 0.27, 0.30};
    public static List<Paciente> generarPacientes(int cantidad) {
        List<Paciente> pacientes = new ArrayList<>();
        long timestampInicio = System.currentTimeMillis() / 1000;
        Random random = new Random();
        for (int i = 0; i < cantidad; i++) {
            String nombre = Nombres[random.nextInt(Nombres.length)];
            String apellido = Apellidos[random.nextInt(Apellidos.length)];
            String rut = i + "-" + (i + 1);
            int categoria = generarCategoria();
            String estado = "En espera";
            long tiempoLlegada = timestampInicio + (i * 600);
            String area = Areas[random.nextInt(Areas.length)];
            Paciente paciente = new Paciente(nombre, apellido, rut, categoria, estado, tiempoLlegada, area);
            pacientes.add(paciente);
        }
        return pacientes;
    }
    private static int generarCategoria() {
        double rand = Math.random();
        double acumulado = 0.0;
        for (int i = 0; i < Categorias.length; i++) {
            acumulado += Probabilidades[i];
            if (rand <= acumulado) {
                return Categorias[i];
            }
        }
        return 5;
    }
    public static void guardarPacientesEnArchivo(List<Paciente> pacientes, String nombreArchivo) {
        try (FileWriter writer = new FileWriter(nombreArchivo)) {
            for (Paciente p : pacientes) {
                writer.write(
                        p.getNombre() + "," + p.getApellido() + "," + p.getRut() + "," + p.getCategoria() + "," + p.getEstado() + "," + p.getTiempoLlegada() + "," + p.getArea() + "\n");
            }
            System.out.println("Archivo guardado exitosamente: " + nombreArchivo);
        } catch (IOException e) {
            System.out.println("Error al guardar el archivo: " + e.getMessage());
        }
    }
    public static void main(String[] args) {
        List<Paciente> pacientes = generarPacientes(144);
        guardarPacientesEnArchivo(pacientes, "pepesillos.csv");
    }
}