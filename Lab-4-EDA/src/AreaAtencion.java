import java.util.PriorityQueue;
import java.util.List;
import java.util.ArrayList;
public class AreaAtencion {
    String nombreA;
    PriorityQueue<Paciente> pacientesHeap;
    int capacidadMaxima;
    AreaAtencion(String nombreA, int capacidadMaxima) {
        this.nombreA = nombreA;
        this.capacidadMaxima = capacidadMaxima;
        this. pacientesHeap = new PriorityQueue<>((p1,p2)-> {
            if (p1.categoria != p2.categoria)
                return Integer.compare (p1.categoria, p2.categoria);
             return Long.compare(p1.tiempoLlegada, p2.tiempoLlegada);});
    }
    String getNombreA() {
        return nombreA;
    }
    int getCapacidadMaxima() {
        return capacidadMaxima;
    }
    void ingresarPaciente(Paciente p) {
          if(pacientesHeap.size() < capacidadMaxima){
              pacientesHeap.offer(p);
          }
          else{
              System.out.println(" Area saturada:" + nombreA );
          }
    }
    boolean estaSaturada(){
        if (pacientesHeap.size() >= capacidadMaxima) {
            return true;
        }
        return false;
    }
   Paciente atenderPaciente(){
        return pacientesHeap.poll();
    }
    List<Paciente>obtenerPacientesPHS(){
      List<Paciente> ListaOrdenada = new ArrayList<>(pacientesHeap);
      ListaOrdenada.sort((p1, p2)-> Integer.compare(p1.categoria, p2.categoria));
      return ListaOrdenada;
    }
}
