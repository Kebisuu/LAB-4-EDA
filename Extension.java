import java.util.*;
import java.io.*;
public class Extension {
    private static final int DURACION_SIMULACION = 24 * 60;
    private static final int[] TIEMPO_MAXIMO_ESPERA = {0, 10, 30, 60, 120, 180};
    private static int prioridadEfectiva(Paciente p, int minutoActual) {
        int espera = minutoActual - (int) p.getTiempoLlegada();
        int mejora = espera / 60;
        return Math.max(1, p.getCategoria() - mejora);
    }
    public static void main(String[] args) {
        List<Paciente> pacientes = SimulacionUrgencias.cargarPacientesDesdeArchivo("Pacientes_24h.txt");
        long primerTimestamp = pacientes.get(0).getTiempoLlegada();
        for (Paciente p : pacientes) {
            long llegadaMin = (p.getTiempoLlegada() - primerTimestamp) / 60;
            p.setTiempoLlegada(llegadaMin);
        }
        List<Paciente> cola = new ArrayList<>(pacientes);
        List<Paciente> pacientesAtendidos = new ArrayList<>();
        Map<Integer, List<Integer>> tiemposPorCategoria = new HashMap<>();
        Map<Integer, Integer> cantidadPorCategoria = new HashMap<>();
        List<Paciente> historialExcedidos = new ArrayList<>();
        for (int minuto = 0; minuto < DURACION_SIMULACION; minuto++) {
            final int minutoActual = minuto;
            cola.sort((p1, p2) -> {
                int prio1 = prioridadEfectiva(p1, minutoActual);
                int prio2 = prioridadEfectiva(p2, minutoActual);
                if (prio1 != prio2)
                    return Integer.compare(prio1, prio2);
                return Long.compare(p1.getTiempoLlegada(), p2.getTiempoLlegada());
            });
            if (!cola.isEmpty() && cola.get(0).getTiempoLlegada() <= minuto) {
                Paciente atendido = cola.remove(0);
                atendido.setEstado("atendido");
                int cat = atendido.getCategoria();
                int espera = minuto - (int) atendido.getTiempoLlegada();
                pacientesAtendidos.add(atendido);
                tiemposPorCategoria.computeIfAbsent(cat, k -> new ArrayList<>()).add(espera);
                cantidadPorCategoria.put(cat, cantidadPorCategoria.getOrDefault(cat, 0) + 1);
                if (cat >= 1 && cat <= 5 && espera > TIEMPO_MAXIMO_ESPERA[cat]) {
                    historialExcedidos.add(atendido);
                }
            }
        }
        int minutoFinal = DURACION_SIMULACION;
        while (!cola.isEmpty()) {
            Paciente atendido = cola.remove(0);
            atendido.setEstado("atendido");
            int cat = atendido.getCategoria();
            int espera = minutoFinal - (int) atendido.getTiempoLlegada();
            pacientesAtendidos.add(atendido);
            tiemposPorCategoria.computeIfAbsent(cat, k -> new ArrayList<>()).add(espera);
            cantidadPorCategoria.put(cat, cantidadPorCategoria.getOrDefault(cat, 0) + 1);
            if (cat >= 1 && cat <= 5 && espera > TIEMPO_MAXIMO_ESPERA[cat]) {
                historialExcedidos.add(atendido);
            }
            minutoFinal++;
        }
        try (PrintWriter out = new PrintWriter("Pacientes24hExtension.txt")) {
            out.println("--- Pacientes Atendidos ---");
            for (Paciente p : pacientesAtendidos) {
                out.printf("Nombre: %s %s | Rut: %s | Cat: %d | Área: %s | Estado: %s\n",
                        p.getNombre(), p.getApellido(), p.getRut(), p.getCategoria(),
                        p.getArea(), p.getEstado());
            }
            out.println("\n--- Estadísticas por Categoría ---");
            for (int cat = 1; cat <= 5; cat++) {
                List<Integer> tiempos = tiemposPorCategoria.getOrDefault(cat, new ArrayList<>());
                int count = cantidadPorCategoria.getOrDefault(cat, 0);
                double prom = tiempos.isEmpty() ? 0 : tiempos.stream().mapToInt(Integer::intValue).average().orElse(0);
                out.printf("Categoría %d: %d pacientes, espera promedio: %.2f min\n", cat, count, prom);
            }
            out.println("\n--- Pacientes que excedieron el tiempo máximo ---");
            if (historialExcedidos.isEmpty()) {
                out.println("Ningún paciente excedió su tiempo máximo de espera.");
            } else {
                for (Paciente p : historialExcedidos) {
                    out.printf("Nombre: %s %s | Rut: %s | Cat: %d | Área: %s | Estado: %s\n",
                            p.getNombre(), p.getApellido(), p.getRut(), p.getCategoria(),
                            p.getArea(), p.getEstado());
                }
            }
            System.out.println("Resultados guardados en Pacientes24hExtension.txt");
        } catch(IOException e) {
            System.out.println("Error al guardar resultados: " + e.getMessage());
        }
    }
}