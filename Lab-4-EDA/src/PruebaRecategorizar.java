import java.util.Scanner;
public class PruebaRecategorizar {
    public static void main(String[] args) {
        Hospital h = new Hospital();
        Paciente p = new Paciente("Martin", "Matthei","1-23" ,3,"En espera",1748424885, "SAPU" );
        h.registrarPaciente(p);
      Scanner sc = new Scanner(System.in);
      System.out.println("El paciente tiene que ser re categorizado? ");
        System.out.println("Ingrese: 1.Si o 2.No");
       int opcion = sc.nextInt();
        if(opcion == 1) {
            System.out.println("A que categoria va a reasignarlo");
            System.out.println(" '1' - '2' - '3' - '4' - '5' ");
            int nuevaCategoria = sc.nextInt();
            h.reasignarCategoria(p.getRut(), nuevaCategoria);
            System.out.println(p.obtenerUltimoCambio());
        }
        else{
            System.out.println("El paciente no ha sido reasignado");
        }

    }
}
