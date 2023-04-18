
import es.upm.babel.cclib.Producto;
import es.upm.babel.cclib.MultiAlmacen;

// importar la librería de monitores
import es.upm.babel.cclib.Monitor;

class MultiAlmacenMon implements MultiAlmacen {
	private int capacidad = 0;
	private Producto almacenado[] = null;
	private int aExtraer = 0;
	private int aInsertar = 0;
	private int nDatos = 0;

	// TODO: declaración de atributos extras necesarios
	// para exclusión mutua y sincronización por condición
	private Monitor mutex;
	private Monitor.Cond condAlmacenar;
	private Monitor.Cond condExtraer;

	// Para evitar la construcción de almacenes sin inicializar la
	// capacidad 
	private MultiAlmacenMon() {
	}

	public MultiAlmacenMon(int n) {
		almacenado = new Producto[n];
		aExtraer = 0;
		aInsertar = 0;
		capacidad = n;
		nDatos = 0;

		// TODO: inicialización de otros atributos
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

		// TODO: implementación de código de bloqueo para 
		// exclusión muytua y sincronización condicional 
		mutex.enter();
		if(productosuctos.length>(this.capacidad/2))
			throw new RuntimeException(new Exception("La cantidad de Producto "+ productosuctos.length+" no se puede almacenar."));
		if ( (nDatos() + productosuctos.length) > capacidad) {
			condAlmacenar.await();
		}
		// Sección crítica
		for (int i = 0; i < productosuctos.length; i++) {
			almacenado[aInsertar] = productosuctos[i];
			nDatos++;
			aInsertar++;
			aInsertar %= capacidad;
		}

		// TODO: implementación de código de desbloqueo para
		// sincronización condicional y liberación de la exclusión mutua  
		desbloqueo(productosuctos);
		mutex.leave();
	}

	public Producto[] extraer(int n) {
		Producto[] result = new Producto[n];

		// TODO: implementación de código de bloqueo para exclusión
		// mutua y sincronización condicional 
		mutex.enter();
		if (n > (this.capacidad / 2)) 
			throw new RuntimeException(new Exception("No se pueden extraer "+ n +"  elementos."));
		if (nDatos() < n) {
			condExtraer.await();
		}
		// Sección crítica
		for (int i = 0; i < result.length; i++) {
			result[i] = almacenado[aExtraer];
			almacenado[aExtraer] = null;
			nDatos--;
			aExtraer++;
			aExtraer %= capacidad;
		}

		// TODO: implementación de código de desbloqueo para
		// sincronización condicional y liberación de la exclusión mutua  
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
