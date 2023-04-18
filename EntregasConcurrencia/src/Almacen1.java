import es.upm.babel.cclib.Producto;
import es.upm.babel.cclib.Almacen;
import es.upm.babel.cclib.Semaphore;

// TODO: importar la clase de los semáforos.

/**
 * Implementación de la clase Almacen que permite el almacenamiento
 * de Producto y el uso simultáneo del almacen por varios threads.
 */
class Almacen1 implements Almacen {
   // Producto a almacenar: null representa que no hay Producto
   private Producto almacenado = null;
   
   private static Semaphore  almacenarproductosucto = new Semaphore(1);
   private static Semaphore extraerproductosucto= new Semaphore(0);
   private static Semaphore aux= new Semaphore(1);
   // TODO: declaración e inicialización de los semáforos
   // necesarios

   public Almacen1() {
   }

   public void almacenar(Producto Producto) {
      // TODO: protocolo de acceso a la sección crítica y código de
      // sincronización para poder almacenar.
	   almacenarproductosucto.await();

      // Sección crítica
      almacenado = Producto;

      // TODO: protocolo de salida de la sección crítica y código de
      // sincronización para poder extraer.
      extraerproductosucto.signal();
   }

   public Producto extraer() {
      Producto result;

      // TODO: protocolo de acceso a la sección crítica y código de
      // sincronización para poder extraer.
      extraerproductosucto.await();
      // Sección crítica
      result = almacenado;
      almacenado = null;

      // TODO: protocolo de salida de la sección crítica y código de
      // sincronización para poder almacenar.
      almacenarproductosucto.signal();

      return result;
   }
}
