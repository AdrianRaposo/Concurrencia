
class CC_03_MutexEA2 {
	static final int N_PASOS = 10000;

	// Generador de números aleatorios para simular tiempos de
	// ejecución
	static final java.util.Random RNG = new java.util.Random(0);

	// Variable compartida
	volatile static int n = 0;

	// Variables para asegurar exclusión mutua

	volatile static boolean QuiereIncre = true;
	volatile static boolean QuiereDecre = true;
	volatile static boolean PasaTu;


	// Sección no crítica
	static void no_sc() {
		System.out.println("NoSC");
		try {
			// No más de 2ms
			Thread.sleep(RNG.nextInt(3));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Secciones críticas
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
				// Sección no crítica
				no_sc();

				// Protocolo de acceso a la sección crítica
				QuiereIncre=true;
				PasaTu=false; //obligo a que se meta aquí y libere el while del decrementador si se cemplen ambas condiciones
				while (QuiereDecre && !PasaTu) {
					//esto evita la inanicion
					QuiereIncre=false;
					QuiereIncre=true;
				}
				// Sección crítica
				sc_inc();
				// Protocolo de salida de la sección crítica
				QuiereIncre=false;
			}
		}
	}
	static class Decrementador extends Thread {
		public void run () {
			for (int i = 0; i < N_PASOS; i++) {
				// Sección no crítica
				no_sc();
				// Protocolo de acceso a la sección crítica
				QuiereDecre=true;
				PasaTu=true; //obligo a que se meta aquí y libere el while del incrementador si se cemplen ambas condiciones
				while (QuiereIncre && PasaTu) {
					//esto evita la inanicion
					QuiereDecre=false;
					QuiereDecre=true;
				}
				// Sección crítica
				sc_dec();
				// Protocolo de salida de la sección crítica
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
