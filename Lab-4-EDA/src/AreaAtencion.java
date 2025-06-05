import java.util.PriorityQueue;
public class AreaAtencion {
    String nombreA;
    PriorityQueue<Paciente>PacienteHeap;
    int capacidadMaxima;
    AreaAtencion(String nombreA, int capacidadMaxima) {
        this.nombreA = nombreA;
        this.capacidadMaxima = capacidadMaxima;
    }
    String getNombreA() {
        return nombreA;
    }
    int getCapacidadMaxima() {
        return capacidadMaxima;
    }
    void ingresarPaciente(Paciente p) {
        PacienteHeap.add(p);
    }
    boolean estaSaturada(){
        return false;
    }
}