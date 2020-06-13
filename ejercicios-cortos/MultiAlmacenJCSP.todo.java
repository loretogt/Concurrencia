import es.upm.babel.cclib.Producto;
import es.upm.babel.cclib.MultiAlmacen;

// importamos la librería JCSP
import org.jcsp.lang.*;

class MultiAlmacenJCSP implements MultiAlmacen, CSProcess {

    // Canales para enviar y recibir peticiones al/del servidor
    private final Any2OneChannel chAlmacenar = Channel.any2one();
    private final Any2OneChannel chExtraer = Channel.any2one();
    private int TAM;

    // Para evitar la construcción de almacenes sin inicializar la
    // capacidad 
    private MultiAlmacenJCSP() {
    }
   
    public MultiAlmacenJCSP(int n) {
	this.TAM = n;

	// COMPLETAR: inicialización de otros atributos

    }

    public void almacenar(Producto[] productos) {
	
	// COMPLETAR: comunicación con el servidor
	
    }
    
    public Producto[] extraer(int n) {
	Producto[] result = new Producto[n];
	
	// COMPLETAR: comunicación con el servidor
	
	return result;
    }
    
    
    // código del servidor
    private static final int ALMACENAR = 0;
    private static final int EXTRAER = 1;
    public void run() {
	// COMPLETAR: declaración de canales y estructuras auxiliares 

	Guard[] entradas = {
	    chAlmacenar.in(),
	    chExtraer.in()
	};
	Alternative servicios = new Alternative(entradas);
	int choice = 0;
	
	while (true) {
	    try {
		choice = servicios.fairSelect();
	    } catch (ProcessInterruptedException e){}
	    switch(choice){
	    case ALMACENAR: 

		// COMPLETAR: tratamiento de la petición

		break;
	    case EXTRAER:

		// COMPLETAR: tratamiento de la petición

		break;
	    }

	    // COMPLETAR: atención de peticiones pendientes
	}
    }
}
