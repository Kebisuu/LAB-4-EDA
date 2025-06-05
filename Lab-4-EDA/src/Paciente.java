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
    Paciente(String nombre, String apellido, String rut, int categoria, String estado, String area, long tiempoLlegada) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.rut = rut;
        this.categoria = categoria;
        this.estado = "En espera";
        this.area = area;
        this.tiempoLlegada = (System.currentTimeMillis() / 1000);
        historialCambios = new Stack<>();
    }
    public long tiempoEsperaActual(){
    return (System.currentTimeMillis() / 1000 - tiempoLlegada) / 60;
    }
    public void registrarCambio(String descripcion){
    historialCambios.push(descripcion);
    }
    public String obtenerUltimoCambio(){
        if(historialCambios.isEmpty()){
            return "No hay cambios";
        }
        else{
            return historialCambios.pop();
        }
    }
    //Código para nosotros (extra)
    public String getNombre() {return nombre;}
    public String getApellido() {return apellido;}
    public String getRut() {return rut;}
    public int getCategoria() {return categoria;}
    public long getTiempoLlegada() {return tiempoLlegada;}
    public String getEstado() {return estado;}
    public String getArea() {return area;}
}
