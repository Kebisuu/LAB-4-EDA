import java.io.*;
import java.util.*;
public class SimulacionUrgencias {
    private static final int DURACION_SIMULACION = 24 * 60;
    private static final int[] TIEMPO_MAXIMO_ESPERA = {0, 10, 30, 60, 120, 180};
    private PriorityQueue<Paciente> cola = new PriorityQueue<>(
            Comparator.comparingInt(Paciente::getCategoria)
                    .thenComparingLong(Paciente::getTiempoLlegada)
    );
    private List<PacienteAtendido> historialAtendidos = new ArrayList<>();
    private List<PacienteAtendido> historialExcedidos = new ArrayList<>();
    private Map<Integer, List<Long>> tiemposPorCategoria = new HashMap<>();
    private Map<Integer, Integer> cantidadPorCategoria = new HashMap<>();
    private List<Paciente> pacientesFuente;
    public SimulacionUrgencias(List<Paciente> pacientesFuente) {
        this.pacientesFuente = pacientesFuente;
    }
    public void simular(int pacientesPorDia) {
        int idxNuevoPaciente = 0;
        int acumulados = 0;
        for (int minuto = 0; minuto < DURACION_SIMULACION ; minuto++) {
            if (minuto % 10 == 0 && idxNuevoPaciente < pacientesPorDia && idxNuevoPaciente < pacientesFuente.size()) {
                Paciente nuevo = pacientesFuente.get(idxNuevoPaciente++);
                nuevo.setTiempoLlegada(minuto);
                cola.offer(nuevo);
                acumulados++;
            }
            List<Paciente> urgentes = getExcedidosDeCola(minuto);
            for (Paciente urgente : urgentes) {
                if (urgente.getTiempoLlegada() < minuto) {
                    atenderPaciente(urgente, minuto);
                    cola.remove(urgente);
                    acumulados = Math.max(0, acumulados-1);
                }
            }
            if (acumulados >= 3) {
                for (int i = 0; i < 2; i++) {
                    atenderSiguientePaciente(minuto);
                }
                acumulados = 0;
            }
            if (minuto % 15 == 0) {
                atenderSiguientePaciente(minuto);
            }
        }
        mostrarResultados();
    }
    private void atenderSiguientePaciente(int minutoActual) {
        Iterator<Paciente> it = cola.iterator();
        while (it.hasNext()) {
            Paciente p = it.next();
            if (p.getTiempoLlegada() < minutoActual) {
                atenderPaciente(p, minutoActual);
                it.remove();
                break;
            }
        }
    }
    private List<Paciente> getExcedidosDeCola(int minutoActual) {
        List<Paciente> urgentes = new ArrayList<>();
        for (Paciente p : cola) {
            int cat = p.getCategoria();
            int espera = minutoActual - (int)p.getTiempoLlegada();
            if (cat >= 1 && cat <= 5 && espera > TIEMPO_MAXIMO_ESPERA[cat]) {
                urgentes.add(p);
            }
        }
        return urgentes;
    }
    private void atenderPaciente(Paciente p, int minutoActual) {
        int cat = p.getCategoria();
        int espera = minutoActual - (int)p.getTiempoLlegada();
        p.setEstado("Atendido");
        historialAtendidos.add(new PacienteAtendido(p, espera));
        tiemposPorCategoria.computeIfAbsent(cat, k -> new ArrayList<>()).add((long)espera);
        cantidadPorCategoria.put(cat, cantidadPorCategoria.getOrDefault(cat, 0) + 1);
        if (cat >= 1 && cat <= 5 && espera > TIEMPO_MAXIMO_ESPERA[cat]) {
            historialExcedidos.add(new PacienteAtendido(p, espera));
        }
    }
    private void mostrarResultados() {
        try (PrintWriter out = new PrintWriter("resultados_simulacion.txt")) {
            out.println("--- Pacientes Atendidos ---");
            for (PacienteAtendido pa : historialAtendidos) {
                Paciente p = pa.paciente;
                out.printf("Nombre: %s %s | Rut: %s | Cat: %d | Área: %s | Espera: %d min\n",
                        p.getNombre(), p.getApellido(), p.getRut(), p.getCategoria(),
                        p.getArea(), pa.tiempoEspera);
            }
            out.println("\n--- Estadísticas por Categoría ---");
            for (int cat = 1; cat <= 5; cat++) {
                List<Long> tiempos = tiemposPorCategoria.getOrDefault(cat, new ArrayList<>());
                int count = cantidadPorCategoria.getOrDefault(cat, 0);
                double prom = tiempos.isEmpty() ? 0 : tiempos.stream().mapToLong(Long::longValue).average().orElse(0);
                out.printf("Categoría %d: %d pacientes, espera promedio: %.2f min\n", cat, count, prom);
            }
            out.println("\n--- Pacientes que excedieron el tiempo máximo ---");
            if (historialExcedidos.isEmpty()) {
                out.println("Ningún paciente excedió su tiempo máximo de espera.");
            } else {
                for (PacienteAtendido pa : historialExcedidos) {
                    Paciente p = pa.paciente;
                    out.printf("Nombre: %s %s | Rut: %s | Cat: %d | Área: %s | Espera: %d min (máx: %d)\n",
                            p.getNombre(), p.getApellido(), p.getRut(), p.getCategoria(),
                            p.getArea(), pa.tiempoEspera, TIEMPO_MAXIMO_ESPERA[p.getCategoria()]);
                }
            }
            System.out.println("Resultados guardados en resultados_simulacion.txt");
        } catch (IOException e) {
            System.out.println("Error al escribir resultados: " + e.getMessage());
        }
    }
    private static class PacienteAtendido {
        Paciente paciente;
        int tiempoEspera;
        PacienteAtendido(Paciente paciente, int tiempoEspera) {
            this.paciente = paciente;
            this.tiempoEspera = tiempoEspera;
        }
    }
    public static void main(String[] args) {
        List<Paciente> pacientes = cargarPacientesDesdeArchivo("pepesillos.csv");
        if (pacientes.isEmpty()) {
            System.out.println("No se encontraron pacientes en el archivo.");
            return;}
        SimulacionUrgencias simulador = new SimulacionUrgencias(pacientes);
        simulador.simular(144);
    }
    public static List<Paciente> cargarPacientesDesdeArchivo(String nombreArchivo) {
        List<Paciente> lista = new ArrayList<>();
        File archivo = new File(nombreArchivo);
        if (!archivo.exists()) {
            System.out.println("Error: Archivo no encontrado. Verifica que '" + nombreArchivo + "' existe en la carpeta correcta.");
            return lista;}
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split("\\s*,\\s*");
                if (datos.length < 7) continue;
                try {
                    String nombre = datos[0].trim();
                    String apellido = datos[1].trim();
                    String rut = datos[2].trim();
                    int categoria = Integer.parseInt(datos[3].trim());
                    String estado = datos[4].trim();
                    long tiempoLlegada = Long.parseLong(datos[5].trim());
                    String area = datos[6].trim();
                    Paciente paciente = new Paciente(nombre, apellido, rut, categoria, estado, tiempoLlegada, area);
                    lista.add(paciente);
                } catch (NumberFormatException e) {
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
        return lista;
    }
}