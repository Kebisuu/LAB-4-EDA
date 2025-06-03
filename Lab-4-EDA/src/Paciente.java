import java.util.Stack;
public class Paciente {
    String nombre;
    String apellido;
    String rut;
    int categoria; // Del 1-5
    long tiempoLlegada;
    String estado; // espera/atencion/atendido
    String area; // SAPU/Urgencia infantil/Urgencia adulto
    Stack<String> historialCambios;
    Paciente(String nombre, String apellido, String rut, int categoria, String estado, String area) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.rut = rut;
        this.categoria = categoria;
        this.estado = estado;
        this.area = area;
        historialCambios = new Stack<>();
    }
    String getNombre() {
        return nombre;
    }
    String getApellido() {
        return apellido;
    }
    String getRut() {
        return rut;
    }
    int getCategoria() {
        return categoria;
    }
    long getTiempoLlegada() {
        return tiempoLlegada;
    }
    String getEstado() {
        return estado;
    }
    String getArea() {
        return area;
    }
    long tiempoEsperaActual(){
    }
    void registrarCambio(String descripcion){
    }
    String obtenerUltimoCambio(){
    }
}