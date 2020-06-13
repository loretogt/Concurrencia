package p2;



public class CC_02_Carrera extends Thread {
	
	static int n=500;
	static Thread [] arr = new Thread[n];
	static int res=0;
	
	

	public void run(){
		
		for ( int i=0; i<n; i++){
			res= res +1 ;
		}
		for ( int i=0; i<n; i++){
			res= res -1  ;
		}
		
	}
	public static void main ( String args[])  throws InterruptedException {

		for ( int i=0; i<n; i++){
			CC_02_Carrera thr = new CC_02_Carrera();
			arr[i]= thr;
			thr.start();

		}
		for ( int i= 0; i<n; i++){
			arr[i].join();
		}
		System.out.println(res);
	}
}
