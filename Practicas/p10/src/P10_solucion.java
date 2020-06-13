import es.upm.babel.cclib.Producto;
import es.upm.babel.cclib.MultiAlmacen;

// importamos la librería JCSP
import org.jcsp.lang.*;

class P10_solucion implements MultiAlmacen, CSProcess {

	// Canales para enviar y recibir peticiones al/del servidor
	private final Any2OneChannel[] chAlmacenar;
	private final Any2OneChannel[] chExtraer;
	private int TAM;

	// Para evitar la construcción de almacenes sin inicializar la
	// capacidad 

	public P10_solucion(int n) {
		this.TAM = n;
		// COMPLETAR: inicialización de otros atributos
		chAlmacenar= new Any2OneChannel [TAM / 2+1];
		chExtraer= new Any2OneChannel [TAM / 2+1];

		for (n=1; n<= TAM/2; n++){
			chAlmacenar[n]= Channel.any2one();
			chExtraer[n] = Channel.any2one();
		}




	}

	public void almacenar(Producto[] productos) {

		// COMPLETAR: comunicación con el servidor
		chAlmacenar[productos.length].out().write(productos);

	}

	public Producto[] extraer(int n) {

		// COMPLETAR: comunicación con el servidor
		One2OneChannel chResp = Channel.one2one();
		chExtraer[n].out().write(chResp);
		return (Producto[])chResp.in().read();
	}


	// código del servidor

	public void run() {
		// COMPLETAR: declaración de canales y estructuras auxiliares 

		int capacidad = TAM;
		Producto [] almacenado = new Producto[capacidad];
		int nDatos=0;
		int aExtraer=0;
		int aInsertar=0;

		AltingChannelInput[] inputs = new AltingChannelInput[(TAM/2+1)*2];
		for ( int n=0; n<= TAM/2; n++){
			inputs[n]= chAlmacenar[n].in();
			inputs[n+TAM / 2+1]= chExtraer[n].in();

		}
		Alternative servicios = new Alternative(inputs);
		int cual, cuantos;
		One2OneChannel cresp;
		Producto[] productos = null;

		//recepcion condicional
		boolean[] sindCond= new boolean [(TAM/2+1)*2];
		while (true) {
			//  refescamos las condiciones
			for ( int n=1;n<= TAM / 2; n++){
				// cpres almacenar
				sindCond[n]= ( TAM- nDatos)>=n;

				//cpres extraer
				sindCond[n+TAM/ 2+1] = nDatos>=n;
			}

			//la select
			cual= servicios.fairSelect(sindCond);

			// tratamiento de casos 

			if ( cual<= TAM/2+1){
				// es peticion de alacenar
				productos= (Producto[]) chAlmacenar[cual].in().read();

				// seccion critica
				for ( int i= 0; i< productos.length; i++){
					almacenado[aInsertar] = productos[i];
					nDatos++;
					aInsertar++;
					aInsertar %= capacidad;
				}
			}
			else{
				cuantos = cual- ((TAM/2)+1);
				// es peticion de extraer
				cresp= (One2OneChannel) chExtraer[cuantos].in().read();

				// secccion critca
				for( int i=0; i< cuantos; i++){
					productos[i]= almacenado[aExtraer];
					almacenado[aExtraer]= null;
					nDatos --;
					aExtraer++;
					aExtraer %= capacidad;

				}
				cresp.out().write(productos);
			}


		}

	}
}
