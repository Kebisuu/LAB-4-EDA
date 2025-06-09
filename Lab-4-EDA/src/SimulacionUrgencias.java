import java.util.*;
import java.io.*;
public class SimulacionUrgencias {
    private static final int DURACION_SIMULACION = 24 * 60;
    private static final int[] TIEMPO_MAXIMO_ESPERA = { 0,10, 30, 60, 120, 180};
    private Hospital hospital;
    private Map<Integer, List<Integer>> tiemposPorCategoria = new HashMap<>();
    private Map<Integer, Integer> cantidadPorCategoria = new HashMap<>();
    private List<Paciente> historialExcedidos = new ArrayList<>();
    private Map<String, Boolean> areaSaturadaFlag = new HashMap<>();
    public SimulacionUrgencias(Hospital hospital) {
        this.hospital = hospital;
    }
    public void simular(int pacientesPorDia) {
        List<Paciente> pacientes = cargarPacientesDesdeArchivo("Pacientes_24h.txt");
        int idxNuevoPaciente = 0;
        int acumulados = 0;
        hospital.areasAtencion.put("SAPU", new AreaAtencion("SAPU", 58));
        hospital.areasAtencion.put("Urgencias_Adultos", new AreaAtencion("Urgencias_Adultos", 48));
        hospital.areasAtencion.put("Urgencias_Infantiles", new AreaAtencion("Urgencias_Infantiles", 38));
        areaSaturadaFlag.put("SAPU", false);
        areaSaturadaFlag.put("Urgencias_Adultos", false);
        areaSaturadaFlag.put("Urgencias_Infantiles", false);

        for (int minuto = 0; minuto < DURACION_SIMULACION; minuto++) {
            for (Map.Entry<String, AreaAtencion> entry : hospital.areasAtencion.entrySet()) {
                String areaNombre = entry.getKey();
                AreaAtencion area = entry.getValue();
                boolean saturada = area.estaSaturada();
                if (saturada && !areaSaturadaFlag.get(areaNombre)) {
                    System.out.println("El área " + areaNombre + " está saturada");
                    areaSaturadaFlag.put(areaNombre, true);
                }
                if (!saturada && areaSaturadaFlag.get(areaNombre)) {
                    areaSaturadaFlag.put(areaNombre, false);
                }
            }
            if (minuto % 10 == 0 && idxNuevoPaciente < pacientesPorDia && idxNuevoPaciente < pacientes.size()) {
                Paciente nuevo = pacientes.get(idxNuevoPaciente++);
                nuevo.setTiempoLlegada(minuto);
                hospital.registrarPaciente(nuevo);
                AreaAtencion area = hospital.obtenerArea(nuevo.getArea());
                if (area != null) area.ingresarPaciente(nuevo);
                acumulados++;
            }
            List<Paciente> urgentes = getExcedidosDeCola(minuto);
            for (Paciente urgente : urgentes) {
                if (urgente.getTiempoLlegada() < minuto) {
                    atenderPaciente(urgente, minuto);
                    hospital.colaAtencion.remove(urgente);
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
        int minutoFinal = DURACION_SIMULACION;
        while (!hospital.colaAtencion.isEmpty()) {
            atenderSiguientePaciente(minutoFinal++);
        }

        mostrarResultados();
    }
    private List<Paciente> getExcedidosDeCola(int minutoActual) {
        List<Paciente> urgentes = new ArrayList<>();
        for (Paciente p : hospital.colaAtencion) {
            int cat = p.getCategoria();
            int espera = minutoActual - (int) p.getTiempoLlegada();
            if (cat >= 1 && cat <= 5 && espera > TIEMPO_MAXIMO_ESPERA[cat]) {
                urgentes.add(p);
            }
        }
        return urgentes;
    }
    private void atenderSiguientePaciente(int minutoActual) {
        Iterator<Paciente> it = hospital.colaAtencion.iterator();
        while (it.hasNext()) {
            Paciente p = it.next();
            if (p.getTiempoLlegada() < minutoActual) {
                atenderPaciente(p, minutoActual);
                it.remove();
                break;
            }
        }
    }
    private void atenderPaciente(Paciente p, int minutoActual) {
        int cat = p.getCategoria();
        int espera = minutoActual - (int) p.getTiempoLlegada();
        p.setEstado("atendido");
        hospital.pacientesAtendidos.add(p);
        p.registrarCambio("Atendido en minuto " + minutoActual + ", espera: " + espera);

        tiemposPorCategoria.computeIfAbsent(cat, k -> new ArrayList<>()).add(espera);
        cantidadPorCategoria.put(cat, cantidadPorCategoria.getOrDefault(cat, 0) + 1);

        if (cat >= 1 && cat <= 5 && espera > TIEMPO_MAXIMO_ESPERA[cat]) {
            historialExcedidos.add(p);
        }
    }
    private void mostrarResultados() {
        try (PrintWriter out = new PrintWriter("resultados_simulacion.txt")) {
            out.println("--- Pacientes Atendidos ---");
            for (Paciente p : hospital.pacientesAtendidos) {
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
            System.out.println("Resultados guardados en resultados_simulacion.txt");
        } catch(IOException e) {
            System.out.println("Error al guardar resultados: " + e.getMessage());
        }
    }
    public static void main(String[] args) {
        Hospital hospital = new Hospital();
        SimulacionUrgencias sim = new SimulacionUrgencias(hospital);
        int pacientesPorDia = 144;
        sim.simular(pacientesPorDia);
    }
    public static List<Paciente> cargarPacientesDesdeArchivo(String nombreArchivo) {
        List<Paciente> lista = new ArrayList<>();
        File archivo = new File(nombreArchivo);
        if (!archivo.exists()) {
            System.out.println("Error: Archivo no encontrado.");
            return lista;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split("\\s*,\\s*");
                if (datos.length < 7) continue;
                String nombre = datos[0].trim();
                String apellido = datos[1].trim();
                String rut = datos[2].trim();
                int categoria = Integer.parseInt(datos[3].trim());
                String estado = datos[4].trim();
                long tiempoLlegada = Long.parseLong(datos[5].trim());
                String area = datos[6].trim();
                Paciente paciente = new Paciente(nombre, apellido, rut, categoria, estado, tiempoLlegada, area);
                lista.add(paciente);
            }
        } catch(IOException e) {
            System.out.println("Error al leer archivo: " + e.getMessage());
        }
        return lista;
    }
}