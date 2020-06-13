package cc.qp;

import es.upm.babel.cclib.ConcIO;

public class SimulatedLector extends Thread {
  
  // randomize utilizado para ejecutar el no determinismo
  private final java.util.Random rdm = 
    new java.util.Random(System.currentTimeMillis());
  
  // referencia al recurso compartido
  private final QuePasa qp;
  // ID del usuario
  private final int uid;
  // El hilo que esta escribiendo
  private final SimulatedEscritor escritor;

  
  public SimulatedLector(int uid, QuePasa qp, SimulatedEscritor escritor) {
    this.qp = qp;
    this.uid = uid;
    this.escritor = escritor;
  }
  
  public void run() {
    while (true) {
      try {
	Thread.sleep(300 * (rdm.nextInt(5)+1));
      } catch (InterruptedException e) {
        ConcIO.printfnl("======= Oops, no se pudo dormir!" + e);
      }
      
      //ejecutamos e imprimimos el/los mensajes leidos en el log
      ConcIO.printfnl("INIT:: "+this.uid + " leyendo... " );
      Mensaje mensaje = qp.leer(this.uid);
      ConcIO.printfnl("END:: "+this.uid + " lee -> " + mensaje.getContenidos()+" enviado por "+mensaje.getRemitente()+" en el grupo "+mensaje.getGrupo());
      if (!mensaje.getGrupo().equals(escritor.miGrupo)) {
        if (!escritor.gruposMiembro.contains(mensaje.getGrupo()))
          escritor.gruposMiembro.add(mensaje.getGrupo());
      }
    }
  }
}
