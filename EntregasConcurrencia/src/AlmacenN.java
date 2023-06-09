import es.upm.babel.cclib.Producto;
import es.upm.babel.cclib.Semaphore;
import es.upm.babel.cclib.Almacen;

// TODO: importar la clase de los semáforos.

/**
 * Implementación de la clase Almacen que permite el almacenamiento
 * FIFO de hasta un determinado número de productosuctos y el uso
 * simultáneo del almacén por varios threads.
 */
class AlmacenN implements Almacen {
   private int capacidad = 0;
   private Producto[] almacenado = null;
   private int nDatos = 0;
   private int aExtraer = 0;
   private int aInsertar = 0;

   // TODO: declaración de los semáforos necesarios
   private Semaphore solicitarproductosucir;
   private Semaphore solicitarExtraer;

   public AlmacenN(int n) {
      capacidad = n;
      almacenado = new Producto[capacidad];
      nDatos = 0;
      aExtraer = 0;
      aInsertar = 0;

      // TODO: inicialización de los semáforos
      solicitarproductosucir= new Semaphore(capacidad);
      solicitarExtraer= new Semaphore(0);
   }

   public void almacenar(Producto Producto) {
      // TODO: protocolo de acceso a la sección crítica y código de
      // sincronización para poder almacenar.
	   solicitarproductosucir.await();

      // Sección crítica
      almacenado[aInsertar] = Producto;
      nDatos++;
      aInsertar++;
      aInsertar %= capacidad;

      // TODO: protocolo de salida de la sección crítica y código de
      // sincronización para poder extraer.
      solicitarExtraer.signal();
   }

   public Producto extraer() {
      Producto result;

      // TODO: protocolo de acceso a la sección crítica y código de
      // sincronización para poder extraer.
      solicitarExtraer.await();

      // Sección crítica
      result = almacenado[aExtraer];
      almacenado[aExtraer] = null;
      nDatos--;
      aExtraer++;
      aExtraer %= capacidad;

      // TODO: protocolo de salida de la sección crítica y código de
      // sincronización para poder almacenar.
      solicitarproductosucir.signal();

      return result;
   }
}
