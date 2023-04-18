
public class CC_02_Carrera {

	private static class Hilos extends Thread{

		private int id;
		private static volatile int cont = 0;

		public Hilos (int id) {
			this.id = id;

		}

		public void run() {
			System.out.println("El hilo "+ id + " ha empezado ha ejecutarse");
			for(int i = 0 ; i<1000; i++) {
				cont ++;
			}
			for(int i = 0 ; i<1000; i++) {
				cont--;
			}
			System.out.println("El hilo "+ id + " ha terminado");
		}
	}
	public static void main (String [] args) {
		Hilos [] hilos = new Hilos[8];
		for(int i = 0; i<hilos.length; i++) {
			hilos[i]= new Hilos(i+1);
			hilos[i].start();
		}
		try{

			for(int i = 0 ; i < hilos.length ; i++)
				hilos[i].join();

		}catch( InterruptedException e){}
		
		System.out.println("Han finalizado todos los hilos.");
		System.out.println("El valor esperado  de cont era 0 y el valor de cont es : " + Hilos.cont );

	}
}
