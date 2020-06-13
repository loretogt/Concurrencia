package p4;

import es.upm.babel.cclib.Semaphore;
import p4.CC_04_MutexSem.Contador;
import p4.CC_04_MutexSem.Decrementador;
import p4.CC_04_MutexSem.Incrementador;

public class solucion_calse_c4 {

	private static int N_THREADS = 2; 
	private static int N_PASOS = 100000;

	

	static class Contador {
		private volatile int n;
		
		private Semaphore s = new Semaphore (1);

		public Contador () {
			this.n = 0;
		}

		public int valorContador () {
			return this.n;
		}

		public void inc () {
			s.await();
			this.n++; 
			s.signal();
		}

		public void dec () { 
			s.await();
			this.n--;
			s.signal();
		}
	}

	static class Incrementador extends Thread { 
		private Contador cont;


		public Incrementador (Contador c) {
			this.cont = c; 
		}

		public void run() {

			for (int i = 0; i < N_PASOS; i++) {

				this.cont.inc(); 
			

			}

		}
	}

	static class Decrementador extends Thread { 

		private Contador cont;

		public Decrementador (Contador c) {
			this.cont = c; 
		}

		public void run() {
			for (int i = 0; i < N_PASOS; i++) {

				this.cont.dec();

			}
		}
	}
	public static void main(String args[]){
		// Creaci ́on del objeto compartido
		Contador cont = new Contador ();

		// Creaci ́on de los arrays que contendr ́an los threads
		Incrementador[] tInc = new Incrementador[N_THREADS];	
		Decrementador[] tDec = new Decrementador[N_THREADS];

		// Creacion de los objetos threads
		for (int i = 0; i < N_THREADS; i++) { 
			tInc[i] = new Incrementador(cont); 
			tDec[i] = new Decrementador(cont);
		}

		// Lanzamiento de los threads
		for (int i = 0; i < N_THREADS; i++) { 
			tInc[i].start();
			tDec[i].start();
		}

		// Espera hasta la terminacion de los threads
		try {
			for (int i = 0; i < N_THREADS; i++) {
				tInc[i].join();
				tDec[i].join(); 
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit (-1);
		}

		// Simplemente se muestra el valor final de la variable:
		System.out.println(cont.valorContador());
		System.exit (0);
	}
}
