package concurrencia;

public class CC_01_Threads extends Thread{


	static int n=9;
	static Thread [] arr = new Thread[n];
	private int num;

	public CC_01_Threads(int numero) {
		num= numero;
	}

	public void run(){

		try {
			System.out.println(" Este es el Thread: " + num);
			Thread.sleep((long) (Math.random()* 10000));
			System.out.println(" El thread " + num+ " ha terminado." );

		} catch (InterruptedException e) {
			System.out.println("Me han interrumpido !");
		}
	}

	public static void main ( String args[])  throws InterruptedException {

		System.out.println("Empieza"); 
		for ( int i=0; i<n; i++){
			CC_01_Threads thr = new CC_01_Threads(i);
			arr[i]= thr;
			thr.start();

		}
		for ( int i= 0; i<n; i++){
			arr[i].join();
		}
		System.out.println("Todos los thread han acabado.");

	}

}
