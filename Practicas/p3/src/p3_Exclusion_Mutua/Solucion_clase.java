package p3_Exclusion_Mutua;

public class Solucion_clase {
	static final int N_PASOS = 10000;
	// Generador de ńumeros aleatorios para simular tiempos de
	// ejecucíon
	static final java.util.Random RNG = new java.util.Random(0);

	// Variable compartida
	volatile static int n = 0;

	private final static int INC=0;
	private final static int DEC=1;

	//indican si el proceso esta ejecutando su seccion critica
	volatile static boolean en_sc_inc=false;
	volatile static boolean en_sc_dec=false;

	volatile static boolean inc_quiere= false;
	volatile static boolean dec_quiere= false;

	volatile static int  turno = INC;


	// Secci ́on no cr ́ıtica
	static void no_sc () {
		//System.out.println("No SC"); 
		try {
			// No m ́as de 2ms
			Thread.sleep(RNG.nextInt(1)); 
		}
		catch (Exception e) { 
			e.printStackTrace();
		}
	}

	static void sc_dec () {
		//System.out.println("Decrementando");
		n--; 
	}

	private static void sc_inc() {
		//System.out.println("Incrementando");
		n++;
	}

	// La labor del proceso incrementador es ejecutar no_sc() y luego
	// sc_inc() durante N_PASOS asegurando exclusi ́on mutua sobre
	// sc_inc ().

	static class Incrementador extends Thread {
		public void run () {
			for (int i = 0; i < N_PASOS; i++) {

				inc_quiere= true;
				turno=DEC;

				while (dec_quiere && turno== DEC) {} // bucle de espera activa 
				en_sc_inc= true;
				
				sc_inc();
				
				en_sc_inc= false;
				
				inc_quiere= false;
				

			}
		}
	}

	// La labor del proceso incrementador es ejecutar no_sc() y luego 
	// sc_dec() durante N_PASOS asegurando exclusi ́on mutua sobre
	// sc_dec ().

	static class Decrementador extends Thread {
		public void run () {
			for (int i = 0; i < N_PASOS; i++) {

				dec_quiere= true;
				turno= INC;

				while (inc_quiere && turno== INC) {} // bucle de espera activa 
				en_sc_dec=true;
				
				sc_dec();
				
				en_sc_dec= false;
				
				dec_quiere= false;

			}
		}
	}

	public static final void main(final String[] args) 
			throws InterruptedException {
		// Creamos las tareas

		Thread  t1 = new Incrementador (); 
		Thread t2 = new Decrementador ();

		// Las ponemos en marcha
		t1.start(); 
		t2.start();

		// Esperamos a que terminen
		t1.join(); 
		t2.join();

		// Simplemente se muestra el valor final de la variable:
		System.out.println(n);
	}

}
