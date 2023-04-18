
public class CC_01_Threads  {

	private static class Hilos extends Thread{
		
		
		private int identificador;
		private int t;

		public Hilos (int identificador, int t) {

			this.identificador= identificador;
			this.t= t;
		}

		public void run() {
			
			System.out.println("Proceso número: "+ identificador);

			try {
			
				Thread.sleep (t) ; 
			} catch ( InterruptedException e ){}
			
			System.out.println("Proceso número: "+ identificador);
		}
	}


	public static void main(String [] args) {

		int n = 30; // Numero de procesos 
		Hilos [] hilos = new Hilos[n]; // vector de procesos 


		for(int i = 0 ; i <n ; i++) {

			hilos[i] = new Hilos(i+1, (int) (Math.random()*5000)); // Inicializo cada hilo con un tiempo aleatorio de espera
			hilos[i].start();

		}
		
		try{
			
			for(int i = 0 ; i < n ; i++)
				hilos[i].join();
			
		}catch( InterruptedException e){}
		
		
		System.out.println("Todos los procesos han terminado.");

	}

}
