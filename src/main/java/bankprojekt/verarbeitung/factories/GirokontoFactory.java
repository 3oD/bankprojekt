package bankprojekt.verarbeitung.factories;

import bankprojekt.verarbeitung.Girokonto;
import bankprojekt.verarbeitung.Konto;
import bankprojekt.verarbeitung.Kunde;

/**
 * A factory class for creating instances of {@link Girokonto}.
 *
 */
public class GirokontoFactory extends KontoFactory {
    private static final double STANDARD_DISPO = 1000;

    @Override
    public Konto createKonto(Kunde inhaber, long kontonummer) {
        return new Girokonto(inhaber, kontonummer, STANDARD_DISPO);
    }
}
