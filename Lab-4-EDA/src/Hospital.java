import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
public class Hospital {
    Map<String, Paciente> pacientesTotales; // llave-valor para ir al paciente, con llave ID
    PriorityQueue<Paciente> colaAtencion; //pacientes en espera
    Map<String, AreaAtencion> areasAtencion; // area especifica para los pacientes
    List<Paciente> pacientesAtendidos; // Historial de los pacientes atendidos

    void registrarPaciente(Paciente p) {
    }
    void reasignarCategoria(String rut, int nuevaCategoria){

    }
    Paciente atenderSiguiente(){

        return null;
    }
    List<Paciente> obtenerPacientesPorCategoria(int categoria){
        return null;
    }
    AreaAtencion obtenerArea(String nombre){
        return null;
    }
}
