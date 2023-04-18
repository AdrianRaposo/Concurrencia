
import es.upm.babel.cclib.Producto;
import es.upm.babel.cclib.MultiAlmacen;

// importar la librer�a de monitores
import es.upm.babel.cclib.Monitor;

class MultiAlmacenMon implements MultiAlmacen {
	private int capacidad = 0;
	private Producto almacenado[] = null;
	private int aExtraer = 0;
	private int aInsertar = 0;
	private int nDatos = 0;

	// TODO: declaraci�n de atributos extras necesarios
	// para exclusi�n mutua y sincronizaci�n por condici�n
	private Monitor mutex;
	private Monitor.Cond condAlmacenar;
	private Monitor.Cond condExtraer;

	// Para evitar la construcci�n de almacenes sin inicializar la
	// capacidad 
	private MultiAlmacenMon() {
	}

	public MultiAlmacenMon(int n) {
		almacenado = new Producto[n];
		aExtraer = 0;
		aInsertar = 0;
		capacidad = n;
		nDatos = 0;

		// TODO: inicializaci�n de otros atributos
		mutex = new Monitor();
		condAlmacenar = mutex.newCond();
		condExtraer = mutex.newCond();
	}

	private int nDatos() {
		return nDatos;
	}

	private int nHuecos() {
		return capacidad - nDatos;
	}

	public void almacenar(Producto[] productosuctos) {

		// TODO: implementaci�n de c�digo de bloqueo para 
		// exclusi�n muytua y sincronizaci�n condicional 
		mutex.enter();
		if(productosuctos.length>(this.capacidad/2))
			throw new RuntimeException(new Exception("La cantidad de Producto "+ productosuctos.length+" no se puede almacenar."));
		if ( (nDatos() + productosuctos.length) > capacidad) {
			condAlmacenar.await();
		}
		// Secci�n cr�tica
		for (int i = 0; i < productosuctos.length; i++) {
			almacenado[aInsertar] = productosuctos[i];
			nDatos++;
			aInsertar++;
			aInsertar %= capacidad;
		}

		// TODO: implementaci�n de c�digo de desbloqueo para
		// sincronizaci�n condicional y liberaci�n de la exclusi�n mutua  
		desbloqueo(productosuctos);
		mutex.leave();
	}

	public Producto[] extraer(int n) {
		Producto[] result = new Producto[n];

		// TODO: implementaci�n de c�digo de bloqueo para exclusi�n
		// mutua y sincronizaci�n condicional 
		mutex.enter();
		if (n > (this.capacidad / 2)) 
			throw new RuntimeException(new Exception("No se pueden extraer "+ n +"  elementos."));
		if (nDatos() < n) {
			condExtraer.await();
		}
		// Secci�n cr�tica
		for (int i = 0; i < result.length; i++) {
			result[i] = almacenado[aExtraer];
			almacenado[aExtraer] = null;
			nDatos--;
			aExtraer++;
			aExtraer %= capacidad;
		}

		// TODO: implementaci�n de c�digo de desbloqueo para
		// sincronizaci�n condicional y liberaci�n de la exclusi�n mutua  
		desbloqueo(result);
		mutex.leave();
		return result;
	}

	private void desbloqueo (Producto [] productosuctos) {
	if ((nDatos() + productosuctos.length) <= capacidad && condAlmacenar.waiting()>0) {
			condAlmacenar.signal();
		} else if (nDatos() >= productosuctos.length) {
			condExtraer.signal();
		}
	}
}
