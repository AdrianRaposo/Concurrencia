import es.upm.babel.cclib.Fabrica;
import es.upm.babel.cclib.Producto;

import org.jcsp.lang.*;

// Clase de los procesos productores
class ProductorCSP implements CSProcess {
    // por este punto de envío enviaremos los datos
    private ChannelOutput petAlmacenar;

    // Evitando construcciones incorrectas
    private ProductorCSP() {
    }

    public ProductorCSP(ChannelOutput petAlmacenar) {
        this.petAlmacenar = petAlmacenar;
    }

    public void run() {
        while (true) {
            Producto p = Fabrica.producir();
            petAlmacenar.write(p);
        }
    }
}
