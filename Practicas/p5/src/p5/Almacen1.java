package p5;
import es.upm.babel.cclib.Producto; 
import es.upm.babel.cclib.Almacen;
import es.upm.babel.cclib.Semaphore;

/**
 * Implementaci ́on de la clase Almacen que permite el almacenamiento 
 * *de producto y el uso simult ́aneo del almacen por varios threads. 
 * */

public class Almacen1 implements Almacen {

	// Producto a almacenar: null representa que no hay producto
	private Producto almacenado = null;

	// TODO: declaraci ́on e inicializaci ́on de los sem ́aforos  necesarios
	private static Semaphore mutex = new Semaphore (1);
	private static Semaphore alm_vacio = new Semaphore (0);
	private static Semaphore alm_lleno = new Semaphore (0);


	public Almacen1 () {
	}

	public void almacenar(Producto producto) {

		// TODO: protocolo de acceso a la secci ́on cr ́ıtica y c ́odigo de // sincronizaci ́on para poder almacenar.
		// Secci ́on cr ́ıtica
		mutex.await(); // protocolo de acceso a seccion critica 
		
		if(almacenado == null){
			mutex.signal();
			alm_vacio.await();
			mutex.await();
		}
		
		almacenado = producto;
		
		// TODO: protocolo de salida de la secci ́on cr ́ıtica y c ́odigo de
		// sincronizaci ́on para poder extraer.
		mutex.signal();
		alm_lleno.signal();
	}

	public Producto extraer() { 
		
		Producto result;
	
		// TODO: protocolo de acceso a la secci ́on cr ́ıtica y c ́odigo de // sincronizaci ́on para poder extraer.
	// Secci ́on cr ́ıtica
		mutex.await(); // protocolo de acceso a seccion critica 
			if( almacenado!= null){
				mutex.signal();
				alm_lleno.await();
				mutex.await();
			}
		
		result = almacenado;
		almacenado = null;
	
	// TODO: protocolo de salida de la secci ́on cr ́ıtica y c ́odigo de // sincronizaci ́on para poder almacenar.
		mutex.signal();
		alm_vacio.signal();
	
	return result; 
	}
}
