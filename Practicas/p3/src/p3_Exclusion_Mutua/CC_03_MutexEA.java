package p3_Exclusion_Mutua;

public class CC_03_MutexEA {
	static final int N_PASOS = 10000;
	// Generador de ńumeros aleatorios para simular tiempos de
	// ejecucíon
	static final java.util.Random RNG = new java.util.Random(0);

	// Variable compartida
	volatile static int n = 0;


	//indican si el proceso esta ejecutando su seccion critica
	volatile static boolean en_sc_inc=false;
	volatile static boolean en_sc_dec=false;
	
	volatile static boolean inc_quiere= false;
	volatile static boolean dec_quiere= false;

	volatile static boolean turno_inc= false;
	

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
				//no_sc();
				
				inc_quiere=true;
				while(dec_quiere){
					inc_quiere=false;
					inc_quiere= true;
				}
				
				en_sc_inc = true;
				turno_inc= true;

				while( en_sc_dec && !turno_inc){}

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
					//no_sc();
					
					dec_quiere=true;
					while(inc_quiere){
						dec_quiere=false;
						dec_quiere= true;
					}
					
					en_sc_dec=true;
					turno_inc= true;
					
					while(en_sc_inc && turno_inc){}
					
					sc_dec();
					en_sc_dec = false;
					dec_quiere=false;
					
				}
			}
		}
		
		public static final void main(final String[] args) throws InterruptedException {
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
