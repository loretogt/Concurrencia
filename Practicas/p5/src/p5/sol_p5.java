package p5;

import es.upm.babel.cclib.Almacen;
import es.upm.babel.cclib.Producto;
import es.upm.babel.cclib.Semaphore;

public class sol_p5 implements Almacen{

	// Producto a almacenar: null representa que no hay producto
		private Producto almacenado = null;

		// TODO: declaraci ́on e inicializaci ́on de los sem ́aforos  necesarios
		private  Semaphore sAlmacenar = new Semaphore (1);
		private  Semaphore sExtraer = new Semaphore (0);

		
		public sol_p5() {
		}

		public void almacenar(Producto producto) {

			// TODO: protocolo de acceso a la secci ́on cr ́ıtica y c ́odigo de // sincronizaci ́on para poder almacenar.
			// Secci ́on cr ́ıtica
			sAlmacenar.await();
			
			almacenado = producto;
			
			sExtraer.signal();
			
			// TODO: protocolo de salida de la secci ́on cr ́ıtica y c ́odigo de
			// sincronizaci ́on para poder extraer.
			
		}

		public Producto extraer() { 
			
			Producto result;
		
			// TODO: protocolo de acceso a la secci ́on cr ́ıtica y c ́odigo de // sincronizaci ́on para poder extraer.
		// Secci ́on cr ́ıtica
			
			sExtraer.await();
			
			result = almacenado;
			almacenado = null;
			
			sAlmacenar.signal();
		
		// TODO: protocolo de salida de la secci ́on cr ́ıtica y c ́odigo de // sincronizaci ́on para poder almacenar.
		
		
		return result; 
		}
}
