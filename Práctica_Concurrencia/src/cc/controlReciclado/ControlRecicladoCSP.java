package cc.controlReciclado;

import java.util.LinkedList;
import java.util.Queue;
import org.jcsp.lang.*;



public class ControlRecicladoCSP implements ControlReciclado, CSProcess {

	// constantes varias
	private enum Estado { LISTO, SUSTITUIBLE, SUSTITUYENDO }

	private final int MAX_P_CONTENEDOR; 
	private final int MAX_P_GRUA;  

	//canales para la comunicacion con el servidor
	private final Any2OneChannel chNotificarPeso;
	private final Any2OneChannel chIncrementarPeso;
	private final Any2OneChannel chNotificarSoltar;
	private final Any2OneChannel chPrepararSustitucion;
	private final Any2OneChannel chNotificarSustitucion;


	// clase auxiliar para peticiones de incrementar peso
	private static class PetIncrementarPeso {
		public int p;
		// Canal para poder bloquear el proceso hasta que se complete la peticion
		public One2OneChannel chACK;

		PetIncrementarPeso (int p) {
			//Inicializamos los atributos de la peticion
			this.p = p;
			this.chACK = Channel.one2one();
		}
	}

	public ControlRecicladoCSP(int max_p_contenedor,
			int max_p_grua) {
		// constantes del sistema
		MAX_P_CONTENEDOR = max_p_contenedor;
		MAX_P_GRUA       = max_p_grua;
		//Inicializacion de los canales
		chNotificarPeso  = Channel.any2one();
		chIncrementarPeso = Channel.any2one();
		chNotificarSoltar = Channel.any2one();
		chPrepararSustitucion = Channel.any2one();
		chNotificarSustitucion = Channel.any2one();

		new ProcessManager(this).start();
	}

	// interfaz ControlReciclado

	//  PRE: 0 < p < MAX_P_GRUA
	// CPRE: self.estado =/= SUSTITUYENDO
	// notificarPeso(p)
	public void notificarPeso(int p) throws IllegalArgumentException {
		if(p<=0 || p> this.MAX_P_GRUA)
			throw new IllegalArgumentException(); // No se cumple la PRE
		this.chNotificarPeso.out().write(p);
	}

	//  PRE: 0 < p < MAX_P_GRUA
	// CPRE: self.estado =/= SUSTITUYENDO /\
	//       self.peso + p <= MAX_P_CONTENEDOR
	// incrementarPeso(p)
	public void incrementarPeso(int p) throws IllegalArgumentException {
		if(p<=0 || p> this.MAX_P_GRUA)
			throw new IllegalArgumentException();// No se cumple la PRE
		// crecion de una peticion de incrementar
		PetIncrementarPeso pet = new PetIncrementarPeso(p); 
		chIncrementarPeso.out().write(pet);
		// Se bloquea el proceso hasta que se reciva al repsuesta del servidor
		pet.chACK.in().read();
	}

	//  PRE: --
	// CPRE: --
	// notificarSoltar()
	public void notificarSoltar() {
		chNotificarSoltar.out().write(null);
	}

	//  PRE: --
	// CPRE: self = (_, sustituible, 0)
	// prepararSustitucion()
	public void prepararSustitucion() {
		chPrepararSustitucion.out().write(null);
	}

	//  PRE: --
	// CPRE: --
	// notificarSustitucion()
	public void notificarSustitucion() {
		chNotificarSustitucion.out().write(null);
	}

	// SERVIDOR
	public void run() {
		
		// estado del recurso

		int peso = 0;
		Estado estado = Estado.LISTO;
		int accediendo = 0;


		Guard[] entradas = {
				chNotificarPeso.in(),
				chIncrementarPeso.in(),
				chNotificarSoltar.in(),
				chPrepararSustitucion.in(),
				chNotificarSustitucion.in()
		};
		Alternative servicios =  new Alternative (entradas);

		final int NOTIFICAR_PESO = 0;
		final int INCREMENTAR_PESO = 1;
		final int NOTIFICAR_SOLTAR = 2;
		final int PREPARAR_SUSTITUCION = 3;
		final int NOTIFICAR_SUSTITUCION = 4;
		// condiciones 
		final boolean[] sincCond = new boolean[5];
		// CPRES qeu siempre son TRUE
		sincCond[NOTIFICAR_SOLTAR] = true; 
		sincCond[NOTIFICAR_SUSTITUCION] = true; 

		Queue<PetIncrementarPeso> peticionesAplazadas = new LinkedList<>();

		boolean desbloqueo = true;

		// bucle de servicio
		while (true) {
			//Actualizamos las CPRES en cada iteracion del bucle
			//CPRES que no tienen que ver con parametros enviados por los clientes
			sincCond[INCREMENTAR_PESO]= estado != Estado.SUSTITUYENDO ;
			sincCond[NOTIFICAR_PESO]= estado != Estado.SUSTITUYENDO;
			sincCond[PREPARAR_SUSTITUCION]= estado == Estado.SUSTITUIBLE && accediendo == 0;


			switch (servicios.fairSelect(sincCond)) {
			//Se cumple la CPRE
			case NOTIFICAR_PESO:
				int p = (int) chNotificarPeso.in().read();
				if(p+peso> MAX_P_CONTENEDOR) 
					estado = Estado.SUSTITUIBLE;
				else if(p+peso<= MAX_P_CONTENEDOR)
					estado =Estado.LISTO;
				desbloqueo = true;
				break;
			case INCREMENTAR_PESO:
				//Se cumple la CPRE
				//Leemos la peticion del cliente
				PetIncrementarPeso pet = (PetIncrementarPeso) chIncrementarPeso.in().read(); 
				// CPRE parametro del cliente 
				if(pet.p +peso <= MAX_P_CONTENEDOR ) { 
					peso+= pet.p;
					accediendo++;
					// Se confirma al cliente que la peticion ha sido realizada
					pet.chACK.out().write(null); 
				}else {
					// No se cumple CPRE, encolamos el proceso hasta qeu se pueda realizar al peticion
					peticionesAplazadas.add(pet); 
				}
				desbloqueo = true;
				break;
			case NOTIFICAR_SOLTAR:
				chNotificarSoltar.in().read();
				accediendo--;
				desbloqueo= true;
				break;
			case PREPARAR_SUSTITUCION:
				//Se cumple la CPRE
				chPrepararSustitucion.in().read();
				estado= Estado.SUSTITUYENDO;
				desbloqueo = false;
				break;
			case NOTIFICAR_SUSTITUCION:

				chNotificarSustitucion.in().read();
				estado= Estado.LISTO;
				peso=0;
				accediendo=0;
				desbloqueo = true;
				break;
			} // switch

			// CODIGO DESBLOQUEO PETICIONES
			// Una vez realizada una peticion por parte de alguno cliente se compruba si se puede realizar alguna mas
			if(desbloqueo) {
				int n = peticionesAplazadas.size();
				// variable auxiliar para recibir las peticiones
				PetIncrementarPeso petAux; 
				for(int i = 0; i<n;i++) {
					//peticion que lleva mas tiempo esperando
					petAux = peticionesAplazadas.peek(); 
					// Se comprueba la CPRE
					if(petAux.p+peso<= this.MAX_P_CONTENEDOR && estado != Estado.SUSTITUYENDO ) { 
						peso+=petAux.p;
						accediendo++;
						// Se cumple la CPRE y desbloqueamos el proceso
						petAux.chACK.out().write(null); 
						peticionesAplazadas.poll();
					}else {// No se cumple la CPRE
						peticionesAplazadas.poll();
						// Se vuelve a encolar la peticion hasta que se pueda realizar
						peticionesAplazadas.add(petAux);
					}

				}
				// En este punto ya no quedan peticones pendientes que se podrian desbloquear

			}
		} // bucle servicio
	} // run() SERVER
} // class ControlRecicladoCSP