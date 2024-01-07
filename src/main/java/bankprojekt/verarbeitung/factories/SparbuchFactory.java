package bankprojekt.verarbeitung.factories;

import bankprojekt.verarbeitung.Konto;
import bankprojekt.verarbeitung.Kunde;
import bankprojekt.verarbeitung.Sparbuch;

/**
 * A factory class for creating instances of {@link Sparbuch}.
 *
 */
public class SparbuchFactory extends KontoFactory {
    @Override
    public Konto createKonto(Kunde inhaber, long nummer) {
        return new Sparbuch(inhaber, nummer);
    }
}
