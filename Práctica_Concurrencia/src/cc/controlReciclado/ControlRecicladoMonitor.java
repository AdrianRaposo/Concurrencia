package cc.controlReciclado;
import java.util.ArrayList;
import es.upm.aedlib.Pair;
import es.upm.babel.cclib.Monitor;

public final class ControlRecicladoMonitor implements ControlReciclado {
	private enum Estado { LISTO, SUSTITUIBLE, SUSTITUYENDO }
	
	//Recursos compartidos
	private Estado estado;
	private int peso;
	private int accediendo;



	private final int MAX_P_CONTENEDOR;
	private final int MAX_P_GRUA;
	private ArrayList<Pair<Integer,Monitor.Cond>> condIncrementarPeso;
	// Monitor Y Conditions
	private Monitor mutex;
	private Monitor.Cond condNotificarPeso;
	private Monitor.Cond condPrepararSustitucion;

	public ControlRecicladoMonitor (int max_p_contenedor,
			
			int max_p_grua) {

		MAX_P_CONTENEDOR = max_p_contenedor;
		MAX_P_GRUA = max_p_grua;
		// Inizializacion de los recursos
		this.estado= Estado.LISTO ;
		this.peso= 0;
		this.accediendo=0;
		//Inicializacion Monitor Y Conds
		condIncrementarPeso = new ArrayList<>();
		mutex = new Monitor();
		condNotificarPeso = mutex.newCond();
		condPrepararSustitucion= mutex.newCond();
	}
	
	//  PRE: 0 < p < MAX_P_GRUA
	// CPRE: self.estado =/= SUSTITUYENDO
	// notificarPeso(p)
	public void notificarPeso(int p) {

		// PRE: Se controla el peso de la grua para que este se correcto.

		if( p <= 0 || p > MAX_P_GRUA)
			throw new IllegalArgumentException(new Exception("El peso "+ p +" es inadecuado para la grua."));

		mutex.enter();

		// Se comprueba la  CPRE:  

		if( estado == Estado.SUSTITUYENDO) {
			condNotificarPeso.await(); // Se bloquea el proceso hasta que el estado se valido.
		} 

		//POST:

		if(this.peso + p > MAX_P_CONTENEDOR) {
			estado = Estado.SUSTITUIBLE;
		}
		else {
			estado = Estado.LISTO;
		}
		//desbloqueo
		desbloqueo();

		mutex.leave();

	}
	//  PRE: 0 < p < MAX_P_GRUA
	// CPRE: self.estado =/= SUSTITUYENDO /\
	//       self.peso + p <= MAX_P_CONTENEDOR
	// incrementarPeso(p)
	public void incrementarPeso(int p) {

		// PRE: Se controla el incremento del peso de la grua para que este se correcto.

		if(p<=0 || p>MAX_P_GRUA)
			throw new IllegalArgumentException(new Exception("El peso "+ p +" es inadecuado para la grua."));
		mutex.enter();

		// Se evalua la CPRE para cada parametro que llega:
		if(this.peso + p > MAX_P_CONTENEDOR || this.estado ==Estado.SUSTITUYENDO) {
			//Almacenamos el parametro junto a su cond
			this.condIncrementarPeso.add(new Pair <>(p , mutex.newCond()));
			this.condIncrementarPeso.get(this.condIncrementarPeso.size()-1).getRight().await();
		}


		//POST:

		this.peso +=p;
		this.accediendo ++;

		//desbloqueo
		desbloqueo();

		mutex.leave();
	}
	//  PRE: --
	// CPRE: --
	// notificarSoltar()
	public void notificarSoltar() {

		mutex.enter();
		//CPRE: TRUE

		//POST:
		this.accediendo--; 
		desbloqueo();
		mutex.leave();

	}
	//  PRE: --
	// CPRE: self = (_, sustituible, 0)
	// prepararSustitucion()
	public void prepararSustitucion() {

		mutex.enter();

		// Evaluamos la  CPRE:

		if (estado != Estado.SUSTITUIBLE || accediendo>0) {
			condPrepararSustitucion.await();
		}

		//POST: 

		this.estado= Estado.SUSTITUYENDO;
		this.accediendo=0;

		mutex.leave();

	}
	// PRE: --
	// CPRE: --
	// notificarSustitucion()
	public void notificarSustitucion() {

		mutex.enter();

		//CPRE: TRUE

		this.estado= Estado.LISTO;
		this.peso=0;
		this.accediendo=0;

		//desbloqueo
		desbloqueo();

		mutex.leave();

	}


	//Metodos auxiliares

	private void desbloqueo() {
		//Variable auxiliar booleana para aseguraranos de solo hacer un desbloqueo
		boolean desbloqueado= false;
		// Evaluamos si la  CPRE de notificarPeso se cumple
		if(!desbloqueado && this.estado!= Estado.SUSTITUYENDO && condNotificarPeso.waiting()>0) {
			desbloqueado= true;
			//Se libera un proceso que cumpla la CPRE
			condNotificarPeso.signal();

		}
		// Evaluamos para todas las posibles CPRE de incrementarPeso si se cumple
		int nProcesosIncrementarPeso= this.condIncrementarPeso.size();
		for(int i =0 ; !desbloqueado &&  i<nProcesosIncrementarPeso; i++) {
			if(condIncrementarPeso.get(i).getLeft() + this.peso<= MAX_P_CONTENEDOR && this.estado != Estado.SUSTITUYENDO && condIncrementarPeso.get(i).getRight().waiting()>0) {
				desbloqueado = true;
				//Se libera un proceso que cumpla la CPRE
				condIncrementarPeso.get(i).getRight().signal();
				//Eliminamos el proceso liberado de la lista
				condIncrementarPeso.remove(i);
			}
		}

		// Evaluamos si la  CPRE de prepararSustitucion se cumple
		if(!desbloqueado && this.estado== Estado.SUSTITUIBLE && this.accediendo==0 && condPrepararSustitucion.waiting()>0) {
			desbloqueado = true;
			//Se libera un proceso que cumpla la CPRE
			condPrepararSustitucion.signal();
		}

	}
}
