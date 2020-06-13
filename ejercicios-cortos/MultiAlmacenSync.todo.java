import es.upm.babel.cclib.Producto;
import es.upm.babel.cclib.MultiAlmacen;

class MultiAlmacenSync implements MultiAlmacen {
    private int capacidad = 0;
    private Producto almacenado[] = null;
    private int aExtraer = 0;
    private int aInsertar = 0;
    private int nDatos = 0;

   // TODO: declaración de atributos extras necesarios

   // Para evitar la construcción de almacenes sin inicializar la
   // capacidad 
   private MultiAlmacenSync() {
   }

   public MultiAlmacenSync(int n) {
      almacenado = new Producto[n];
      aExtraer = 0;
      aInsertar = 0;
      capacidad = n;
      nDatos = 0;

      // TODO: inicialización de otros atributos
   }

   private int nDatos() {
         return nDatos;
   }
   
   private int nHuecos() {
      return capacidad - nDatos;
   }

   synchronized public void almacenar(Producto[] productos) {
      // TODO: implementación de código de bloqueo para sincronización
      // condicional 

      // Sección crítica
      for (int i = 0; i < productos.length; i++) {
         almacenado[aInsertar] = productos[i];
         nDatos++;
         aInsertar++;
         aInsertar %= capacidad;
      }

      // TODO: implementación de código de desbloqueo para
      // sincronización condicional 
   }

   synchronized public Producto[] extraer(int n) {
      Producto[] result = new Producto[n];

      // TODO: implementación de código de bloqueo para sincronización
      // condicional 

      // Sección crítica
      for (int i = 0; i < result.length; i++) {
         result[i] = almacenado[aExtraer];
         almacenado[aExtraer] = null;
         nDatos--;
         aExtraer++;
         aExtraer %= capacidad;
      }

      // TODO: implementación de código de desbloqueo para
      // sincronización condicional 

      return result;
   }
}
