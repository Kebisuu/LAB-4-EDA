import java.util.*;
public class Hospital {
    private Map<String, Paciente> pacientesTotales;
    private PriorityQueue<Paciente> colaAtencion;
    private Map<String, AreaAtencion> areasAtencion;
    private List<Paciente> pacientesAtendidos;
    public Hospital() {
        pacientesTotales = new HashMap<>();
        colaAtencion = new PriorityQueue<>((p1,p2) -> {
            if (p1.getCategoria() != p2.getCategoria())
                return Integer.compare(p1.categoria, p2.categoria);
                return Long.compare(p1.tiempoLlegada, p2.tiempoLlegada);
        });
        areasAtencion = new HashMap<>();
        pacientesAtendidos = new ArrayList<>();
    }
    public void registrarPaciente(Paciente p) {
        pacientesTotales.put(p.getRut(), p);
        colaAtencion.offer(p);
        System.out.println("Paciente registrado: " + p);
    }
    public void reasignarCategoria(String rut, int nuevaCategoria){
        Paciente p = pacientesTotales.get(rut);
        if(p != null){
            p.registrarCambio("Categoria actualizada: " + p.getCategoria() + " a " + nuevaCategoria);
            p.setCategoria(nuevaCategoria);
        }
    }
    public Paciente atenderSiguiente() {
        Paciente p = colaAtencion.poll();
        if(p != null){
            p.setEstado("atendido");
            pacientesAtendidos.add(p);
            System.out.println("Paciente atendido: " + p);
        }
        return p;
    }
    List<Paciente> obtenerPacientesPorCategoria(int categoria) {
        List<Paciente> pacientes = new ArrayList<>();
        for(Paciente p : colaAtencion){
            if(p.getCategoria() == categoria) {
                pacientes.add(p);
            }
        }
    return pacientes;
    }
    public AreaAtencion obtenerArea(String nombreA) {
        return areasAtencion.get(nombreA);
    }
}