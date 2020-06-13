import es.upm.babel.cclib.Producto;
import es.upm.babel.cclib.MultiAlmacen;

import java.util.LinkedList;
import java.util.Queue;

// importamos la librería JCSP
import org.jcsp.lang.*;

class MultiAlmacenJCSP implements MultiAlmacen, CSProcess {

	// Canales para enviar y recibir peticiones al/del servidor
	private final Any2OneChannel chAlmacenar;
	private final Any2OneChannel chExtraer ;
	private int TAM;
	
	
	public MultiAlmacenJCSP(int n) {
		this.TAM = n;

		// COMPLETAR: inicialización de otros atributos
		chAlmacenar= Channel.any2one();
		chExtraer= Channel.any2one();

	}

	public void almacenar(Producto[] productos) {

		// COMPLETAR: comunicación con el servidor
		One2OneChannel chResp = Channel.one2one();
		chAlmacenar.out().write(new PeticionAlmacenar (chResp, productos));
		chResp.in().read();
	}

	public Producto[] extraer(int n) {
		One2OneChannel chResp = Channel.one2one();

		chExtraer.out()
	}


	// código del servidor
	private static final int ALMACENAR = 0;
	private static final int EXTRAER = 1;
	public void run() {
		// COMPLETAR: declaración de canales y estructuras auxiliares 

		Queue<Producto> cola = new LinkedList<Producto>();
		boolean[] sindCond = new boolean[TAM];

		Guard[] entradas = { chAlmacenar.in(), chExtraer.in() };

		Alternative servicios = new Alternative(entradas);
		int choice = 0;

		while (true) {
			Producto item;
			
			for ( int i=0; i<TAM/2;i++){
				sindCond[i]= cola.size()<TAM;
			}
			for ( int i=TAM/2; i<TAM;i++){
				sindCond[i]= cola.size()>0;
			}
			try {
				choice = servicios.fairSelect();
			} catch (ProcessInterruptedException e){}

			switch(choice){
			case ALMACENAR: 

				// COMPLETAR: tratamiento de la petición
				item=(Producto) petAlm[choice].in().read();
				break;
			case EXTRAER:
				
				// COMPLETAR: tratamiento de la petición
				ChannelOutput resp=  (ChannelOutput) petExt[0].in().read();
				resp.write(cola.peek());
				cola.poll();
				break;
			}

			// COMPLETAR: atención de peticiones pendientes
		}
	}
}
