package bankprojekt.verarbeitung.factories;

import bankprojekt.verarbeitung.Girokonto;
import bankprojekt.verarbeitung.Konto;
import bankprojekt.verarbeitung.Kunde;

/**
 * The GirokontoFactory class is a concrete implementation of the abstract class KontoFactory.
 * It is responsible for creating instances of the Girokonto class.
 *
 * This factory class provides two methods to create Girokonto instances:
 * 1. createKonto() - This method creates a Girokonto object with default values.
 * 2. createKontoMitInhaber(Kunde inhaber) - This method creates a Girokonto object with the specified owner and
 *    initial balance. It also sets the standard dispo amount for the Girokonto.

 * Note: This class inherits from the abstract class KontoFactory.
 *
 * @see KontoFactory
 */
public class GirokontoFactory extends KontoFactory {
    private static final double STANDARD_DISPO = 1000;

    @Override
    public Konto createKonto(Kunde inhaber, long kontonummer) {
        return new Girokonto(inhaber, kontonummer, STANDARD_DISPO);
    }
}
