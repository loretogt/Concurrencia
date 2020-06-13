import es.upm.babel.cclib.Monitor;
import es.upm.babel.cclib.MultiAlmacen;
import es.upm.babel.cclib.Producto;

public class P9_resultado  implements MultiAlmacen{
	private int capacidad = 0;
	private Producto almacenado[] = null;
	private int aExtraer = 0;
	private int aInsertar = 0;
	private int nDatos = 0; 


	// TODO: declaración de atributos extras necesarios
	// para exclusión mutua y sincronización por condición
	private Monitor mutex;
	private Monitor.Cond[] cAlm;
	private Monitor.Cond[] cExt;



	public P9_resultado(int n) {
		capacidad = n;
		almacenado = new Producto[capacidad];
		nDatos = 0;
		aExtraer = 0;
		aInsertar = 0;

		mutex = new Monitor();
		cAlm = new Monitor.Cond[n/2];
		cExt = new Monitor.Cond[n/2];

		for ( int i= 1; i< n/2+1 ;i++){ // para que la posicion 1 corresponda a que haya un dato, sino estaria todo deplazado
			cAlm[i]= mutex.newCond();
			cExt[i]=mutex.newCond();
		}



	}

	private int nDatos() {
		return nDatos;
	}

	private int nHuecos() {
		return capacidad - nDatos;
	}

	public void almacenar(Producto[] productos) {
		// aqui va otro if de la pre

		// TODO: implementación de código de bloqueo para 
		// exclusión muytua y sincronización condicional 
		mutex.enter();

		if( productos.length > nHuecos()){
			cAlm[productos.length].await();

		}

		// Sección crítica
		for (int i = 0; i < productos.length; i++) {
			almacenado[aInsertar] = productos[i];
			nDatos++;
			aInsertar++;
			aInsertar %= capacidad;
		}

		// TODO: implementación de código de desbloqueo para
		// sincronización condicional y liberación de la exclusión mutua  

		desbloquear();
		mutex.leave();

	}

	public Producto[] extraer(int n) {
		if ( n> capacidad/2){
			throw new IllegalArgumentException(); 
		}


		mutex.enter();
		Producto[] result = new Producto[n];

		if ( n>nDatos()){
			cExt[n].await();
		}


		// Sección crítica
		for (int i = 0; i < result.length; i++) {
			result[i] = almacenado[aExtraer];
			almacenado[aExtraer] = null;
			nDatos--;
			aExtraer++;
			aExtraer %= capacidad;
		}

		// TODO: implementación de código de desbloqueo para
		// sincronización condicional y liberación de la exclusión mutua  
		desbloquear();
		mutex.leave();

		return result;
	}
	private void desbloquear(){ // se puede modificar para que sea mas justo 
		boolean signaled = false;
		for ( int i= 0;i < cAlm.length && !signaled ; i++){
			if ( nHuecos()>= i && cAlm[i].waiting() > 0){
				cAlm[i].signal();
				signaled = true;
			}
		}
		for ( int i= 0;i < cExt.length && !signaled ; i++){
			if ( nDatos()>= i && cExt[i].waiting() > 0){
				cExt[i].signal();
				signaled = true;
			}
		}
	}
}
