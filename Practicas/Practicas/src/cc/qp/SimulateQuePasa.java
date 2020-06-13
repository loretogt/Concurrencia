package cc.qp;


import java.util.Scanner;
import java.util.LinkedList;
import java.util.List;

/**
 * Main class.
 * 
 * @author Babel Group
 */
public class SimulateQuePasa {
  
  
  static String ingreso;
  // randomize utilizado para ejecutar el no determinismo
  static final java.util.Random rdm = 
    new java.util.Random(System.currentTimeMillis());
  
  public static final void main(final String[] args) {
    
    final QuePasa sharedResource = new QuePasaMonitor();
    // final QuePasa sharedResource = new QuePasaCSP();
    
    // usuarios 
    LinkedList<Integer> usarios = new LinkedList<Integer>();
    
    int numUsarios = rdm.nextInt(3)+3;
    System.out.println("Vamos a simular usando "+numUsarios+" usarios");
    
    LinkedList<String> grupos = new LinkedList<String>();
    for (int i=0; i<numUsarios; i++) {
      grupos.add("grupo_"+i);
    }
    
    // Creacion de los usuarios del sistema
    for (int i=0; i<numUsarios; i++) {
      LinkedList<Integer> otrosUsarios = new LinkedList<Integer>();
      for (int j=0; j<numUsarios; j++) {
        if (i != j) otrosUsarios.add(j);
      }
      
      SimulatedEscritor escritor =
        new SimulatedEscritor
        (i,
         sharedResource,
         grupos.get(i),
         otrosUsarios);
      
      SimulatedLector lector =
        new SimulatedLector
        (i,
         sharedResource,
          escritor);
      escritor.start();
      lector.start();
    }
  }
}
