/**
 * Programa concurrente para el control del acceso al puente de un
 * solo sentido.
 */
class CC_07_Puente {

   static private class ControlEntrada extends Thread {
      private Puente.Entrada e;
      private ControlAccesoPuente cap;

      public ControlEntrada (Puente.Entrada e,
                             ControlAccesoPuente cap) {
         this.e = e;
         this.cap = cap;
      }

      public void run() {
         while (true) {
            Puente.detectar(this.e);
            cap.solicitarEntrada(this.e);
            Puente.abrir(this.e);
         }
      }
   }

   static private class AvisoSalida extends Thread {
      private Puente.Salida s;
      private ControlAccesoPuente cap;

      public AvisoSalida (Puente.Salida s,
                          ControlAccesoPuente cap) {
         this.s = s;
         this.cap = cap;
      }

      public void run() {
         while (true) {
            Puente.detectar(this.s);
            cap.avisarSalida(this.s);
         }
      }
   }

   public static final void main(final String[] args)
      throws InterruptedException
   {
      ControlAccesoPuente cap;
      ControlEntrada ceN, ceS;
      AvisoSalida asN, asS;

      cap = new ControlAccesoPuente();
      ceN = new ControlEntrada(Puente.Entrada.N,cap);
      ceS = new ControlEntrada(Puente.Entrada.S,cap);
      asN = new AvisoSalida(Puente.Salida.N,cap);
      asS = new AvisoSalida(Puente.Salida.S,cap);

      ceN.start();
      ceS.start();
      asN.start();
      asS.start();
   }
}
