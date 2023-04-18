import es.upm.babel.cclib.Producto; 
import es.upm.babel.cclib.MultiAlmacen;
import java.util.LinkedList;
import java.util.Queue;

// importamos la librer ́ıa JCSP
import org.jcsp.lang.*;

class MultiAlmacenJCSP implements MultiAlmacen, CSProcess {

	// Canales para enviar y recibir peticiones al/del servidor
	private final Any2OneChannel chAlmacenar = Channel.any2one(); 
	private final Any2OneChannel chExtraer = Channel.any2one(); 
	private int TAM;
	private Queue<Producto> almacen;

	// Para evitar la construcci ́on de almacenes sin inicializar la capacidad
	private MultiAlmacenJCSP() {
	}

	public MultiAlmacenJCSP(int n) {
		this.TAM = n;
		// COMPLETAR: inicializaci ́on de otros atributos
		almacen = new LinkedList<Producto>();
	}

	public void almacenar(Producto[] productos) {
		if(productos.length>= TAM/2)
			throw new RuntimeException(new Exception("La cantidad de productos qeu se van a introducir es inadecuada"));
			
		chAlmacenar.out().write(productos);
	}

	public Producto[] extraer(int n) { 
		if(n>= TAM/2)
			throw new RuntimeException(new Exception("La cantidad de productos qeu se van a extraer es inadecuada"));
		
		Producto[] result = new Producto[n];
		// COMPLETAR: comunicaci ́on con el servidor
		One2OneChannel sincro =Channel.one2one();
		Object [] peticionExtraer = {result,sincro};
		chExtraer.out().write(peticionExtraer);
		result=(Producto[])sincro.in().read();
		return result;
	}

	// c ́odigo del servidor
	private static final int ALMACENAR = 0;
	private static final int EXTRAER = 1; 
	public void run() {
		// COMPLETAR: declaraci ́on de canales y estructuras auxiliares
		
		Queue<Producto []> queueAlmacenar=new LinkedList<>();
		Queue<Object []> queueExtraer=new LinkedList<>();
		
		Guard[] entradas = { 
				chAlmacenar.in(), 
				chExtraer.in()
		};
		Alternative servicios = new Alternative(entradas); 
		int opcion = 0;
		while (true) { 
			try {
				opcion = servicios.fairSelect();
			} catch (ProcessInterruptedException e){}

			switch(opcion){
			case ALMACENAR:
				// COMPLETAR: tratamiento de la petici ́on
				queueAlmacenar.add((Producto[]) chAlmacenar.in().read());
				break;
			case EXTRAER:
				// COMPLETAR: tratamiento de la petici ́on
				queueExtraer.add((Object[]) chExtraer.in().read());
				break;
			}
			// COMPLETAR: atenci ́on de peticiones pendientes
			revisarPeticiones(queueAlmacenar,queueExtraer);

		}
	}
	//METODO AUXILIAR
	private void revisarPeticiones(Queue<Producto[]> queueAlmacenar, Queue<Object[]> queueExtraer) {
		int n1 = queueAlmacenar.size();
		int n2 = queueExtraer.size();
		Producto[] petAlm;
		Object[] petExt;
		for(int i= 0; i<n1; i++) {
			petAlm= queueAlmacenar.peek();

			if(this.almacen.size()+petAlm.length<=TAM) {
				for(int j = 0; i< petAlm.length;i++) {
					almacen.add(petAlm[j]);
				}
				queueAlmacenar.poll();
			}
			else {
				queueAlmacenar.poll();
				queueAlmacenar.add(petAlm);
			}

		}

		for(int i= 0; i<n2; i++) {
			petExt= queueExtraer.peek();
			Producto [] respuesta= (Producto[]) petExt[0];
			One2OneChannel c_respuesta= (One2OneChannel) petExt[0];
			

			if( respuesta.length<=almacen.size()  ) {
				for(int j = 0; i< respuesta.length;i++) {
					respuesta[j]=almacen.poll();
				}
				c_respuesta.out().write(respuesta);
				queueExtraer.poll();
			}
			else {
				queueExtraer.poll();
				queueExtraer.add(petExt);
			}
		}



	}
}