
class CC_03_MutexEA2 {
	static final int N_PASOS = 10000;

	// Generador de n�meros aleatorios para simular tiempos de
	// ejecuci�n
	static final java.util.Random RNG = new java.util.Random(0);

	// Variable compartida
	volatile static int n = 0;

	// Variables para asegurar exclusi�n mutua

	volatile static boolean QuiereIncre = true;
	volatile static boolean QuiereDecre = true;
	volatile static boolean PasaTu;


	// Secci�n no cr�tica
	static void no_sc() {
		System.out.println("NoSC");
		try {
			// No m�s de 2ms
			Thread.sleep(RNG.nextInt(3));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Secciones cr�ticas
	static void sc_inc() {
		//System.out.println("Incrementando");
		n++;
	}

	static void sc_dec() {
		//System.out.println("Decrementando");
		n--;
	}

	static class Incrementador extends Thread {
		public void run () {
			for (int i = 0; i < N_PASOS; i++) {
				// Secci�n no cr�tica
				no_sc();

				// Protocolo de acceso a la secci�n cr�tica
				QuiereIncre=true;
				PasaTu=false; //obligo a que se meta aqu� y libere el while del decrementador si se cemplen ambas condiciones
				while (QuiereDecre && !PasaTu) {
					//esto evita la inanicion
					QuiereIncre=false;
					QuiereIncre=true;
				}
				// Secci�n cr�tica
				sc_inc();
				// Protocolo de salida de la secci�n cr�tica
				QuiereIncre=false;
			}
		}
	}
	static class Decrementador extends Thread {
		public void run () {
			for (int i = 0; i < N_PASOS; i++) {
				// Secci�n no cr�tica
				no_sc();
				// Protocolo de acceso a la secci�n cr�tica
				QuiereDecre=true;
				PasaTu=true; //obligo a que se meta aqu� y libere el while del incrementador si se cemplen ambas condiciones
				while (QuiereIncre && PasaTu) {
					//esto evita la inanicion
					QuiereDecre=false;
					QuiereDecre=true;
				}
				// Secci�n cr�tica
				sc_dec();
				// Protocolo de salida de la secci�n cr�tica
				QuiereDecre=false;
			}
		}
	}

	public static final void main(final String[] args)
			throws InterruptedException
	{
		// Creamos las tareas
		Thread t1 = new Incrementador();
		Thread t2 = new Decrementador();

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
