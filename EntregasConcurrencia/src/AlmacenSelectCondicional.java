import es.upm.babel.cclib.Producto;

import org.jcsp.lang.*;
import java.util.*;

public class AlmacenSelectCondicional implements CSProcess {

    // Punto de recepción de los canales para almacenar y extraer
    private AltingChannelInput petAlmacenar = null;
    private AltingChannelInput petExtraer = null;

    // Estado derivado del recurso compartido
    private Queue<Producto> cola = new LinkedList<Producto>();
    final int MAX = 10;

    // Evitando construcciones incorrectas
    private AlmacenSelectCondicional () {
    }

    public AlmacenSelectCondicional (final AltingChannelInput petAlmacenar,
                                     final AltingChannelInput petExtraer) {
	this.petAlmacenar = petAlmacenar;
	this.petExtraer  = petExtraer; 
    }

    public void run() {
        // Nombres simbólicos para los índices
        final int ALMACENAR = 0;
        final int EXTRAER = 1;
        // Entradas de la select
	final AltingChannelInput[] entradas = {petAlmacenar, petExtraer};
        // Recepción alternativa
	final Alternative servicios =  new Alternative (entradas);
        // Sincronización condicional en la select
        final boolean[] sincCond = new boolean[2];

	while (true) {
            ChannelOutput resp;
            Producto item;

            // Preparación de las precondiciones
            sincCond[ALMACENAR] = cola.size() < MAX;
            sincCond[EXTRAER] = cola.size() > 0;

            switch (servicios.fairSelect(sincCond)) {
            case ALMACENAR: 
                item = (Producto) petAlmacenar.read();
                cola.add(item);
                break;
            case EXTRAER:
                resp = (ChannelOutput) petExtraer.read();
                resp.write(cola.peek());
                cola.poll();
                break;
            }
	}
    }   
}



