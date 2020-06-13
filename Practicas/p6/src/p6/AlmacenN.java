package p6;
import es.upm.babel.cclib.Producto;
import es.upm.babel.cclib.Semaphore;
import es.upm.babel.cclib.Almacen;

//ESTA BIEN, ES ASI COMO HAY QUE HACERLO 


// TODO: importar la clase de los semáforos.

/**
 * Implementación de la clase Almacen que permite el almacenamiento
 * FIFO de hasta un determinado número de productos y el uso
 * simultáneo del almacén por varios threads.
 */
class AlmacenN implements Almacen {
	private int capacidad = 0;
	private Producto[] almacenado = null;
	private int nDatos = 0;
	private int aExtraer = 0;
	private int aInsertar = 0;

	// TODO: declaración de los semáforos necesarios
	private  Semaphore sAlmacenar;
	private  Semaphore sExtraer;
	private  Semaphore mutex;
	

	public AlmacenN(int n) {
		capacidad = n;
		almacenado = new Producto[capacidad];
		nDatos = 0;
		aExtraer = 0;
		aInsertar = 0;

		sAlmacenar=  new Semaphore (n);
		sExtraer = new Semaphore (0);
		mutex = new Semaphore (1);

		

	}

	public void almacenar(Producto producto) {
		// TODO: protocolo de acceso a la sección crítica y código de
		// sincronización para poder almacenar.
		sAlmacenar.await();
		mutex.await();

		// Sección crítica
		almacenado[aInsertar] = producto;
		nDatos++;
		aInsertar++;
		aInsertar %= capacidad;

		// TODO: protocolo de salida de la sección crítica y código de
		// sincronización para poder extraer.
		mutex.signal();
		sExtraer.signal();


	}

	public Producto extraer() {
		Producto result;

		// TODO: protocolo de acceso a la sección crítica y código de
		// sincronización para poder extraer.
		sExtraer.await();
		mutex.await();

		// Sección crítica
		result = almacenado[aExtraer];
		almacenado[aExtraer] = null;
		nDatos--;
		aExtraer++;
		aExtraer %= capacidad;

		// TODO: protocolo de salida de la sección crítica y código de
		// sincronización para poder almacenar.

		mutex.signal();
		sAlmacenar.signal();

		return result;
	}
}

