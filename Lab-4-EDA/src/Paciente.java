import java.util.Stack;
public class Paciente {
    String nombre;
    String apellido;
    String rut;
    int categoria;
    long tiempoLlegada;
    String estado;
    String area;
    Stack<String> historialCambios;
    Paciente(String nombre, String apellido, String rut, int categoria, String estado, long tiempoLlegada, String area) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.rut = rut;
        this.categoria = categoria;
        this.estado = "En espera";
        this.tiempoLlegada = tiempoLlegada;
        this.area = area;
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
    public String getNombre() {return nombre;}
    public void setNombre(String nombre) {this.nombre = nombre;}
    public String getApellido() {return apellido;}
    public void setApellido(String apellido) {this.apellido = apellido;}
    public String getRut() {return rut;}
    public void setRut(String rut) {this.rut = rut;}
    public int getCategoria() {return categoria;}
    public void setCategoria(int categoria) {this.categoria = categoria;}
    public long getTiempoLlegada() {return tiempoLlegada;}
    public void setTiempoLlegada(long nuevoTiempo) {this.tiempoLlegada = nuevoTiempo;}
    public String getEstado() {return estado;}
    public void setEstado(String estado) {this.estado = estado;}
    public String getArea() {return area;}
    public void setArea(String area) {this.area = area;}
}