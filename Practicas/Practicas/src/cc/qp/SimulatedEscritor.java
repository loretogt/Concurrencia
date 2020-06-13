package cc.qp;


import es.upm.babel.cclib.ConcIO;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SimulatedEscritor extends Thread {
  
  // randomize utilizado para ejecutar el no determinismo
  private final java.util.Random rdm = 
    new java.util.Random(System.currentTimeMillis());
  // contador usado para hacer trackeo de los mensajes enviados
  private int msgCounter = 0;
  
  // referencia al recurso compartido
  private final QuePasa qp;
  // ID del usuario
  private final int uid;
  
  // Un grupo que puedo crear
  volatile String miGrupo;
  private boolean miGrupoCreado;
  // Una lista de usarios que no incluye a mi, que no estan miembros en mi grupo
  private List<Integer> otrosUsarios;
  
  // Una lista de grupos que no he creado pero en quales soy miembro
  List<String> gruposMiembro;
  
  
  public SimulatedEscritor(int uid,
                           QuePasa qp,
                           String miGrupo,
                           List<Integer> otrosUsarios) {
    this.qp = qp;
    this.uid = uid;
    this.miGrupo = miGrupo;
    this.miGrupoCreado = false;
    this.otrosUsarios = otrosUsarios;
    this.gruposMiembro =
      Collections.synchronizedList(new LinkedList<String>());
  }
  
  public void run() {
    while (true) {
      // nos dormimos un rato para no saturar el sistema
      // y para emular mejor el comportamiento real
      try {
	Thread.sleep(300 * (rdm.nextInt(5)+1));
      } catch (InterruptedException e) {
	ConcIO.printfnl("================= Oops, no se pudo dormir!" + e);
      }
      
      List<Integer> operationsEnabled = new LinkedList<Integer>();
      
      if (!miGrupoCreado)
        operationsEnabled.add(0);
      if (miGrupoCreado && (otrosUsarios.size() > 0))
        operationsEnabled.add(1);
      if (gruposMiembro.size() > 0)
        operationsEnabled.add(2);
      if (miGrupoCreado || (gruposMiembro.size() > 0))
        operationsEnabled.add(3);
      
      int[] weights = new int[] { 10, 180, 5, 180};
      int rndLimit = 0;
      for (Integer enabledOperation : operationsEnabled)
        rndLimit += weights[enabledOperation];
      
      // decidimos que operacion se va a ejecutar usando random
      int rndNum = Math.abs(rdm.nextInt()%rndLimit);
      int chooseNum = rndNum;
      
      int operation = -1;
      for (Integer enabledOperation : operationsEnabled) {
        if (operation == -1) {
          if (chooseNum < weights[enabledOperation])
            operation = enabledOperation;
          else
            chooseNum -= weights[enabledOperation];
        }
      }
      
      if (operation == -1) {
        System.out.println("*** Error: miscalculated operation");
        System.out.println
          ("operationsEnabled="+operationsEnabled+" "+
           "weights="+Arrays.toString(weights)+" "+
           "rndLimit="+rndLimit+" "+
           "rndNum="+rndNum);
        throw new RuntimeException();
      }
      
      String call = null;
      switch (operation) {
      case 0: // operacion crearGrupo
        
        try {
          call = "crearGrupo("+uid+","+miGrupo+")";
          ConcIO.printfnl("INIT:: "+call);
          qp.crearGrupo(this.uid,miGrupo);
          ConcIO.printfnl("END:: "+call+": "+this.uid +
                          " ha creado el grupo "+miGrupo);
          miGrupoCreado = true;
          
        } catch (PreconditionFailedException e1) {
          ConcIO.printfnl("======= Oops, violo la precondicion! "+call);
        }
        
	break;
        
        
      case 1: // anadirMiembro
        
        try {
          // Eligimos un usario
          int usarioIndex = Math.abs(rdm.nextInt()%otrosUsarios.size());
          int usario = otrosUsarios.get(usarioIndex);
          
          call = "anadirMiembro("+uid+","+miGrupo+","+usario+")";
          
          ConcIO.printfnl("INIT:: "+call);
          qp.anadirMiembro(this.uid,miGrupo,usario);
          ConcIO.printfnl("END:: "+call+": "+this.uid +
                          " ha anadido " +usario+" al grupo "+miGrupo);
          otrosUsarios.remove(usarioIndex);
        } catch (PreconditionFailedException e1) {
          ConcIO.printfnl("======= Oops, violo la precondicion! "+call);
        }
        break;
        
      case 2: // salirGrupo
        
        try {
          // Eligimos un grupo en que soy miembro y no creador
          int grupoIndex = Math.abs(rdm.nextInt()%gruposMiembro.size());
          String grupo = gruposMiembro.get(grupoIndex);
          call = "salirGrupo("+uid+","+grupo+")";
          
          ConcIO.printfnl("INIT:: "+call);
          qp.salirGrupo(this.uid,grupo);
          ConcIO.printfnl("END:: "+call+": "+this.uid +
                          " ha salido del grupo "+grupo);

          // Puede que se añade el grupo despues de borrarlo --
          // pero no pasa nada como solo estamos simulando el comportamiento
          // de un usario tipico
          gruposMiembro.remove(grupoIndex);
        } catch (PreconditionFailedException e1) {
          ConcIO.printfnl("======= Oops, violo la precondicion! "+call);
        }
        break;
        
      case 3: // mandarMensaje
        
        try {
          int grupoIndex =
            miGrupoCreado ? 
            Math.abs(rdm.nextInt()%(gruposMiembro.size()+1)) :
            Math.abs(rdm.nextInt()%gruposMiembro.size());
          
          String grupo = 
            (grupoIndex == gruposMiembro.size()) ?
            miGrupo :
            gruposMiembro.get(grupoIndex);
          String msg =
            "msg_"+uid+"_"+msgCounter++;

          call = "mandarMensaje("+uid+","+grupo+","+msg+")";
          ConcIO.printfnl("INIT:: "+call);
          qp.mandarMensaje(this.uid,grupo,msg);
          ConcIO.printfnl("END:: "+call+": "+this.uid +
                          " ha mandado el mensaje "+msg+
                          " al grupo "+grupo);
        } catch (PreconditionFailedException e1) {
          ConcIO.printfnl("======= Oops, violo la precondicion! "+call);
        }
        break;
      }
    }
  }
}

  
