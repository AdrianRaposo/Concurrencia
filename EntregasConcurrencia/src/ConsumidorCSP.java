import es.upm.babel.cclib.Consumo;
import es.upm.babel.cclib.Producto;

import org.jcsp.lang.*;

// Clase de los procesos consumidores
class ConsumidorCSP implements CSProcess {
    // por este punto de envío pediremos los datos...
    private ChannelOutput petExtraer;
    // enviando el canal por el que queremos que nos respondan:
    private One2OneChannel chResp = Channel.one2one();

    // Evitando construcciones incorrectas
    private ConsumidorCSP() {
    }

    public ConsumidorCSP(ChannelOutput petExtraer) {
        this.petExtraer = petExtraer;
    }

    public void run() {
        while (true) {
	    petExtraer.write(chResp.out());
	    Producto p = (Producto) chResp.in().read();
            Consumo.consumir(p);
        }
    }
}
