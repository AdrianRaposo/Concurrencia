import es.upm.babel.cclib.Almacen;
import es.upm.babel.cclib.Productor;
import es.upm.babel.cclib.Consumidor;

import org.jcsp.lang.*;

/**
 * Programa concurrente para productor-buffer-consumidor con almacen
 * de capacidad 10 implementado con JCSP (AlmacenSelectCondicional).
 */
class CC_10_P10CJCSP {
    // Clase sin instancias
    private CC_10_P10CJCSP() {
    }

    public static final void main(final String[] args)
        throws InterruptedException {

        // Los canales de la select del servidor se crean aquí
        Any2OneChannel chPetAlmacenar = Channel.any2one();
        Any2OneChannel chPetExtraer  = Channel.any2one();

        // Ahora creamos los procesos, usando las construcciones de
        // CSP
        new Parallel 
            (new CSProcess[] {
                new ProductorCSP(chPetAlmacenar.out()),
                new ProductorCSP(chPetAlmacenar.out()),
                new ProductorCSP(chPetAlmacenar.out()),
                new ProductorCSP(chPetAlmacenar.out()),
                new AlmacenSelectCondicional(chPetAlmacenar.in(),
                                             chPetExtraer.in()),
                new ConsumidorCSP(chPetExtraer.out()),
                new ConsumidorCSP(chPetExtraer.out())
                // new ConsumidorCSP(chPetExtraer.out())
                // new ConsumidorCSP(chPetExtraer.out())
            }).run();
    }
}
